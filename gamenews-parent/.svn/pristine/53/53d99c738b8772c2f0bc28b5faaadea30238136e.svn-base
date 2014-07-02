package com.yy.android.gamenews.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.ArticleFlag;
import com.duowan.gamenews.ArticleInfo;
import com.duowan.gamenews.ArticleType;
import com.duowan.gamenews.Channel;
import com.duowan.gamenews.GetChannelArticleListRsp;
import com.duowan.gamenews.RefrshType;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.event.CheckExpireEvent;
import com.yy.android.gamenews.event.CommentEvent;
import com.yy.android.gamenews.event.RefreshEvent;
import com.yy.android.gamenews.model.ArticleModel;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.util.IPageCache;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.TimeUtil;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.thread.BackgroundTask;
import com.yy.android.gamenews.R;

import de.greenrobot.event.EventBus;

public class ArticleListFragment extends BaseListFragment<ArticleInfo> {
	private static final String KEY_CHANNEL = "channel";
	private static final String KEY_VIEWED_LIST = "viewed_list";
	private static final String KEY_ARTICLE_LIST = "article_list";
	private static final String KEY_NEED_LOAD = "data_loaded";
	private static final String KEY_RESPONSE = "response";

	private IPageCache mPageCache;
	private ArticleListAdapter mAdapter;
	private Channel mChannel;
	private ListView mListView;

	private ArrayList<Long> mViewedList = new ArrayList<Long>();
	private GetChannelArticleListRsp mRsp;
	private boolean mIsLoading;
	private Preference mPref;
	private boolean mNeedLoadDisk = true;

	private static final String LOG_TAG = "ArticleListFragment";

	public static ArticleListFragment newInstance(Channel channel) {
		ArticleListFragment fragment = new ArticleListFragment();
		Bundle args = new Bundle();
		args.putSerializable(KEY_CHANNEL, channel);
		fragment.setArguments(args);
		return fragment;
	}

	public static ArticleListFragment newInstance(
			ArrayList<ArticleInfo> infoList) {
		ArticleListFragment fragment = new ArticleListFragment();
		Bundle args = new Bundle();
		args.putSerializable(KEY_ARTICLE_LIST, infoList);
		fragment.setArguments(args);
		return fragment;
	}

	public Channel getChannel() {
		return mChannel;
	}

