package com.test.hwautotest.reboot;

import java.io.BufferedWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.android.internal.telephony.ITelephony;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class BootService extends Service {
	private KeyguardManager mKeyguard;
	private SharedPreferences prefs;
	private int rebootTimes;
	private int count;
	private boolean isReboot;
	private String ISREBOOT = "isRoot";
	private String REBOOT_TIMES = "reboot_times";
	private String COUNT = "count";
	private String FILENAME = "filename";
	private String fileName;
	private String STATE;
	public String content;
	protected static final int LOGINOVER = 0;
	protected static final int UNKNOW = 1;
	protected static final int STOP = 2;
	private Handler handler;
	private boolean SimStatus;
	Timer timer = new Timer(); 
	private int recLen = 30; 

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.i("look", "BootService Start");
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		read_status();
		HandlerThread myThread = new HandlerThread("myHandlerThread");
		myThread.start();
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				if (msg.what == LOGINOVER) {
					if (count > 0) {
						if(getSimState().equals("良好")){
							SimStatus = true;
						}else{
							SimStatus = false;		
						}
						content = count + "/" + getNetType() + "/" +
								getSimState() + "/" + IsCanUseSdCard();
						Log.i("look", content);
						writeFile(fileName, content);
					}
					if (isReboot) {
						if (rebootTimes > 0) {
							reboot();
							rebootTimes = rebootTimes - 1;
							count = count + 1;
							save_status();

						} else {
							Log.i("look", "0");
							Editor editor = prefs.edit();
							isReboot = false;
							editor.putBoolean(ISREBOOT, isReboot);
							editor.commit();
							stopSelf();
						}
					} else {
						stopSelf();
					}
					Log.i("look", "BootService end");

				}
			}
		};
		
		if(!getNetType()){
			timer.schedule(timeTask, 1000, 1000);
		}else{
			timeTask.run();
		}
//		handler.removeCallbacks(myThread);
	}

	TimerTask timeTask = new TimerTask() {
		@Override
		public void run() {
			Log.i("look", Thread.currentThread().getName());
			recLen--;
			Log.i("look", "recLen = "+recLen);
//			if(isScreenLocked(getApplicationContext()))
			if(count != 0){
				if(recLen > 0){
					Log.i("look", "getNetType = "+getNetType());
					if(getNetType()){
						handler.sendMessageDelayed(handler.obtainMessage(LOGINOVER),3000);
						timer.cancel(); 
						Log.i("look","Choice 2");
					}else {
						handler.sendMessage(handler.obtainMessage(UNKNOW));
						Log.i("look","Choice 3");
					}
				}else{
					handler.sendMessage(handler.obtainMessage(LOGINOVER));
					timer.cancel(); 
					Log.i("look","Choice 4");
				}
			}else{
				handler.sendMessage(handler.obtainMessage(LOGINOVER));
				timer.cancel();
				Log.i("look","Choice 5");
			}
		}
		
			
	};
	
	public final static boolean isScreenLocked(Context c) {
        KeyguardManager mKeyguardManager = (KeyguardManager) c.getSystemService(Context.KEYGUARD_SERVICE);
        return !mKeyguardManager.inKeyguardRestrictedInputMode();
    }
	private void read_status() {
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.rebootTimes = prefs.getInt(REBOOT_TIMES, 1);
		this.isReboot = prefs.getBoolean(ISREBOOT, true);
		this.count = prefs.getInt(COUNT, 0);
		this.fileName = prefs.getString(FILENAME, null);
		Log.i("look", "rebootTimes: " + rebootTimes);
		Log.i("look", "fileName: " + fileName);
		Log.i("look", "count: " + count);
	}

	private void save_status() {
		Editor editor = prefs.edit();
		editor.putInt(REBOOT_TIMES, rebootTimes);
		editor.putInt(COUNT, count);
		editor.putString(FILENAME, fileName);
		editor.putBoolean(ISREBOOT, isReboot);
		editor.commit();
	}

	private void reboot() {
		Intent reboot = new Intent(Intent.ACTION_REBOOT);
		reboot.setAction("android.intent.action.REBOOT");
		reboot.putExtra("nowait", 1);
		reboot.putExtra("interval", 1);
		reboot.putExtra("window", 0);
		sendBroadcast(reboot);
	}

	public void writeFile(String fileName, String content) {
		try {
			FileWriter fw = new FileWriter(fileName, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.newLine();
			bw.flush();
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String getSimState() {
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);// 取得相关系统服务
		System.out.println(tm.getSimState());
		switch (tm.getSimState()) { // getSimState()取得sim的状态 有下面6中状态
		case TelephonyManager.SIM_STATE_ABSENT:
			STATE = "无卡";
			break;
		case TelephonyManager.SIM_STATE_UNKNOWN:
			STATE = "未知状态";
			break;
		case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
			STATE = "需要NetworkPIN解锁";
			break;
		case TelephonyManager.SIM_STATE_PIN_REQUIRED:
			STATE = "需要PIN解锁";
			break;
		case TelephonyManager.SIM_STATE_PUK_REQUIRED:
			STATE = "需要PUK解锁";
			break;
		case TelephonyManager.SIM_STATE_READY:
			STATE = "良好";
			break;
		}
		return STATE;
	}

	public boolean IsCanUseSdCard() {
		 
		try {
			return Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean getNetType(){
		
		boolean isConnent = false;
		ITelephony phone = (ITelephony)ITelephony.Stub.asInterface(ServiceManager.getService("phone"));
		int i;
		try {
			i = phone.getNetworkType();
			Log.i("look","net: "+ i);
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(getSimState().equals("良好")){
			try {
				if ((phone.getNetworkType() == 2) || (phone.getNetworkType() == 1) || (phone.getNetworkType() == 8) ||(phone.getNetworkType() == 10)){
					isConnent = true;
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		}
		return isConnent;
	}
	
	
}
