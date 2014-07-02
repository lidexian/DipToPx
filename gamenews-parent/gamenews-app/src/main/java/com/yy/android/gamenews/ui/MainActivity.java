package com.yy.android.gamenews.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Toast;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.Channel;
import com.duowan.gamenews.UserInitRsp;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.event.FragmentCallbackEvent;
import com.yy.android.gamenews.model.InitModel;
import com.yy.android.gamenews.ui.common.SwitchImageLoader;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.ui.view.ImagePagerAdapter;
import com.yy.android.gamenews.ui.view.WelcomeView;
import com.yy.android.gamenews.ui.view.WelcomeView.OnCompletedListener;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.PushUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.gamenews.R;

import de.greenrobot.event.EventBus;

public class MainActivity extends BaseActivity {
	private static final String TAG = MainActivity.class.getSimpleName();
	public static final String TAG_NAME_NEWS = "news";
	public static final String TAG_NAME_BRUSH = "brush";
	public static final String TAG_NAME_INFO = "info";

	public static final String ACTION_BRUSH_CLICKED = "action_brush_clicked";
	private static final String KEY_CURRENT_TAB = "current_tab";

	private NewsFragment mNewsFragment;
	private ArticleListFragment mInfoFragment;
	private SwitchImageLoader mImageLoader;

	private View mNewsTab;
	private ImageView mBrushTab;
	private ImageView mIndicator;
	private View mInfoTab;

	private Animation mAnimCenterRotate;
	private Animation mAnimRadioIn;
	private Animation mAnimRadioOut;

	private View mRadioGroup;
	private ActionBar mActionBar;

	private Preference mPref;
	private boolean mIsFirstLaunch;

	private List<Channel> mActiveChannelList;

	private static final int DURATION_EXIT_APP = 2000; // 在该间隔内按两次返回键退出应用
	private static final int DURATION_RADIO_SHOW_MIN = 2000; // 要隐藏时，layout最少要显示的时间
	private static final int FILT_DURATION = 200; // 在该时间内被发送过来的消息会覆盖之前的消息
	private static final int MSG_SHOW_RADIO = 1001;
	private static final int MSG_HIDE_RADIO = 1002;
	private static final int MSG_FAKE_EXIT_APP = 1003;

	private Handler mHandler = new UIHandler(this);

	private static class UIHandler extends Handler {
		private WeakReference<MainActivity> mRef;