	public ArticleListFragment() {
		super(0);
		mPageCache = new IPageCache();
		mPref = Preference.getInstance();
	}

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		EventBus.getDefault().register(this);
		if (bundle != null) {
			mChannel = (Channel) bundle.getSerializable(KEY_CHANNEL);
			mViewedList = (ArrayList<Long>) bundle
					.getSerializable(KEY_VIEWED_LIST);
			mRsp = (GetChannelArticleListRsp) bundle
					.getSerializable(KEY_RESPONSE);
		} else {
			mChannel = (Channel) getArguments().getSerializable(KEY_CHANNEL);
		}
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	protected ImageAdapter<ArticleInfo> getAdapter() {
		if (mAdapter == null) {
			mAdapter = new ArticleListAdapter(getActivity());
			mAdapter.setChannel(mChannel);
			mAdapter.setViewedArticleList(mViewedList);
		}
		return mAdapter;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(KEY_CHANNEL, mChannel);
		outState.putSerializable(KEY_VIEWED_LIST, mViewedList);
		outState.putBoolean(KEY_NEED_LOAD, mNeedLoadDisk);
		outState.putSerializable(KEY_RESPONSE, mRsp);

		super.onSaveInstanceState(outState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		mListView = getListView();
		if (mListView != null) {
			mListView.setOnItemClickListener(mOnItemClickListener);
		}

		if (savedInstanceState != null) {
			mNeedLoadDisk = savedInstanceState.getBoolean(KEY_NEED_LOAD);
		}
		if (mNeedLoadDisk) { // 只有在第一次进入界面加载，避免因被系统杀掉而再次加载
			new BgTask().execute();
		}
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onDestroyView() {
		setListAdapter(null);
		super.onDestroyView();
	}

	@Override
	public void onResume() {
		if (mCommentEvent != null && mSelectedPos != -1) {

			ListAdapter adapter = mListView.getAdapter();
			if (adapter != null) {
				if (mSelectedPos < adapter.getCount()) {
					ArticleInfo model = (ArticleInfo) mListView.getAdapter()
							.getItem(mSelectedPos);
					model.setCommentCount(mCommentEvent.getCommentCount());
					mAdapter.notifyDataSetChanged();
					saveListToDisk(getDataSource());
				}
			}
			mSelectedPos = -1;
			mCommentEvent = null;
		}
		super.onResume();
	}

	private CommentEvent mCommentEvent;

	public void onEvent(CommentEvent event) {
		mCommentEvent = event;
	}

	public void onEvent(RefreshEvent event) {
		if (event == null) {
			return;
		}

		Channel channel = event.mChannel;
		if (mChannel == channel // 考虑到为空的情况
				|| (channel != null && channel.equals(mChannel))) {
			callRefresh();
		}
	}

	public void onEvent(CheckExpireEvent event) {
		if (event == null) {
			return;
		}

		Channel channel = event.mChannel;
		if (mChannel == channel // 考虑到为空的情况
				|| (channel != null && channel.equals(mChannel))) {
			checkExpire();
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if ((scrollState == OnScrollListener.SCROLL_STATE_FLING || scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)) {
			mAdapter.pause();
		} else {
			mAdapter.resume();
		}
		super.onScrollStateChanged(view, scrollState);

	}

	private int mSelectedPos = -1;
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Adapter adapter = (Adapter) parent.getAdapter();
			ArticleInfo model = (ArticleInfo) adapter.getItem(position);
			if (model != null) {
				mSelectedPos = position;
				switch (model.getArticleType()) {
				case ArticleType._ARTICLE_TYPE_ARTICLE: {

					if (mChannel != null) {
						if (!mViewedList.contains(model.getId())) {
							if (mViewedList.size() >= Constants.CACHE_SIZE_VIEWED_ARTI_LIST) {
								mViewedList.remove(0);
							}
							mViewedList.add(model.getId());

							ArrayList<Long> savedList = (ArrayList<Long>) mViewedList
									.clone();
							mSaveCacheTask
									.execute(
											Constants.CACHE_KEY_VIEWED_ARTICLE_LIST,
											savedList,
											Constants.CACHE_DURATION_FOREVER);
							mAdapter.notifyDataSetChanged();
						}
					}
					ArticleDetailActivity.startArticleDetailActivity(
							getActivity(), model);
					break;
				}
				case ArticleType._ARTICLE_TYPE_SPECIAL: {
					SpecialArticleActivity.startSpecialArticleActivity(
							getActivity(), model.getId());
					// Intent intent = new Intent(getActivity(),
					// SpecialArticleActivity.class);
					// intent.putExtra(SpecialArticleActivity.KEY_SPECIAL_INFO,
					// model);
					// startActivity(intent);
					break;
				}
				case ArticleType._ARTICLE_TYPE_BANG:
				case ArticleType._ARTICLE_TYPE_ACTIVITY:
				case ArticleType._ARTICLE_TYPE_CAIDAN: {

					Intent intent = new Intent(getActivity(),
							AppWebActivity.class);
					intent.putExtra(AppWebActivity.KEY_URL,
							model.getSourceUrl());
					intent.putExtra(AppWebActivity.KEY_FROM,
							AppWebActivity.FROM_HD);
					startActivity(intent);
					break;
				}
				}
			}
		};
	};

	protected void requestData(final int refresh) {
		if (mIsLoading) {
			return;
		}

		if (mChannel == null) {
			requestFinish(refresh, null, true);
			return;
		}

		mIsLoading = true;
		Map<Integer, String> attachInfo = null;
		if (mRsp != null) {
			attachInfo = mRsp.getAttachInfo();
		}
		ArticleModel.getArticleList(
				new ResponseListener<GetChannelArticleListRsp>(getActivity()) {
					@Override
					public void onResponse(GetChannelArticleListRsp data) {

						requestFinish(refresh, data, false);
					}

					@Override
					public void onError(Exception e) {
						requestFinish(refresh, null, true);

						if (e != null) {
							ToastUtil.showToast(R.string.http_error);
						}
						super.onError(e);
					}
				}, // Listener
				refresh, mChannel.getId(), attachInfo, false);
	}

	private void prepareEmptyText(boolean error) {
		String emptyText = null;
		if (Constants.MY_FAVOR_CHANNEL_ID == mChannel.getId()) {
			List<Channel> channelList = mPref.getMyFavorChannelList();
			if (channelList == null || channelList.size() == 0) {
				emptyText = strEmptyAddChannel;
			}
		}

		if (emptyText == null) {
			if (error) {
				emptyText = strEmptyReload;
			} else {
				emptyText = strEmptyNoData;
			}
		}
		setEmptyText(emptyText);
	}

	private void requestFinish(int refresh, GetChannelArticleListRsp data,
			boolean error) {

		prepareEmptyText(error);

		mIsLoading = false;
		ArrayList<ArticleInfo> dataList = null;
		boolean hasMore = false;

		if (data != null) {
			dataList = data.getArticleList();
			mRsp = data;
		}
		// 置顶 +
		// newTopList是新拉到的列表里的置顶列表
		List<ArticleInfo> newTopList = new ArrayList<ArticleInfo>();
		if (dataList != null && dataList.size() > 0) {

			hasMore = data.hasMore;

			for (int i = 0; i < dataList.size(); i++) {
				ArticleInfo info = dataList.get(i);
				if ((info.flag & ArticleFlag._ARTICLE_FLAG_TOP) != 0) {
					newTopList.add(info);
					dataList.remove(i);
					i--;
				}
			}

			// oldTopList是原来列表里有的置顶列表
			List<ArticleInfo> oldTopList = new ArrayList<ArticleInfo>();
			List<ArticleInfo> oldList = getDataSource();
			if (oldList != null) {
				for (int i = 0; i < oldList.size(); i++) {
					ArticleInfo info = oldList.get(i);
					if ((info.flag & ArticleFlag._ARTICLE_FLAG_TOP) != 0) {
						String time = info.extraInfo
								.get((long) ArticleFlag._ARTICLE_FLAG_TOP);
						if (TimeUtil.isExpire(time)) { // 如果过期则取消置顶
							info.flag = info.flag
									& ~ArticleFlag._ARTICLE_FLAG_TOP;
						} else {
							oldTopList.add(info);
							oldList.remove(i);
							i--;
						}
					} else {
						break; // 置顶元素始终在列表顶端
					}
				}
			}

			List<ArticleInfo> headList = null;
			if (refresh == RefrshType._REFRESH_TYPE_LOAD_MORE) { // 如果是加载更多，则将置顶数据加到原列表头
				headList = getDataSource();
			} else { // 如果是刷新，则将置顶数据加到新列表头
				headList = dataList;
			}
			if (headList != null) {
				headList.addAll(0, oldTopList);
				headList.addAll(0, newTopList);
			}
			// 置顶 -
		}

		requestFinish(refresh, dataList, hasMore, false);
		// rearrange(dataSource);
		// 保存到本地
		saveListToDisk(getDataSource());
	}

	private void saveListToDisk(ArrayList<ArticleInfo> list) {
		ArrayList<ArticleInfo> savedList = new ArrayList<ArticleInfo>();
		ArrayList<ArticleInfo> totalList = list;
		if (totalList == null || mRsp == null) {
			return;
		}
		if (totalList.size() > Constants.CACH_SIZE_HOME_ARTI_LIST) {
			savedList.addAll(totalList.subList(0,
					Constants.CACH_SIZE_HOME_ARTI_LIST));
		} else {
			savedList.addAll(totalList);
		}
		if (savedList.size() > 0) {
			mRsp.setArticleList(savedList);
		}

		GetChannelArticleListRsp savedRsp = (GetChannelArticleListRsp) mRsp
				.clone();
		String key = Constants.CACHE_KEY_HOME_LIST + mChannel.getId();
		mSaveCacheTask
				.execute(key, savedRsp, Constants.CACHE_DURATION_HOMELIST);
	}

	private SaveCacheTask mSaveCacheTask = new SaveCacheTask();

	private class SaveCacheTask extends BackgroundTask<Object, Void, Void> {
		@Override
		protected Void doInBackground(Object... params) {

			String key = (String) params[0];
			Object value = params[1];
			int duration = (Integer) params[2];

			mPageCache.setObject(key, value, duration);
			return null;
		}
	}

	private class BgTask extends BackgroundTask<Void, Void, Boolean> {
		@SuppressWarnings("unchecked")
		@Override
		protected Boolean doInBackground(Void... params) {
			mNeedLoadDisk = false;

			// 如果是我的最爱的频道，且用户没有添加喜欢的频道，则清空我的最爱的缓存
			// 并提示用户去添加喜欢的频道
			if (Constants.MY_FAVOR_CHANNEL_ID == mChannel.getId()) {
				List<Channel> channelList = mPref.getMyFavorChannelList();
				if (channelList == null || channelList.size() == 0) {
					mPageCache.setObject(Constants.CACHE_KEY_HOME_LIST
							+ mChannel.getId(), null,
							Constants.CACHE_DURATION_HOMELIST);
					return false;
				}

			}

			mRsp = mPageCache.getObject(Constants.CACHE_KEY_HOME_LIST
					+ mChannel.getId(), GetChannelArticleListRsp.class);

			List<Long> idList = mPageCache.getObject(
					Constants.CACHE_KEY_VIEWED_ARTICLE_LIST, ArrayList.class);
			if (idList != null) {

				mViewedList.addAll(idList);
			}

			return true;
		}

		@Override
		protected void onPostExecute(Boolean needReload) {

			if (isDetached()) {
				return;
			}
			ArrayList<ArticleInfo> list = null;

			if (mRsp != null) {
				list = mRsp.getArticleList();
			}

			if ((list == null || list.size() == 0) && needReload) {
				loadData();
			} else {
				requestFinish(RefrshType._REFRESH_TYPE_LOAD_MORE, mRsp, false);
			}
			super.onPostExecute(needReload);
		}
	}

	public void checkExpire() {
		if (mChannel != null) {
			List<ArticleInfo> dataList = getDataSource();
			if (dataList != null && dataList.size() > 0) { // 如果列表为空，不需要刷新
				if (mPageCache.isExpire(Constants.CACHE_KEY_HOME_LIST
						+ mChannel.getId())) {
					callRefresh();
				}
			}
		}
	}

	@Override
	protected void onEmptyViewClicked() {
		if (Constants.MY_FAVOR_CHANNEL_ID == mChannel.getId()) {
			List<Channel> channelList = mPref.getMyFavorChannelList();
			if (channelList == null || channelList.size() == 0) {
				Intent intent = new Intent(getActivity(),
						ChannelDepotActivity.class);
				startActivity(intent);
			} else {
				super.onEmptyViewClicked();
			}

		} else {
			super.onEmptyViewClicked();
		}
	}
}