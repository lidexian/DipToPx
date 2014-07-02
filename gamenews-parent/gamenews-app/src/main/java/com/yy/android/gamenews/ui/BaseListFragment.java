package com.yy.android.gamenews.ui;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.duowan.gamenews.RefrshType;
import com.yy.android.gamenews.GameNewsApplication;
import com.yy.android.gamenews.R;
import com.yy.android.gamenews.event.FragmentCallbackEvent;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.ui.common.RefreshListWrapper;
import com.yy.android.gamenews.ui.common.RefreshListWrapper.OnListViewEventListener;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.gamenews.util.Util;

import de.greenrobot.event.EventBus;

public abstract class BaseListFragment<E> extends ListFragment implements
		OnListViewEventListener {
	private View mEmptyLayout;
	private View mEmptyView;
	private TextView mEmptyTextView;
	private ListView mDataView;

	private View mUpdateCountLayout;
	private TextView mUpdatedCountTv;

	private ViewGroup mContainer;
	private LayoutInflater mInflater;

	private View mProgressBar;
	private View mProgressBarInner;
	private Animation mLoadingAnimation = null;
	private ImageAdapter<E> mAdapter;

	private RefreshListWrapper mListWrapper;
	private ArrayList<E> mDataSource = new ArrayList<E>();

	private Context mContext;
	private static final String KEY_CURRENT_VIEW = "current_view";
	private static final String KEY_HIDE_LOADING_BAR = "is_hide_loading_bar";
	private static final String KEY_DATA_SOURCE = "list_datasource";
	private int mCurrentView = VIEW_TYPE_EMPTY;

	public ArrayList<E> getDataSource() {
		return mDataSource;
	}

	private int mType;

	public BaseListFragment(int type) {
		mType = type;
		mContext = GameNewsApplication.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup parentView = (ViewGroup) super.onCreateView(inflater,
				container, savedInstanceState);
		mInflater = inflater;

		mUpdateCountLayout = mInflater.inflate(
				R.layout.article_list_updated_count, null);
		// mUpdateCountLayout.setVisibility(View.GONE);
		mUpdatedCountTv = (TextView) mUpdateCountLayout
				.findViewById(R.id.update_count_tv);
		parentView.addView(mUpdateCountLayout, new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));

		return parentView;
	}

	protected void setContainer(ViewGroup container) {
		if (container == null) {
			return;
		}

		if (mEmptyLayout == null) {
			mEmptyLayout = mInflater.inflate(R.layout.global_reload, null,
					false);

			showView(mEmptyLayout, false);
			mEmptyTextView = (TextView) mEmptyLayout
					.findViewById(R.id.reload_empty_text);

			mEmptyView = mEmptyLayout.findViewById(R.id.reload_layout);
			mEmptyView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onEmptyViewClicked();
				}
			});
			mProgressBar = mEmptyLayout.findViewById(R.id.reload_progressbar);
			mProgressBarInner = mEmptyLayout
					.findViewById(R.id.reload_progressbar_inner);
		} else {
			if (mContainer != null) {
				mContainer.removeView(mEmptyLayout);
			}
		}

		mContainer = container;
		mContainer.addView(mEmptyLayout);
	}

	protected String strEmptyReload;
	protected String strEmptyNoData;
	protected String strEmptyAddChannel;
	private String strUpdatedCount;
	private String strUpdatedCountZero;

	protected void setEmptyText(String text) {
		mEmptyTextView.setText(text);
	}

	protected void setEmptyViewClickable(boolean clickable) {
		mEmptyView.setClickable(clickable);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		mDataView = getListView();
		strEmptyAddChannel = getString(R.string.main_add_fav);
		strEmptyReload = getString(R.string.global_empty_reload);
		strEmptyNoData = getString(R.string.global_empty_no_data);
		strUpdatedCount = getString(R.string.global_update_count);
		strUpdatedCountZero = getString(R.string.global_update_count_zero);
		setContainer((ViewGroup) mDataView.getParent());

		boolean hasLoadingBar = true;
		if (savedInstanceState != null) {

			hasLoadingBar = savedInstanceState.getBoolean(KEY_HIDE_LOADING_BAR,
					true);
			mDataSource = (ArrayList<E>) savedInstanceState
					.getSerializable(KEY_DATA_SOURCE);
			if (mDataSource == null) {
				mDataSource = new ArrayList<E>();
			}

			mCurrentView = savedInstanceState.getInt(KEY_CURRENT_VIEW,
					VIEW_TYPE_EMPTY);

			if (hasData()) {
				mCurrentView = VIEW_TYPE_DATA;
			}
		}

		if (mDataView != null) {
			mDataView.setHeaderDividersEnabled(false);
			mDataView.setFooterDividersEnabled(false);
			mListWrapper = new RefreshListWrapper(mContext, mDataView);

			mListWrapper.setOnListViewEventListener(this);
			if (hasLoadingBar) {
				mListWrapper.showLoadingBar();
			} else {
				mListWrapper.hideLoadingBar();
			}
			mAdapter = getAdapter();
			mAdapter.setDataSource(mDataSource);
			showView(mCurrentView);
			setListAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();
		}

		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(KEY_HIDE_LOADING_BAR, mListWrapper.hasLoadingBar());
		outState.putInt(KEY_CURRENT_VIEW, mCurrentView);
		outState.putSerializable(KEY_DATA_SOURCE, mDataSource);
		super.onSaveInstanceState(outState);
	}

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
		if ((scrollState == OnScrollListener.SCROLL_STATE_IDLE)) {
			if (view.getFirstVisiblePosition() != 0) {
				notifyListener(FragmentCallbackEvent.FRGMT_LIST_SCROLL_END,
						null);
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount, int direction) {
		int event = 0;
		Object params = null;
		if (firstVisibleItem == 0) {
			// Log.d(TAG, "firstvisibleitem = " + firstVisibleItem);
			event = FragmentCallbackEvent.FRGMT_LIST_SCROLL_TO_HEAD;
		} else {
			switch (direction) {
			case RefreshListWrapper.DIRECTION_DOWN: {
				event = FragmentCallbackEvent.FRGMT_LIST_SCROLL_DOWN;
				break;
			}
			case RefreshListWrapper.DIRECTION_UP: {

				event = FragmentCallbackEvent.FRGMT_LIST_SCROLL_UP;
				break;
			}
			}
		}
		notifyListener(event, params);
	}

	protected static final int VIEW_TYPE_EMPTY = 1;
	protected static final int VIEW_TYPE_DATA = 2;
	protected static final int VIEW_TYPE_LOADING = 3;

	protected void onEmptyViewClicked() {
		refreshData();
	}

	private void showView(View view, boolean show) {
		int visibility = show ? View.VISIBLE : View.INVISIBLE;
		if (view != null) {
			view.setVisibility(visibility);
		}
	}

	protected void showView(int viewType) {
		mCurrentView = viewType;
		switch (viewType) {
		case VIEW_TYPE_DATA: {
			showView(mDataView, true);
			showView(mEmptyView, false);
			hidenLoading();
			showView(mEmptyLayout, false);

			break;
		}
		case VIEW_TYPE_EMPTY: {
			showView(mDataView, false);
			showView(mEmptyView, true);
			hidenLoading();
			showView(mEmptyLayout, true);
			break;
		}
		case VIEW_TYPE_LOADING: {
			showView(mDataView, false);
			showView(mEmptyView, false);
			showLoading();
			showView(mEmptyLayout, true);
			break;
		}
		}
	}

	private void showLoading() {
		if (mLoadingAnimation == null) {
			mLoadingAnimation = AnimationUtils.loadAnimation(mContext,
					R.anim.article_detail_loading);
			mLoadingAnimation.setInterpolator(new LinearInterpolator());
			mLoadingAnimation.setFillAfter(true);// 动画停止时保持在该动画结束时的状态
		}
		showView(mProgressBar, true);
		if (mProgressBarInner != null) {
			mProgressBarInner.startAnimation(mLoadingAnimation);
		}
	}

	protected void hidenLoading() {
		showView(mProgressBar, false);
		if (mProgressBarInner != null) {
			mProgressBarInner.clearAnimation();
		}
	}

	private boolean hasData() {
		return mDataSource != null && mDataSource.size() != 0;
	}

	// 加载数据，将新数据加到list尾
	protected void loadData() {
		if (!hasData()) {
			showView(VIEW_TYPE_LOADING);
			notifyListener(FragmentCallbackEvent.FRGMT_LIST_REFRESHING, null);
		}

		requestData(RefrshType._REFRESH_TYPE_LOAD_MORE);
	}

	public void callRefresh() {
		if (mDataView == null) {
			return;
		}
		mDataView.setSelection(0);
		notifyListener(FragmentCallbackEvent.FRGMT_LIST_SCROLL_TO_HEAD, null);
		mListWrapper.onRefreshing();
		refreshData();
	}

	// 刷新
	protected void refreshData() {
		if (!hasData()) {
			showView(VIEW_TYPE_LOADING);
		}
		notifyListener(FragmentCallbackEvent.FRGMT_LIST_REFRESHING, null);
		requestData(RefrshType._REFRESH_TYPE_REFRESH);
		StatsUtil.statsReport(mContext, "stats_refresh");
		StatsUtil.statsReportByMta(mContext, "stats_refresh","刷新");
		StatsUtil.statsReportByHiido("stats_refresh", "");
	}

	protected abstract void requestData(int refreType);

	protected abstract ImageAdapter<E> getAdapter();

	private int mLastEvent; // 防止多余的刷新

	private void notifyListener(int eventType, Object params) {

		if (mLastEvent != eventType) {
			mLastEvent = eventType;
			FragmentCallbackEvent event = new FragmentCallbackEvent();
			event.mEventType = eventType;
			event.mParams = params;
			event.mFragment = this;
			EventBus.getDefault().post(event);
		}
	}

	private boolean mNeedShowUpdatedCount = true;

	protected void setNeedShowUpdatedCount(boolean needShow) {
		mNeedShowUpdatedCount = needShow;
	}

	protected void requestFinish(int refresh, ArrayList<E> data,
			boolean hasMore, boolean replace) {

		BaseAdapter adapter = mAdapter;
		if (adapter == null) {
			return;
		}
		if (data != null) {
			// 如果是刷新，则添加到顶端
			if (refresh == RefrshType._REFRESH_TYPE_REFRESH) {

				mListWrapper.onRefreshComplete();
				if (mNeedShowUpdatedCount) {
					showUpdatedToast(data.size());
				}
				if (replace) {
					mDataSource.clear();
				}
				mDataSource.addAll(0, data);
			} else {

				mDataSource.addAll(data);
			}
			adapter.notifyDataSetChanged();
		}
		if (!hasMore) {
			if (refresh == RefrshType._REFRESH_TYPE_REFRESH) {
				mListWrapper.onRefreshComplete();
			}
			mListWrapper.hideLoadingBar();
		} else {
			mListWrapper.showLoadingBar();
		}

		checkShowEmptyView();
		notifyListener(FragmentCallbackEvent.FRGMT_LIST_REFRESH_DONE, null);
	}

	private Animation mAlphaAnimOut;
	private Animation mAlphaAnimIn;

	private void showUpdatedToast(int count) {

		if (count == 0) {
			return;
		}
		String toastString = "+" + count;

		// if(count > 0) {
		// toastString = String.format(strUpdatedCount, count);
		// } else {
		// toastString = strUpdatedCountZero;
		// }

		mUpdatedCountTv.setText(toastString);
		if (mAlphaAnimIn == null) {
			mAlphaAnimIn = getBubbleAnimation();
			mAlphaAnimIn.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					mUpdatedCountTv.setVisibility(View.VISIBLE);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					mUpdatedCountTv.setVisibility(View.INVISIBLE);
				}
			});
		}

		mUpdatedCountTv.startAnimation(mAlphaAnimIn);
