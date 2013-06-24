package com.test.hwautotest.ftp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


import android.app.Activity;
import android.app.Application;
import android.util.Log;

/**
 *  退出程序关闭所有Activity类 
 * 
 * @author center 
 */
public class MyApplication extends Application {
	private ArrayList<Activity> activityList = new ArrayList<Activity>();
	private static MyApplication instance;

	private MyApplication() {
		
	}

	// 单例模式中获取唯一的MyApplication实例
	public static MyApplication getInstance() {
		if (null == instance) {
			instance = new MyApplication();
		}
		return instance;
	} 
	
	// 添加Activity到容器中
	public void addActivity(Activity activity) {
		activityList.add(activity);
//		Log.i("look","add activity !size:"+ activityList.size());
	} 
	
	// 遍历所有Activity并finish
	public void exit() {
		for (Activity activity : activityList) {
			activity.finish();
		}
		System.exit(0);
	}
}
// 在其他的activity中调用MyApplication.getInstance().addActivity(***activity)使其加入列表。
// 然后在要退出的按钮等事件监听中调用MyApplication.getInstance().exit()就行了
