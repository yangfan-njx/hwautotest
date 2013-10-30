package com.test.hwautotest.sms;

import java.util.Random;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.test.hwautotest.R;
import com.test.hwautotest.mms.Telephony.Sms;;

public class SMSActivity extends Activity {

	private Button addMessage;
	private EditText messageNumber;
	private Button clearMessage;
	private TextView messageTitle;
	private RadioGroup words;
	private RadioButton senventy;
	private RadioButton oneHundredsixty;
	private RadioButton eightHundreds;
	
	private RadioGroup type;
	private RadioButton inbox;
	private RadioButton sent;
	private RadioButton drag;
	private RadioGroup read;
	private RadioButton alreadyRead;
	private RadioButton unRead;
	private CheckBox mIsSmsnumber;//复选号码
	private EditText mSmsnumber;//填入号码
	
	private String customSmsnumber;//自定义号码
	
	private ProgressDialog m_pDialog;
	
	public static final int NUMBER_SENVENTY = 0;
    public static final int NUMBER_ONEHUNDREDSIXTY = 1;
    public static final int NUMBER_EIGHTHUNDREDS = 2;
	SMSUtils mMessageUtils = new SMSUtils(this);
	
	int messageType = Sms.MESSAGE_TYPE_INBOX;
	int wordsNumber = 0;
	int readSatuts = Sms.MESSAGE_READ;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms);
		d();
		messageTitle = (TextView)findViewById(R.id.messageTitle);
		addMessage = (Button)findViewById(R.id.addMessage);
		clearMessage = (Button)findViewById(R.id.clearMessage);
		messageNumber = (EditText)findViewById(R.id.messageNumber);
		words = (RadioGroup)findViewById(R.id.words);
		senventy = (RadioButton)findViewById(R.id.senventy);
		oneHundredsixty = (RadioButton)findViewById(R.id.oneHundredsixty);
		eightHundreds = (RadioButton)findViewById(R.id.eightHundreds);
		
		type = (RadioGroup)findViewById(R.id.type);
		inbox = (RadioButton)findViewById(R.id.inbox);
		sent = (RadioButton)findViewById(R.id.sent);
		drag = (RadioButton)findViewById(R.id.drag);
		read = (RadioGroup)findViewById(R.id.read);
		alreadyRead = (RadioButton)findViewById(R.id.alreadyRead);
		unRead = (RadioButton)findViewById(R.id.unRead);
		mIsSmsnumber=(CheckBox)findViewById(R.id.issmsnumber);
		mSmsnumber=(EditText)findViewById(R.id.smsnumber);
		
		
		messageTitle.setText("信息(当前数量:" + mMessageUtils.getCount() + ")");
		
		words.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(checkedId == senventy.getId()){
					wordsNumber = NUMBER_SENVENTY;
				}else if(checkedId == oneHundredsixty.getId()){
					wordsNumber = NUMBER_ONEHUNDREDSIXTY;
				}else if(checkedId == eightHundreds.getId()){
					wordsNumber = NUMBER_EIGHTHUNDREDS;
				}
			}
		});
	
		type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(checkedId == inbox.getId()){
					messageType = Sms.MESSAGE_TYPE_INBOX;
				}else if(checkedId == sent.getId()){
					messageType = Sms.MESSAGE_TYPE_SENT;
				}else if(checkedId == drag.getId()){
					messageType = Sms.MESSAGE_TYPE_DRAFT;
				}
			}
		});
		
		read.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(checkedId == alreadyRead.getId()){
					readSatuts = Sms.MESSAGE_READ;
				}else if(checkedId == unRead.getId()){
					readSatuts = Sms.MESSAGE_UNREAD;
				}
			}
		});
		
		addMessage.setOnClickListener(new View.OnClickListener() {
		
		private String mNumber;
		private int number;

		@Override
		public void onClick(View v) {

			mNumber = messageNumber.getText().toString();
			customSmsnumber=mSmsnumber.getText().toString().trim();
			if (mNumber.equals("")|| !mNumber.matches("\\d*")) {//防止输入非数字的条数
				mMessageUtils.DisplayToast("新增条数：请输入整数");
			}else if(mIsSmsnumber.isChecked()
					&& customSmsnumber.equals("")|| !customSmsnumber.matches("\\d*")||customSmsnumber.length()>11){//
				mMessageUtils.DisplayToast("号码：请输入11位以内数字");
			}
//			else if(!words.isSelected()){
//				mMessageUtils.DisplayToast("号码：请输入11位以内数字");
//			}
			else {
				number = Integer.parseInt(mNumber);
				
				m_pDialog = new ProgressDialog(SMSActivity.this);
				m_pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				m_pDialog.setTitle("正在添加"+"记录");
				m_pDialog.setCanceledOnTouchOutside(false);
				m_pDialog.setMax(number);
				m_pDialog.setIndeterminate(false);
				m_pDialog.setCancelable(true);
				m_pDialog.show();
				ProgressDailogAsyncTask asyncTask = new ProgressDailogAsyncTask(number);
				asyncTask.execute();

			}

				messageTitle.setText("信息(当前数量:" + mMessageUtils.getCount() + ")");
			
			}
		});
		clearMessage.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			try {
				// Query SMS
				Uri uriSms = Uri.parse("content://sms");
				getContentResolver().delete(uriSms, null, null);
				mMessageUtils.DisplayToast("清空短信完成");
				messageTitle.setText("信息(当前数量:" + mMessageUtils.getCount() + ")");
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			}
		});
		
		//添加号码自定义，监听复选框
		mIsSmsnumber.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
						// TODO Auto-generated method stub
						mSmsnumber.setEnabled(arg1);
							
					}
				});
	}
	private class ProgressDailogAsyncTask extends AsyncTask<Integer, Integer, Boolean> {
		
		private int number;
//		private int wordsNumber;
//		private int readSatuts;
//		private int messageType;
		
		private ProgressDailogAsyncTask (int number){
			super();
			this.number = number;
//			this.wordsNumber = wordsNumber;
//			this.readSatuts = readSatuts;
//			this.messageType = messageType;
		}
		
		@Override
		protected Boolean doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			int i = 0;
			Random mRandom = new Random();
			for (i = 0; i < number; i++) {
		
				try {
					if(mIsSmsnumber.isChecked()){
						mMessageUtils.insertSms(wordsNumber, readSatuts, messageType,customSmsnumber);
					}else{
						mMessageUtils.insertSms(wordsNumber, readSatuts, messageType);
					}
					
		
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				publishProgress(i + 1);
		
			}
//			return params[0].intValue() + "";
			return true;
		
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			m_pDialog.setTitle("添加完成");
			messageTitle.setText("信息(当前数量:" + mMessageUtils.getCount() + ")");
			m_pDialog.cancel();
		}
		
		// 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
		@Override
		protected void onPreExecute() {
			// m_pDialog.setTitle("正在添加");
		}
		
		/**
		 * 这里的Intege参数对应AsyncTask中的第二个参数
		 * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
		 * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
			int vlaue = values[0];
			m_pDialog.setProgress(vlaue);
		}
		}
			
	/**
	 * 查询数据库字段名称
	 */
	public void d(){
		ContentResolver CR = this.getContentResolver();
		Uri uri = Uri.parse("content://sms");
		Cursor cursor = CR.query(uri, null, null, null, null);
		String []cols=cursor.getColumnNames();
		for(int i=0;i<cols.length;i++){
			Log.i("look", cols[i]);
		}
		
	}
	
}
