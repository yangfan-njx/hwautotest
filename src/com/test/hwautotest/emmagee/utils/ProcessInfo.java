/*
 * Copyright (c) 2012-2013 NetEase, Inc. and other contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.test.hwautotest.emmagee.utils;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * get information of processes
 *
 */
public class ProcessInfo {

	private static final String LOG_TAG = "Emmagee-"
			+ ProcessInfo.class.getSimpleName()+"look";

	private static final String PACKAGE_NAME = "com.test.hwautotest";

	/**
	 * get information of all running processes,including package name ,process
	 * name ,icon ,pid and uid.
	 *
	 * @param context
	 *            context of activity
	 * @return running processes list
	 */
	public List<Programe> getRunningProcess(Context context) {
		Log.i(LOG_TAG, "get running processes");

		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> run = am.getRunningAppProcesses();
		PackageManager pm = context.getPackageManager();
		List<Programe> progressList = new ArrayList<Programe>();
		boolean launchTag;

		for (ApplicationInfo appinfo : getPackagesInfo(context)) {
			launchTag = false;
			Programe programe = new Programe();
			if (
					//不过滤应用，列出所有，包括系统和后安装
//					((appinfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0)
//					|| 
					((appinfo.processName != null) && (appinfo.processName
							.equals(PACKAGE_NAME)))) {//过滤自己
				continue;
			}
			for (RunningAppProcessInfo runningProcess : run) {
				if ((runningProcess.processName != null)
						&& runningProcess.processName.equals(appinfo.processName)
						) {
					launchTag = true;
					programe.setPid(runningProcess.pid);
					programe.setUid(runningProcess.uid);
					break;
				}
			}
			programe.setPackageName(appinfo.processName);
			programe.setProcessName(appinfo.loadLabel(pm).toString());
//			if (launchTag) {
				programe.setIcon(appinfo.loadIcon(pm));
//			}
			
			progressList.add(programe);
			sortList(progressList);
		}
		Log.i(LOG_TAG, "**all:");
		for(Programe p:progressList){
//			Log.i(LOG_TAG, "pro-"+p.getProcessName());
//			Log.i(LOG_TAG, "uid-"+p.getUid());
//			Log.i(LOG_TAG, "pid-"+p.getPid());
		}
		return progressList;
	}

	/**
	 * get information of all applications.
	 *
	 * @param context
	 *            context of activity
	 * @return packages information of all applications
	 */
	private List<ApplicationInfo> getPackagesInfo(Context context) {
		PackageManager pm = context.getApplicationContext().getPackageManager();
		List<ApplicationInfo> appList = pm
				.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
		return appList;
	}
	
	/**
	 * 对列表排序
	 */
	private void sortList(List<Programe>progressList){
		Collections.sort(progressList, new Comparator<Programe>(){
			@Override
			public int compare(Programe lhs, Programe rhs) {
				Collator myCollator = Collator.getInstance(java.util.Locale.CHINA);//按中文字符排序，顺序按“中文拼音-英文字母”
				return myCollator.compare(lhs.getProcessName(), rhs.getProcessName());
			}
			
			//默认String按ASCII中文码比较，但并不完全按拼音排序
//			@Override
//			public int compare(Programe lhs, Programe rhs) {
//				// TODO Auto-generated method stub
//				if(lhs==null||rhs==null){
//					return 0;
//				}else {
//					return lhs.getProcessName().compareTo(rhs.getProcessName());
//					
//				}
//			}
		}); 
	}
	
	
	
}
