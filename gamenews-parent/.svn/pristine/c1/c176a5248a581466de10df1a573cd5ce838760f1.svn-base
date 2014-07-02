package com.yy.android.gamenews.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.duowan.gamenews.Channel;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.GameNewsApplication;
import com.yy.android.gamenews.R;

public class Util {

	public static final String getDeviceUUID(Context context) {
		final TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = ""
				+ android.provider.Settings.Secure.getString(
						context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String uniqueId = deviceUuid.toString();

		return uniqueId;
	}

	public static boolean isWifiConnected() {
		ConnectivityManager conMan = (ConnectivityManager) GameNewsApplication
				.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		if (State.CONNECTED.equals(wifi)) {
			return true;
		}
		return false;
	}

	public static boolean isNetworkConnected() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) GameNewsApplication
				.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (mNetworkInfo != null) {
			return mNetworkInfo.isAvailable();
		}
		return false;
	}

	public static String getStackTraceString(Throwable tr) {
		if (tr == null) {
			return "";
		}

		return android.util.Log.getStackTraceString(tr);

		// return tr.getMessage();
	}

	public static boolean isSDExists() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	private static final int INSTALL_GAME_COUNT = 2;

	/**
	 * 选取用户安装的游戏用于首页推荐
	 * 
	 * @return
	 */
	public static ArrayList<String> getInitChannelList() {
		List<GameName> installedGameChannels = new ArrayList<GameName>(); // 保存手机中装的游戏
		List<ApplicationInfo> appInfos = getInstalledApplication();
		List<GameName> defaultGameChannels = getDefaultGameList();

		PackageManager pManager = GameNewsApplication.getInstance()
				.getPackageManager();
		for (ApplicationInfo info : appInfos) {
			String name = info.loadLabel(pManager).toString();

			for (GameName game : defaultGameChannels) {
				if (game.name.equals(name)) {
					int index = installedGameChannels.size();
					for (int i = 0; i < installedGameChannels.size(); i++) {
						if (game.index < installedGameChannels.get(i).index) {
							index = i;
						}
					}

					installedGameChannels.add(index, game);
					break;
				}
			}

		}

		ArrayList<String> installedGameName = new ArrayList<String>();
		for (GameName gamename : installedGameChannels) {
			installedGameName.add(gamename.name);
		}
		// int dataSize = installedGameChannels.size();
		// List<Channel> randomList = new ArrayList<Channel>(); //
		// 从手机中装的游戏中随机选取两款
		// // 如果安装游戏数量少于需返回的数量，则直接返回。否则随机选取需返回数量的游戏
		// if (dataSize < INSTALL_GAME_COUNT) {
		// randomList = installedGameChannels;
		// } else {
		// Random random = new Random();
		// while (randomList.size() < INSTALL_GAME_COUNT) {
		// int randomPos = random.nextInt(dataSize);
		// Channel channel = installedGameChannels.remove(randomPos);
		// randomList.add(channel);
		// dataSize = installedGameChannels.size();
		// }
		// }
		// // 推荐两款热门
		// // TODO: 推荐两款热门频道，需要产品提供
		// Channel channel = new Channel();
		// channel.setName("手游");
		// randomList.add(channel);
		//
		// channel = new Channel();
		// channel.setName("美女");
		// randomList.add(channel);
		// -

		return installedGameName;
	}

	/**
	 * 拿到所有安装的应用程序
	 * 
	 * @return
	 */
	public static List<ApplicationInfo> getInstalledApplication() {
		PackageManager pManager = GameNewsApplication.getInstance()
				.getPackageManager();
		List<ApplicationInfo> infos = pManager
				.getInstalledApplications(PackageManager.GET_META_DATA);
		return infos;
	}

	/**
	 * 用于排序
	 * 
	 * @author Administrator
	 * 
	 */
	private static class GameName {
		private String name;
		private int index;
	}

	private static List<GameName> getDefaultGameList() {
		List<GameName> list = new ArrayList<GameName>();

		AssetManager asm = GameNewsApplication.getInstance().getAssets();

		InputStream is = null;
		int index = 0;
		try {
			is = asm.open(Constants.FILE_NAME_GAME_LIST);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String value = null;
			while ((value = reader.readLine()) != null) {

				// TODO:当前只读取了游戏名字，需要读取游戏id和icon
				GameName channel = new GameName();
				channel.name = value;
				channel.index = index++;
				list.add(channel);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	private static Object sLock = new Object();
	private static String sVersionName;
	private static int sVersionCode;

	public static String getVersionName() {
		if (sVersionName == null) {
			synchronized (sLock) {
				sVersionName = GameNewsApplication.getInstance()
						.getPackageInfo().versionName;
			}
		}
		return sVersionName;
	}

	public static int getVersioCode() {
		if (sVersionCode == 0) {
			synchronized (sLock) {
				sVersionCode = GameNewsApplication.getInstance()
						.getPackageInfo().versionCode;
			}
		}
		return sVersionCode;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static void copyText(String text) {
		Context context = GameNewsApplication.getInstance();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("simple text", text);
			clipboard.setPrimaryClip(clip);
		} else {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(text);
		}
	}

	public static boolean isSubscribedChannel(List<Channel> channels,
			Channel channel) {
		boolean flag = false;
		if (channel != null && channels != null) {
			for (Channel item : channels) {
				if (channel.id == item.id) {
					flag = true;
					break;

				}

			}
		}
		return flag;
	}

	public static void validChannelData(Channel channel) {
		if (channel.icon == null) {
			channel.icon = "";
		}
		if (channel.image == null) {
			channel.image = "";
		}
		if (channel.name == null) {
			channel.name = "";
		}
	}

	private static float mDensity;
	private static int mAppWidthDip;
	private static int mAppWidth;
	private static int mAppHeight;
	private static int mAppHeightDip;

	public static float getDensity() {
		if (mDensity != 0)
			return mDensity;

		mDensity = GameNewsApplication.getInstance().getResources()
				.getDisplayMetrics().density;

		return mDensity;
	}

	public static int getAppHeightDip() {
		if (mAppHeightDip != 0)
			return mAppHeightDip;

		final Context context = GameNewsApplication.getInstance();
		mAppHeightDip = Util.px2dip(context, getEquipmentHeight(context));

		return mAppHeightDip;
	}

	public static int getAppHeight() {
		if (mAppHeight != 0)
			return mAppHeight;

		mAppHeight = getEquipmentHeight(GameNewsApplication.getInstance());

		return mAppHeight;
	}

	public static int getAppWidthDip() {
		if (mAppWidthDip != 0)
			return mAppWidthDip;

		final Context context = GameNewsApplication.getInstance();
		mAppWidthDip = Util.px2dip(context, getEquipmentWidth(context));

		return mAppWidthDip;
	}

	public static int getAppWidth() {
		if (mAppWidth != 0)
			return mAppWidth;

		mAppWidth = getEquipmentWidth(GameNewsApplication.getInstance());

		return mAppWidth;
	}

	public static float getDensityDpi() {
		return GameNewsApplication.getInstance().getResources()
				.getDisplayMetrics().densityDpi;
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = getDensity();
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int getEquipmentWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	public static int getEquipmentHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	public static void showHelpTips(Context context, View targetView,
			OnDismissListener dismissListener) {
		final PopupWindow tipsView = new PopupWindow(context);
		tipsView.setOutsideTouchable(true);
		tipsView.setTouchable(true);
		tipsView.setFocusable(true);

		tipsView.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		tipsView.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		tipsView.setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				tipsView.dismiss();
				return true;
			}
		});
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View helpView = inflater.inflate(R.layout.help_view, null);
		tipsView.setContentView(helpView);
		TextView tips = (TextView) helpView.findViewById(R.id.help_tips);
		tipsView.setBackgroundDrawable(context.getResources().getDrawable(
				R.drawable.bg_help_down));
		if (dismissListener != null) {
			tipsView.setOnDismissListener(dismissListener);
		}
		switch (targetView.getId()) {
		case R.id.info_btn:
			tips.setText(R.string.tips_new_info);

			break;
		case R.id.brush_btn:
			tips.setText(R.string.tips_brush);
			break;
		case R.id.news_btn:
			tips.setText(R.string.tips_mine);
			tipsView.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.bg_help_down1));
			break;
		case R.id.add_title:
			tips.setText(R.string.tips_subscribe);
			tipsView.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.bg_help_up));
			break;
		default:
			break;
		}

		int[] location = new int[2];
		targetView.getLocationOnScreen(location);
		Rect tempRect = new Rect(location[0], location[1], location[0]
				+ targetView.getWidth(), location[1] + targetView.getHeight());
		helpView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		int helpViewWidth = helpView.getMeasuredWidth();
		int helpViewHeight = helpView.getMeasuredHeight();

		if (R.id.add_title == targetView.getId()) {
			tipsView.showAsDropDown(targetView, 0, dip2px(context, 10));
		} else if (R.id.news_btn == targetView.getId()) {
			int xPos = (tempRect.left + tempRect.right) / 2 - helpViewWidth
					+ dip2px(context, 25);
			int yPos = (int) (tempRect.top - helpViewHeight - dip2px(context,
					10));
			tipsView.showAtLocation(targetView, Gravity.NO_GRAVITY, xPos, yPos);
		} else {
			int xPos = (tempRect.left + tempRect.right) / 2 - helpViewWidth / 2;
			int yPos = (int) (tempRect.top - helpViewHeight - dip2px(context,
					10));
			tipsView.showAtLocation(targetView, Gravity.NO_GRAVITY, xPos, yPos);
		}

		int step = Preference.getInstance().getCurrentGuideStep();
		if (step < Preference.STEP_DONE) {
			Preference.getInstance().setGuideStep(step + 1);
		}
	}

	public static String getUrlDomainName(String url) throws URISyntaxException {
		if (url == null) {
			return null;
		}
		URI uri = new URI(url);
		String domain = uri.getHost();
		return domain.startsWith("www.") ? domain.substring(4) : domain;
	}

	public static String getUrlQuery(String url) throws URISyntaxException {
		if (url == null) {
			return null;
		}
		URI uri = new URI(url);
		String query = uri.getQuery();
		return query;
	}
}