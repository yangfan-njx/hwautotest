package com.test.hwautotest.calllog;

import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

import com.test.utils.RandomUtils;
import com.test.utils.Utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

public class CallLogUtils extends Utils {
	

	public Context mContext;
	public CallLogUtils(Context mContext) {
		super(mContext);
		this.mContext = mContext;
		// TODO Auto-generated constructor stub
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
	
	
	/**
	 * 插入一条随机通话记录　
	 * (来电：CallLog.Calls.INCOMING_TYPE (常量值：1)
	 * 已拨：CallLog.Calls.OUTGOING_TYPE (常量值：2) 
	 * 未接：CallLog.Calls.MISSED_TYPE(常量值：3))
	 * 
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
//			localContentValues.put("simid", 0);
			mContext.getContentResolver().insert(CallLog.Calls.CONTENT_URI,
			localContentValues);
			
		

	}
	
	
	
	/**
	 * 插入一条自定义发送数据的记录
	 * @param type
	 * @param mContext
	 * @param phone	发送号码
	 * @param year 
	 * @param mon
	 * @param day
	 * @param hour
	 * @param min
	 */
	public void insertCallLog(int type, Context mContext,String phone,int year,int mon,int day,int hour,int min) {
		// TODO Auto-generated method stub
			String read = "0";
			Time time=new Time();
			
			ContentValues localContentValues = new ContentValues();
			Random random1 = new Random();
			RandomUtils mRandomUtils = new RandomUtils();
			
			//号码
			if(phone==null || phone.equals("")){
				localContentValues.put("number",mRandomUtils.numberRandom());
			}else{
				Log.i("look", "自定义号码："+phone);
				localContentValues.put("number",phone);
			}
			
			
			//日期和时间
			if(year>=0 & mon>=0 & day>=0 & (hour<0 | min<0) ){//日期设定，时间当前
				Log.i("look", "日期设定，时间当前");
				time.setToNow();
				int dateH=time.hour;
				int dateMIN=time.minute;
				time.set(0,dateMIN,dateH,day, mon, year);
				 Log.i("look", "time h:"+ time.hour+" time m:"+time.minute);
			}else if(year<0 | mon<0 | day<0 & (hour>=0 & min>=0) ){//日期当前，时间设定
				Log.i("look", "日期当前，时间设定");
				time.setToNow();
				int dateD=time.monthDay;
				int dateM=time.month;
				int dateY=time.year;
				time.set(0, min, hour, dateD, dateM, dateY);
			}else if(year>=0 & mon>=0 & day>=0 & (hour>=0 & min>=0) ){//日期时间都设定
				Log.i("look", "日期时间都设定");
				time.setToNow();
				time.set(0, min, hour, day, mon, year);
			}else{//日期时间都当前
				Log.i("look", "日期时间都当前");
				time.setToNow();
			}
//			localContentValues.put("date",Long.valueOf(System.currentTimeMillis()));
			localContentValues.put("date",Long.valueOf(time.toMillis(false)));
			
//			localContentValues.put("duration", String.valueOf(random1.nextInt(3600)));//1单位秒
			localContentValues.put("duration", Math.random()*(System.currentTimeMillis()-time.toMillis(false))/1000);//通话时长。以当前时间到设定时间之间随机
			
			localContentValues.put("type", type);
			if(type == 3){
				read = "1";
			}
			localContentValues.put("new", "0");
			localContentValues.put("is_read", read);
			//simid 0是卡1，1是卡2。没对应序号的卡在卡槽，不能显示成图标
//			localContentValues.put("simid", 1);
			mContext.getContentResolver().insert(CallLog.Calls.CONTENT_URI,
			localContentValues);
			
		

	}
}
