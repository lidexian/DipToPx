package com.yy.android.gamenews.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.duowan.gamenews.Channel;
import com.duowan.gamenews.UserInitRsp;

public class Preference {
	// Preference
	public static final String PREF_NAME = "gamenews_pref";

	private SharedPreferences mPref;

	/**
	 * mCacheMap 把preference的数据保存到内存中
	 */
	private Map<String, Object> mCacheMap = new HashMap<String, Object>();
	private static final String LOG_TAG = Preference.class.getSimpleName();
	private static Preference mInstance = new Preference();
	private boolean isInited;

	public void init(Context context) {
		if (mPref == null) {
			mPref = context.getSharedPreferences(PREF_NAME,
					Context.MODE_PRIVATE);
		}
		isInited = true;
	}

	public boolean isInited() {
		return isInited;
	}

	public static Preference getInstance() {
		return mInstance;
	}

	public SharedPreferences getPreference() {

		return mPref;
	}

	// 是否是首次启动app
	private static final String KEY_IS_FIRST_LAUNCH = "is_first_launch";
	// 频道
	private static final String KEY_CHANNEL_LIST = "channel_list";
	private static final String KEY_SEARCH_SUGGESTION = "channel_search_suggestion";
	private static final String KEY_LAST_GET_SUGGESTION = "last_get_suggestion";
	// 用户登录后拿到的id
	private static final String KEY_USER_INIT_LOGIN = "user_init_login";
	// 未登录时生成的id
	private static final String KEY_USER_INIT_DEFAULT = "user_init_default";
	// 是否仅在wifi下自动加载图片
	private static final String KEY_USER_ONLY_WIFI = "only_wifi";
	// 喜欢的评论
	private static final String KEY_COMMENTS_LIKE = "comments_like";
	// 赞的文章
	private static final String KEY_ARTICLES_LIKE = "articles_like";
	// 踩的文章
	private static final String KEY_ARTICLES_DISLIKE = "articles_dislike";
	// 是否仅在wifi下自动加载图片
	private static final String KEY_PUSH_MSG_ENABLED = "push_msg_enabled";

	// 用户收藏数
	private static final String KEY_MY_FAV_COUNT = "my_fav_count";

	// 引导页步骤
	private static final String KEY_GUIDE_STEP = "guide_step";
	// 活动频道
	private static final String KEY_ACTIVE_CHANNEL_LIST = "active_channel_list";

	public void saveInitRsp(UserInitRsp rsp) {
		if (rsp == null) {
			return;
		}

		saveObject(KEY_USER_INIT_LOGIN, rsp);
	}

	/**
	 * 清除登录信息
	 */
	public void clearLoginInfo() {
		saveObject(KEY_USER_INIT_LOGIN, null);
	}

	public UserInitRsp getInitRsp() {
		UserInitRsp rsp = (UserInitRsp) getObject(KEY_USER_INIT_LOGIN);
		if (rsp == null) {
			rsp = (UserInitRsp) getObject(KEY_USER_INIT_DEFAULT);
		}

		return rsp;
	}

	public boolean isUserLogin() {
		UserInitRsp rsp = (UserInitRsp) getObject(KEY_USER_INIT_LOGIN);
		return rsp != null && rsp.getAccessToken() != null;
	}

	public void saveDefaultInitRsp(UserInitRsp rsp) {

		if (rsp != null) {
			saveObject(KEY_USER_INIT_DEFAULT, rsp);
		}
	}

	public void saveMyFavCount(int count) {
		saveObject(KEY_MY_FAV_COUNT, count);
	}

	public int getMyFavCount() {
		Integer count = getObject(KEY_MY_FAV_COUNT);
		if (count == null) {
			return 0;
		}
		return count;
	}

	/**
	 * 设置是否仅在wifi下自动加载图片
	 */
	public void setOnlyWifi(boolean isOnlyWifi) {
		mPref.edit().putBoolean(KEY_USER_ONLY_WIFI, isOnlyWifi).commit();
		mCacheMap.put(KEY_USER_ONLY_WIFI, isOnlyWifi);
	}

	/**
	 * 是否仅在wifi下自动加载图片
	 * 
	 * @return
	 */
	public boolean isOnlyWifi() {
		if (mCacheMap.containsKey(KEY_USER_ONLY_WIFI)) {
			return (Boolean) mCacheMap.get(KEY_USER_ONLY_WIFI);
		}

		boolean isOnlyWifi = mPref.getBoolean(KEY_USER_ONLY_WIFI, false);
		mCacheMap.put(KEY_USER_ONLY_WIFI, isOnlyWifi);
		return isOnlyWifi;
	}
	
	public void saveActiveChannelList(List<Channel> list) {
		saveObject(KEY_ACTIVE_CHANNEL_LIST, list);
	}

	public List<Channel> getActiveChannelList() {
		return getObject(KEY_ACTIVE_CHANNEL_LIST);
	}
	
	/**
	 * 设置是否推送通知
	 * 
	 * @param enabled
	 */
	public void setPushMsgEnabled(boolean enabled) {
		mPref.edit().putBoolean(KEY_PUSH_MSG_ENABLED, enabled).commit();
	}

	/**
	 * 是否推送通知
	 * 
	 * @return
	 */
	public boolean isPushMsgEnabled() {
		return mPref.getBoolean(KEY_PUSH_MSG_ENABLED, true);
	}

