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
package com.test.hwautotest.emmagee.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.netease.qa.emmagee.utils.MailSender;
import com.test.hwautotest.R;
import com.test.hwautotest.emmagee.activity.EmmageeMainPageActivity;
import com.test.hwautotest.emmagee.utils.CpuInfo;
import com.test.hwautotest.emmagee.utils.EncryptData;
import com.test.hwautotest.emmagee.utils.MemoryInfo;
import com.test.hwautotest.emmagee.utils.MyApplication;

/**
 * Service running in background
 * 
 */
public class EmmageeService extends Service {

	private final static String LOG_TAG = "Emmagee-"
			+ EmmageeService.class.getSimpleName();

	private WindowManager windowManager = null;
	private WindowManager.LayoutParams wmParams = null;
	private View viFloatingWindow;
	private float mTouchStartX;
	private float mTouchStartY;
	private float startX;
	private float startY;
	private float x;
	private float y;
	private int statusBarHeight;//状态栏高度。点击悬浮窗误差值
	private TextView txtTotalMem;
	private TextView txtUnusedMem;
	private TextView txtTraffic;
	private ImageView imgClose;
	private Button btnWifi;
	private int delaytime;
	private DecimalFormat fomart;
	private MemoryInfo memoryInfo;
	private WifiManager wifiManager;
	private Handler handler = new Handler();
	private CpuInfo cpuInfo;
	private String time;
	private boolean isFloating;
	private String processName, packageName, settingTempFile;
	private int pid, uid;
	private boolean isServiceStop = true;
	private String sender, password, recipients, smtp;
	private String[] receivers;
	private EncryptData des;

	public static BufferedWriter bw;
	public static FileOutputStream out;
	public static OutputStreamWriter osw;
	public static String resultFilePath;
	public static boolean isStop = true;
	
	
	private LinearLayout linLayout;
	private ImageView imgMeminfo;

	@Override
	public void onCreate() {
		Log.i(LOG_TAG, "onCreate");
		super.onCreate();
		isServiceStop = false;
		isStop = false;
		memoryInfo = new MemoryInfo();
		fomart = new DecimalFormat();
		fomart.setMaximumFractionDigits(2);
		fomart.setMinimumFractionDigits(0);
		des = new EncryptData("emmagee");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(LOG_TAG, "onStart");
		setForeground(true);
		super.onStart(intent, startId);

		pid = intent.getExtras().getInt("pid");
		uid = intent.getExtras().getInt("uid");
		processName = intent.getExtras().getString("processName");
		packageName = intent.getExtras().getString("packageName");
		settingTempFile = intent.getExtras().getString("settingTempFile");

		cpuInfo = new CpuInfo(getBaseContext(), pid, Integer.toString(uid));
		readSettingInfo(intent);
		delaytime = Integer.parseInt(time) * 1000;
		if (isFloating) {
			viFloatingWindow = LayoutInflater.from(this).inflate(
					R.layout.emmagee_floating, null);
			txtUnusedMem = (TextView) viFloatingWindow
					.findViewById(R.id.memunused);
			txtTotalMem = (TextView) viFloatingWindow
					.findViewById(R.id.memtotal);
			txtTraffic = (TextView) viFloatingWindow.findViewById(R.id.traffic);
			btnWifi = (Button) viFloatingWindow.findViewById(R.id.wifi);

			wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			if (wifiManager.isWifiEnabled()) {
				btnWifi.setText(R.string.closewifi);
			} else {
				btnWifi.setText(R.string.openwifi);
			}
			txtUnusedMem.setText("计算中,请稍后...");
			txtUnusedMem.setTextColor(android.graphics.Color.RED);
			txtTotalMem.setTextColor(android.graphics.Color.RED);
			txtTraffic.setTextColor(android.graphics.Color.RED);
			imgClose = (ImageView) viFloatingWindow.findViewById(R.id.emmagee_close);
			linLayout=(LinearLayout)viFloatingWindow.findViewById(R.id.Lin);
			imgMeminfo=(ImageView)viFloatingWindow.findViewById(R.id.emmagee_meminfo);
			createFloatingWindow();
		}
		createResultCsv();
		handler.postDelayed(task, 1000);
	}

