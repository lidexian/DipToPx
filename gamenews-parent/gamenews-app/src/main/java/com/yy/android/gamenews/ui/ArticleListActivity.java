package com.yy.android.gamenews.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.duowan.gamenews.ArticleInfo;
import com.duowan.gamenews.Channel;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.event.FragmentCallbackEvent;
import com.yy.android.gamenews.event.SubscribeEvent;
import com.yy.android.gamenews.model.ChannelModel;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.util.IPageCache;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.gamenews.R;

import de.greenrobot.event.EventBus;

public class ArticleListActivity extends BaseActivity {
	private static final String TAG = MainActivity.class.getSimpleName();

	private static final String TAG_NAME_FRAGMENT = "ArticleListFragment";

	public static final String ACTION_BRUSH_CLICKED = "action_brush_clicked";
	private ArticleListFragment mInfoFragment;
	private ActionBar mActionBar;
	private ArrayList<ArticleInfo> mDatasource;

	private ImageView mBrushTab;
	private ImageView mIndicator;
	private View mRadioGroup;
	private Animation mAnimCenterRotate;
	private Animation mAnimRadioIn;
	private Animation mAnimRadioOut;

	public static final String KEY_ARTICLE_LIST = "article_list";
	public static final String KEY_CHANNEL = "channel";
	public static final String KEY_VIEW_TYPE = "key_view_type";
	public static final String VIEW_TYPE_MY_FAVOR = "view_my_favor"; // 不用传入数据，自己读取
	public static final String VIEW_TYPE_LIST = "view_list"; // 传入KEY_ARTICLE_LIST
	public static final String VIEW_TYPE_CHANNEL = "view_channel"; // 传入KEY_CHANNEL

	private String mViewType;

	private IPageCache mPageCache;
	private Channel mChannel;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.v(TAG, "onCreate");

		setContentView(R.layout.activity_article_list);
		mRadioGroup = findViewById(R.id.article_list_radio);
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setOnLeftClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		mActionBar.setRightVisibility(View.GONE);
		mPageCache = new IPageCache();
		Intent intent = getIntent();
		if (intent != null) {
			mChannel = (Channel) intent.getSerializableExtra(KEY_CHANNEL);
			mViewType = intent.getStringExtra(KEY_VIEW_TYPE);
			if (mViewType == null || "".equals(mViewType)) {
				mViewType = VIEW_TYPE_CHANNEL;
			}

			if (VIEW_TYPE_LIST.equals(mViewType)) {
				mDatasource = (ArrayList<ArticleInfo>) intent
						.getSerializableExtra(KEY_ARTICLE_LIST);
			}
		}

