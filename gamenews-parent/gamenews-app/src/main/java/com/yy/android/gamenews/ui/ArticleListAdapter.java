package com.yy.android.gamenews.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.gamenews.ArticleFlag;
import com.duowan.gamenews.ArticleInfo;
import com.duowan.gamenews.ArticleType;
import com.duowan.gamenews.Channel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.ui.common.SwitchImageLoader;
import com.yy.android.gamenews.util.TimeUtil;
import com.yy.android.gamenews.R;

public class ArticleListAdapter extends ImageAdapter<ArticleInfo> {
	private LayoutInflater mInflater;
	private List<Long> mViewedArticleList;
	private Context mContext;
	private Channel mChannel;
	private DisplayImageOptions mBigDisplayer = SwitchImageLoader.DEFAULT_ARTICLE_ITEM_BIG_DISPLAYER;
	private DisplayImageOptions mDisplayer = SwitchImageLoader.DEFAULT_ARTICLE_ITEM_BIG_DISPLAYER;

	private static final String LOG_TAG = "[ArticleListAdapter]";

	public ArticleListAdapter(Context context) {
		super(context);
		mContext = context;
		mInflater = LayoutInflater.from(context);
		// mViewedArticleList = new IPageCache().getObject(
		// Constants.CACHE_KEY_VIEWED_ARTICLE_LIST, ArrayList.class);
	}

	public void setChannel(Channel channel) {
		mChannel = channel;
	}

	public void setViewedArticleList(List<Long> list) {
		mViewedArticleList = list;
	}

	@Override
	public int getItemViewType(int position) {

		ArticleInfo model = getItem(position);
		if ((model.flag & ArticleFlag._ARTICLE_FLAG_BIGIMAGE) != 0) {
			return VIEW_TYPE_VERTICAL_BIG;
		}
		List<String> imageList = model.getImageList();

		if (imageList == null || imageList.size() < 2) {
			return VIEW_TYPE_HORIZONTAL;
		} else {
			return VIEW_TYPE_VERTICAL;
		}
	}

	private static final int VIEW_TYPE_HORIZONTAL = 0;
	private static final int VIEW_TYPE_VERTICAL = 1;
	private static final int VIEW_TYPE_VERTICAL_BIG = 2;

	@Override
	public int getViewTypeCount() {

		return 3;
	}

	private boolean isItemViewed(ArticleInfo info) {
		if (info == null) {
			return false;
		}
		long id = info.getId();
		if (mViewedArticleList != null) {
			return mViewedArticleList.contains(id);
		}

		return false;
	}

	private DisplayImageOptions getDisplayOptions(int viewType) {
		switch (viewType) {
		case VIEW_TYPE_VERTICAL_BIG: {
			return mBigDisplayer;
		}
		}

		return mDisplayer;
	}