	/**
	 * read configuration file.
	 * 
	 * @throws IOException
	 */
	private void readSettingInfo(Intent intent) {
		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream(settingTempFile));
			String interval = properties.getProperty("interval").trim();
			isFloating = "true"
					.equals(properties.getProperty("isfloat").trim()) ? true
					: false;
			sender = properties.getProperty("sender").trim();
			password = properties.getProperty("password").trim();
			recipients = properties.getProperty("recipients").trim();
			time = "".equals(interval) ? "5" : interval;
			recipients = properties.getProperty("recipients");
			receivers = recipients.split("\\s+");
			smtp = properties.getProperty("smtp");
		} catch (IOException e) {
			time = "5";
			isFloating = true;
			Log.e(LOG_TAG, e.getMessage());
		}
	}

	/**
	 * write the test result to csv format report.
	 */
	private void createResultCsv() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String mDateTime;
		if ((Build.MODEL.equals("sdk")) || (Build.MODEL.equals("google_sdk")))
			mDateTime = formatter.format(cal.getTime().getTime() + 8 * 60 * 60
					* 1000);
		else
			mDateTime = formatter.format(cal.getTime().getTime());

		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			resultFilePath = android.os.Environment
					.getExternalStorageDirectory()
					+ File.separator
					+ "TestReport"+File.separator
					+ "状态监测_结果_" 
					+ processName+"_"+mDateTime + ".csv";
		} else {
			resultFilePath = getBaseContext().getFilesDir().getPath()
					+ File.separator
					+ "TestReport"+File.separator
					+ "状态监测_结果_" 
					+  processName+"_"+mDateTime+ ".csv";
		}
		readyResultFile();
//		try {
//			File resultFile = new File(resultFilePath);
//			//创建报告目录
//			if(!resultFile.getParentFile().exists()){
//				resultFile.getParentFile().mkdir();
//			}
//				
////			Log.e(LOG_TAG, "resultFile.createNewFile()："+resultFile.createNewFile());
//			readyResultFile();
//			out = new FileOutputStream(resultFile,true);
//			osw = new OutputStreamWriter(out, "GBK");
//			bw = new BufferedWriter(osw);
//			long totalMemorySize = memoryInfo.getTotalMemory();
//			String totalMemory = fomart.format((double) totalMemorySize / 1024);
//			bw.write("指定应用的CPU内存监控情况\r\n" + "应用包名：," + packageName + "\r\n"
//					+ "应用名称: ," + processName + "\r\n" + "应用PID: ," + pid
//					+ "\r\n" + "机器内存大小(MB)：," + totalMemory + "MB\r\n"
//					+ "机器CPU型号：," + cpuInfo.getCpuName() + "\r\n"
//					+ "机器android系统版本：," + memoryInfo.getSDKVersion() + "\r\n"
//					+ "手机型号：," + memoryInfo.getPhoneType() + "\r\n" + "UID：,"
//					+ uid + "\r\n");
//			bw.write("时间" + "," + "应用占用内存PSS(MB)" + "," + "应用占用内存比(%)" + ","
//					+ " 机器剩余内存(MB)" + "," + "应用占用CPU率(%)" + "," + "CPU总使用率(%)"
//					+ "," + "流量(KB)" + "\r\n");
//		} catch (IOException e) {
//			Log.e(LOG_TAG, "resultFile异常啦！"+e.getMessage());
//		}
	}

	/**
	 * create a floating window to show real-time data.
	 */
	private void createFloatingWindow() {
		SharedPreferences shared = getSharedPreferences("float_flag",
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = shared.edit();
		editor.putInt("float", 1);
		editor.commit();
		windowManager = (WindowManager) getApplicationContext()
				.getSystemService("window");
		wmParams = ((MyApplication) getApplication()).getMywmParams();
		wmParams.type = 2002;
		wmParams.flags |= 8;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		wmParams.x = 0;
		wmParams.y = 0;
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.format = 1;
		windowManager.addView(viFloatingWindow, wmParams);
		viFloatingWindow.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				x = event.getRawX();
				y = event.getRawY();
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// state = MotionEvent.ACTION_DOWN;
					startX = x;
					startY = y;
					mTouchStartX = event.getX();
					mTouchStartY = event.getY();
					Log.d("startP", "startX" + mTouchStartX + "====startY"
							+ mTouchStartY);
					break;
				case MotionEvent.ACTION_MOVE:
					// state = MotionEvent.ACTION_MOVE;
					updateViewPosition();
					break;

				case MotionEvent.ACTION_UP:
					// state = MotionEvent.ACTION_UP;
					updateViewPosition();
//					showImg();//一直显示缩小按钮
					mTouchStartX = mTouchStartY = 0;
					break;
				}
				return true;
			}
		});

