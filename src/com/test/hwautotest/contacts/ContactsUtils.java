package com.test.hwautotest.contacts;

import com.test.utils.RandomUtils;

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
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.util.Log;
import android.widget.Toast;

public class ContactsUtils {
	private Context mContext;
	
	RandomUtils mRandonUtils = new RandomUtils();
	public ContactsUtils(Context mContext){
		this.mContext = mContext;
	}
	public void DisplayToast(String string) {
		// TODO Auto-generated method stub
		Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
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
	 * 方法名：添加联系人
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
			 
			values.clear();
			values.put(Data.RAW_CONTACT_ID, rawContactId);
			values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
			if (language.equals("english")) {
				name = mRandonUtils.englishNameRandom();
			} else {
				name = mRandonUtils.chineseNameRandom();
			}

			values.put(StructuredName.GIVEN_NAME, name);
			mContext.getContentResolver().insert(Data.CONTENT_URI, values);
			values.clear();
			values.put(Data.RAW_CONTACT_ID, rawContactId);
			values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
			values.put(Phone.TYPE, Phone.TYPE_HOME);
			values.put(Phone.NUMBER,mRandonUtils.numberRandom());
			mContext.getContentResolver().insert(Data.CONTENT_URI, values);
			addEmail(rawContactId,addEmail);
			addCompany(rawContactId,addCompany);
			addAddress(rawContactId, addAddress);
			addWebsite(rawContactId, addWebsite);
			addnickname(rawContactId, addNickName);
			addNotes(rawContactId, addNotes);
			addIM(rawContactId, addIM);
		
	}
	/**
	 * 添加email地址
	 * @param rawContactId
	 * @param addEmail
	 */
	public void addEmail(long rawContactId,boolean addEmail){
		if(addEmail){
			ContentValues values = new ContentValues();
			values.put(Data.RAW_CONTACT_ID, rawContactId);
			values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
			values.put(Email.ADDRESS, mRandonUtils.emailRandom());
			System.out.println(mRandonUtils.emailRandom());
			mContext.getContentResolver().insert(Data.CONTENT_URI, values);
			values.clear();
		}
	}
	
	/**
	 * 添加备注
	 * @param rawContactId
	 * @param addNotes
	 */
	public void addNotes(long rawContactId , boolean addNotes){
		if(addNotes){
			ContentValues values = new ContentValues();
			values.put(Data.RAW_CONTACT_ID, rawContactId);
			values.put(Data.MIMETYPE, Note.CONTENT_ITEM_TYPE);
			values.put(Note.NOTE, mRandonUtils.noteRandom());
			mContext.getContentResolver().insert(Data.CONTENT_URI, values);
			values.clear();
		}
	}
	
	/**
	 * 添加地址
	 * @param rawContactId
	 * @param addAddress
	 */
	public void addAddress(long rawContactId , boolean addAddress){
		if(addAddress){
			ContentValues values = new ContentValues();
			values.put(Data.RAW_CONTACT_ID, rawContactId);
			values.put(Data.MIMETYPE, SipAddress.CONTENT_ITEM_TYPE);
			values.put(SipAddress.SIP_ADDRESS, mRandonUtils.noteRandom());
			mContext.getContentResolver().insert(Data.CONTENT_URI, values);
			values.clear();
		}
	}
	
	/**
	 * 添加昵称
	 * @param rawContactId
	 * @param addNickname
	 */
	public void addnickname(long rawContactId , boolean addNickName){
		if(addNickName){
			ContentValues values = new ContentValues();
			values.put(Data.RAW_CONTACT_ID, rawContactId);
			values.put(Data.MIMETYPE, Nickname.CONTENT_ITEM_TYPE);
			values.put(Nickname.NAME, mRandonUtils.noteRandom());
			mContext.getContentResolver().insert(Data.CONTENT_URI, values);
			values.clear();
		}
	}
	
	/**
	 * 添加网址
	 * @param rawContactId
	 * @param addWebsite
	 */
	public void addWebsite(long rawContactId , boolean addWebsite){
		if(addWebsite){
			ContentValues values = new ContentValues();
			values.put(Data.RAW_CONTACT_ID, rawContactId);
			values.put(Data.MIMETYPE, Website.CONTENT_ITEM_TYPE);
			values.put(Website.URL, "www."+mRandonUtils.noteRandom()+".com");
			mContext.getContentResolver().insert(Data.CONTENT_URI, values);
			values.clear();
		}
	}
	
	/**
	 * 添加公司
	 * @param rawContactId
	 * @param addCompangy
	 */
	public void addCompany(long rawContactId , boolean addCompangy){
		if(addCompangy){
			ContentValues values = new ContentValues();
			values.put(Data.RAW_CONTACT_ID, rawContactId);
			values.put(Data.MIMETYPE, Organization.CONTENT_ITEM_TYPE);
			values.put(Organization.COMPANY,mRandonUtils.noteRandom());
			mContext.getContentResolver().insert(Data.CONTENT_URI, values);
			values.clear();
		}
	}
	/**
	 * 添加IM 
	 * @param rawContactId
	 * @param addIM
	 */
	public void addIM(long rawContactId ,boolean addIM){
		if(addIM){
			ContentValues values = new ContentValues();
			values.put(Data.RAW_CONTACT_ID, rawContactId);
			values.put(Data.MIMETYPE, Im.CONTENT_ITEM_TYPE);
			values.put(Im.DATA,mRandonUtils.noteRandom());
			mContext.getContentResolver().insert(Data.CONTENT_URI, values);
			values.clear();
		}
	}
	
	
}