		List<Channel> channels = Preference.getInstance()
				.getMyFavorChannelList();
		if (channels == null) {
			channels = new ArrayList<Channel>();
		}
		if (mChannel != null) {
			if (!Util.isSubscribedChannel(channels, mChannel)) {
				mActionBar.setRightVisibility(View.VISIBLE);
				mActionBar.setOnRightClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						List<Channel> channels = Preference.getInstance()
								.getMyFavorChannelList();
						if (channels == null) {
							channels = new ArrayList<Channel>();
						}
						if (channels.size() >= Constants.SUBSCRIBE_MOST_LIMIT) {
							ToastUtil
									.showToast(R.string.channel_manage_too_mandy2);
							return;
						}
						channels.add(mChannel);
						mActionBar.setRightVisibility(View.GONE);
						mActionBar.setOnRightClickListener(null);
						ToastUtil.showToast(R.string.channel_add_success);
						Preference.getInstance().saveMyFavorChannelList(
								channels);
						ChannelModel.updateMyFavChannelList(
								ArticleListActivity.this,
								(ArrayList<Channel>) channels);
						SubscribeEvent event = new SubscribeEvent();
						event.isSubscribeChanged = true;
						event.isSubscribeMultiple = false;
						EventBus.getDefault().post(event);
					}
				});
			}
		}

		if (mDatasource == null) {
			mDatasource = new ArrayList<ArticleInfo>();
		}

		if (VIEW_TYPE_CHANNEL.equals(mViewType) && mChannel != null) {
			mActionBar.setTitle(mChannel.getName());
		}
		if (savedInstanceState != null) { // onSaveInstanceState里保存的当前选择的tab
			mInfoFragment = (ArticleListFragment) getSupportFragmentManager()
					.findFragmentByTag(TAG_NAME_FRAGMENT);
		}

		mAnimRadioOut = AnimationUtils.loadAnimation(this,
				R.anim.main_radio_tans_out);
		mAnimRadioOut.setAnimationListener(mAnimListener);
		mAnimRadioIn = AnimationUtils.loadAnimation(this,
				R.anim.main_radio_tans_in);
		mAnimRadioIn.setAnimationListener(mAnimListener);
		mAnimCenterRotate = AnimationUtils.loadAnimation(this,
				R.anim.main_radio_center_rotation);
		mAnimCenterRotate.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mIndicator.setVisibility(View.INVISIBLE);
			}
		});
		mIndicator = (ImageView) findViewById(R.id.main_radio_center_indicator);

		mBrushTab = (ImageView) findViewById(R.id.brush_btn);
		mBrushTab.setOnClickListener(mOnClickListener);
		setIndicatorRefreshing(false);
	}

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.brush_btn:
				onBrushClick();
				break;
			default:
				break;
			}
		}
	};

	private void onBrushClick() {

		if (!mIsRadioVisible) {
			showMainRadio(0);
		} else {
			setIndicatorRefreshing(true);
			mInfoFragment.callRefresh();
		}
	}

	private boolean isAnimating;
	private boolean mIsRadioVisible = true; // 初始化时为显示状态
	private AnimationListener mAnimListener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			isAnimating = true;
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			if (animation == mAnimRadioIn) {
				mIsRadioVisible = true;
			} else {
				mIsRadioVisible = false;
			}
			isAnimating = false;
		}
	};

	@Override
	protected void onStart() {
		EventBus.getDefault().register(this);
		super.onStart();
	}

	@Override
	protected void onStop() {
		EventBus.getDefault().unregister(this);
		super.onPause();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onResume() {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();

		if (VIEW_TYPE_MY_FAVOR.equals(mViewType)) {

			ArrayList<ArticleInfo> myFavorList = mPageCache.getObject(
					Constants.CACHE_KEY_MYFAVOR_LIST, ArrayList.class);
			mDatasource.clear();
			if (myFavorList != null) {
				mDatasource.addAll(myFavorList);
			}
		}
		if (mInfoFragment == null) {
			if (VIEW_TYPE_MY_FAVOR.equals(mViewType)
					|| VIEW_TYPE_LIST.equals(mViewType)) {

				mInfoFragment = ArticleListFragment.newInstance(mDatasource);
			} else if (VIEW_TYPE_CHANNEL.equals(mViewType)) {

				mInfoFragment = ArticleListFragment.newInstance(mChannel);
			} else {
				Log.e(TAG, "[onResume]mViewType = " + mViewType);
				return;
			}
			transaction.add(R.id.container, mInfoFragment, TAG_NAME_FRAGMENT);
		} else {
			((BaseAdapter) mInfoFragment.getListAdapter())
					.notifyDataSetChanged();
			;
		}

		transaction.show(mInfoFragment);
		transaction.commitAllowingStateLoss();
		super.onResume();
	}

	private static final int FILT_DURATION = 200; // 在该时间内被发送过来的消息会覆盖之前的消息
	private static final int MSG_SHOW_RADIO = 1001;
	private static final int MSG_HIDE_RADIO = 1002;
	private Handler mHandler = new UIHandler(ArticleListActivity.this);

	private static class UIHandler extends Handler {
		private WeakReference<ArticleListActivity> mRef;

		public UIHandler(ArticleListActivity activity) {
			mRef = new WeakReference<ArticleListActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			ArticleListActivity activity = mRef.get();
			if (activity == null) {
				return;
			}
			switch (msg.what) {
			case MSG_SHOW_RADIO: {
				activity.showMainRadioNow();
				break;
			}
			case MSG_HIDE_RADIO: {
				activity.hideMainRadioNow();
				break;
			}
			}
		}
	}

	private void showMainRadioNow() {
		if (isAnimating) {
			showMainRadio(10);
			return;
		}
		if (!mIsRadioVisible) {
			mAnimRadioOut.cancel();
			mRadioGroup.startAnimation(mAnimRadioIn);
		}
	}

	private void hideMainRadioNow() {
		if (isAnimating) {
			hideMainRadio(10);
			return;
		}
		if (mIsRadioVisible) {
			mAnimRadioIn.cancel();
			mRadioGroup.startAnimation(mAnimRadioOut);
		}
	}

	private void showMainRadio(int delay) {
		if (!mHandler.hasMessages(MSG_SHOW_RADIO)) {
			mHandler.removeMessages(MSG_HIDE_RADIO);
			mHandler.sendEmptyMessageDelayed(MSG_SHOW_RADIO, delay);
		}
	}

	private void hideMainRadio(int delay) {
		if (!mHandler.hasMessages(MSG_HIDE_RADIO)) {
			mHandler.removeMessages(MSG_SHOW_RADIO);
			mHandler.sendEmptyMessageDelayed(MSG_HIDE_RADIO, delay);
		}
	}

	private void setIndicatorRefreshing(boolean isRefreshing) {

		if (mAnimCenterRotate == null) {
			return;
		}
		if (isRefreshing) {
			mIndicator.setVisibility(View.VISIBLE);
			mIndicator
					.setBackgroundResource(R.drawable.btn_main_radio_refreshing);
			mIndicator.startAnimation(mAnimCenterRotate);
		} else {
			mIndicator.setVisibility(View.INVISIBLE);
			mAnimCenterRotate.cancel();
			mAnimCenterRotate.reset();
			mIndicator.clearAnimation();
		}

	}

	public void onEvent(FragmentCallbackEvent event) {
		if (event == null) {
			return;
		}
		int eventType = event.mEventType;
		switch (eventType) {
		case FragmentCallbackEvent.FRGMT_LIST_SCROLL_DOWN: {
			showMainRadio(FILT_DURATION);
			break;
		}
		case FragmentCallbackEvent.FRGMT_LIST_SCROLL_UP: {
			hideMainRadio(FILT_DURATION);
			break;
		}
		case FragmentCallbackEvent.FRGMT_LIST_SCROLL_TO_HEAD: {
			showMainRadio(0);
			break;
		}
		case FragmentCallbackEvent.FRGMT_LIST_SCROLL_END: {
			break;
		}
		case FragmentCallbackEvent.FRGMT_LIST_REFRESHING: {
			setIndicatorRefreshing(true);
			break;
		}
		case FragmentCallbackEvent.FRGMT_LIST_REFRESH_DONE: {
			setIndicatorRefreshing(false);
			break;
		}
		case FragmentCallbackEvent.FRGMT_TAB_CHANGED: {
			showMainRadio(0);
			break;
		}
		}
	}
}