//		btnWifi.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				try {
//					btnWifi = (Button) viFloatingWindow.findViewById(R.id.wifi);
//					String buttonText = (String) btnWifi.getText();
//					String wifiText = getResources().getString(
//							R.string.openwifi);
//					if (buttonText.equals(wifiText)) {
//						wifiManager.setWifiEnabled(true);
//						btnWifi.setText(R.string.closewifi);
//					} else {
//						wifiManager.setWifiEnabled(false);
//						btnWifi.setText(R.string.openwifi);
//					}
//				} catch (Exception e) {
//					Toast.makeText(viFloatingWindow.getContext(), "操作wifi失败",
//							Toast.LENGTH_LONG).show();
//					Log.e(LOG_TAG, e.toString());
//				}
//			}
//		});
		
		//添加最小化按钮功能
		imgClose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showMemi();
			}
		});
	}

	/**
	 * show the image.
	 */
	private void showImg() {
		if (Math.abs(x - startX) < 1.5 && Math.abs(y - startY) < 1.5
				&& !imgClose.isShown()) {
			imgClose.setVisibility(View.VISIBLE);
		} else if (imgClose.isShown()) {
			imgClose.setVisibility(View.GONE);
		}
	
	}
	
	/**
	 * 关闭悬浮窗显示.
	 */
	private void showMemi() {
		if (!imgMeminfo.isShown()) {
			imgMeminfo.setVisibility(View.VISIBLE);
			linLayout.setVisibility(View.VISIBLE);
		} else if (imgMeminfo.isShown()) {
			imgMeminfo.setVisibility(View.GONE);
			linLayout.setVisibility(View.GONE);
			
		}}

	private Runnable task = new Runnable() {

		public void run() {
			Intent intent = new Intent();
			if (!isServiceStop) {
				intent.putExtra("isServiceStop", false);
				dataRefresh();
				handler.postDelayed(this, delaytime);
				
				if (isFloating)
					windowManager.updateViewLayout(viFloatingWindow, wmParams);
			} else {
				intent.putExtra("isServiceStop", true);
				intent.setAction("com.test.hwautotest.action.emmageeService");
				sendBroadcast(intent);
				stopSelf();
			}
		}
	};

	/**
	 * refresh the performance data showing in floating window.
	 * 
	 * @throws FileNotFoundException
	 * 
	 * @throws IOException
	 */
	private void dataRefresh() {
		int pidMemory = memoryInfo.getPidMemorySize(pid, getBaseContext());
		long freeMemory = memoryInfo.getFreeMemorySize(getBaseContext());
		String freeMemoryKb = fomart.format((double) freeMemory / 1024);
		String processMemory = fomart.format((double) pidMemory / 1024);
		ArrayList<String> processInfo = cpuInfo.getCpuRatioInfo();
		if (isFloating) {
			String processCpuRatio = "0";
			String totalCpuRatio = "0";
			String trafficSize = "0";
			int tempTraffic = 0;
			double trafficMb = 0;
			boolean isMb = false;
			if (!processInfo.isEmpty()) {
				processCpuRatio = processInfo.get(0);
				totalCpuRatio = processInfo.get(1);
				trafficSize = processInfo.get(2);
				if ("".equals(trafficSize) && !("-1".equals(trafficSize))) {
					tempTraffic = Integer.parseInt(trafficSize);
					if (tempTraffic > 1024) {
						isMb = true;
						trafficMb = (double) tempTraffic / 1024;
					}
				}
			}
			if ("0".equals(processMemory) && "0.00".equals(processCpuRatio)) {
				closeOpenedStream();
				isServiceStop = true;
				return;
			}
			if (processCpuRatio != null && totalCpuRatio != null) {
				txtUnusedMem.setText("占用内存:" + processMemory + "MB" + ",机器剩余:"
						+ freeMemoryKb + "MB");
				txtTotalMem.setText("占用CPU:" + processCpuRatio + "%"
						+ ",总体CPU:" + totalCpuRatio + "%");
				if ("-1".equals(trafficSize)) {
					txtTraffic.setText("本程序或本设备不支持流量统计");
				} else if (isMb)
					txtTraffic.setText("消耗流量:" + fomart.format(trafficMb)
							+ "MB");
				else
					txtTraffic.setText("消耗流量:" + trafficSize + "KB");
			}
		}
	}

	/**
	 * update the position of floating window.
	 */
	private void updateViewPosition() {
		offsetTouch();
		wmParams.x = (int) (x - mTouchStartX);
		wmParams.y = (int) (y - mTouchStartY)-statusBarHeight;
		windowManager.updateViewLayout(viFloatingWindow, wmParams);
	}

	private void offsetTouch(){
		View rootView  = viFloatingWindow.getRootView();  
        Rect r = new Rect();  
        rootView.getWindowVisibleDisplayFrame(r);  
        statusBarHeight = r.top;  
	}
	
	/**
	 * close all opened stream.
	 */
	public static void closeOpenedStream() {
		try {
			if (bw != null)
				bw.close();
			if (osw != null)
				osw.close();
			if (out != null)
				out.close();
		} catch (Exception e) {
			Log.d(LOG_TAG, e.getMessage());
		}
	}

	@Override
	public void onDestroy() {
		Log.i(LOG_TAG, "onDestroy");
		if (windowManager != null)
			windowManager.removeView(viFloatingWindow);
		handler.removeCallbacks(task);
		closeOpenedStream();
		isStop = true;
//		boolean isSendSuccessfully = false;
//		try {
//			isSendSuccessfully = MailSender.sendTextMail(sender,
//					des.decrypt(password), smtp,
//					"Emmagee Performance Test Report", "see attachment",
//					resultFilePath, receivers);
//		} catch (Exception e) {
//			isSendSuccessfully = false;
//		}
//		if (isSendSuccessfully) {
//			Toast.makeText(this, "测试结果报表已发送至邮箱:" + recipients,
//					Toast.LENGTH_LONG).show();
//		} else {
//			Toast.makeText(this,
//					"测试结果未成功发送至邮箱，结果保存在:" + EmmageeService.resultFilePath,
//					Toast.LENGTH_LONG).show();
//		}

		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	public String getFromAsset(String fileName) {
		String res = "";
		try {
			InputStream in = getResources().getAssets().open(fileName);
			int length = in.available();
			byte[] buffer = new byte[length];
			in.read(buffer);
			res = EncodingUtils.getString(buffer, "GBK");
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}  
	
	private void readyResultFile(){
//		InputStream in = getResources().getAssets().open("状态监测结果_模版xls.csv");
		InputStream input;
		try {
			///输出CSV
			//xls2.csv一般csv 能追加
			//xls.csv兼容模式-图 保持原样，未追加
			//xls.xls表格格式-图 保持原样，未追加
			
			///输出XLS
			//xls2.csv一般csv 能追加
			//xls.csv兼容模式-图 保持原样，未追加
			//xls.xls不变
			
			//图合并
			///输出XLS
			//xls2.csv一般csv 
			//xls.csv兼容模式-图 有图 无加
			//xls.xls  有图 无加
//			input = getResources().getAssets().open("xls2node.xls");
//			OutputStream output;
//			
			File resultFile = new File(resultFilePath);
			//创建报告目录
			if(!resultFile.getParentFile().exists()){
				resultFile.getParentFile().mkdir();
			}
//				
//			
//			
//			output = new FileOutputStream(resultFile);
//			byte[] temp = new byte[1024];
//			int count = 0;
//			while ((count = input.read(temp)) > 0) {
//				output.write(temp, 0, count);
//			}
//			output.flush();
//			output.close();
//			input.close();
			
			Log.e(LOG_TAG, "resultFile.createNewFile()："+resultFile.createNewFile());
			out = new FileOutputStream(resultFile,true);
			osw = new OutputStreamWriter(out, "GBK");
			bw = new BufferedWriter(osw);
			long totalMemorySize = memoryInfo.getTotalMemory();
			String totalMemory = fomart.format((double) totalMemorySize / 1024);
			bw.write("指定应用的CPU内存监控情况\r\n" + "应用包名：," + packageName + "\r\n"
					+ "应用名称: ," + processName + "\r\n" + "应用PID: ," + pid
					+ "\r\n" + "机器内存大小(MB)：," + totalMemory + "MB\r\n"
					+ "机器CPU型号：," + cpuInfo.getCpuName() + "\r\n"
					+ "机器android系统版本：," + memoryInfo.getSDKVersion() + "\r\n"
					+ "手机型号：," + memoryInfo.getPhoneType() + "\r\n" + "UID：,"
					+ uid + "\r\n");
			bw.write("时间" + "," + "应用占用内存PSS(MB)" + "," + "应用占用内存比(%)" + ","
					+ " 机器剩余内存(MB)" + "," + "应用占用CPU率(%)" + "," + "CPU总使用率(%)"
					+ "," + "流量(KB)" + "\r\n");
		} catch (IOException e) {
			Log.e(LOG_TAG, "resultFile异常啦！"+e.getMessage());
		}

		
	}
	
}