	public UserInitRsp getDefaultInitRsp() {
		UserInitRsp rsp = getObject(KEY_USER_INIT_DEFAULT);// new
															// UserInitRsp();
		return rsp;
	}

	/**
	 * 判断是否是第一次启动，该方法只有在第一次被调用时返回true
	 * 
	 * @return true if this method is called at the first time. false otherwise
	 */
	public boolean isFirstLaunch() {
		boolean isFirstLaunch = mPref.getBoolean(KEY_IS_FIRST_LAUNCH, true);
		return isFirstLaunch;
	}

	public void finishFirstLaunch() {
		mPref.edit().putBoolean(KEY_IS_FIRST_LAUNCH, false).commit();
	}

	@SuppressWarnings("unchecked")
	public List<Channel> getMyFavorChannelList() {
		List<Channel> channelList = (List<Channel>) getObject(KEY_CHANNEL_LIST);// new
																				// ArrayList<Channel>();
		return channelList;
	}

	public void saveLastGetSuggestionTime(long timeV) {
		mPref.edit().putLong(KEY_LAST_GET_SUGGESTION, timeV).commit();
	}

	public long getLastGetSuggestionTime() {
		return mPref.getLong(KEY_LAST_GET_SUGGESTION, 0);
	}

	private void saveObject(String key, Object object) {
		mCacheMap.put(key, object);
		if (object == null) {
			mPref.edit().putString(key, "").commit();
			return;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);

			String productBase64 = new String(Base64.encode(baos.toByteArray(),
					Base64.DEFAULT));
			mPref.edit().putString(key, productBase64).commit();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T getObject(String key) {
		if (mCacheMap.containsKey(key)) {
			return (T) mCacheMap.get(key);
		}
		String productBase64 = mPref.getString(key, "");

		byte[] data = Base64.decode(productBase64.getBytes(), Base64.DEFAULT);

		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		ObjectInputStream ois = null;
		T obj = null;
		try {
			ois = new ObjectInputStream(bais);
			obj = (T) ois.readObject();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		mCacheMap.put(key, obj);

		return obj;
	}

	// private Object getObject(String key) {
	// if (mCacheMap.containsKey(key)) {
	// return mCacheMap.get(key);
	// }
	// String productBase64 = mPref.getString(key, "");
	//
	// byte[] data = Base64.decode(productBase64.getBytes(), Base64.DEFAULT);
	//
	// ByteArrayInputStream bais = new ByteArrayInputStream(data);
	// ObjectInputStream ois = null;
	// Object obj = null;
	// try {
	// ois = new ObjectInputStream(bais);
	// obj = ois.readObject();
	// } catch (IOException e1) {
	// e1.printStackTrace();
	// } catch (ClassNotFoundException e) {
	// e.printStackTrace();
	// }
	//
	// mCacheMap.put(key, obj);
	//
	// return obj;
	// }

	public void saveMyFavorChannelList(List<Channel> channelList) {
		if (channelList == null) {
			Log.w(LOG_TAG, "[saveMyFavorChannelList] channelList is null");
			return;
		}

		Set<Channel> tempSet = new LinkedHashSet<Channel>();
		tempSet.addAll(channelList);
		channelList.clear();
		channelList.addAll(tempSet);
		saveObject(KEY_CHANNEL_LIST, channelList);
	}

	public void saveMyCommentsLike(Set<String> commentList) {
		if (commentList == null) {
			Log.w(LOG_TAG, "[saveMyCommentsLike] commentList is null");
			return;
		}
		saveObject(this.KEY_COMMENTS_LIKE, commentList);
	}

	public Set<String> getMyCommentsLike() {
		Set<String> commentList = (Set<String>) getObject(KEY_COMMENTS_LIKE);
		return commentList;
	}

	public void saveMyArticlesLike(Set<Long> articleList) {
		if (articleList == null) {
			Log.w(LOG_TAG, "[saveMyCommentsLike] commentList is null");
			return;
		}
		saveObject(this.KEY_ARTICLES_LIKE, articleList);
	}

	public Set<Long> getMyArticlesLike() {
		Set<Long> articleList = (Set<Long>) getObject(KEY_ARTICLES_LIKE);
		return articleList;
	}

	public void saveMyArticlesDislike(Set<Long> articleList) {
		if (articleList == null) {
			Log.w(LOG_TAG, "[saveMyCommentsLike] commentList is null");
			return;
		}
		saveObject(this.KEY_ARTICLES_DISLIKE, articleList);
	}

	public Set<Long> getMyArticlesDislike() {
		Set<Long> articleList = (Set<Long>) getObject(KEY_ARTICLES_DISLIKE);
		return articleList;
	}

	public void saveSearchSuggestion(Map<String, ArrayList<String>> map) {
		if (map == null) {
			return;
		}
		saveObject(KEY_SEARCH_SUGGESTION, map);
	}

	public Map<String, ArrayList<String>> getSearchSuggestion() {
		Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		map = getObject(KEY_SEARCH_SUGGESTION);
		return map;
	}

	public static final int STEP_INFO = 0;
	public static final int STEP_BRUSH = 1;
	public static final int STEP_ME = 2;
	public static final int STEP_FAV = 3;
	public static final int STEP_DONE = 4;
	public int getCurrentGuideStep() {
		return mPref.getInt(KEY_GUIDE_STEP, STEP_INFO);
	}

	public void setGuideStep(int step) {
		mPref.edit().putInt(KEY_GUIDE_STEP, step).commit();
	}
}
