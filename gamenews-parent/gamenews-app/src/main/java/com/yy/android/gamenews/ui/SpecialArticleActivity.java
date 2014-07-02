package com.yy.android.gamenews.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.duowan.android.base.event.UniPacketErrorEvent;
import com.duowan.android.base.event.VolleyErrorEvent;
import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.android.base.net.VolleyErrorHelper;
import com.duowan.gamenews.ArticleInfo;
import com.duowan.gamenews.GetSpecialArticleListRsp;
import com.duowan.gamenews.PicInfo;
import com.duowan.gamenews.RefrshType;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.R;
import com.yy.android.gamenews.event.CommentEvent;
import com.yy.android.gamenews.model.ArticleModel;
import com.yy.android.gamenews.ui.common.RefreshListWrapper;
import com.yy.android.gamenews.ui.common.RefreshListWrapper.OnListViewEventListener;
import com.yy.android.gamenews.ui.common.SwitchImageLoader;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.util.IPageCache;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;

import de.greenrobot.event.EventBus;

/**
 * 专题页
 * 
 * @author carlosliu
 * 
 */
public class SpecialArticleActivity extends BaseActivity {

	private View mHeader;
	private ListView mListView;
	private RefreshListWrapper mListWrapper;
	private ActionBar mActionBar;
	private ImageView mImageView;
	private TextView mDescriptionView;
	private View mDesLayout;

	private SwitchImageLoader mImageLoader;
	private List<Long> mViewedList;

	private IPageCache mPageCache;
	private ArticleListAdapter mAdapter;
	private List<ArticleInfo> mDataSource = new ArrayList<ArticleInfo>();
	private GetSpecialArticleListRsp mRsp;

	private ArticleInfo mSpecialInfo;
	private boolean mIsLoading;
	public static final String KEY_SPECIAL_INFO = "special_info";
	public static final String KEY_SPECIAL_ID = "special_id";

	private String mDescTitle;
	private long mSpecialId;

	public static void startSpecialArticleActivity(Context context, long id) {
		Intent intent = new Intent(context, SpecialArticleActivity.class);
		intent.putExtra(SpecialArticleActivity.KEY_SPECIAL_ID, id);
		context.startActivity(intent);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_special_article);

		EventBus.getDefault().register(this);
		mDescTitle = getString(R.string.special_desc);

		Intent intent = getIntent();
		if (intent != null) {
			mSpecialInfo = (ArticleInfo) intent
					.getSerializableExtra(KEY_SPECIAL_INFO);

			mSpecialId = intent.getLongExtra(KEY_SPECIAL_ID, 0);

			if (mSpecialInfo != null) {
				mSpecialId = mSpecialInfo.getId();
			}
		}
		mHeader = getLayoutInflater().inflate(R.layout.special_article_header,
				null);
		mImageView = (ImageView) mHeader.findViewById(R.id.special_image_view);
		mDescriptionView = (TextView) mHeader.findViewById(R.id.special_desc);
		mDesLayout = mHeader.findViewById(R.id.special_desc_layout);

		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setOnLeftClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		mListView = (ListView) findViewById(R.id.special_article_list);
		mListView.setHeaderDividersEnabled(false);
		mListView.setFooterDividersEnabled(false);
		mListView.setOnItemClickListener(mOnItemClickListener);
		mListWrapper = new RefreshListWrapper(this, mListView, mHeader);
		mListWrapper.setOnListViewEventListener(mListViewListener);

		mAdapter = new ArticleListAdapter(this);
		mAdapter.setDataSource(mDataSource);
		mListView.setAdapter(mAdapter);
		mPageCache = new IPageCache();

		mImageLoader = SwitchImageLoader.getInstance();

		mViewedList = mPageCache.getObject(
				Constants.CACHE_KEY_VIEWED_ARTICLE_LIST, ArrayList.class);

		if (mViewedList == null) {
			mViewedList = new ArrayList<Long>();
		}
		mAdapter.setViewedArticleList(mViewedList);
		initData();

