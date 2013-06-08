package com.test.hwautotest.reboot;

import java.util.Timer;
import java.util.TimerTask;

import com.android.internal.telephony.ITelephony;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.gemini.GeminiPhone;

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
import android.os.RemoteException;
import android.os.ServiceManager;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

@SuppressLint("HandlerLeak")
public class BootService extends Service {
	private SharedPreferences prefs;
	private Handler handler;
	private int rebootTimes;
	private int count;
	private String ISSDEXIST = "isSdExist";
	private String ISREBOOT = "isRoot";
	private String REBOOT_TIMES = "reboot_times";
	private String COUNT = "count";
	private String FILENAME = "filename";
	private String fileName;
	private String content;
	private String ISSTOPSD = "isStopSd";
	private String ISSTOPINTRENAL = "isStopInternal";
	private String ISSTOPSIM1NETWORK = "isStopSim1Network";
	private String ISSTOPSIM2NETWORK = "isStopSim2Network";
	private String strengthSim1 = "null";
	private String strengthSim2 = "null";
	private String SDStatus;
	private String InternalStatus;
	private String sim1Status;
	private String sim2Status;
	private boolean isStopSd;
	private boolean isStopInternal;
	private boolean isStopSim1NetWork;
	private boolean isStopSim2NetWork;
	private boolean isGetSim1Type;
	private boolean isGetSim2Type;
	private boolean isCanUseSdCard;
	private boolean isCanUseInternal;
	private boolean isReboot;
	private boolean isSdExist;
	boolean isSim1Insert;
	boolean isSim2Insert;
	protected static final int LOGINOVER = 0;
	protected static final int UNKNOW = 1;
	protected static final int STOP = 2;
	
	
	Timer timer = new Timer();
	RebootUtils mRebootUtils = new RebootUtils(BootService.this);
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
//		GeminiPhone mGeminiPhone = (GeminiPhone)PhoneFactory.getDefaultPhone();
		ITelephony phone = (ITelephony)ITelephony.Stub.asInterface(ServiceManager.getService("phone"));
		
		
		try {
			isSim2Insert = phone.isSimInsert(1);
			isSim1Insert = phone.isSimInsert(0);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		isGetSim1Type = mRebootUtils.getNetType(0);
		isGetSim2Type = mRebootUtils.getNetType(1);
		
		read_status();
		HandlerThread myThread = new HandlerThread("myHandlerThread");
		myThread.start();
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				if (msg.what == LOGINOVER) {
					if (count > 0) {
						isGetSim1Type = mRebootUtils.getNetType(0);
						isGetSim2Type = mRebootUtils.getNetType(1);
						
						isCanUseSdCard = mRebootUtils.IsCanUseMemory(mRebootUtils.getSdPath());
						isCanUseInternal = mRebootUtils.IsCanUseMemory(mRebootUtils.getInternalPath());
						strengthSim1 = mRebootUtils.getStrength(0);
						strengthSim2 = mRebootUtils.getStrength(1);
						sim1Status = String.valueOf(isGetSim1Type);
						sim2Status = String.valueOf(isGetSim2Type);
						SDStatus = String.valueOf(isCanUseSdCard);
						InternalStatus = String.valueOf(isCanUseInternal);
						
						if(!isSdExist){
							SDStatus = "null";
						}else if(!mRebootUtils.getStatus(isSdExist) && isSdExist){
							SDStatus = "false";
						}
						
						if(!isSim1Insert){
							sim1Status = "null";
						}else if (!isSim2Insert){
							sim2Status = "null";
						}
						
						content = count + "/" + sim1Status +"/"+strengthSim1+ "/"+ mRebootUtils.getSimState(0)+"/" + sim2Status +"/"+strengthSim2+ "/"
								+ mRebootUtils.getSimState(1) + "/" + InternalStatus + "/" + SDStatus;
						
						if(isGetSim1Type == false && isStopSim1NetWork == true){
							rebootTimes = 0;
						}else if(isGetSim2Type == false && isStopSim2NetWork == true){
							rebootTimes = 0;
						}else if (isCanUseSdCard == false && isStopSd == true){
							rebootTimes = 0;
						}else if (isCanUseInternal == false && isStopInternal == true){
							rebootTimes = 0;
						}else if (mRebootUtils.isScreenLocked(BootService.this) == true){
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
		
		if((!isGetSim1Type && isSim1Insert) 
				|| (!isGetSim2Type && isSim2Insert)){
			timer.schedule(timeTask, 1000, 1000);
		}else {
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
					Log.i("look", "getNetType = "+mRebootUtils.getNetType(0));
					isGetSim1Type = mRebootUtils.getNetType(0);
					isGetSim2Type = mRebootUtils.getNetType(1);
					if((!isSim1Insert && isGetSim2Type) || (!isSim2Insert && isGetSim1Type) 
							|| (isSim1Insert && isSim2Insert && isGetSim1Type &&isGetSim2Type)){
							handler.sendMessageDelayed(handler.obtainMessage(LOGINOVER),5000);
							timer.cancel(); 
							Log.i("look","Choice 2");
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
	
	
	
	
	private void read_status() {
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.rebootTimes = prefs.getInt(REBOOT_TIMES, 1);
		this.isReboot = prefs.getBoolean(ISREBOOT, true);
		this.count = prefs.getInt(COUNT, 0);
		this.fileName = prefs.getString(FILENAME, null);
		this.isStopSim1NetWork = prefs.getBoolean(ISSTOPSIM1NETWORK, false);
		this.isStopSim2NetWork = prefs.getBoolean(ISSTOPSIM2NETWORK, false);
		this.isStopSd = prefs.getBoolean(ISSTOPSD, false);
		this.isStopInternal = prefs.getBoolean(ISSTOPINTRENAL, false);
		this.isSdExist = prefs.getBoolean(ISSDEXIST, false);
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
