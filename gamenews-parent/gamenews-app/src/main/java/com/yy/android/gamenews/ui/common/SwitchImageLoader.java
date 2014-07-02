package com.yy.android.gamenews.ui.common;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.yy.android.gamenews.GameNewsApplication;
import com.yy.android.gamenews.R;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.Util;

/**
 * 封装了ImageLoader，添加网络检查，当用户有设置isWifiOnly时，仅在wifi情况下更新图片
 * 
 * @author carlosliu
 * 
 */
public class SwitchImageLoader {

	private static SwitchImageLoader INSTANCE;
	private ImageLoader mImageLoader;
	private boolean isWifiConnected;
	private Preference mPref;
	public static DisplayImageOptions DEFAULT_DISPLAYER = new DisplayImageOptions.Builder()
			.resetViewBeforeLoading(true).cacheInMemory(true).cacheOnDisc(true)
			.imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
	public static DisplayImageOptions DEFAULT_USER_DISPLAYER = new DisplayImageOptions.Builder()
			.resetViewBeforeLoading(true).cacheInMemory(true).cacheOnDisc(true)
			.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
			.showImageOnFail(R.drawable.ic_person_default_dark)
			.showImageOnLoading(R.drawable.ic_person_default_dark)
			.showImageForEmptyUri(R.drawable.ic_person_default_dark).build();
	public static DisplayImageOptions DEFAULT_CHANNEL_SMALL_DISPLAYER = new DisplayImageOptions.Builder()
			.resetViewBeforeLoading(true).cacheInMemory(true).cacheOnDisc(true)
			.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
			.showImageOnFail(R.drawable.ic_placeholder_small)
			.showImageOnLoading(R.drawable.ic_placeholder_small)
			.showImageForEmptyUri(R.drawable.ic_placeholder_small).build();
	public static DisplayImageOptions DEFAULT_CHANNEL_BIG_DISPLAYER = new DisplayImageOptions.Builder()
			.resetViewBeforeLoading(true).cacheInMemory(true).cacheOnDisc(true)
			.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
			.showImageOnFail(R.drawable.ic_placeholder_big)
			.showImageOnLoading(R.drawable.ic_placeholder_big)
			.showImageForEmptyUri(R.drawable.ic_placeholder_big).build();
	public static DisplayImageOptions DEFAULT_ARTICLE_ITEM_DISPLAYER = new DisplayImageOptions.Builder()
			.resetViewBeforeLoading(true).cacheInMemory(true).cacheOnDisc(true)
			.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
			.showImageOnFail(R.drawable.article_list_item_loading)
			.showImageOnLoading(R.drawable.article_list_item_loading)
			.showImageForEmptyUri(R.drawable.article_list_item_loading).build();

	public static DisplayImageOptions DEFAULT_ARTICLE_ITEM_BIG_DISPLAYER = new DisplayImageOptions.Builder()
			.resetViewBeforeLoading(true).cacheInMemory(true).cacheOnDisc(true)
			.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
			.showImageOnFail(R.drawable.article_list_item_loading_big)
			.showImageOnLoading(R.drawable.article_list_item_loading_big)
			.showImageForEmptyUri(R.drawable.article_list_item_loading_big).build();

	public SwitchImageLoader() {
		mImageLoader = ImageLoader.getInstance();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		GameNewsApplication.getInstance().registerReceiver(mReceiver, filter);
		isWifiConnected = Util.isWifiConnected();
		mPref = Preference.getInstance();
	}

	public static SwitchImageLoader getInstance() {
		if (INSTANCE == null) {
			synchronized (SwitchImageLoader.class) {
				if (INSTANCE == null) {
					INSTANCE = new SwitchImageLoader();
				}
			}
		}
		return INSTANCE;
	}

	public static void init(Context context) {
		// ImageUtil.setDiscCache(context);
		File individualCacheDir = StorageUtils
				.getIndividualCacheDirectory(context);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				GameNewsApplication.getInstance())
				.discCache(
						new LimitedAgeDiscCache(individualCacheDir,
								3600 * 24 * 7))
				.defaultDisplayImageOptions(DEFAULT_DISPLAYER).build();
		ImageLoader.getInstance().init(config);
	}

	public void displayImage(String url, ImageView view) {
		displayImage(url, view, false);
	}

	public void displayImage(String url, ImageView view,
			DisplayImageOptions options) {
		displayImage(url, view, options, false);
	}

	/**
	 * 
	 * @param url
	 * @param view
	 * @param forceUpdate
	 *            是否强制update
	 */
	public void displayImage(String url, ImageView view, boolean forceUpdate) {

		// TODO:: bug
		if (needLoadImage() || forceUpdate
				|| mImageLoader.getDiscCache().get(url).exists()) {
			mImageLoader.displayImage(url, view);
		}
	}

	/**
	 * 
	 * @param url
	 * @param view
	 * @param forceUpdate
	 *            是否强制update
	 */
	public void displayImage(String url, ImageView view,
			DisplayImageOptions options, boolean forceUpdate) {

		// TODO:: bug
		if (needLoadImage() || forceUpdate
				|| mImageLoader.getDiscCache().get(url).exists()) {
			mImageLoader.displayImage(url, view, options);
		}
	}

	public boolean needLoadImage() {
		boolean isOnlyWifi = mPref.isOnlyWifi();
		if (isOnlyWifi && !isWifiConnected) { // 如果开启仅wifi，并且当前不是wifi时，不加载图片
			return false;
		}
		return true;
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			isWifiConnected = Util.isWifiConnected();
		};
	};

	public void pause() {
		mImageLoader.pause();
	}

	public void resume() {
		mImageLoader.resume();
	}
}