package com.yy.android.gamenews.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.TimeoutError;
import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.ArticleInfo;
import com.duowan.gamenews.LoginActionFlag;
import com.duowan.gamenews.MeRsp;
import com.duowan.gamenews.PlatType;
import com.duowan.gamenews.User;
import com.duowan.gamenews.UserInitReq;
import com.duowan.gamenews.UserInitRsp;
import com.tencent.android.tpush.XGPushManager;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.model.ArticleModel;
import com.yy.android.gamenews.model.ChannelModel;
import com.yy.android.gamenews.model.InitModel;
import com.yy.android.gamenews.ui.common.SwitchImageLoader;
import com.yy.android.gamenews.ui.common.UiUtils;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.ui.view.AppDialog;
import com.yy.android.gamenews.util.DataCleanManager;
import com.yy.android.gamenews.util.IPageCache;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.PushUtil;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.UpdateManager;
import com.yy.android.gamenews.util.UpdateManager.OnUpdateInfoListener;
import com.yy.android.gamenews.util.Util;
import com.yy.android.gamenews.util.thread.BackgroundTask;
import com.yy.android.gamenews.R;

public class MyHomeActivity extends BaseActivity implements OnClickListener {
	private View mLoginLayout;
	private View mUserInfoLayout;
	private View mLogoutView;
	private CheckBox mOnlyWifiCb;
	private CheckBox mPushMsgCb;
	private TextView mUserName;
	private TextView mFavorCountTv;
	private TextView mCacheSize;
	private TextView mCleanCache;
	private TextView mVersionTv;
	private TextView mFeedBack;
	private ImageView mUserPicView;
	private ActionBar mActionBar;

	private IPageCache mPageCache;
	private Preference mPref;
	private SHARE_MEDIA mLoginType;

	private int mFavCount;

	UMSocialService mController;

	private ArrayList<ArticleInfo> mMyFavorList;
	private SwitchImageLoader mImageLoader;

