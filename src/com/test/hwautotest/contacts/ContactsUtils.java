package com.test.hwautotest.contacts;

import com.test.utils.RandomUtils;
import com.test.utils.Utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.SipAddress;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.util.Log;
import android.widget.Toast;

public class ContactsUtils extends Utils{
	
	private Context mContext;
	
	RandomUtils mRandonUtils = new RandomUtils();
	
	public ContactsUtils(Context mContext) {
		super(mContext);
		this.mContext = mContext;
		// TODO Auto-generated constructor stub
	}
	

	/**
	 * 方法名：获取手机联系人数量
	 * 
	 * @return
	 */
	public int getCount() {
		ContentResolver CR = mContext.getContentResolver();
		Uri uri = RawContacts.CONTENT_URI;
		Cursor cursor = CR.query(uri, null, null, null, null);
		int amount = cursor.getCount();
		return amount;
	}

	/**
	 * 方法名：添加一名随机联系人
	 * 
	 * @param number
	 */
	public void addContacts(String language, boolean addEmail,boolean addNotes ,
			boolean addAddress ,boolean addCompany ,boolean addIM ,boolean addNickName,boolean addWebsite) {

			String name = null;
			ContentValues values = new ContentValues();
			
			values.put(RawContacts.ACCOUNT_TYPE, "Local Phone Account");
			values.put(RawContacts.ACCOUNT_NAME, "Phone");
			Uri rawContactUri = mContext.getContentResolver().insert(RawContacts.CONTENT_URI, values);
			long rawContactId = ContentUris.parseId(rawContactUri);
//			mContext.getContentResolver().insert(Data.CONTENT_URI, values);
			
			//姓名
			values.clear();
			values.put(Data.RAW_CONTACT_ID, rawContactId);
			
			values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
			if (language.equals("chinese")) {
				name = mRandonUtils.chineseNameRandom();
			} else if(language.equals("english")){
				name = mRandonUtils.englishNameRandom();
			}else if(language.equals("chineseandenglish")){
				name = mRandonUtils.chineseandenglishNameRandom();
			}

			values.put(StructuredName.GIVEN_NAME, name);
			mContext.getContentResolver().insert(Data.CONTENT_URI, values);
			
			addEmail(rawContactId,addEmail, values);//
			addCompany(rawContactId,addCompany, values);//
			addAddress(rawContactId, addAddress, values);
			addWebsite(rawContactId, addWebsite, values);//
			addnickname(rawContactId, addNickName, values);//
			addNotes(rawContactId, addNotes, values);//
			addIM(rawContactId, addIM, values);//
//			addEmail(addEmail,values);
//			addCompany(addCompany,values);
//			addAddress(addAddress,values);
//			addWebsite(addWebsite,values);
//			addnickname(addNickName,values);
//			addNotes(addNotes,values);
//			addIM(addIM,values);
			
//			
			//号码
			values.clear();
			values.put(Data.RAW_CONTACT_ID, rawContactId);//加上后号码和姓名才出现，但速度也变慢了
			values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
			values.put(Phone.TYPE, Phone.TYPE_HOME);
			values.put(Phone.NUMBER,mRandonUtils.numberRandom());
			mContext.getContentResolver().insert(Data.CONTENT_URI, values);
			
			
		
	}
	/**
	 * 添加email地址
	 * @param rawContactId
	 * @param addEmail
	 */
	public void addEmail(long rawContactId,boolean addEmail,ContentValues values){
		if(addEmail){
			values.clear();
//			 values = new ContentValues();
			values.put(Data.RAW_CONTACT_ID, rawContactId);
			values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
			values.put(Email.ADDRESS, mRandonUtils.emailRandom());
			System.out.println(mRandonUtils.emailRandom());
			mContext.getContentResolver().insert(Data.CONTENT_URI, values);
			
		}
	}
	
	/**
	 * 添加备注
	 * @param rawContactId
	 * @param addNotes
	 */
	public void addNotes(long rawContactId , boolean addNotes,ContentValues values){
		if(addNotes){
			values.clear();
//			 values = new ContentValues();
			values.put(Data.RAW_CONTACT_ID, rawContactId);
			values.put(Data.MIMETYPE, Note.CONTENT_ITEM_TYPE);
			values.put(Note.NOTE, mRandonUtils.noteRandom());
			mContext.getContentResolver().insert(Data.CONTENT_URI, values);
			
		}
	}
	