//		mAlphaAnimIn.startNow();

		// mUpdatedCountTv.removeCallbacks(mAlphaRunnable);
		// mUpdatedCountTv.postDelayed(mAlphaRunnable, 2000);

	}

	private Animation getBubbleAnimation() {
		AnimationSet animSet = new AnimationSet(false);

		int screenWidth = Util.getAppWidth();
		int screenHeight = Util.getAppHeight();
		int viewHeight = mUpdatedCountTv.getHeight();

		int duration = 2000;

		Animation anim = new TranslateAnimation(0, 0, 0,
				-(screenHeight / 3 - viewHeight / 2));
		anim.setFillAfter(true);
		anim.setInterpolator(new LinearInterpolator());
		anim.setDuration(duration);
		anim.setRepeatCount(0);

		animSet.addAnimation(anim);

		anim = new TranslateAnimation(0, (float)(screenWidth / 5), 0, 0);
		anim.setFillAfter(true);
		anim.setInterpolator(new DecelerateInterpolator());
		anim.setDuration(duration / 2);
		anim.setRepeatCount(0);

		animSet.addAnimation(anim);

		anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		anim.setDuration(duration / 4);
		anim.setRepeatCount(0);
		anim.setFillAfter(true);
		animSet.addAnimation(anim);

		int alphaAnimDuration = duration / 5;
		anim = new AlphaAnimation(0.0f, 1f);
		anim.setDuration(alphaAnimDuration);
		anim.setInterpolator(new LinearInterpolator());
		anim.setFillAfter(true);
		
		anim = new AlphaAnimation(1f, 0.0f);
		anim.setDuration(alphaAnimDuration);
		anim.setInterpolator(new LinearInterpolator());
		anim.setStartOffset(duration - alphaAnimDuration);
		anim.setFillAfter(true);

		animSet.addAnimation(anim);

		return animSet;
	}

	private Runnable mAlphaRunnable = new Runnable() {

		@Override
		public void run() {
			if (mAlphaAnimOut == null) {
				mAlphaAnimOut = AnimationUtils.loadAnimation(mContext,
						R.anim.article_list_updated_fadeout);
			}
			mUpdatedCountTv.startAnimation(mAlphaAnimOut);
		}
	};

	private void checkShowEmptyView() {
		if (hasData()) {
			showView(VIEW_TYPE_DATA);
		} else {
			showView(VIEW_TYPE_EMPTY);
		}
	}
}