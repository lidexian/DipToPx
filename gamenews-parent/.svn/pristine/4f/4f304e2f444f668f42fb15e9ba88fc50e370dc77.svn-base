package com.yy.android.gamenews.util;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.AppInitReq;
import com.duowan.gamenews.AppInitRsp;
import com.duowan.gamenews.Channel;
import com.duowan.gamenews.ServiceType;
import com.duowan.gamenews.UserInitRsp;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.model.ChannelModel;
import com.yy.android.gamenews.model.InitModel;
import com.yy.android.gamenews.util.UpdateManager.OnUpdateInfoListener;
import com.yy.android.gamenews.util.thread.BackgroundTask;

public class AppInitTask extends BackgroundTask<Void, Void, Boolean> {
	private IPageCache mPageCache;
	private Preference mPref;

	private boolean mAppInitRunning;
	private boolean mUserInitRunning;
	private boolean mArticleListRunning;
	private boolean mLaunchRunning;
	private boolean mIsAsyncTaskRunning;
	private boolean mIsAppExit;

	private static final int TASK_APP_INIT = 1001;
	private static final int TASK_USER_INIT = 1002;
	private static final int TASK_GET_ARTICLE_LIST = 1003;
	private static final int TASK_LAUNCH = 1004;

	private static final int STAY_MIN_TIME = 3000; // 最少停留3秒

	private FragmentActivity mActivity;

	private static final String LOG_TAG = "AppInitTask";

	public AppInitTask(FragmentActivity activity) {
		mPref = Preference.getInstance();
		mActivity = activity;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		long time = System.currentTimeMillis();
		mIsAsyncTaskRunning = true;
		Log.d(LOG_TAG, "[doInBackground] executed");

		/**
		 * 判断是否是第一次启动app 如果是第一次启动，加载： 1. 欢迎页面 2. 推荐频道 3. 首页内容
		 * 
		 * 如果不是第一次启动，那么每次启动时检查： 首页内容是否有更新，如果有更新，下载并更新缓存
		 */

		sendUserInit();

		// 检查是否是第一次启动app
		if (!mPref.isFirstLaunch()) {
			sendAppInit();
			ArrayList<Channel> channels = (ArrayList<Channel>) mPref
					.getMyFavorChannelList();
			ChannelModel.updateMyFavChannelList(mActivity, channels);
		}
		checkUpdate();

		long delay = System.currentTimeMillis() - time; // 计算以上过程使用了多长时间

		// 计算还需停留多久
		if (delay < STAY_MIN_TIME) {
			delay = STAY_MIN_TIME - delay;
		}
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean startNow) {
		Log.d(LOG_TAG, "[onPostExecute] executed");
		mIsAsyncTaskRunning = false;
		onTaskFinish();
		super.onPostExecute(startNow);
	}

	private synchronized boolean isTaskEnd() {

		Log.d(LOG_TAG, "mAppInitRunning = " + mAppInitRunning
				+ "\n mUserInitRunning = " + mUserInitRunning
				+ "\n mLaunchRunning = " + mLaunchRunning
				+ "\n mArticleListRunning = " + mArticleListRunning
				+ "\n mIsAsyncTaskRunning = " + mIsAsyncTaskRunning
				+ "\n mIsAppExit = " + mIsAppExit);
		return !(mAppInitRunning || mUserInitRunning || mLaunchRunning
				|| mArticleListRunning || mIsAsyncTaskRunning);
	}

	private synchronized void setTaskStatus(int taskId, boolean status) {

		Log.d(LOG_TAG, "[setTaskStatus]");
		switch (taskId) {
		case TASK_APP_INIT: {
			Log.d(LOG_TAG, "[setTaskStatus], taskId = TASK_APP_INIT, status = "
					+ status);
			mAppInitRunning = status;
			break;
		}
		case TASK_GET_ARTICLE_LIST: {
			Log.d(LOG_TAG, "[setTaskStatus], taskId = TASK_APP_INIT, status = "
					+ status);
			mArticleListRunning = status;
			break;
		}
		case TASK_LAUNCH: {
			Log.d(LOG_TAG, "[setTaskStatus], taskId = TASK_APP_INIT, status = "
					+ status);
			mLaunchRunning = status;
			break;
		}
		case TASK_USER_INIT: {
			Log.d(LOG_TAG, "[setTaskStatus], taskId = TASK_APP_INIT, status = "
					+ status);
			mUserInitRunning = status;
			break;
		}
		}
	}

	private void sendUserInit() {

		if (mPref.getInitRsp() == null) {
			setTaskStatus(TASK_USER_INIT, true);
			InitModel.sendUserInitReq(mActivity, mUserInitRspListener, null,
					false);
		}
	}

