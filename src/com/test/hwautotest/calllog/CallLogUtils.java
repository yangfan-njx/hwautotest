package com.test.hwautotest.calllog;

import java.util.Random;

import com.test.utils.RandomUtils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.widget.Toast;

public class CallLogUtils {
	public Context mContext;
	
	public CallLogUtils(Context mContext){
		this.mContext = mContext;
	}
	/**
	 * 方法名：获取通话记录的数量
	 * 
	 * @return
	 */
	public int getCount() {
		ContentResolver CR = mContext.getContentResolver();
		Uri uri = CallLog.Calls.CONTENT_URI;
		Cursor cursor = CR.query(uri, null, null, null, null);
		int amount = cursor.getCount();
		return amount;
	}
	
	public void DisplayToast(String string) {
		// TODO Auto-generated method stub
		Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 方法名:插入通话记录　(来电：CallLog.Calls.INCOMING_TYPE (常量值：1)
	 * 已拨：CallLog.Calls.OUTGOING_TYPE (常量值：2) 未接：CallLog.Calls.MISSED_TYPE
	 * (常量值：3))
	 * 
	 * @param number
	 *            　 输入号码
	 * @param date
	 *            　 输入呼入呼出时间
	 * @param type
	 *            　 1来电2已拔3未接
	 * @param read
	 *            　 0已看1未看　
	 * @param time
	 *      呼入呼出时长
	 * @return 
	 */
	public void insertCallLog(int type, Context mContext) {
		// TODO Auto-generated method stub
			String read = "0";
			ContentValues localContentValues = new ContentValues();
			Random random1 = new Random();
			RandomUtils mRandomUtils = new RandomUtils();
			localContentValues.put("number",mRandomUtils.numberRandom());
			localContentValues.put("date",Long.valueOf(System.currentTimeMillis()));
			
			localContentValues.put("duration", String.valueOf(random1.nextInt(3600)));
			localContentValues.put("type", type);
			if(type == 3){
				read = "1";
			}
			localContentValues.put("new", "0");
			localContentValues.put("is_read", read);
			mContext.getContentResolver().insert(CallLog.Calls.CONTENT_URI,
			localContentValues);
			
		

	}
}