	private View getConvertView(int viewType) {
		ViewHolder holder = null;
		View convertView = null;
		switch (viewType) {
		case VIEW_TYPE_HORIZONTAL: {
			convertView = mInflater.inflate(R.layout.list_item_article_h, null);
			holder = new ViewHolder();

			holder.mCommentCount = (TextView) convertView
					.findViewById(R.id.list_article_count);
			holder.mFrom = (TextView) convertView
					.findViewById(R.id.list_article_from);
			holder.imageViewList.add((ImageView) convertView
					.findViewById(R.id.list_article_img1));
			holder.maskViewList.add((ImageView) convertView
					.findViewById(R.id.list_article_mask_img1));
			holder.mTime = (TextView) convertView
					.findViewById(R.id.list_article_time);
			holder.mTitle = (TextView) convertView
					.findViewById(R.id.list_article_title);
			holder.mCornerImg = (ImageView) convertView
					.findViewById(R.id.list_article_corner);
			holder.mInfoLayout = convertView
					.findViewById(R.id.list_article_info_layout);
			break;
		}
		case VIEW_TYPE_VERTICAL: {
			convertView = mInflater.inflate(R.layout.list_item_article_v, null);
			holder = new ViewHolder();
			holder.mCommentCount = (TextView) convertView
					.findViewById(R.id.list_article_count);
			holder.mFrom = (TextView) convertView
					.findViewById(R.id.list_article_from);
			holder.imageViewList.add((ImageView) convertView
					.findViewById(R.id.list_article_img1));
			holder.imageViewList.add((ImageView) convertView
					.findViewById(R.id.list_article_img2));
			holder.imageViewList.add((ImageView) convertView
					.findViewById(R.id.list_article_img3));
			holder.maskViewList.add((ImageView) convertView
					.findViewById(R.id.list_article_mask_img1));
			holder.maskViewList.add((ImageView) convertView
					.findViewById(R.id.list_article_mask_img2));
			holder.maskViewList.add((ImageView) convertView
					.findViewById(R.id.list_article_mask_img3));
			holder.mTime = (TextView) convertView
					.findViewById(R.id.list_article_time);
			holder.mTitle = (TextView) convertView
					.findViewById(R.id.list_article_title);
			holder.mCornerImg = (ImageView) convertView
					.findViewById(R.id.list_article_corner);
			holder.mInfoLayout = convertView
					.findViewById(R.id.list_article_info_layout);
			break;
		}
		case VIEW_TYPE_VERTICAL_BIG: {
			convertView = mInflater.inflate(
					R.layout.list_item_article_v_single_big, null);
			holder = new ViewHolder();

			holder.mCommentCount = (TextView) convertView
					.findViewById(R.id.list_article_count);
			holder.mFrom = (TextView) convertView
					.findViewById(R.id.list_article_from);
			holder.imageViewList.add((ImageView) convertView
					.findViewById(R.id.list_article_img1));
			holder.maskViewList.add((ImageView) convertView
					.findViewById(R.id.list_article_mask_img1));
			holder.mTime = (TextView) convertView
					.findViewById(R.id.list_article_time);
			holder.mTitle = (TextView) convertView
					.findViewById(R.id.list_article_title);
			holder.mCornerImg = (ImageView) convertView
					.findViewById(R.id.list_article_corner);
			holder.mInfoLayout = convertView
					.findViewById(R.id.list_article_info_layout);
			break;
		}
		default: {
			// never goes here
			break;
		}
		}
		if (convertView != null) {
			convertView.setTag(holder);
		}
		return convertView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		int itemViewType = getItemViewType(position);
		if (convertView == null) {
			convertView = getConvertView(itemViewType);
		}
		
		holder = (ViewHolder) convertView.getTag();

		convertView
				.setBackgroundResource(R.drawable.article_list_item_selector);

		// Log.d(LOG_TAG, "[getView] + for item :" + position);
		if (holder != null) {
			holder.mCornerImg.setVisibility(View.GONE);
			holder.mCommentCount.setVisibility(View.VISIBLE);
			holder.mFrom.setVisibility(View.VISIBLE);
			holder.mTime.setVisibility(View.VISIBLE);
			holder.mInfoLayout.setVisibility(View.VISIBLE);

			ArticleInfo model = getItem(position);

			if (model != null) {
				int articleType = model.getArticleType();

				if (articleType == ArticleType._ARTICLE_TYPE_CAIDAN) {
					holder.mTitle.setTextAppearance(mContext,
							R.style.HomeListBingoPrimaryText);
				} else if (isItemViewed(model)) {
					holder.mTitle.setTextAppearance(mContext,
							R.style.HomeListPrimaryTextDark);
				} else {

					holder.mTitle.setTextAppearance(mContext,
							R.style.HomeListPrimaryText);
				}
				holder.mCommentCount.setVisibility(View.VISIBLE);
				holder.mCommentCount.setText("" + model.getCommentCount());

				String sourceName = model.getSourceName();
				if (mChannel != null) {
					int id = mChannel.getId();
					String channelName = model.getChannelName();
					if (id == Constants.MY_FAVOR_CHANNEL_ID
							|| id == Constants.RECOMMD_ID) {
						if (channelName != null && !"".equals(channelName)) {
							sourceName = String.format("%s-%s", channelName,
									sourceName);
						}
					}
				}

				holder.mFrom.setText(sourceName);
				holder.mTime.setText(TimeUtil.perseTime(mContext,
						model.getTime()));
				holder.mTitle.setText(model.getTitle());
				holder.mCommentCount.setText("" + model.getCommentCount());

				if (articleType == ArticleType._ARTICLE_TYPE_SPECIAL) {
					holder.mCornerImg
							.setImageResource(R.drawable.ic_special_corner);
					holder.mCornerImg.setVisibility(View.VISIBLE);
					holder.mCommentCount.setVisibility(View.GONE);

				} else if (articleType == ArticleType._ARTICLE_TYPE_ACTIVITY) {
					holder.mCornerImg
							.setImageResource(R.drawable.ic_active_corner);
					holder.mCornerImg.setVisibility(View.VISIBLE);
					holder.mCommentCount.setVisibility(View.GONE);
					convertView
							.setBackgroundResource(R.drawable.article_list_item_active_selector);
				} else if (articleType == ArticleType._ARTICLE_TYPE_CAIDAN) {
					holder.mCornerImg
							.setImageResource(R.drawable.ic_bingo_corner);
					holder.mCornerImg.setVisibility(View.VISIBLE);
					holder.mInfoLayout.setVisibility(View.GONE);
					convertView
							.setBackgroundResource(R.drawable.article_list_item_bingo_selector);
				} else if (articleType == ArticleType._ARTICLE_TYPE_BANG) {
					holder.mCornerImg
							.setImageResource(R.drawable.ic_data_corner);
					holder.mCornerImg.setVisibility(View.VISIBLE);
					holder.mInfoLayout.setVisibility(View.GONE);
				} else {

					int resId = 0;
					long flag = model.flag;
					if ((flag & ArticleFlag._ARTICLE_FLAG_ADV) == ArticleFlag._ARTICLE_FLAG_ADV) {
						resId = R.drawable.ic_adv;
					} else if ((flag & ArticleFlag._ARTICLE_FLAG_HOT) == ArticleFlag._ARTICLE_FLAG_HOT) {
						resId = R.drawable.ic_hot;
					} else if ((flag & ArticleFlag._ARTICLE_FLAG_RECOMM) == ArticleFlag._ARTICLE_FLAG_RECOMM) {
						resId = R.drawable.ic_recom;
					}
					if (resId != 0) {
						holder.mCornerImg.setImageResource(resId);
						holder.mCornerImg.setVisibility(View.VISIBLE);
					}
				}

				List<String> videoList = model.getVideoList();
				List<String> urlList = model.getImageList();

				boolean isVideo;
				List<String> imgList = null;

				if ((model.flag & ArticleFlag._ARTICLE_FLAG_BIGIMAGE) != 0) {
					String bigImage = model.extraInfo
							.get((long) ArticleFlag._ARTICLE_FLAG_BIGIMAGE);
					imgList = new ArrayList<String>();
					imgList.add(bigImage);
					isVideo = false;
				} else if (videoList != null && videoList.size() > 0) {
					imgList = videoList;
					isVideo = true;
				} else {
					isVideo = false;
					imgList = urlList;
				}
				for (int i = 0; i < holder.imageViewList.size(); i++) {
					ImageView view = holder.imageViewList.get(i);
					ImageView mask = holder.maskViewList.get(i);
					if (isVideo) {
						mask.setVisibility(View.VISIBLE);
					} else {
						mask.setVisibility(View.GONE);
					}

					String url = null;
					if (imgList != null && i < imgList.size()) {
						url = imgList.get(i);
					}
					if (url != null) {
						view.setVisibility(View.VISIBLE);
						view.setImageResource(R.drawable.article_list_item_loading);
						displayImage(url, view, getDisplayOptions(itemViewType));
					} else {
						view.setVisibility(View.GONE);
					}
				}
			}
		}
		// Log.d(LOG_TAG, "[getView] - for item :" + position);
		return convertView;
	}

	private static class ViewHolder {
		TextView mTitle;
		TextView mFrom;
		TextView mCommentCount;
		TextView mTime;
		List<ImageView> imageViewList = new ArrayList<ImageView>();
		List<ImageView> maskViewList = new ArrayList<ImageView>();
		View mInfoLayout;
		// ImageView mImageView1;
		// ImageView mImageView2;
		// ImageView mImageView3;
		ImageView mCornerImg;
	}
}