	private void loadDefaultChannel() {
		ArrayList<String> channelList = null;
		if(Constants.isFunctionEnabled(Constants.APPTYPE_GAMENEWS)) {
			channelList = Util.getInitChannelList();
		}
		AppInitReq req = new AppInitReq();
		req.setServiceType(ServiceType._ST_GET_RECOMMEND_LIST
				| ServiceType._ST_GET_SUGGEST_LIST
				| ServiceType._ST_GET_ACTIVE_LIST);

		if(Constants.isFunctionEnabled(Constants.APPTYPE_GAMENEWS)) {
			req.setGameName(channelList);
		}

		setTaskStatus(TASK_APP_INIT, true);
		InitModel.sendAppInitReq(mActivity, req, mAppInitRspListener);
	}

	private void sendAppInit() {
		AppInitReq req = new AppInitReq();
		req.setServiceType(ServiceType._ST_GET_SUGGEST_LIST
				| ServiceType._ST_GET_ACTIVE_LIST);

		setTaskStatus(TASK_APP_INIT, true);
		InitModel.sendAppInitReq(mActivity, req, mAppInitRspListener);
	}

	private void checkUpdate() {
		setTaskStatus(TASK_LAUNCH, true);
		UpdateManager manager = new UpdateManager(mActivity);

		manager.setOnUpdateInfoListener(new OnUpdateInfoListener() {

			@Override
			public void onClick(int button, boolean isForceUpdate) {
				setTaskStatus(TASK_LAUNCH, false);
				if (isForceUpdate) {
					mActivity.finish();
				} else {
					onTaskFinish();
				}
			}

			@Override
			public void onCheckFinish(boolean needUpdate, boolean isForceUpdate) {
				if (!needUpdate) {
					setTaskStatus(TASK_LAUNCH, false);
					onTaskFinish();
				}
			}
		});
		manager.doLaunch();
	}

	// private void loadMyFavorList() {
	//
	// Map<Integer, String> attachInfo = null;
	//
	// setTaskStatus(TASK_GET_ARTICLE_LIST, true);
	// ArticleModel.getArticleList(
	// mArticleListRspListener, // Listener
	// RefrshType._REFRESH_TYPE_REFRESH, Constants.RECOMMD_ID,
	// attachInfo, false);
	// }

	private ResponseListener<UserInitRsp> mUserInitRspListener = new ResponseListener<UserInitRsp>(
			mActivity) {
		public void onResponse(UserInitRsp rsp) {
			setTaskStatus(TASK_USER_INIT, false);

			if (mPref.isFirstLaunch()) {
				loadDefaultChannel();
			}
			mPref.saveDefaultInitRsp(rsp);
			onTaskFinish();
		};

		public void onError(Exception e) {
			setTaskStatus(TASK_USER_INIT, false);
			if (mPref.isFirstLaunch()) {
				loadDefaultChannel();
			}
			onTaskFinish();
		};
	};

	private ResponseListener<AppInitRsp> mAppInitRspListener = new ResponseListener<AppInitRsp>(
			mActivity) {

		@Override
		public void onResponse(AppInitRsp rsp) {
			if (rsp != null) {
				List<Channel> channelList = rsp.getRecommendList();
				if (channelList != null && channelList.size() != 0) {

					mPref.saveMyFavorChannelList(rsp.getRecommendList());
					ChannelModel.updateMyFavChannelList(mActivity,
							rsp.getRecommendList());
				}

				List<Channel> activeChannelList = rsp.getActiveList();
				if (channelList != null) {
					mPref.saveActiveChannelList(activeChannelList);
				}
			}

			setTaskStatus(TASK_APP_INIT, false);
			onTaskFinish();
		}

		@Override
		public void onError(Exception e) {

			setTaskStatus(TASK_APP_INIT, false);
			onTaskFinish();
			super.onError(e);
		}
	};

	private void onTaskFinish() {
		Log.d(LOG_TAG, "[startMainApp] executed");
		if (isTaskEnd() && !mIsAppExit) {
			if (mOnAppInitTaskListener != null) {
				mOnAppInitTaskListener.onTaskFinished();
			}
		}
	}

	public void endTask() {
		mIsAppExit = true;
	}

	private OnAppInitTaskListener mOnAppInitTaskListener;

	public void setOnAppInitTaskListener(OnAppInitTaskListener listener) {
		mOnAppInitTaskListener = listener;
	}

	public interface OnAppInitTaskListener {
		public void onTaskFinished();
	}
}