		setDataView(mListView);
		super.onCreate(savedInstanceState);
	}

	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Adapter adapter = (Adapter) parent.getAdapter();
			ArticleInfo model = (ArticleInfo) adapter.getItem(position);
			if (model != null) {
				Intent intent = new Intent(SpecialArticleActivity.this,
						ArticleDetailActivity.class);
				intent.putExtra(ArticleDetailActivity.KEY_ARTICLE_INFO, model);
				startActivity(intent);
				currentModel = model;
				if (mViewedList.size() >= Constants.CACHE_SIZE_VIEWED_ARTI_LIST) {
					mViewedList.remove(0);
				}
				mViewedList.add(model.getId());
				mPageCache.setObject(Constants.CACHE_KEY_VIEWED_ARTICLE_LIST,
						mViewedList, Constants.CACHE_DURATION_FOREVER);
				mAdapter.notifyDataSetChanged();
			}
		};
	};

	private boolean hasData() {
		return mDataSource != null && mDataSource.size() != 0;
	}

	private void checkShowEmptyView() {
		if (hasData()) {
			showView(VIEW_TYPE_DATA);
		} else {
			showView(VIEW_TYPE_EMPTY);
		}
	}

	private OnListViewEventListener mListViewListener = new OnListViewEventListener() {

		@Override
		public void onRefresh() {
			refreshData();
		}

		@Override
		public void onLoading() {
			loadData();
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if ((scrollState == OnScrollListener.SCROLL_STATE_FLING || scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)) {
				mAdapter.pause();
			} else {
				mAdapter.resume();
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount, int direction) {
		}
	};

	private void initData() {
		requestData(0); // 第一次传0
	}
	// 加载数据，将新数据加到list尾
	private void loadData() {
		requestData(RefrshType._REFRESH_TYPE_LOAD_MORE);
	}

	// 刷新数据，将新数据加到list头
	private void refreshData() {
		mListWrapper.showLoadingBar();
		requestData(RefrshType._REFRESH_TYPE_REFRESH);
	}

	public void callRefresh() {
		if (mListView != null) {
			mListView.setSelection(0);
		}
		refreshData();
		mListWrapper.onRefreshing();
	}

	private void requestData(final int refresh) {

		if (mIsLoading) {
			return;
		}

		if (!hasData()) {
			showView(VIEW_TYPE_LOADING);
		}

		mIsLoading = true;
		Map<Integer, String> attachInfo = null;
		if (mRsp != null) {
			attachInfo = mRsp.getAttachInfo();
		}
		ArticleModel.getSpecialArticleList(
				new ResponseListener<GetSpecialArticleListRsp>(
						SpecialArticleActivity.this) {
					@Override
					public void onResponse(GetSpecialArticleListRsp data) {
						requestFinish(refresh, data);
						mIsLoading = false;

						setEmptyText(getString(R.string.global_empty_no_data));
						setEmptyViewClickable(false);
						checkShowEmptyView();
					}

					@Override
					public void onError(Exception e) {
						requestFinish(refresh, null);
						mIsLoading = false;

						setEmptyText(getString(R.string.global_empty_reload));
						setEmptyViewClickable(true);
						checkShowEmptyView();

						if (e != null) {
							ToastUtil.showToast(R.string.http_error);
						}
						super.onError(e);
					}

				}, // Listener
				refresh, mSpecialId, attachInfo);
	}

	private void requestFinish(int refresh, GetSpecialArticleListRsp data) {
		if (data != null) {
			mRsp = data;
			List<PicInfo> coverList = data.getCover();
			if (coverList != null) {

				if (coverList.size() > 0) {
					PicInfo cover = coverList.get(0);
					String coverUrl = cover.getUrl();
					int imgWidth = cover.getWidth();
					int imgHeight = cover.getHeight();

					int viewWidth = Util.getAppWidth();
					float scale = (float)viewWidth / (float)imgWidth;
					int viewHeight = (int) (scale * imgHeight);
					mImageView.getLayoutParams().height = viewHeight;
					mImageView.invalidate();

					mImageLoader.displayImage(coverUrl, mImageView);
					mImageView.setVisibility(View.VISIBLE);
				} else {
					mImageView.setVisibility(View.GONE);
				}
			}
			String description = data.getDesc();

			if (description != null && !"".equals(description)) {
				SpannableStringBuilder desc = new SpannableStringBuilder();
				desc.append("　　　　");
				desc.append(description);

				// int start = 0;
				// int end = mDescTitle.length();
				// desc.setSpan(new ForegroundColorSpan(Color.WHITE), start,
				// end,
				// Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				// int bgColor = getResources().getColor(R.color.actionbar_bg);
				// desc.setSpan(new BackgroundColorSpan(bgColor), start, end,
				// Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				// desc.setSpan(new AbsoluteSizeSpan(14, true), start, end,
				// Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

				mDescriptionView.setText(desc);
				mDesLayout.setVisibility(View.VISIBLE);
			} else {
				mDesLayout.setVisibility(View.GONE);
			}
			// mActionBar.setTitle(data.getName());
			
			// 如果是刷新，则添加到顶端
			if (refresh == RefrshType._REFRESH_TYPE_REFRESH) {
				mListWrapper.onRefreshComplete();
				mDataSource.addAll(0, data.getArticleList());
			} else {
				mDataSource.addAll(data.getArticleList());
			}
			mAdapter.notifyDataSetChanged();
		}
		boolean hasMore = (data != null && data.getHasMore());

		if (hasMore) {

			mListWrapper.showLoadingBar();
		} else {
			if (refresh == RefrshType._REFRESH_TYPE_REFRESH) {
				mListWrapper.onRefreshComplete();
			}
			mListWrapper.hideLoadingBar();
		}
	}

	public void onEventMainThread(VolleyErrorEvent event) {
		String errorMsg = VolleyErrorHelper.getMessage(this, event.error);
		ToastUtil.showToast(errorMsg);
	}

	public void onEventMainThread(UniPacketErrorEvent event) {
		ToastUtil.showToast(event.msg);
	}

	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	};
	ArticleInfo currentModel;
	@Override
	public void onResume() {
		if (mCommentEvent != null && currentModel != null) {

			ListAdapter adapter = mListView.getAdapter();
			if (adapter != null) {
				currentModel.setCommentCount(mCommentEvent.getCommentCount());
				mAdapter.notifyDataSetChanged();
			}
			currentModel = null;
			mCommentEvent = null;
		}
		super.onResume();
	}

	private CommentEvent mCommentEvent;

	public void onEvent(CommentEvent event) {
		mCommentEvent = event;
	}
	
	@Override
	protected void onEmptyViewClicked() {
		
		refreshData();
		super.onEmptyViewClicked();
	}
}