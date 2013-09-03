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
package com.test.hwautotest.emmagee.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.test.hwautotest.R;
import com.test.hwautotest.emmagee.service.EmmageeService;
import com.test.hwautotest.emmagee.utils.ProcessInfo;
import com.test.hwautotest.emmagee.utils.Programe;
import com.test.hwautotest.ftp.MyApplication;
import com.test.utils.ViewHolder;

/**
 * Main Page of Emmagee
 * 
 */
public class EmmageeMainPageActivity extends Activity {

	private static final String LOG_TAG = "Emmagee"
			+ EmmageeMainPageActivity.class.getSimpleName();

	private static final int TIMEOUT = 20000;

	private List<Programe> processList;
	private ProcessInfo processInfo;
	private Intent monitorService;
	private ListView lstViProgramme;
	private Button btnTest;
	private boolean isRadioChecked = false;
	private int pid, uid;
	private  String processName, packageName, settingTempFile;

	private boolean isServiceStop = false;
	private UpdateReceiver receiver;

	private MenuItem menu_settings;
	
	private static ListAdapter listAdapter;
	private  static int selectPosition = -1;//如果是实例成员，切换出当前界面后就重新赋值；而若是静态成员，则一直按之前的
	private static int mFirstItemTop;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.emmagee_mainpage);
		createNewFile();
		processInfo = new ProcessInfo();
		lstViProgramme = (ListView) findViewById(R.id.processList);
		//点list勾选
		lstViProgramme.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				selectPosition=arg2;
				listAdapter.notifyDataSetChanged();//立即刷新当前界面的适配器内容数据，而不用等到拖动过后
			}
		});
		
		//保存当前滚动位置
		lstViProgramme.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onScrollStateChanged(AbsListView arg0, int scrollState) {
				// TODO Auto-generated method stub
//				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
//					scrolledX = lstViProgramme.getScrollX();//无效
//					scrolledY = lstViProgramme.getScrollY();//无效
//				mFirstItem = lstViProgramme.getFirstVisiblePosition();
				mFirstItemTop = lstViProgramme.getChildAt(0).getTop();
//				} 
				
			}});
		
		listAdapter=new ListAdapter();//保存全局变量便于启用数据刷新方法notifyDataSetChanged()
		lstViProgramme.setAdapter(listAdapter);
		
		btnTest = (Button) findViewById(R.id.test);
		btnTest.setText("开始测试");
		btnTest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				monitorService = new Intent();
				monitorService.setClass(EmmageeMainPageActivity.this,
						EmmageeService.class);
				if ("开始测试".equals(btnTest.getText().toString())) {
					if (isRadioChecked) {
						Intent intent = getPackageManager()
								.getLaunchIntentForPackage(packageName);
						Log.d(LOG_TAG, packageName);
						try {
							startActivity(intent);
						} catch (NullPointerException e) {
							Toast.makeText(EmmageeMainPageActivity.this, "该程序无法启动",
									Toast.LENGTH_LONG).show();
							return;
						}
						waitForAppStart(packageName);
						monitorService.putExtra("processName", processName);
						monitorService.putExtra("pid", pid);
						monitorService.putExtra("uid", uid);
						monitorService.putExtra("packageName", packageName);
						monitorService.putExtra("settingTempFile",
								settingTempFile);
						startService(monitorService);
						
						btnTest.setEnabled(false);
						lstViProgramme.setEnabled(false);
						btnTest.setText("停止测试");
					} else {
						Toast.makeText(EmmageeMainPageActivity.this, "请选择需要测试的应用程序",
								Toast.LENGTH_LONG).show();
					}
				} else {
					btnTest.setText("开始测试");
					initSelected();
					Toast.makeText(EmmageeMainPageActivity.this,
							"测试结果文件：" + EmmageeService.resultFilePath,
							Toast.LENGTH_LONG).show();
					stopService(monitorService);
				}
				listAdapter.notifyDataSetChanged();//立即刷新当前界面的适配器内容数据，而不用等到拖动过后
			}
		});
	}

	/**
	 * customized BroadcastReceiver
	 */
	public class UpdateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			isServiceStop = intent.getExtras().getBoolean("isServiceStop");
			if (isServiceStop) {
				btnTest.setText("开始测试");
			}
		}
	}

	@Override
	protected void onStart() {
		Log.d(LOG_TAG, "onStart");
		super.onStart();
		receiver = new UpdateReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.test.hwautotest.action.emmageeService");
		this.registerReceiver(receiver, filter);
//		listAdapter=new ListAdapter();//保存全局变量便于启用数据刷新方法notifyDataSetChanged()
//		lstViProgramme.setAdapter(listAdapter);
//		listAdapter.notifyDataSetChanged();//立即刷新当前界面的适配器内容数据，而不用等到拖动过后
		
		
	}

	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.d(LOG_TAG, "onRestart");
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	@Override
	public void onResume() {
		super.onResume();
		Log.d(LOG_TAG, "onResume");
		initBtnTest();
		initSelected();
		initListScroll();
	}

	/**
	 * create new file to reserve setting data.
	 */
	private void createNewFile() {
		Log.i(LOG_TAG, "create new file to save setting data");
		settingTempFile = getBaseContext().getFilesDir().getPath()
				+ "/EmmageeSettings.properties";
		Log.i(LOG_TAG, "settingFile = " + settingTempFile);
		File settingFile = new File(settingTempFile);
		if (!settingFile.exists()) {
			try {
				settingFile.createNewFile();
				Properties properties = new Properties();
				properties.setProperty("interval", "5");
				properties.setProperty("isfloat", "true");
				properties.setProperty("sender", "");
				properties.setProperty("password", "");
				properties.setProperty("recipients", "");
				properties.setProperty("smtp", "");
				FileOutputStream fos = new FileOutputStream(settingTempFile);
				properties.store(fos, "Setting Data");
				fos.close();
			} catch (IOException e) {
				Log.d(LOG_TAG, "create new file exception :" + e.getMessage());
			}
		}
	}

	/**
	 * wait for test application started.
	 * 
	 * @param packageName
	 *            package name of test application
	 */
	private void waitForAppStart(String packageName) {
		Log.d(LOG_TAG, "wait for app start");
		boolean isProcessStarted = false;
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() < startTime + TIMEOUT) {
			processList = processInfo.getRunningProcess(getBaseContext());
			for (Programe programe : processList) {
				if ((programe.getPackageName() != null)
						&& (programe.getPackageName().equals(packageName))) {
					pid = programe.getPid();
					Log.d(LOG_TAG, "pid:" + pid);
					uid = programe.getUid();
					if (pid != 0) {
						isProcessStarted = true;
						break;
					}
				}
			}
			if (isProcessStarted) {
				break;
			}
		}
	}

	/**
	 * show a dialog when click return key.
	 * 
	 * @return Return true to prevent this event from being propagated further,
	 *         or false to indicate that you have not handled this event and it
	 *         should continue to be propagated.
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//			showDialog(0);
			//切换其他界面不终止监测
//			if (monitorService != null) {
//				Log.d(LOG_TAG, "stop service");
//				stopService(monitorService);
//			}
//			Log.d(LOG_TAG, "exit Emmagee");
//			EmmageeService.closeOpenedStream();
//			finish();
//			System.exit(0);
		}
		return super.onKeyDown(keyCode, event);
	}

//	/**
//	 * set menu options,including cancel and setting options.
//	 * 
//	 * @return true
//	 */
//	public boolean onCreateOptionsMenu(Menu menu) {
//		menu.add(0, Menu.FIRST, 0, "退出").setIcon(
//				android.R.drawable.ic_menu_delete);
//		menu.add(0, Menu.FIRST, 1, "设置").setIcon(
//				android.R.drawable.ic_menu_directions);
//		return true;
//	}

