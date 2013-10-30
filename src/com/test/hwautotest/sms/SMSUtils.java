package com.test.hwautotest.sms;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import com.test.utils.*;


public class SMSUtils extends Utils{
	

	public Context mContext;
	RandomUtils mRandomUtils = new RandomUtils();
	public SMSUtils(Context mContext) {
		super(mContext);
		this.mContext = mContext;
		// TODO Auto-generated constructor stub
	}
	/**
	 * 方法名：获取手机短信数量
	 * 
	 * @return
	 */
	public int getCount() {
		ContentResolver CR = mContext.getContentResolver();
		Uri uri = Uri.parse("content://sms");
		Cursor cursor = CR.query(uri, null, null, null, null);
		int amount = cursor.getCount();
		return amount;
	}
	
	public void DisplayToast(String string) {
		// TODO Auto-generated method stub
		Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 插入一条随机短信
	 * 
	 * 
	 */
	public void insertSms(int number,int read,int type) {
		final String ADDRESS = "address";
		final String BODY = "body";
		ContentValues values = new ContentValues();
		values.put(ADDRESS, mRandomUtils.numberRandom()); /* 接收的号码 */
		values.put("read", read);
		values.put("type", type); /* 1INBOX收件箱／2SENT发件箱 ,3DRAG草稿箱*/
		values.put(BODY,mRandomUtils.contentRandom(number)); /* smsbody *//* insert sql */
		mContext.getContentResolver().insert(Uri.parse("content://sms"), values);
	}
	
	/**
	 * 插入一条自定义短信
	 * 
	 * 
	 */
	public void insertSms(int number,int read,int type,String phone) {
		final String ADDRESS = "address";
		final String BODY = "body";
		ContentValues values = new ContentValues();
//		values.put(ADDRESS, mRandomUtils.numberRandom()); /* 发送的号码 */
		values.put(ADDRESS, phone); /* 发送的号码 */
		values.put("read", read);
		values.put("type", type); /* 1INBOX收件箱／2SENT发件箱 ,3DRAG草稿箱*/
		values.put(BODY,mRandomUtils.contentRandom(number)); /* smsbody *//* insert sql */
		mContext.getContentResolver().insert(Uri.parse("content://sms"), values);
	}
	
	
	
}



