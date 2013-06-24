package com.test.hwautotest.ftp;

import it.sauronsoftware.ftp4j.FTPClient;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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

	public   static void goActivityWithString2(Context context
			,Class<?> tarActivityClass
			,String keyName,String data_filePath,String key2,String data_fileSize
			,boolean closeCurrent
			,String keyFtpType,int ftpType){//<?>通配所有类型
		Intent intent=new Intent();
		intent.putExtra(keyName, data_filePath);
		intent.putExtra(key2, data_fileSize);
		intent.putExtra(keyFtpType, ftpType);
		
		intent.setClass(context, tarActivityClass);
		context.startActivity(intent);
		if(closeCurrent){
			((Activity)context).finish();
		}


	}
	
	/**
	 * 跳转界面，并传输字符值
	 * 
	 * @param context
	 * @param tarActivityClass
	 * @param keyName
	 * @param data_filePath
	 */

	public   static void goActivityWithString3(Context context
			,Class<?> tarActivityClass
			,String keyName,String data_filePath,String key2,String data_fileSize,
			boolean closeCurrent){//<?>通配所有类型
		
		Intent intent=new Intent();
		Bundle bundle = new Bundle(); 
		intent.putExtra(keyName, data_filePath);
		intent.putExtra(key2, data_fileSize);
		context.startActivity(intent);
		if(closeCurrent){
			((Activity)context).finish();
		}


	}

	/**
	 * 跳转界面，并传输字符值(默认不关闭当前界面)
	 * @param context
	 * @param tarActivityClass
	 * @param keyName
	 * @param data_filePath
	 */
	public   static void goActivityWithString2(Context context
			,Class<?> tarActivityClass
			,String keyName,String data_filePath,String key2,String data_fileSize
			){//<?>通配所有类型
		goActivityWithString2(context, tarActivityClass, keyName, data_filePath, key2, data_fileSize, false,"",-1);


	}
	
	/**
	 * 跳转界面，并传输字符值
	 * 
	 * @param context
	 * @param tarActivityClass
	 * @param keyName
	 * @param data_filePath
	 */

	public   static void goActivityWithString2(Context context
			,Class<?> tarActivityClass
			,String keyName,String data_filePath,String key2,String data_fileSize
			,boolean closeCurrent){//<?>通配所有类型
		goActivityWithString2(context, tarActivityClass, keyName, data_filePath, key2, data_fileSize, closeCurrent,"",-1);


	}
}
