package com.test.hwautotest.reboot;

import java.util.Timer;
import java.util.TimerTask;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

@SuppressLint("HandlerLeak")
public class BootService extends Service {
	private SharedPreferences prefs;
	private int rebootTimes;
	private int count;
	private boolean isReboot;
	private String ISREBOOT = "isRoot";
	private String REBOOT_TIMES = "reboot_times";
	private String COUNT = "count";
	private String FILENAME = "filename";
	private String fileName;
	public String content;
	private String ISSTOPSTORAGE = "isStopStorage";
	private String ISSTOPNETWORK = "isStopNetwork";
	boolean isStopStorage;
	boolean isStopNetWork;
	protected static final int LOGINOVER = 0;
	protected static final int UNKNOW = 1;
	protected static final int STOP = 2;
	private Handler handler;
	private String strength = "null";
	Timer timer = new Timer();
	RebootUtils mRebootUtils = new RebootUtils(this);
	private int recLen = 60; 
	private TelephonyManager telephoneManager;

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
		telephoneManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		
		read_status();
		HandlerThread myThread = new HandlerThread("myHandlerThread");
		myThread.start();
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				if (msg.what == LOGINOVER) {
					if (count > 0) {
						boolean isGetType = mRebootUtils.getNetType();
						boolean isCanUseSdCard = mRebootUtils.IsCanUseSdCard();
						content = count + "/" + isGetType +"/"+strength+ "/" +
								mRebootUtils.getSimState() + "/" + isCanUseSdCard;
						
						if(isGetType == false && isStopNetWork == true){
							rebootTimes = 0;
						}else if (isCanUseSdCard == false && isStopStorage == true){
							rebootTimes = 0;
						}else if (isScreenLocked(BootService.this) == true){
							rebootTimes = 0;
						}
						
						Log.i("look", content);
						mRebootUtils.writeFile(fileName, content);
					}
					
					if (isReboot) {
						if (rebootTimes > 0) {
							mRebootUtils.reboot();
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
//							
						}
					} else {
						stopSelf();
					}
					Log.i("look", "BootService end");

				}
			}
		};
		
		if(!mRebootUtils.getNetType()){
			timer.schedule(timeTask, 1000, 1000);
		}else{
			timeTask.run();
		}

	}
	
	
	TimerTask timeTask = new TimerTask() {
		

		@Override
		public void run() {
			Log.i("look", Thread.currentThread().getName());
			recLen--;
			Log.i("look", "recLen = "+recLen);
			if(count != 0){
				if(recLen > 0){
					Log.i("look", "getNetType = "+mRebootUtils.getNetType());
					if(mRebootUtils.getNetType()){
						handler.sendMessageDelayed(handler.obtainMessage(LOGINOVER),5000);
						telephoneManager.listen(phoneStateListener,  
				                PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
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
	
	PhoneStateListener phoneStateListener = new PhoneStateListener() {

		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			// TODO Auto-generated method stub
			super.onSignalStrengthsChanged(signalStrength);
			if (signalStrength.isGsm()) {
				int ASU = signalStrength.getGsmSignalStrength();
				strength =String.valueOf(-113+(2*ASU))+"dBm";
			}else{
				strength = String.valueOf(signalStrength.getCdmaDbm()+"dBm");
			}
			Log.i("look","strength: "+strength);
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
		this.isStopNetWork = prefs.getBoolean(ISSTOPNETWORK, false);
		this.isStopStorage = prefs.getBoolean(ISSTOPSTORAGE, false);
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
	
	 
}
