package com.yy.android.gamenews.service;

import java.io.File;
import java.io.InputStream;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.duowan.gamenews.bean.Manifest;
import com.duowan.gamenews.bean.ManifestItem;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.GameNewsApplication;
import com.yy.android.gamenews.util.FileUtil;

public class UpgradeService extends Service {
	private static final String TAG = UpgradeService.class.getSimpleName();

	private SharedPreferences mPref;
	private Thread mThread;

	@Override
	public void onCreate() {
		mPref = GameNewsApplication.getInstance().getSharedPreferences(
				Constants.KEY_UPDATE_GLOBAL, MODE_PRIVATE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (mThread == null) {
			synchronized (this) {
				if (mThread == null) {
					mThread = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								process();
							} catch (Exception e) {
								Log.e(TAG, "process failed", e);
							} finally {
								stopSelf();
							}
						}
					});
					mThread.start();
				}
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void process() throws Exception {
		processManifest();
	}

	private void processManifest() throws Exception {
		boolean newVersionReady = mPref.getBoolean(Constants.NEW_VERSION_READY,
				false);
		if (newVersionReady) {
			return;
		}

		int currDir = mPref.getInt(Constants.CURR_DIR, 0);
		int newDir = 1 - currDir;
		File currPath = getDir(String.valueOf(currDir), MODE_PRIVATE);
		File newPath = getDir(String.valueOf(newDir), MODE_PRIVATE);

		Manifest currManifest = FileUtil.parseManifest(FileUtil
				.readFile(new File(currPath, Constants.MANIFEST_FILE)));

		byte[] newManifestText = FileUtil.download(Constants.MANIFEST_URL);
		Manifest newManifest = FileUtil.parseManifest(newManifestText);

		if (currManifest.getVersion().equals(newManifest.getVersion())) {
			return;
		}

		FileUtil.deleteDirectory(newPath.getAbsolutePath());
		if (!new File(newPath.getAbsolutePath()).exists()) {
			new File(newPath.getAbsolutePath()).mkdirs();
		}
		for (ManifestItem newItem : newManifest.getData().values()) {
			String name = newItem.getUrl().substring(
					newItem.getUrl().lastIndexOf("/") + 1,
					newItem.getUrl().length());
			String tpye = newItem.getUrl().substring(
					newItem.getUrl().lastIndexOf(".") + 1,
					newItem.getUrl().length());
			File currItemTpye = null;
			File newItemTpye = null;
			File newItemFile = null;
			if ((!tpye.equals("html"))
					&& (tpye != null || TextUtils.isEmpty(tpye))) {
				currItemTpye = new File(currPath, tpye);
				newItemTpye = new File(newPath, tpye);
				if (!newItemTpye.exists()) {
					newItemTpye.mkdirs();
				}
				newItemFile = new File(newItemTpye, name);
			} else {
				newItemFile = new File(newPath, name);
			}

			// copy if not changed
			ManifestItem currItem = currManifest.getData()
					.get(newItem.getUrl());
			if (currItem != null
					&& currItem.getVersion().equals(newItem.getVersion())) {
				if (currItemTpye != null && currItemTpye.exists()) {
					FileUtil.copyFile(new File(currItemTpye, name), new File(
							newItemTpye, name));
				} else {
					FileUtil.copyFile(new File(currPath, name), new File(
							newPath, name));
				}
				continue;
			}

			// otherwise, download
			FileUtil.download(newItem.getUrl(), newItemFile);

		}
		
		// copy image
		String currIndexDir;
		AssetManager assetManager = this.getAssets();
		currIndexDir = Constants.NEWS_IMAGE_LOADING;
		File fileDirImageLoading = new File(newPath, currIndexDir);
		if (!fileDirImageLoading.exists()) {
			fileDirImageLoading.createNewFile();
		}
		InputStream imageLoadingIn = assetManager.open(currIndexDir);
		try {
			FileUtil.save(imageLoadingIn, fileDirImageLoading);
		} finally {
			imageLoadingIn.close();
		}
		currIndexDir = Constants.NEWS_IMAGE_FAIL;
		File fileDirImageFail = new File(newPath, currIndexDir);
		if (!fileDirImageFail.exists()) {
			fileDirImageFail.createNewFile();
		}
		InputStream imageFailIn = assetManager.open(currIndexDir);
		try {
			FileUtil.save(imageFailIn, fileDirImageFail);
		} finally {
			imageFailIn.close();
		}
		
		// download new version
		FileUtil.writeFile(new File(newPath, Constants.MANIFEST_FILE),
				newManifestText);
		mPref.edit().putBoolean(Constants.NEW_VERSION_READY, true).commit();
	}

}
