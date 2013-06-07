package com.test.hwautotest.reboot;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.test.hwautotest.R;

public class RebootActivity extends Activity implements OnClickListener {
	private Button btnReboot;
	private CheckBox stopSd;
	private CheckBox stopInternal;
	private CheckBox stopNetwork;
	boolean isStopStorage;
	boolean isStopNetWork;
	private SharedPreferences prefs;
	private int rebootTimes ;
	private int count = 0;
	private boolean isReboot = false;
	private String FILENAME = "filename";
	private String times;
	private String REBOOT_TIMES = "reboot_times";
	private String ISREBOOT = "isRoot";
	private String COUNT = "count";
	private String fileName;
	private String ISSTOPSTORAGE = "isStopStorage";
	private String ISSTOPNETWORK = "isStopNetwork";
	private EditText timesInput;
	RebootUtils mRebootUtils = new RebootUtils(this);
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.reboot);
		Log.i("look","boot Start");
		
		btnReboot = (Button) findViewById(R.id.reboot);
		timesInput = (EditText) findViewById(R.id.TimeInput);
		stopNetwork = (CheckBox) findViewById(R.id.stopNetWork);
		stopSd = (CheckBox) findViewById(R.id.stopSd);
		btnReboot.setOnClickListener(this);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);  
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try {
			times = timesInput.getText().toString();
			rebootTimes = Integer.parseInt(times);
			Log.i("look","rebootTimes: "+rebootTimes);
			isStopNetWork = stopNetwork.isChecked();
			isStopStorage = stopStorage.isChecked();
			isReboot = true;
			fileName = mRebootUtils.fileName();
			save_status(rebootTimes,isReboot,fileName);
			Intent i = new Intent(RebootActivity.this,BootService.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startService(i);
			RebootActivity.this.finish();
		} catch (Exception e) {
			// TODO: handle exception
			if (times != "") {
				DisplayToast("请输入整数");
			}
		}
		
	}
	 
	  
	public void save_status(int rebootTimes,boolean isReboot,String fileName) {  
	        Editor editor = prefs.edit();  
	        editor.putInt(REBOOT_TIMES, rebootTimes);  
	        editor.putBoolean(ISREBOOT, isReboot);
	        editor.putBoolean(ISSTOPNETWORK, isStopNetWork);
	        editor.putBoolean(ISSTOPSTORAGE, isStopStorage);
	        editor.putString(FILENAME, fileName);
	        
	        editor.putInt(COUNT, count);
	        editor.commit();  
	   } 
	
	public void DisplayToast(String string) {
		// TODO Auto-generated method stub
		Toast.makeText(RebootActivity.this, string, Toast.LENGTH_SHORT).show();
	}


}
