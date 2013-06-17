package com.test.hwautotest.ftp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ActivityUtil extends Activity {
	private static final String TAG = "look";

	public static void toast(Context context, String hint) {
		Toast.makeText(context, hint, Toast.LENGTH_SHORT).show();
	}

	public static void goActivity(Context context, Class<?> tarActivityClass) {// <?>通配所有类型
		Intent intent = new Intent();

		intent.setClass(context, tarActivityClass);
		context.startActivity(intent);
	}

	public static void goActivityWithDoData(Context context,
			Class<?> tarActivityClass, int data_ftpType, String data_filePath) {// <?>通配所有类型
		Intent intent = new Intent();
		intent.putExtra("ftpType", data_ftpType);
		intent.putExtra("filePath", data_filePath);

		intent.setClass(context, tarActivityClass);
		context.startActivity(intent);
	}

	/**
	 * 跳转界面，并传输字符值
	 * 
	 * @param context
	 * @param tarActivityClass
	 * @param keyName
	 * @param data_filePath
	 */
	public static void goActivityWithString(Context context,
			Class<?> tarActivityClass, String keyName, String data_filePath) {// <?>通配所有类型
		Intent intent = new Intent();
		intent.putExtra(keyName, data_filePath);

		intent.setClass(context, tarActivityClass);
		context.startActivity(intent);
	}

	/**
	 * 跳转界面，并传输字符值
	 * 
	 * @param context
	 * @param tarActivityClass
	 * @param keyName
	 * @param data_filePath
	 */
	public static void goActivityWithString2(Context context,
			Class<?> tarActivityClass, String keyName, String data_filePath,
			String key2, String data_fileSize) {// <?>通配所有类型
		Intent intent = new Intent();
		intent.putExtra(keyName, data_filePath);
		intent.putExtra(key2, data_fileSize);

		intent.setClass(context, tarActivityClass);
		context.startActivity(intent);
	}

}
