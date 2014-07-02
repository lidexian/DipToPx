package com.yy.android.gamenews.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.R;
import com.yy.hiidostatis.api.HiidoSDK;

public class BaseActivity extends FragmentActivity {
	protected static final HiidoSDK.PageActionReportOption REPORT = HiidoSDK.PageActionReportOption.REPORT_ON_FUTURE_RESUME;

	@Override
	public void onResume() {
		super.onResume();
		// MobclickAgent.onPageStart(this.getClass().getSimpleName());
		MobclickAgent.onResume(this);
		HiidoSDK.instance().onResume(Constants.UID, this);
		// 腾讯云统计
		com.tencent.stat.StatService.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// MobclickAgent.onPageEnd(this.getClass().getSimpleName());
		MobclickAgent.onPause(this);
		HiidoSDK.instance().onPause(this, REPORT);
		// 腾讯云统计
		com.tencent.stat.StatService.onPause(this);
	}

	private View mEmptyLayout;
	private View mEmptyView;
	private TextView mEmptyTextView;
	// private View mLoadingView;
	private View mDataView;

	private ViewGroup mContainer;
	private LayoutInflater mInflater;

	private View mProgressBar;
	private View mProgressBarInner;
	private Animation mLoadingAnimation = null;
	private boolean mDestroyed;

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

	protected void setEmptyText(String text) {
		mEmptyTextView.setText(text);
	}

	protected void setEmptyViewClickable(boolean clickable) {
		mEmptyView.setClickable(clickable);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		ViewGroup view = (ViewGroup) findViewById(android.R.id.content);
		mInflater = getLayoutInflater();
		setContainer(view);

		super.onCreate(savedInstanceState);
	}

	protected void onCreate(Bundle savedInstanceState, boolean loading) {

		if (loading) {
			ViewGroup view = (ViewGroup) findViewById(android.R.id.content);
			mInflater = getLayoutInflater();
			setContainer(view);
		}

		super.onCreate(savedInstanceState);
	}

	protected void setDataView(View dataView) {
		mDataView = dataView;
	}

	// @Override
	// public void onViewCreated(View view, Bundle savedInstanceState) {
	// mDataView = getListView();
	//
	// setContainer((ViewGroup) mDataView.getParent());
	// super.onViewCreated(view, savedInstanceState);
	// }

	protected static final int VIEW_TYPE_EMPTY = 1;
	protected static final int VIEW_TYPE_DATA = 2;
	protected static final int VIEW_TYPE_LOADING = 3;

	protected void onEmptyViewClicked() {

	}

	private void showView(View view, boolean show) {
		int visibility = show ? View.VISIBLE : View.INVISIBLE;
		if (view != null) {
			view.setVisibility(visibility);
		}
	}

	protected void showView(int viewType) {
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

	protected void showLoading() {
		if (mLoadingAnimation == null) {
			mLoadingAnimation = AnimationUtils.loadAnimation(
					getApplicationContext(), R.anim.article_detail_loading);
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDestroyed = true;
	}

	public boolean isDestroyed() {
		return mDestroyed;
	}
}