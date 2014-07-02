package com.yy.android.gamenews.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.Channel;
import com.duowan.gamenews.GetColumnChannelListRsp;
import com.duowan.gamenews.SearchChannelRsp;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.R;
import com.yy.android.gamenews.event.SubscribeEvent;
import com.yy.android.gamenews.model.ChannelModel;
import com.yy.android.gamenews.ui.common.SwitchImageLoader;
import com.yy.android.gamenews.ui.view.ExpandableHeightGridView;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;

import de.greenrobot.event.EventBus;

public class ChannelGridFragment extends Fragment {

	private TextView mLable;
	private ExpandableHeightGridView mGridView;
	private GridItemViewAdapter mAdapter;
	private TextView mAnother;
	private String mType;
	private String mAttachInfo;
	private boolean mHasMore = false;
	private int mColumnId = -1;
	private String mKeyWord;
	private int mTips = 0;
	private List<Channel> mChannels = new ArrayList<Channel>();
	private ArrayList<Channel> mChangedChannels = new ArrayList<Channel>();
	private boolean mHasChanged = false;

	OnClickListener mSelectChannelListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Channel channelData = (Channel) v.getTag();
			if (channelData == null) {
				return;
			}
			View view = v.findViewById(R.id.channel_chosen);
			if (view.getVisibility() == View.VISIBLE) {
				view.setVisibility(View.INVISIBLE);
				if (Util.isSubscribedChannel(mChannels, channelData)) {
					mChannels.remove(channelData);
				}
			} else if (view.getVisibility() == View.INVISIBLE) {
				if (mChannels.size() >= Constants.SUBSCRIBE_MOST_LIMIT) {
					String tips;
					switch (mTips) {
					case 0:
						tips = getResources().getString(
								R.string.channel_manage_too_mandy0);
						mTips++;
						break;
					case 1:
						tips = getResources().getString(
								R.string.channel_manage_too_mandy1);
						mTips++;
						break;
					case 2:
						tips = getResources().getString(
								R.string.channel_manage_too_mandy2);
						mTips++;
						break;
					case 3:
						tips = getResources().getString(
								R.string.channel_manage_too_mandy3);
						mTips++;
						break;

					default:
						mTips = 1;
						tips = getResources().getString(
								R.string.channel_manage_too_mandy0);
						break;
					}
					ToastUtil.showToast(tips);
					return;
				}
				if (!Util.isSubscribedChannel(mChannels, channelData)) {
					Util.validChannelData(channelData);
					mChannels.add(channelData);
				}
				view.setVisibility(View.VISIBLE);
			}
			updateChangedChannels(channelData);
			mHasChanged = true;
		}
	};

	public static ChannelGridFragment newInstance(String key,
			String attachInfo, boolean hasMore, ArrayList<Channel> channels) {
		ChannelGridFragment fragment = new ChannelGridFragment();
		Bundle args = new Bundle();
		args.putString(Constants.EXTRA_GRID_FG_TYPE,
				Constants.EXTRA_GRID_FG_TYPE_SEARCH);
		args.putString(Constants.EXTRA_GRID_FG_KEY_WORD, key);
		args.putBoolean(Constants.EXTRA_HAS_MORE, hasMore);
		args.putString(Constants.EXTRA_ATTACHINFO, attachInfo);
		args.putSerializable(Constants.EXTRA_GRID_FG_CHANNELS, channels);
		fragment.setArguments(args);
		return fragment;
	}

	public static ChannelGridFragment newInstance(int columnId,
			String columnName) {
		ChannelGridFragment fragment = new ChannelGridFragment();
		Bundle args = new Bundle();
		args.putString(Constants.EXTRA_GRID_FG_TYPE,
				Constants.EXTRA_GRID_FG_TYPE_MORE);
		args.putInt(Constants.EXTRA_COLUMN_ID, columnId);
		args.putString(Constants.EXTRA_COLUMN_NAME, columnName);
		fragment.setArguments(args);
		return fragment;
	}

	public void refreshSearchResults(String key, boolean hasMore,
			String attachInfo, ArrayList<Channel> channels) {
		if (mAdapter == null) {
			return;
		}
		mAdapter.clearAll();
		if (channels != null && channels.size() > 0) {
			mLable.setVisibility(View.GONE);
			mKeyWord = key;
			mHasMore = hasMore;
			mAttachInfo = attachInfo;
			mAdapter.append(channels);
		}

		if (!mHasMore) {
			mAnother.setText(R.string.channel_no_more);
			mAnother.setEnabled(false);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mChannels = Preference.getInstance().getMyFavorChannelList();
		if (mChannels == null) {
			mChannels = new ArrayList<Channel>();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.channel_grid_fragment, container,
				false);
		mLable = (TextView) view.findViewById(R.id.channel_more_lable);
		mGridView = (ExpandableHeightGridView) view.findViewById(R.id.gridview);
		mAnother = (TextView) view.findViewById(R.id.another);
		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mHasChanged) {
			Preference.getInstance().saveMyFavorChannelList(mChannels);
			ChannelModel.updateMyFavChannelList(getActivity(),
					(ArrayList<Channel>) mChannels);
			SubscribeEvent event = new SubscribeEvent();
			event.isSubscribeChanged = true;
			if (mChangedChannels.size() > 1) {
				event.isSubscribeMultiple = true;
			} else {
				event.isSubscribeMultiple = false;
			}
			EventBus.getDefault().post(event);
			mHasChanged = false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		mAdapter = new GridItemViewAdapter(getActivity());
		mGridView.setAdapter(mAdapter);
		Bundle arguments = getArguments();
		if (arguments != null) {
			mType = arguments.getString(Constants.EXTRA_GRID_FG_TYPE);
			if (Constants.EXTRA_GRID_FG_TYPE_SEARCH.equals(mType)) {
				mLable.setVisibility(View.GONE);
				mKeyWord = arguments
						.getString(Constants.EXTRA_GRID_FG_KEY_WORD);

				mHasMore = arguments.getBoolean(Constants.EXTRA_HAS_MORE);
				mAttachInfo = arguments.getString(Constants.EXTRA_ATTACHINFO);
				ArrayList<Channel> channels = (ArrayList<Channel>) arguments
						.getSerializable(Constants.EXTRA_GRID_FG_CHANNELS);
				if (channels != null && channels.size() > 0) {
					mAdapter.append(channels);
				}
				mAnother.setVisibility(View.VISIBLE);
				if (!mHasMore) {
					mAnother.setText(R.string.channel_no_more);
					mAnother.setEnabled(false);
				}
			} else if (Constants.EXTRA_GRID_FG_TYPE_MORE.equals(mType)) {
				mColumnId = arguments.getInt(Constants.EXTRA_COLUMN_ID);
				String columnName = arguments
						.getString(Constants.EXTRA_COLUMN_NAME);
				mLable.setVisibility(View.INVISIBLE);
				mLable.setText(columnName);

				getColumnChannelList(mColumnId, null, 18);

			}
		}

		mAnother.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Constants.EXTRA_GRID_FG_TYPE_MORE.equals(mType)) {
					if (mHasMore) {
						getColumnChannelList(mColumnId, mAttachInfo, 18);
					} else {
						mAnother.setText(R.string.channel_no_more);
						mAnother.setEnabled(false);
					}
				} else if (Constants.EXTRA_GRID_FG_TYPE_SEARCH.equals(mType)) {
					if (mHasMore) {
						searchChannel(mKeyWord, mAttachInfo, 18);
					} else {
						mAnother.setText(R.string.channel_no_more);
						mAnother.setEnabled(false);
					}
				}
			}
		});
	}

	public class GridItemViewAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private ArrayList<Channel> mData;

		public GridItemViewAdapter(Context c) {
			mInflater = (LayoutInflater) c
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mData = new ArrayList<Channel>();
		}

		public int getCount() {
			return mData.size();
		}

		public Object getItem(int position) {
			return mData.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public void clearAll() {
			mData.clear();
		}

		public void append(ArrayList<Channel> datas) {
			for (Channel data : datas) {
				Util.validChannelData(data);
			}
			mData.addAll(datas);
			notifyDataSetChanged();
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.channel_columns_item,
						null);
			}

			Channel channelData = mData.get(position);
			FrameLayout frameLayout = (FrameLayout) convertView
					.findViewById(R.id.channel_framelayout);
			ImageView channelImage = (ImageView) convertView
					.findViewById(R.id.channel_image);
			TextView channelName = (TextView) convertView
					.findViewById(R.id.channel_name);
			FrameLayout channelChosen = (FrameLayout) convertView
					.findViewById(R.id.channel_chosen);
			if (Util.isSubscribedChannel(mChannels, channelData)) {
				channelChosen.setVisibility(View.VISIBLE);
			} else {
				channelChosen.setVisibility(View.INVISIBLE);
			}
			SwitchImageLoader.getInstance().displayImage(
					channelData.getImage(), channelImage,
					SwitchImageLoader.DEFAULT_CHANNEL_BIG_DISPLAYER, true);
			channelName.setText(channelData.getName());
			frameLayout.setTag(channelData);
			frameLayout.setOnClickListener(mSelectChannelListener);

			return convertView;
		}

	}

	private void updateChangedChannels(Channel channel) {
		if (Util.isSubscribedChannel(mChangedChannels, channel)) {
			mChangedChannels.remove(channel);
		} else {
			Util.validChannelData(channel);
			mChangedChannels.add(channel);
		}
	}

	private void getColumnChannelList(int columnId, String attachInfo, int count) {
		if (getActivity() == null) {
			return;
		}
		ResponseListener<GetColumnChannelListRsp> responseListener = new ResponseListener<GetColumnChannelListRsp>(
				getActivity()) {
			public void onResponse(GetColumnChannelListRsp response) {
				mHasMore = response.getHasMore();
				mAttachInfo = response.getAttachInfo();
				ArrayList<Channel> list = response.getChannelList();
				mAdapter.append(list);
				mLable.setVisibility(View.VISIBLE);
				mAnother.setVisibility(View.VISIBLE);
				if (!mHasMore) {
					mAnother.setText(R.string.channel_no_more);
					mAnother.setEnabled(false);
				}
			}

			public void onError(Exception e) {
				ToastUtil.showToast(R.string.http_error);
			};
		};
		ChannelModel.getColumnChannelList(responseListener, columnId,
				attachInfo, count);
	}

	private void searchChannel(String keyWord, String attachInfo, int count) {
		if (getActivity() == null) {
			return;
		}
		ResponseListener<SearchChannelRsp> responseListener = new ResponseListener<SearchChannelRsp>(
				getActivity()) {
			public void onResponse(SearchChannelRsp response) {
				mHasMore = response.getHasMore();
				mAttachInfo = response.getAttachInfo();
				ArrayList<Channel> list = response.getChannelList();
				mAdapter.append(list);
				mAnother.setVisibility(View.VISIBLE);
				if (!mHasMore) {
					mAnother.setText(R.string.channel_no_more);
					mAnother.setEnabled(false);
				}
			}
		};
		ChannelModel
				.searchChannel(responseListener, keyWord, attachInfo, count);
	}

}