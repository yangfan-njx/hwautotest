package com.test.hwautotest.reboot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TimerTask;

import android.R.string;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.preference.PreferenceManager;
import android.sax.StartElementListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {
	public Context mContext;
	private Handler handler;
	protected static final String TAG = "look";
	protected static final int LOGINOVER = 0;

	// public String START = "start";
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("look", "service start");
		
		Intent i = new Intent(context, BootService.class);
		context.startService(i);
	 }
}

		