//	/**
//	 * trigger menu options.
//	 * 
//	 * @return false
//	 */
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getOrder()) {
//		case 0:
////			showDialog(0);
//			if (monitorService != null) {
//				Log.d(LOG_TAG, "stop service");
//				stopService(monitorService);
//			}
//			Log.d(LOG_TAG, "exit Emmagee");
//			EmmageeService.closeOpenedStream();
//			finish();
//			System.exit(0);
//			break;
//		case 1:
//			Intent intent = new Intent();
//			intent.setClass(EmmageeMainPageActivity.this, SettingsActivity.class);
//			intent.putExtra("settingTempFile", settingTempFile);
//			startActivityForResult(intent, Activity.RESULT_FIRST_USER);
//			break;
//		default:
//			break;
//		}
//		return false;
//	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.emmagee_main, menu);
		return true;
	}
	private void toast(String hint) {
		Toast.makeText(this, hint,Toast.LENGTH_SHORT).show();
	}
	private static final String TAG = "look";
	
	
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		// TODO Auto-generated method stub
		menu_settings=menu.getItem(1);
		initSettings();
		
		return super.onMenuOpened(featureId, menu);
	}


	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int selectId=item.getItemId();
		switch (selectId) {
		// 响应每个菜单项(通过菜单项的ID)
		case R.id.action_about://关于
			toast("负责人:黄圣权");
			break;
		case R.id.action_settings://设置
			Intent intent = new Intent();
			intent.setClass(EmmageeMainPageActivity.this, SettingsActivity.class);
			intent.putExtra("settingTempFile", settingTempFile);
			startActivityForResult(intent, Activity.RESULT_FIRST_USER);
			break;
			
		case R.id.action_exit://退出
			toast("退出程序");
			// 退出
			if (monitorService != null) {
				Log.d(LOG_TAG, "stop service");
				stopService(monitorService);
			}
			Log.d(LOG_TAG, "exit Emmagee");
			EmmageeService.closeOpenedStream();
			finish();
			System.exit(0);
			break;
		default:
			// 对没有处理的事件，交给父类来处理
			return super.onOptionsItemSelected(item);

		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * create a dialog.
	 * 
	 * @return a dialog
	 */
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			return new AlertDialog.Builder(this)
					.setTitle("确定退出程序？")
					.setPositiveButton(
							"确定",
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (monitorService != null) {
										Log.d(LOG_TAG, "stop service");
										stopService(monitorService);
									}
									Log.d(LOG_TAG, "exit Emmagee");
									EmmageeService.closeOpenedStream();
									finish();
									System.exit(0);
								}
							}).setNegativeButton("取消", null).create();
		default:
			return null;
		}
	}

	/**
	 * customizing adapter.
	 * 
	 */
	private class ListAdapter extends BaseAdapter {
		List<Programe> programe;
//		int selectPosition = -1;

		public ListAdapter() {
			programe = processInfo.getRunningProcess(getBaseContext());
		}

		@Override
		public int getCount() {
			return programe.size();
		}

		@Override
		public Object getItem(int position) {
			return programe.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		/**
		 * position 属于listview的第几个，从0开始
		 * 
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			Viewholder holder = null;
			final int oldPosition = position;
			if(convertView==null){
				holder=new Viewholder();
				convertView = EmmageeMainPageActivity.this.getLayoutInflater().inflate(
						R.layout.emmagee_list_item, null);
				holder.imgViAppIcon = (ImageView) convertView.findViewById(R.id.image);//关联图标
				holder.txtAppName = (TextView) convertView.findViewById(R.id.text);//关联名称
				holder.rdoBtnApp = (RadioButton) convertView.findViewById(R.id.chooseBtn);//关联单选键
				convertView.setTag(holder);//保存
			}else{
				holder=(Viewholder)convertView.getTag();//提取已保存的
			}
			holder.rdoBtnApp.setId(position);
			holder.rdoBtnApp
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							if (isChecked) {
								isRadioChecked = true;
								// 取消上次勾选的
								if (selectPosition != -1) {
									RadioButton tempButton = (RadioButton) findViewById(selectPosition);
									if ((tempButton != null)
											&& (selectPosition != oldPosition)) {
										tempButton.setChecked(false);
									}
								}
								// 获取当前勾选的
								selectPosition = buttonView.getId();// 编号和listview的从属位置一致
								packageName = programe.get(selectPosition)
										.getPackageName();
								processName = programe.get(selectPosition)
										.getProcessName();

							}
						}
					});
			
			if (selectPosition == position){
				if (!holder.rdoBtnApp.isChecked())
					holder.rdoBtnApp.setChecked(true);//勾选单选键
			}else{
				holder.rdoBtnApp.setChecked(false);//取消单选键，防止多重勾选现象
			}
			
			Programe pr = (Programe) programe.get(position);
			holder.imgViAppIcon.setImageDrawable(pr.getIcon());//赋值图标
			holder.txtAppName.setText(pr.getProcessName());//赋值名称
			
			//按钮启动后，列表置灰
			if ("停止测试".equals(btnTest.getText().toString())) {// 已启动，则置灰
				holder.imgViAppIcon.setEnabled(false);
				holder.rdoBtnApp.setEnabled(false);
				holder.txtAppName.setEnabled(false);
			}else{
				holder.imgViAppIcon.setEnabled(true);
				holder.rdoBtnApp.setEnabled(true);
				holder.txtAppName.setEnabled(true);
			}
			return convertView;
		}

	}
	

	@Override
	public void finish() {
		super.finish();
	}

	protected void onStop() {
		unregisterReceiver(receiver);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	/**
	 * save status of all installed processes
	 */
	class Viewholder {
		TextView txtAppName;
		ImageView imgViAppIcon;
		RadioButton rdoBtnApp;
	}
	
	/**
	 * 初始化控制按钮的状态
	 */
	private void initBtnTest(){
		Log.d(LOG_TAG, "！！！initBtnTest()");
		btnTest.setEnabled(true);
		if (!EmmageeService.isStop) {
			btnTest.setText("停止测试");
		}
//		else {//不加此项修改，避免启动服务延迟造成误差
//			btnTest.setText("开始测试");
//		}
	}
	/**
	 * 恢复列表开始测试时的位置
	 */
	private void initListScroll(){
//		Log.d(LOG_TAG, "！！！initListScroll()"+mFirstItem+"/"+mFirstItemTop);
		
		
//		if ("停止测试".equals(btnTest.getText().toString())) {//已启动则恢复之前已选的位置
//			lstViProgramme.scrollTo(scrolledX, scrolledY);
//		lstViProgramme.setSelectionFromTop(mFirstItem, mFirstItemTop);//恢复之前的界面
		lstViProgramme.setSelectionFromTop(selectPosition-1, mFirstItemTop);//恢复以启动项置顶的界面
//		}
	}
	/**
	 * 初始化勾选状态
	 */
	private void initSelected(){
		Log.d(LOG_TAG, "！！！initSelected()");
		
		if ("停止测试".equals(btnTest.getText().toString())) {//启动了，则保存之前启动过的勾选
			lstViProgramme.setEnabled(false);
			//通过保存静态变量的位置参数实现保存勾选
		}else {
			//激活列表选择
			lstViProgramme.setEnabled(true);
//			selectPosition=-1;//未启动则清空
		}
	}
	
	/**
	 * 初始化菜单设置项状态
	 */
	private void initSettings(){
		if (!EmmageeService.isStop) {
			menu_settings.setEnabled(false);
		}else{
			menu_settings.setEnabled(true);
		}
	}
	
	
}