		public UIHandler(MainActivity activity) {
			mRef = new WeakReference<MainActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			MainActivity activity = mRef.get();
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

	OnDismissListener mDismissListener = new OnDismissListener() {

		@Override
		public void onDismiss() {
			int step = Preference.getInstance().getCurrentGuideStep();
			if (step >= Preference.STEP_DONE) {
				return;
			}
			if (step == Preference.STEP_BRUSH) {
				Util.showHelpTips(MainActivity.this, mBrushTab,
						mDismissListener);
			} else if (step == Preference.STEP_ME) {
				Util.showHelpTips(MainActivity.this, mNewsTab, mDismissListener);
			}
		}
	};

	private void checkAndShowTips() {
		int step = Preference.getInstance().getCurrentGuideStep();
		if (step == Preference.STEP_INFO) {
			Util.showHelpTips(MainActivity.this, mInfoTab, mDismissListener);
		} else if (step == Preference.STEP_BRUSH) {
			Util.showHelpTips(MainActivity.this, mBrushTab, mDismissListener);
		} else if (step == Preference.STEP_ME) {
			Util.showHelpTips(MainActivity.this, mNewsTab, mDismissListener);
		}
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
		// mInfoTab.getViewTreeObserver().removeOnGlobalLayoutListener(this);
		// } else {
		// mInfoTab.getViewTreeObserver().removeGlobalOnLayoutListener(this);
		// }
	}

	private String mCurrentTabName = TAG_NAME_INFO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");

		setContentView(R.layout.activity_main);

		handleIntent(getIntent());

		mPref = Preference.getInstance();
		mIsFirstLaunch = mPref.isFirstLaunch();
		if (mIsFirstLaunch) {
			initViewPager();
		}
		if (mPref.isPushMsgEnabled()) {
			PushUtil.start(getApplicationContext());
		}

		mImageLoader = SwitchImageLoader.getInstance();
		mRadioGroup = findViewById(R.id.main_radio);

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
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				mIndicator.setVisibility(View.INVISIBLE);
			}
		});
		// mAnimCenterBreath = AnimationUtils.loadAnimation(this,
		// R.anim.main_radio_center_breath);

		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setOnLeftClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Preference pref = Preference.getInstance();
				// pref.saveMyFavorChannelList(pref.getMyFavorChannelList());

				startActivity(new Intent(MainActivity.this,
						MyHomeActivity.class));
				overridePendingTransition(R.anim.myhome_open_enter,
						R.anim.myhome_open_exit);
			}
		});

		mActiveChannelList = mPref.getActiveChannelList();
		if (mActiveChannelList != null && mActiveChannelList.size() > 0) {
			Channel channel = mActiveChannelList.get(0);
			ImageLoader.getInstance().displayImage(channel.getIcon(),
					mActionBar.getRightImageView(), mActiveImgLoadingListener);
		}

		if (savedInstanceState != null) { // onSaveInstanceState里保存的当前选择的tab
			mCurrentTabName = savedInstanceState.getString(KEY_CURRENT_TAB);
			mNewsFragment = (NewsFragment) getSupportFragmentManager()
					.findFragmentByTag(TAG_NAME_NEWS);
			mInfoFragment = (ArticleListFragment) getSupportFragmentManager()
					.findFragmentByTag(TAG_NAME_BRUSH);
		}

		mIndicator = (ImageView) findViewById(R.id.main_radio_center_indicator);

		mNewsTab = findViewById(R.id.news_btn);
		mNewsTab.setOnClickListener(mOnClickListener);

		mBrushTab = (ImageView) findViewById(R.id.brush_btn);
		mBrushTab.setOnClickListener(mOnClickListener);

		mInfoTab = findViewById(R.id.info_btn);
		mInfoTab.setOnClickListener(mOnClickListener);

		setIndicatorRefreshing(false);
	}

	private boolean mHasActiveImage;
	private ImageLoadingListener mActiveImgLoadingListener = new ImageLoadingListener() {

		@Override
		public void onLoadingStarted(String imageUri, View view) {
		}

		@Override
		public void onLoadingFailed(String imageUri, View view,
				FailReason failReason) {
			mActionBar.setRightVisibility(View.INVISIBLE);
			mActionBar.setOnRightClickListener(null);
		}

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {

			int imageHeight = loadedImage.getHeight();
			int imageWidth = loadedImage.getWidth();
			int viewHeight = getResources().getDimensionPixelSize(
					R.dimen.actionbar_img_height);

			float scale = (float) viewHeight / (float) imageHeight;
			int viewWidth = (int) (scale * imageWidth);
			LayoutParams params = view.getLayoutParams();
			params.width = viewWidth;

			view.invalidate();

			mHasActiveImage = true;
			mActionBar.setRightVisibility(View.VISIBLE);
			mActionBar.setOnRightClickListener(mOnRightClickListener);
		}

		@Override
		public void onLoadingCancelled(String imageUri, View view) {
			mActionBar.setRightVisibility(View.INVISIBLE);
			mActionBar.setOnRightClickListener(null);
		}

	};

	private OnClickListener mOnRightClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (mActiveChannelList == null || mActiveChannelList.size() <= 0) {
				v.setVisibility(View.INVISIBLE);
				v.setOnClickListener(null);
				return;
			}

			Channel channel = mActiveChannelList.get(0);
			Intent intent = new Intent(MainActivity.this,
					ArticleListActivity.class);
			intent.putExtra(ArticleListActivity.KEY_CHANNEL, channel);
			startActivity(intent);
		}
	};

	public void handleIntent(Intent intent) {
		if (null == intent || intent.getBooleanExtra(ACTION_EXIT_APP, false)) {
			finish();
			return;
		}

		int type = intent.getIntExtra(Constants.PUSH_TYPE, -1);
		long id = intent.getLongExtra(Constants.PUSH_ID, -1);
		String url = intent.getStringExtra(Constants.PUSH_URL);

		if (type == 2) {
			// 网页
			if (url != null) {
				AppWebActivity.startWebActivityFromNotice(this, url);
			}
		} else if (type == 1) {
			// 专题
			if (id != -1) {
				SpecialArticleActivity.startSpecialArticleActivity(this, id);
			}
		} else if (type == 0) {
			// 文章
			if (id != -1) {
				ArticleDetailActivity.startArticleDetailActivity(this, id);
			}
		}
	}

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.news_btn: {
				changeTab(TAG_NAME_NEWS);
				break;
			}
			case R.id.brush_btn: {
				onBrushClick();
				break;
			}
			case R.id.info_btn: {
				changeTab(TAG_NAME_INFO);
				break;
			}
			}
		}
	};

	private void onBrushClick() {

		if (!mIsRadioVisible) {
			showMainRadio(0);
		} else {
			setIndicatorRefreshing(true);
			if (TAG_NAME_NEWS.equals(mCurrentTabName)) {
				mNewsFragment.refreshCurrent();
			} else {
				mInfoFragment.callRefresh();
			}
		}

	}
	
	private void checkExpireRefresh() {
		if (TAG_NAME_NEWS.equals(mCurrentTabName)) {
			mNewsFragment.checkExpireCurrent();
		} else {
			mInfoFragment.checkExpire();
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

	// private long mLastDisplayTime;

	private void showMainRadio(int delay) {
		Log.d(TAG, "[showMainRadio]");

		// boolean hasShowMsg = mHandler.hasMessages(MSG_SHOW_RADIO);
		// boolean hasHideMsg = mHandler.hasMessages(MSG_HIDE_RADIO);

		if (!mHandler.hasMessages(MSG_SHOW_RADIO)) {
			mHandler.removeMessages(MSG_HIDE_RADIO);
			mHandler.sendEmptyMessageDelayed(MSG_SHOW_RADIO, delay);
		}
	}

	private void hideMainRadio(int delay) {

		// long currentTime = System.currentTimeMillis();
		// int duration = (int) (currentTime - mLastDisplayTime);
		// if (duration < DURATION_RADIO_SHOW_MIN) {
		// delay = DURATION_RADIO_SHOW_MIN - duration;
		// }
		if (!mHandler.hasMessages(MSG_HIDE_RADIO)) {
			Log.d(TAG, "[hideMainRadio], delay = " + delay);
			mHandler.removeMessages(MSG_SHOW_RADIO);
			mHandler.sendEmptyMessageDelayed(MSG_HIDE_RADIO, delay);
		}
	}

	private void showMainRadioNow() {
		// mLastDisplayTime = System.currentTimeMillis();

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

	/**
	 * 根据tab名称来做切换动作，显示相应tab并高亮
	 * 
	 * @param tabName
	 */
	private void changeTab(final String tabName) {
		mCurrentTabName = tabName;
		if (TAG_NAME_NEWS.equals(tabName)) {
			mNewsTab.setSelected(true);
			mInfoTab.setSelected(false);
			mActionBar.setTitle(getString(R.string.main_brush));

			mActionBar.setRightVisibility(View.INVISIBLE);
			mActionBar.setOnRightClickListener(null);
		} else if (TAG_NAME_INFO.equals(tabName)) {
			mNewsTab.setSelected(false);
			mInfoTab.setSelected(true);
			mActionBar.setTitle(getString(R.string.main_info_banner));

			mActiveChannelList = mPref.getActiveChannelList();
			if (mActiveChannelList != null && mActiveChannelList.size() > 0
					&& mHasActiveImage) {
				mActionBar.setRightVisibility(View.VISIBLE);
				mActionBar.setOnRightClickListener(mOnRightClickListener);
			}

		}
		
		changeTabFragment(tabName);
	}

	/**
	 * 根据tab名称来显示对应的fragment，如果fragment为空，会创建并添加
	 * 
	 * @param tabName
	 *            tab的名称
	 */
	private void changeTabFragment(String tabName) {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		// 如果为空，则添加
		if (mNewsFragment == null) {
			mNewsFragment = new NewsFragment();
			transaction.add(R.id.container, mNewsFragment, TAG_NAME_NEWS);
		}
		if (mInfoFragment == null) {
			Channel channel = new Channel();
			channel.setId(Constants.RECOMMD_ID);
			mInfoFragment = ArticleListFragment.newInstance(channel);
			transaction.add(R.id.container, mInfoFragment, TAG_NAME_BRUSH);
		}
		if (TAG_NAME_NEWS.equals(tabName)) {
			transaction.show(mNewsFragment).hide(mInfoFragment);
		} else {
			transaction.show(mInfoFragment).hide(mNewsFragment);
		}

		transaction.commitAllowingStateLoss();
	}

	@Override
	protected void onStart() {
		EventBus.getDefault().register(this);
		super.onStart();
	}

	@Override
	protected void onStop() {
		EventBus.getDefault().unregister(this);
		super.onStop();
	}

	@Override
	public void onResume() {

		changeTab(mCurrentTabName); // 这里可以优化：不用每次onResume都change，放在onResume里面是为了不卡住第一界面的显示
		if (mPref.isUserLogin()) {
			UserInitRsp user = mPref.getInitRsp();
			if (user != null && user.getUser().getIcon() != null) {
				mActionBar.showLeftImgBorder(true);
				mImageLoader.displayImage(user.getUser().getIcon(),
						mActionBar.getLeftImageView(), true);
			} else {
				mActionBar.showLeftImgBorder(false);
				mActionBar.setLeftImageResource(R.drawable.ic_person_default);
			}
		} else {
			mActionBar.showLeftImgBorder(false);
			mActionBar.setLeftImageResource(R.drawable.ic_person_default);
		}

		if (mPref.getInitRsp() == null) {
			InitModel.sendUserInitReq(this, mUserInitRspListener, null, false);
		}
		
		if(!mFromOtherActivity) {
			checkExpireRefresh();
		}
		mFromOtherActivity = false;
		super.onResume();
	}
	
	/*
	 *  判断是否有启动过其它的activity, 此flag用于判断是否要检查列表数据过期
	 *  现在的逻辑是，只有当activity处于列表页时，如果列表内容上次加载距当前时间
	 *  超过一定时间，则在下次onResume时自动刷新
	 */
	private boolean mFromOtherActivity; //
	@Override
	public void startActivity(Intent intent) {
		mFromOtherActivity = true;
		super.startActivity(intent);
	}

	private ResponseListener<UserInitRsp> mUserInitRspListener = new ResponseListener<UserInitRsp>(
			this) {
		public void onResponse(UserInitRsp rsp) {
			mPref.saveDefaultInitRsp(rsp);
		};

		public void onError(Exception e) {
		};
	};

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(KEY_CURRENT_TAB, mCurrentTabName);
		super.onSaveInstanceState(outState);
	}

	public static final String ACTION_EXIT_APP = "exit_app";

	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
		super.onNewIntent(intent);
	}

	@Override
	public void onBackPressed() {
		if (mHandler.hasMessages(MSG_FAKE_EXIT_APP)) {
			super.onBackPressed();
		} else {
			Toast.makeText(this, R.string.main_exit_app, DURATION_EXIT_APP)
					.show();
			mHandler.sendEmptyMessageDelayed(MSG_FAKE_EXIT_APP,
					DURATION_EXIT_APP);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		// ToastUtil.showToast("onMenuOpened");
		return false; // 返回为true则显示系统menu
	}

	public static void exitApp(Activity from) {
		Intent intent = new Intent(from, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(ACTION_EXIT_APP, true);
		from.startActivityForResult(intent, -1);
	}

	// public void onEventMainThread(VolleyErrorEvent event) {
	// String errorMsg = VolleyErrorHelper.getMessage(this, event.error);
	// ToastUtil.showToast(errorMsg);
	// }
	//
	// public void onEventMainThread(UniPacketErrorEvent event) {
	// ToastUtil.showToast(event.msg);
	// }

	public void onEvent(FragmentCallbackEvent event) {
		if (event == null) {
			return;
		}

		Fragment eventFragment = event.mFragment;
		if (mInfoFragment != eventFragment) { // 我的最爱界面
			if (eventFragment instanceof ArticleListFragment) {
				ArticleListFragment articleFragment = (ArticleListFragment) eventFragment;
				Channel channel = articleFragment.getChannel();
				Channel currentChannel = mNewsFragment.getCurrentChannel();
				if (currentChannel == null || !currentChannel.equals(channel)) {
					return;
				}
			}
		}

		// Log.d(TAG, "[onEvent]" + ", eventId = " + event.getEventType());
		int eventType = event.mEventType;
		switch (eventType) {
		case FragmentCallbackEvent.FRGMT_LIST_SCROLL_DOWN: {
			// Log.d(TAG, "[onEvent]" + ", eventId = FRGMT_LIST_SCROLL_DOWN");
			showMainRadio(FILT_DURATION);
			break;
		}
		case FragmentCallbackEvent.FRGMT_LIST_SCROLL_UP: {
			// Log.d(TAG, "[onEvent]" + ", eventId = FRGMT_LIST_SCROLL_UP");
			hideMainRadio(FILT_DURATION);
			break;
		}
		case FragmentCallbackEvent.FRGMT_LIST_SCROLL_TO_HEAD: {
			// Log.d(TAG, "[onEvent]" +
			// ", eventId = FRGMT_LIST_SCROLL_TO_HEAD");
			showMainRadio(0);
			break;
		}
		case FragmentCallbackEvent.FRGMT_LIST_SCROLL_END: {
			// Log.d(TAG, "[onEvent]" + ", eventId = FRGMT_LIST_SCROLL_END");
			// hideMainRadio(DURATION_RADIO_SHOW_MIN);
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

	private WelcomeView mWelcomeView;
	private ViewPager mViewPager;
	private ImagePagerAdapter mAdapter;

	private void initViewPager() {
		mViewPager = (ViewPager) findViewById(R.id.welcome_pager);
		mAdapter = new ImagePagerAdapter(this);
		mViewPager.setVisibility(View.VISIBLE);

		List<View> resList = new ArrayList<View>();

		View layout1 = getLayoutInflater().inflate(
				R.layout.welcome_pager_layout, null);
		ImageView view1 = (ImageView) layout1
				.findViewById(R.id.welcome_pager_img);
		view1.setBackgroundResource(R.drawable.welcome_1);
		resList.add(layout1);

		View layout2 = getLayoutInflater().inflate(
				R.layout.welcome_pager_layout, null);
		ImageView view2 = (ImageView) layout2
				.findViewById(R.id.welcome_pager_img);
		view2.setBackgroundResource(R.drawable.welcome_2);
		resList.add(layout2);

		mWelcomeView = new WelcomeView(this);
		mWelcomeView.setOnCompletedListener(new OnCompletedListener() {
			@Override
			public void onCompleted() {
				mPref.finishFirstLaunch();
				checkAndShowTips();
				mViewPager.setVisibility(View.GONE);
			}
		});
		resList.add(mWelcomeView);
		mAdapter.updateDatasource(resList);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(new SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				if (position == 2) {
					mViewPager.setOnTouchListener(new OnTouchListener() {
						private float mStartY;

						@Override
						public boolean onTouch(View v, MotionEvent event) {

							float y = event.getY();

							switch (event.getAction()) {
							case MotionEvent.ACTION_DOWN: {
								if (mStartY == 0) {
									mStartY = y;
								}

								break;
							}
							case MotionEvent.ACTION_UP: {
								mWelcomeView.onFinish();
								mStartY = 0;
								break;
							}
							case MotionEvent.ACTION_MOVE: {
								if (mStartY - y > 20) {
									mWelcomeView.onFinish();
								}
								break;
							}
							}
							return true;
						}
					});
					mWelcomeView.start();
				}
			}
		});
	}
}