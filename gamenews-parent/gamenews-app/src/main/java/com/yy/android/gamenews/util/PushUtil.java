package com.yy.android.gamenews.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

import com.tencent.android.tpush.XGPushActivity;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushReceiver;
import com.tencent.android.tpush.service.XGPushService;
import com.yy.android.gamenews.model.ChannelModel;
import com.yy.android.gamenews.receiver.CustomPushReceiver;

public class PushUtil {
	public static void start(Context context) {
		// registerPush之前调用
		// 防止被安全软件等禁止掉组件，导致广播接收不了或service无法启动
		enableComponentIfNeeded(context, XGPushService.class.getName());
		enableComponentIfNeeded(context, XGPushReceiver.class.getName());
		// 2.30及以上版本
		enableComponentIfNeeded(context, XGPushActivity.class.getName());
		// CustomPushReceiver改为自己继承XGPushBaseReceiver的类，若有的话
		enableComponentIfNeeded(context, CustomPushReceiver.class.getName());

		XGPushManager.registerPush(context);
	}

	public static void stop(Context context) {
		XGPushManager.unregisterPush(context);
		ChannelModel.pushInit("");
	}

	// 启用被禁用组件方法
	private static void enableComponentIfNeeded(Context context,
			String componentName) {
		PackageManager pmManager = context.getPackageManager();
		if (pmManager != null) {
			ComponentName cnComponentName = new ComponentName(
					context.getPackageName(), componentName);
			int status = pmManager.getComponentEnabledSetting(cnComponentName);
			if (status != PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
				pmManager.setComponentEnabledSetting(cnComponentName,
						PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
						PackageManager.DONT_KILL_APP);
			}
		}
	}
}
