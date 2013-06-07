package com.test.hwautotest.reboot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import com.android.internal.telephony.ITelephony;
import com.test.utils.Utils;


public class RebootUtils extends Utils{ 
	
	private Context mContext;
	private String STATE;
	
	public RebootUtils(Context mContext) {
		super(mContext);
		// TODO Auto-generated constructor stub
	}

	
	
	/**
	 * 获取文件名字
	 * @return
	 */
	public String fileName(){
		String PATH = getSdPath()+"/Reboot";
		File road =  new File(PATH);
		if(!road.exists()){
			road.mkdirs();
		}
		 return PATH+"/"+"Reboot_"+System.currentTimeMillis()+".txt";
	}
	
	/**
	 * 手机执行重启
	 */
	public void reboot() {
		Intent reboot = new Intent(Intent.ACTION_REBOOT);
		reboot.setAction("android.intent.action.REBOOT");
		reboot.putExtra("nowait", 1);
		reboot.putExtra("interval", 1);
		reboot.putExtra("window", 0);
		mContext.sendBroadcast(reboot);
	}
	
	/**
	 * 
	 * 写入结果到文件中
	 * 
	 * @param fileName
	 * @param content
	 */
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

	/**
	 * 获取SIM卡状态
	 * @return
	 */
	public String getSimState() {
		TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);// 取得相关系统服务
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

	/**
	 * 
	 * 获取内存可读可写状态
	 * @return
	 */
	public boolean IsCanUseMemory(String path) {
		
		File file =  new File(path);
		return file.canWrite() && file.canRead();
 
	}
	
	/**
	 * 
	 * 获取SIM卡的网络状态
	 * @return
	 */
	public boolean getNetType(){
		
		boolean isConnent = false;
		TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);// 取得相关系统服务
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
				if ((phone.getNetworkType() == TelephonyManager.NETWORK_TYPE_CDMA) || (phone.getNetworkType() == TelephonyManager.NETWORK_TYPE_EDGE) 
						|| (phone.getNetworkType() == TelephonyManager.NETWORK_TYPE_GPRS ) || (phone.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSDPA)
						||(phone.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS)||(phone.getNetworkType() == TelephonyManager.NETWORK_TYPE_EVDO_0)
						||(phone.getNetworkType() == TelephonyManager.NETWORK_TYPE_EVDO_A )||(phone.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSPA)
						||(phone.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSUPA ) ||(phone.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSPAP)){
					
					if(!tm.getNetworkOperator().equals("")){
						isConnent = true; 
					}
						
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		}
		return isConnent;
	}
	
	

	
}