	private Dialog mCleanCacheDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_myhome);
		mController = UMServiceFactory.getUMSocialService("com.umeng.login",
				RequestType.SOCIAL);
		mController.getConfig().setSsoHandler(
				new QZoneSsoHandler(this, Constants.QQ_APP_ID,
						Constants.QQ_APP_KEY));// 为了避免每次都从服务器获取APP ID、APP
												// KEY，请设置APP ID跟APP KEY
		mController.getConfig().supportQQPlatform(this, Constants.QQ_APP_ID,
				Constants.QQ_APP_KEY, "http://www.umeng.com/social");
		mController.getConfig().setSsoHandler(new SinaSsoHandler());

		mImageLoader = SwitchImageLoader.getInstance();
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setOnLeftClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		mUserPicView = (ImageView) findViewById(R.id.home_user_img);
		mLoginLayout = findViewById(R.id.home_login_layout);
		mUserInfoLayout = findViewById(R.id.home_user_img_layout);
		mLogoutView = findViewById(R.id.logout_btn);
		mLogoutView.setOnClickListener(this);

		mCacheSize = (TextView) findViewById(R.id.tv_cache_size);
		mCleanCache = (TextView) findViewById(R.id.tv_clean_cache);

		mOnlyWifiCb = (CheckBox) findViewById(R.id.cb_use_data);
		mPushMsgCb = (CheckBox) findViewById(R.id.cb_allow_push);
		mVersionTv = (TextView) findViewById(R.id.tv_version_number);
		mFavorCountTv = (TextView) findViewById(R.id.tv_favor_count);
		mUserName = (TextView) findViewById(R.id.tv_home_user_name);
		mFeedBack = (TextView) findViewById(R.id.feedback);
		mFeedBack.setOnClickListener(this);

		mPref = Preference.getInstance();
		mPageCache = new IPageCache();
		updateSettings();
		ArticleModel.getMeRsp(new ResponseListener<MeRsp>(MyHomeActivity.this) {
			@Override
			public void onResponse(MeRsp response) {
				mFavCount = response.favCount;
				mPref.saveMyFavCount(mFavCount);
				updateSettings();
			}
		});
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.myhome_close_enter,
				R.anim.myhome_close_exit);
	}

	private void updateSettings() {
		new UpdateSettingsTask().execute();
		// boolean isOnlyWifi = mPref.isOnlyWifi();
		// boolean isPushEnabled = mPref.isPushMsgEnabled();
		//
		// mOnlyWifiCb.setChecked(isOnlyWifi);
		// mPushMsgCb.setChecked(isPushEnabled);
		// mVersionTv.setText(Util.getVersionName());
		//
		// mFavCount = mPref.getMyFavCount();
		//
		// if (mFavCount < 0) {
		// mFavCount = 0;
		// }
		// mFavorCountTv.setText("" + mFavCount);
		// if (mFavCount == 0) {
		// mFavorCountTv.setEnabled(false);
		// } else {
		// mFavorCountTv.setEnabled(true);
		// }
		//
		// long cacheSize = DataCleanManager.getAppCacheSize(this);
		// mCacheSize.setText(DataCleanManager.FormetFileSize(cacheSize));
	}

	@Override
	public void onResume() {
		updateLoginStatus(mPref.isUserLogin());
		updateSettings();
		super.onResume();
	}

	private void updateLoginStatus(boolean isLogin) {
		if (isLogin) {
			UserInitRsp rsp = mPref.getInitRsp();
			if (rsp != null) {
				User user = rsp.getUser();
				if (user == null || user.getIcon() == null
						|| "".equals(user.getIcon())) {
					mUserPicView.setImageResource(R.drawable.btn_login_yy);
				} else {
					mImageLoader.displayImage(rsp.getUser().getIcon(),
							mUserPicView, true);
				}
				mUserName.setText(rsp.getUser().getName());
			}

			mLoginLayout.setVisibility(View.INVISIBLE);
			mUserInfoLayout.setVisibility(View.VISIBLE);
			mLogoutView.setVisibility(View.VISIBLE);
		} else {
			mLoginLayout.setVisibility(View.VISIBLE);
			mUserInfoLayout.setVisibility(View.INVISIBLE);
			mLogoutView.setVisibility(View.INVISIBLE);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case LoginYYActivity.REQUEST_LOGIN: {
			if (resultCode == RESULT_OK) {
				UserInitRsp rsp = (UserInitRsp) data
						.getSerializableExtra(LoginYYActivity.EXTRA_USER_INIT_RSP);

				onLoginSucc(rsp);
			}
			break;
		}
		}
		/** 使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_qq_btn: {
			// ToastUtil.showToast("暂未支持 即将开通");
			mLoginType = SHARE_MEDIA.QZONE;
			login();
			break;
		}
		case R.id.login_weibo_btn: {
			mLoginType = SHARE_MEDIA.SINA;
			login();
			break;
		}
		case R.id.login_yy_btn: {
			if (!Util.isNetworkConnected()) {
				ToastUtil.showToast(R.string.http_not_connected);
				return;
			}
			Intent intent = new Intent(this, LoginYYActivity.class);
			startActivityForResult(intent, LoginYYActivity.REQUEST_LOGIN);
			break;
		}
		case R.id.feedback: {
			Intent intent = new Intent(this, FeedbackActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.my_event_btn: {
			Intent intent = new Intent(this, AppWebActivity.class);
			UserInitRsp rsp = mPref.getInitRsp();
			String accessToken = "";
			if (rsp != null) {
				accessToken = rsp.getAccessToken();
			}
			intent.putExtra(AppWebActivity.KEY_URL, Constants.MY_EVENT_URL
					+ "&token=" + accessToken);
			intent.putExtra(AppWebActivity.KEY_FROM, AppWebActivity.FROM_HD);
			startActivity(intent);
			break;
		}
		case R.id.logout_btn: {

			UiUtils.showDialog(this, R.string.global_caption,
					R.string.my_msg_confim_logout, R.string.global_ok,
					R.string.global_cancel, new AppDialog.OnClickListener() {

						@Override
						public void onDialogClick(int nButtonId) {
							if (nButtonId == AppDialog.BUTTON_POSITIVE) {
								// 更新默认用户信息
								mIsLogin = false;
								getGameNewsUserInfo(null);

								StatsUtil.statsReport(MyHomeActivity.this,
										"stats_logout");
								StatsUtil.statsReportByMta(MyHomeActivity.this,
										"stats_logout", "更新默认用户信息");
								StatsUtil
										.statsReportByHiido("stats_logout", "");
							}
						}
					});
			break;
		}
		case R.id.clean_cache_layout: {

			UiUtils.showDialog(this, R.string.global_caption,
					R.string.my_msg_confim_clean, R.string.global_ok,
					R.string.global_cancel, new AppDialog.OnClickListener() {

						@Override
						public void onDialogClick(int nButtonId) {
							if (nButtonId == AppDialog.BUTTON_POSITIVE) {
								mCleanCacheDialog = UiUtils
										.cleanCacheDialogShow(
												MyHomeActivity.this,
												getResources()
														.getString(
																R.string.clean_cacheing));
								new CleanCacheTask().execute();
								// DataCleanManager
								// .cleanAppCache(MyHomeActivity.this);
								// updateSettings();
								// Toast.makeText(MyHomeActivity.this,
								// R.string.my_msg_clean_succ,
								// Toast.LENGTH_LONG).show();
							}
						}
					});
			break;
		}
		case R.id.cb_use_data:
			onOnlyWifiClicked(mOnlyWifiCb.isChecked());
			break;
		case R.id.my_only_wifi_layout: {
			onOnlyWifiClicked(!mOnlyWifiCb.isChecked());
			break;
		}
		case R.id.cb_allow_push:
			onAllowPushClicked(mPushMsgCb.isChecked());
			break;
		case R.id.my_allow_push_layout: {
			onAllowPushClicked(!mPushMsgCb.isChecked());
			break;
		}
		case R.id.tv_favor_count: {
			Intent intent = new Intent(this, MyFavorListActivity.class);
			// intent.putExtra(ArticleListActivity.KEY_ARTICLE_LIST,
			// mMyFavorList);
			startActivity(intent);
			break;
		}
		case R.id.check_update: {
			UpdateManager manager = new UpdateManager(this);

			manager.setOnUpdateInfoListener(new OnUpdateInfoListener() {

				@Override
				public void onClick(int button, boolean isForceUpdate) {
					if (isForceUpdate) {
						finish();
					}
				}

				@Override
				public void onCheckFinish(boolean needUpdate,
						boolean isForceUpdate) {
					// Do nothing
				}
			});
			manager.checkUpdate();
			break;
		}
		}
	}

	private void onOnlyWifiClicked(boolean status) {
		mPref.setOnlyWifi(status);
		mOnlyWifiCb.setChecked(status);
	}

	private void onAllowPushClicked(boolean status) {
		mPref.setPushMsgEnabled(status);
		mPushMsgCb.setChecked(status);
		if (!status) {
			PushUtil.stop(getApplicationContext());
		} else {
			PushUtil.start(getApplicationContext());
		}
	}

	private void login() {
		if (!Util.isNetworkConnected()) {
			ToastUtil.showToast(R.string.http_not_connected);
			return;
		}
		mIsLogin = true;
		mController.doOauthVerify(MyHomeActivity.this, mLoginType,
				mAuthListener);
	}

	private UMAuthListener mAuthListener = new UMAuthListener() {
		@Override
		public void onComplete(Bundle value, SHARE_MEDIA platform) {
			mController.getPlatformInfo(MyHomeActivity.this, mLoginType,
					mUserInfoListener);

			StatsUtil.statsReport(MyHomeActivity.this, "stats_login",
					"login_type", platform.name());
			StatsUtil.statsReportByMta(MyHomeActivity.this, "stats_login",
					"login_type", platform.name());
			StatsUtil.statsReportByHiido("stats_login", "login_type:"
					+ platform.name());
		}

		@Override
		public void onCancel(SHARE_MEDIA arg0) {

		}

		@Override
		public void onError(SocializeException arg0, SHARE_MEDIA arg1) {
			Toast.makeText(MyHomeActivity.this, R.string.my_msg_login_fail,
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onStart(SHARE_MEDIA arg0) {
		}
	};

	private UMDataListener mUserInfoListener = new UMDataListener() {
		@Override
		public void onComplete(int status, Map<String, Object> info) {

			if (status != 200 || info == null) {
				Toast.makeText(MyHomeActivity.this, R.string.my_msg_login_fail,
						Toast.LENGTH_SHORT).show();
				Log.d("TestData", "发生错误：" + status);
				return;
			}

			Object accessToken = info.get("access_token");
			Object name = info.get("screen_name");
			Object image = info.get("profile_image_url");
			Object openId = info.get("openid");
			String openIdStr = openId == null ? "" : openId.toString();
			String accessTokenStr = accessToken == null ? "" : accessToken
					.toString();
			String nameStr = name == null ? "" : name.toString();
			String imageStr = image == null ? "" : image.toString();

			UserInitReq req = new UserInitReq();
			req.setUserIcon(imageStr);
			req.setUserName(nameStr);
			Map<Integer, String> token = new HashMap<Integer, String>();
			token.put(0, accessTokenStr);
			token.put(1, openIdStr);

			req.setSocialAccessToken(token);
			if (SHARE_MEDIA.QZONE.equals(mLoginType)) {
				req.setPlatType(PlatType._PLAT_TYPE_QQ);
			} else if (SHARE_MEDIA.SINA.equals(mLoginType)) {
				req.setPlatType(PlatType._PLAT_TYPE_SINA);
			}

			getGameNewsUserInfo(req);
		}

		@Override
		public void onStart() {
			// TODO Auto-generated method stub

		}
	};

	private void getGameNewsUserInfo(UserInitReq req) {

		InitModel.sendUserInitReq(MyHomeActivity.this, mRspListener, req, true);
	}

	private void onLoginSucc(UserInitRsp rsp) {
		mPref.saveInitRsp(rsp);

		if (rsp != null) {
			int flag = rsp.flag;
			if ((flag & LoginActionFlag._LOGIN_ACTION_FLAG_YY_TDOU) != 0) {
				String msg = rsp.extraInfo.get(flag);
				if(msg != null && !"".equals(msg)) {
					UiUtils.showDialog(this, R.string.global_caption, msg,
							R.string.global_ok);
				}
			}
		}

		Toast.makeText(getApplicationContext(), R.string.my_msg_login_succ,
				Toast.LENGTH_SHORT).show();
		updateLoginStatus(true);

		StatsUtil.statsReport(MyHomeActivity.this, "stats_login", "login_type",
				"yy");
		StatsUtil.statsReportByMta(MyHomeActivity.this, "stats_login", "login_type",
				"yy");
		StatsUtil.statsReportByHiido("stats_login", "login_type:yy");
	}

	private boolean mIsLogin;
	private ResponseListener<UserInitRsp> mRspListener = new ResponseListener<UserInitRsp>(
			this) {
		public void onResponse(UserInitRsp rsp) {
			if (mIsLogin) { // 登录
				onLoginSucc(rsp);
			} else { // 退出登录
				if (mPref != null) {
					mPref.clearLoginInfo();
					mPref.saveDefaultInitRsp(rsp);
					updateLoginStatus(false);
				}
			}
		};

		public void onError(Exception e) {
			if (mIsLogin) {
				if (e instanceof TimeoutError) {

					Toast.makeText(getApplicationContext(),
							R.string.my_msg_login_timeout, Toast.LENGTH_LONG)
							.show();
				} else {
					Toast.makeText(getApplicationContext(),
							R.string.my_msg_login_fail, Toast.LENGTH_LONG)
							.show();
				}
			} else {
				Toast.makeText(getApplicationContext(),
						R.string.my_msg_logout_fail, Toast.LENGTH_LONG).show();
			}
		};
	};

	private class CleanCacheTask extends BackgroundTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			DataCleanManager.cleanAppCache(MyHomeActivity.this);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			updateSettings();
			UiUtils.dialogDismiss(mCleanCacheDialog);
			Toast.makeText(MyHomeActivity.this, R.string.my_msg_clean_succ,
					Toast.LENGTH_LONG).show();
			super.onPostExecute(result);
		}
	}

	private class UpdateSettingsTask extends BackgroundTask<Void, Void, Void> {
		private boolean isOnlyWifi;
		private boolean isPushEnabled;
		private String versionName;
		private long cacheSize;

		@Override
		protected Void doInBackground(Void... params) {
			isOnlyWifi = mPref.isOnlyWifi();
			isPushEnabled = mPref.isPushMsgEnabled();
			versionName = Util.getVersionName();
			mFavCount = mPref.getMyFavCount();
			if (mFavCount < 0) {
				mFavCount = 0;
			}
			cacheSize = DataCleanManager.getAppCacheSize(MyHomeActivity.this);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mOnlyWifiCb.setChecked(isOnlyWifi);
			mPushMsgCb.setChecked(isPushEnabled);
			mVersionTv.setText(versionName);
			mFavorCountTv.setText("" + mFavCount);
			if (mFavCount == 0) {
				mFavorCountTv.setEnabled(false);
			} else {
				mFavorCountTv.setEnabled(true);
			}
			mCacheSize.setText(DataCleanManager.FormetFileSize(cacheSize));

			super.onPostExecute(result);
		}
	}
	// private class GetFavCountTask extends BackgroundTask<Void, Void, Void> {
	// @Override
	// protected Void doInBackground(Void... params) {
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(Void result) {
	// updateSettings();
	// super.onPostExecute(result);
	// }
	// }
}