	/**
	 * 添加地址
	 * @param rawContactId
	 * @param addAddress
	 */
	public void addAddress(long rawContactId , boolean addAddress,ContentValues values){
		Log.i("look", "address:"+addAddress);
		if(addAddress){
			values.clear();  
	        values.put(Data.RAW_CONTACT_ID, rawContactId);  
	        values.put(Data.MIMETYPE, StructuredPostal.CONTENT_ITEM_TYPE);  
	        String address=mRandonUtils.noteRandom();
	        values.put(StructuredPostal.FORMATTED_ADDRESS,address);  
//	        values.put(StructuredPostal.POSTCODE, mEmployee.getPostCode());  
//	        values.put(StructuredPostal.TYPE, StructuredPostal.TYPE_WORK);  
	        mContext.getContentResolver().insert(Data.CONTENT_URI, values);  
		}
		
		
		
//		if(addAddress){
//			values.clear();
////			ContentValues values = new ContentValues();
//			values.put(Data.RAW_CONTACT_ID, rawContactId);
//			values.put(Data.MIMETYPE, SipAddress.CONTENT_ITEM_TYPE);
//			String address=mRandonUtils.noteRandom();
//			values.put(android.provider.ContactsContract.CommonDataKinds.SipAddress.SIP_ADDRESS, "hhh");
//			Log.i("look", "address:"+addAddress+" 内容："+address);
//			mContext.getContentResolver().insert(Data.CONTENT_URI, values);
//			
//		}
	}
	
	/**
	 * 添加昵称
	 * @param rawContactId
	 * @param addNickname
	 */
	public void addnickname(long rawContactId , boolean addNickName,ContentValues values){
		if(addNickName){
			values.clear();
//			ContentValues values = new ContentValues();
			values.put(Data.RAW_CONTACT_ID, rawContactId);
			values.put(Data.MIMETYPE, Nickname.CONTENT_ITEM_TYPE);
			values.put(Nickname.NAME, mRandonUtils.noteRandom());
			mContext.getContentResolver().insert(Data.CONTENT_URI, values);
			
		}
	}
	
	/**
	 * 添加网址
	 * @param rawContactId
	 * @param addWebsite
	 */
	public void addWebsite(long rawContactId , boolean addWebsite,ContentValues values){
		if(addWebsite){
			values.clear();
//			ContentValues values = new ContentValues();
			values.put(Data.RAW_CONTACT_ID, rawContactId);
			values.put(Data.MIMETYPE, Website.CONTENT_ITEM_TYPE);
			values.put(Website.URL, "www."+mRandonUtils.noteRandom()+".com");
			mContext.getContentResolver().insert(Data.CONTENT_URI, values);
			
		}
	}
	
	/**
	 * 添加公司
	 * @param rawContactId
	 * @param addCompangy
	 */
	public void addCompany(long rawContactId , boolean addCompangy,ContentValues values){
		if(addCompangy){
			values.clear();
//			ContentValues values = new ContentValues();
			values.put(Data.RAW_CONTACT_ID, rawContactId);
			values.put(Data.MIMETYPE, Organization.CONTENT_ITEM_TYPE);
			values.put(Organization.COMPANY,mRandonUtils.noteRandom());
			mContext.getContentResolver().insert(Data.CONTENT_URI, values);
			
		}
	}
	/**
	 * 添加IM 
	 * @param rawContactId
	 * @param addIM
	 */
	public void addIM(long rawContactId ,boolean addIM,ContentValues values){
		if(addIM){
			values.clear();
//			ContentValues values = new ContentValues();
			values.put(Data.RAW_CONTACT_ID, rawContactId);
			values.put(Data.MIMETYPE, Im.CONTENT_ITEM_TYPE);
			values.put(Im.DATA,mRandonUtils.noteRandom());
			mContext.getContentResolver().insert(Data.CONTENT_URI, values);
		}
	}
	
	
	
}
