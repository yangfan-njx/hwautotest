
package com.test.hwautotest.reboot;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.gemini.GeminiPhone;
import com.test.hwautotest.R;

public class RebootActivity extends Activity implements OnClickListener {
	private Button btnReboot;
	private CheckBox stopSd;
	private CheckBox stopInternal;
	private CheckBox stopSim1Network;
	private CheckBox stopSim2Network;
	boolean isStopSd;
	boolean isStopInternal;
	boolean isStopSim1NetWork;
	boolean isStopSim2NetWork;
	private SharedPreferences prefs;
	private int rebootTimes = 0;
	private int count = 0;
	private boolean isReboot = false;
	private boolean isSdExist;
	private boolean isWriteLog = false;
	boolean isSim1Insert;
	boolean isSim2Insert;
	private String ISSDEXIST = "isSdExist";
	private String FILENAME = "filename";
	private String times;
	private String REBOOT_TIMES = "reboot_times";
	private String ISREBOOT = "isRoot";
	private String COUNT = "count";
	private String ISWRITELOG = "isWriteLog";
	private String fileName;
	private String ISSTOPSD = "isStopSd";
	private String ISSTOPINTRENAL = "isStopInternal";
	private String ISSTOPSIM1NETWORK = "isStopSim1Network";
	private String ISSTOPSIM2NETWORK = "isStopSim2Network";
	private GeminiPhone mGeminiPhone;
	private EditText timesInput;
	RebootUtils mRebootUtils = new RebootUtils(this);
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reboot);
		Log.i("look","boot Start");
//		GeminiPhone mGeminiPhone = (GeminiPhone)PhoneFactory.getDefaultPhone();
		btnReboot = (Button) findViewById(R.id.reboot);
		timesInput = (EditText) findViewById(R.id.TimeInput);
		stopSim1Network = (CheckBox) findViewById(R.id.stopSim1NetWork);
		stopSim2Network = (CheckBox) findViewById(R.id.stopSim2NetWork);
		stopSd = (CheckBox) findViewById(R.id.stopSd);
		stopInternal = (CheckBox) findViewById(R.id.stopInternal);
		btnReboot.setOnClickListener(this);
		prefs = PreferenceManager.getDefaultSharedPreferences(this); 
		ITelephony phone = (ITelephony)ITelephony.Stub.asInterface(ServiceManager.getService("phone"));
		isSim2Insert =mRebootUtils.isSimInsert(1);
		isSim1Insert =mRebootUtils.isSimInsert(0);
		isSdExist = mRebootUtils.isSdExists();
		
		stopSd.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(stopSd.isChecked() && !isSdExist){
					mRebootUtils.DisplayToast("未插入SD卡");
					stopSd.setChecked(false);
				}
			}
		});
		
		stopSim1Network.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				Log.i("look",stopSim1Network.isChecked()+ " "+isSim1Insert+"");
				if(stopSim1Network.isChecked() && !isSim1Insert){
					mRebootUtils.DisplayToast("未插入SIM1卡");
					stopSim1Network.setChecked(false);
				}
			}
		});
		
		stopSim2Network.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(stopSim2Network.isChecked() && !isSim2Insert){
					mRebootUtils.DisplayToast("未插入SIM2卡");
					stopSim2Network.setChecked(false);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try {
			times = timesInput.getText().toString();
			rebootTimes = Integer.parseInt(times);
			Log.i("look","rebootTimes: "+rebootTimes);
			isStopSim1NetWork = stopSim1Network.isChecked();
			isStopSim2NetWork = stopSim2Network.isChecked();
			isStopSd = stopSd.isChecked();
			isStopInternal = stopInternal.isChecked();
			isReboot = true;
			isWriteLog = true;
			fileName = mRebootUtils.fileName();
			save_status(rebootTimes,isReboot,fileName);
			Intent i = new Intent(RebootActivity.this,BootService.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Log.i("look","isReboot: "+isReboot);
			startService(i);
			RebootActivity.this.finish();
		} catch (Exception e) {
			// TODO: handle exception
			if (times != "") {
				mRebootUtils.DisplayToast("请输入整数");
			}
		}
		
	}
	 
	  
	public void save_status(int rebootTimes,boolean isReboot,String fileName) {  
	        Editor editor = prefs.edit();  
	        editor.putInt(REBOOT_TIMES, rebootTimes);  
	        editor.putBoolean(ISREBOOT, isReboot);
	        editor.putBoolean(ISSTOPSIM1NETWORK, isStopSim1NetWork);
	        editor.putBoolean(ISSTOPSIM2NETWORK, isStopSim2NetWork);
	        editor.putBoolean(ISSTOPSD, isStopSd);
	        editor.putBoolean(ISSTOPINTRENAL, isStopInternal);
	        editor.putBoolean(ISSDEXIST, isSdExist);
	        editor.putString(FILENAME, fileName);
	        editor.putBoolean(ISWRITELOG, isWriteLog);
	        editor.putInt(COUNT, count);
	        editor.commit();  
	   } 
	
	


}

