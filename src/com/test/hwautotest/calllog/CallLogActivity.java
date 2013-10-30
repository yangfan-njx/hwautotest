package com.test.hwautotest.calllog;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.TreeSet;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.test.hwautotest.R;
import com.test.hwautotest.R.id;

public class CallLogActivity extends Activity {

	private TextView calllogTitle;
	private Button clearCallLog;
	private Button addCallLog;
	private EditText calllogNumber;
//	private RadioGroup mType;
	private CheckBox mReceive;
	private CheckBox mOutgoing;
	private CheckBox mMissed;
	private String mNumber;
	
	private CheckBox mIsCallnumber;
	private CheckBox mIsCalldate;
	private CheckBox mIsCalltime;
	private EditText mCallnumber;
	private Button showCalldate;
	private Button showCalltime;
	
	private OnDateSetListener myDateSetListener;
	
	private String customCallnumber;//自定义号码
	private int customYear=-1,customMon=-1,customDay=-1,customHour=-1,customMin=-1;//自定义日期时间
	private Time time;
	
//	private int type;
	private TreeSet<Integer> types;//多选类型
	private int number;
	private ProgressDialog m_pDialog;
	private SharedPreferences prefs;//使用SharedPreferences存储
	private final String IsCallLogTaskFinish="isCallLogTaskFinish";

	CallLogUtils mCallLogUtils = new CallLogUtils(this);

	protected void onCreate(Bundle savedInstanceState) {
		System.out.println("hhh");
		Log.i("look", "customYear"+customYear);
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calllog);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		setTaskFinish(true);
		isCallLogTaskFinish();
		calllogTitle = (TextView) findViewById(R.id.calllogTitle);
		clearCallLog = (Button) findViewById(R.id.clearCallLog);
		addCallLog = (Button) findViewById(R.id.addCallLog);
		calllogNumber = (EditText) findViewById(R.id.calllogNumber);
		
		//自定义内容
		mIsCallnumber=(CheckBox)findViewById(R.id.iscallnumber);
		mIsCalldate=(CheckBox)findViewById(R.id.iscalldate);
		mIsCalltime=(CheckBox)findViewById(R.id.iscalltime);
		mCallnumber=(EditText)findViewById(R.id.callnumber);
		showCalldate=(Button)findViewById(R.id.calldate);
		showCalltime=(Button)findViewById(R.id.calltime);
		
		
		
		time = new Time();
		time.setToNow();
		customYear = time.year;
		customMon = time.month;
		customDay = time.monthDay;
		customHour = time.hour;
		customMin = time.minute;
		
//		mType = (RadioGroup) findViewById(R.id.Type);
		types=new TreeSet<Integer>();
		types.add(CallLog.Calls.INCOMING_TYPE);//默认添加已接电话
		
		mReceive = (CheckBox) findViewById(R.id.Received);
		mOutgoing = (CheckBox) findViewById(R.id.Outgoing);
		mMissed = (CheckBox) findViewById(R.id.Missed);

		calllogTitle.setText("通话记录(当前数量:" + mCallLogUtils.getCount() + ")");
		//点击按钮弹出日期选择对话框
		showCalldate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showDialog(1);
			}
		});
		
		//点击弹出时间选择对话框
		showCalltime.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showDialog(2);
			}
		});
		
		 //设置时间之后点击SET就会将时间改为你刚刚设置的时间
		myDateSetListener = new DatePickerDialog.OnDateSetListener() {
		  
		  public void onDateSet(DatePicker view, int year, int monthOfYear,
		    int dayOfMonth) {
			  time.setToNow();
			  if(year>time.year){
				  mCallLogUtils.DisplayToast("日期：请选当年以前的年数");
			  }else if(monthOfYear>time.month){
				  mCallLogUtils.DisplayToast("日期：请选当月以前的月数");
			  }else if(dayOfMonth>time.monthDay){
				  mCallLogUtils.DisplayToast("日期：请选当天以前的日数");
				} else {
					showCalldate.setText(year + "年" + (monthOfYear + 1) + "月"
							+ dayOfMonth + "日");
					customYear = year;
					customMon = monthOfYear;
					customDay = dayOfMonth;
				}
		  
		   
		  }
		 };    
//		mType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//
//			@Override
//			public void onCheckedChanged(RadioGroup group, int checkedId) {
//				// TODO Auto-generated method stub
//				if (checkedId == mReceive.getId()) {
//					type = CallLog.Calls.INCOMING_TYPE;
//				} else if (checkedId == mOutgoing.getId()) {
//					type = CallLog.Calls.OUTGOING_TYPE;
//				} else if (checkedId == mMissed.getId()) {
//					type = CallLog.Calls.MISSED_TYPE;
//				} else {
//					type = CallLog.Calls.INCOMING_TYPE;
//				}
//			}
//		});
		
		mReceive.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton recCheckBox,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					types.add(CallLog.Calls.INCOMING_TYPE);
				}else  if(types.contains(CallLog.Calls.INCOMING_TYPE)){
					types.remove(CallLog.Calls.INCOMING_TYPE);
				}
			}
		});
		mOutgoing.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton recCheckBox,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					types.add(CallLog.Calls.OUTGOING_TYPE);
				}else if(types.contains(CallLog.Calls.OUTGOING_TYPE)){
					types.remove(CallLog.Calls.OUTGOING_TYPE);
				}
			}
		});
		mMissed.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton recCheckBox,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					types.add(CallLog.Calls.MISSED_TYPE);
				}else  if(types.contains(CallLog.Calls.MISSED_TYPE)){
					types.remove(CallLog.Calls.MISSED_TYPE);
				}
			}
		});
		
		//添加号码自定义，监听复选框
		mIsCallnumber.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
					mCallnumber.setEnabled(arg1);
					
			}
		});
		
		//添加日期自定义，监听复选框
		mIsCalldate.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				showCalldate.setEnabled(arg1);
			}
		});
		
		//添加时间自定义，监听复选框
		mIsCalltime.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				showCalltime.setEnabled(arg1);
			}
		});
		
		
		
		//提交数据库前，过滤不规范操作
		addCallLog.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mNumber = calllogNumber.getText().toString().trim();
				customCallnumber=mCallnumber.getText().toString().trim();
				if (mNumber.equals("")|| !mNumber.matches("\\d*")) {//防止输入非数字的条数
					mCallLogUtils.DisplayToast("新增条数：请输入整数");
				}else if(types.size()==0){//防止未勾选类型
					mCallLogUtils.DisplayToast("类型：请勾选通话类型");
				} else if(mIsCallnumber.isChecked()
						&& customCallnumber.equals("")|| !customCallnumber.matches("\\d*")||customCallnumber.length()>11){//
						mCallLogUtils.DisplayToast("号码：请输入11位以内数字");
				}else {
					number = Integer.parseInt(mNumber);
					addCallogProgressDialog("记录");

					ProgressDailogAsyncTask asyncTask = new ProgressDailogAsyncTask(number);
					asyncTask.execute();

				}
					
			}
		});

		clearCallLog.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					getContentResolver().delete(CallLog.Calls.CONTENT_URI,
							null, null);
					calllogTitle.setText("通话记录(当前数量:"
							+ mCallLogUtils.getCount() + ")");
					mCallLogUtils.DisplayToast("清空通话记录完成");
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}

		});

	}

	private class ProgressDailogAsyncTask extends
			AsyncTask<Integer, Integer, Boolean> {

		int number = 0;
//		String phone=null;
//		int year=-1;
//		int mon=-1;
//		int day=-1;
//		int hour=-1;
//		int min=-1;
		
		private ProgressDailogAsyncTask(int number) {
			super();
			this.number = number;
		}
		
//		private ProgressDailogAsyncTask(int number,String phone,int year,int mon,int day,int hour,int min) {
//			super();
//			this.number = number;
//			this.phone=phone;
//			this.year=year;
//			this.mon=mon;
//			this.day=day;
//			this.hour=hour;
//			this.min=min;
//		}
		@Override
		protected Boolean doInBackground(Integer... params) {
			// TODO Auto-generated method stub

			setTaskFinish(false);

			boolean result = false;
			int process = 0;

				
				if (types.contains(CallLog.Calls.INCOMING_TYPE)) {
					for (int i=0; i < number ; i++,process++) {
						try {
							boolean isCallnumber=mIsCallnumber.isChecked();
							boolean isCalldate=mIsCalldate.isChecked();
							boolean isCalltime=mIsCalltime.isChecked();
							if(isCallnumber && !isCalldate && !isCalltime){//号码
								mCallLogUtils.insertCallLog(
										CallLog.Calls.INCOMING_TYPE,
										CallLogActivity.this,customCallnumber,-1,-1,-1,-1,-1);
							}else if(!isCallnumber && isCalldate && !isCalltime){//日期
								mCallLogUtils.insertCallLog(
										CallLog.Calls.INCOMING_TYPE,
										CallLogActivity.this,null,customYear,customMon,customDay,-1,-1);
							}else if(!isCallnumber && !isCalldate && isCalltime){//时间
								mCallLogUtils.insertCallLog(
										CallLog.Calls.INCOMING_TYPE,
										CallLogActivity.this,null,-1,-1,-1,customHour,customMin);
							}else if(isCallnumber && isCalldate && !isCalltime){//号码 日期
								mCallLogUtils.insertCallLog(
										CallLog.Calls.INCOMING_TYPE,
										CallLogActivity.this,customCallnumber,customYear,customMon,customDay,-1,-1);
							}else if(isCallnumber && !isCalldate && isCalltime){//号码 时间
								mCallLogUtils.insertCallLog(
										CallLog.Calls.INCOMING_TYPE,
										CallLogActivity.this,customCallnumber,-1,-1,-1,customHour,customMin);
							}else if(!isCallnumber && isCalldate && isCalltime){//日期 时间
								mCallLogUtils.insertCallLog(
										CallLog.Calls.INCOMING_TYPE,
										CallLogActivity.this,null,customYear,customMon,customDay,customHour,customMin);
							}else if(isCallnumber && isCalldate && isCalltime){//号码 日期 时间
								mCallLogUtils.insertCallLog(
										CallLog.Calls.INCOMING_TYPE,
										CallLogActivity.this,customCallnumber,customYear,customMon,customDay,customHour,customMin);
							}else{
								mCallLogUtils.insertCallLog(
										CallLog.Calls.INCOMING_TYPE,
										CallLogActivity.this);
							}
							
							

							calllogTitle.setText("通话记录(当前数量:"
									+ mCallLogUtils.getCount() + ")");

						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
						publishProgress(process + 1);
					}
					

				}

				if (types.contains(CallLog.Calls.OUTGOING_TYPE)) {
					for (int i=0; i < number ; i++,process++) {
						try {
							boolean isCallnumber=mIsCallnumber.isChecked();
							boolean isCalldate=mIsCalldate.isChecked();
							boolean isCalltime=mIsCalltime.isChecked();
							if(isCallnumber && !isCalldate && !isCalltime){//号码
								mCallLogUtils.insertCallLog(
										CallLog.Calls.OUTGOING_TYPE,
										CallLogActivity.this,customCallnumber,-1,-1,-1,-1,-1);
							}else if(!isCallnumber && isCalldate && !isCalltime){//日期
								mCallLogUtils.insertCallLog(
										CallLog.Calls.OUTGOING_TYPE,
										CallLogActivity.this,null,customYear,customMon,customDay,-1,-1);
							}else if(!isCallnumber && !isCalldate && isCalltime){//时间
								mCallLogUtils.insertCallLog(
										CallLog.Calls.OUTGOING_TYPE,
										CallLogActivity.this,null,-1,-1,-1,customHour,customMin);
							}else if(isCallnumber && isCalldate && !isCalltime){//号码 日期
								mCallLogUtils.insertCallLog(
										CallLog.Calls.OUTGOING_TYPE,
										CallLogActivity.this,customCallnumber,customYear,customMon,customDay,-1,-1);
							}else if(isCallnumber && !isCalldate && isCalltime){//号码 时间
								mCallLogUtils.insertCallLog(
										CallLog.Calls.OUTGOING_TYPE,
										CallLogActivity.this,customCallnumber,-1,-1,-1,customHour,customMin);
							}else if(!isCallnumber && isCalldate && isCalltime){//日期 时间
								mCallLogUtils.insertCallLog(
										CallLog.Calls.OUTGOING_TYPE,
										CallLogActivity.this,null,customYear,customMon,customDay,customHour,customMin);
							}else if(isCallnumber && isCalldate && isCalltime){//号码 日期 时间
								mCallLogUtils.insertCallLog(
										CallLog.Calls.OUTGOING_TYPE,
										CallLogActivity.this,customCallnumber,customYear,customMon,customDay,customHour,customMin);
							}else{
								mCallLogUtils.insertCallLog(
										CallLog.Calls.OUTGOING_TYPE,
										CallLogActivity.this);
							}
							
							

							calllogTitle.setText("通话记录(当前数量:"
									+ mCallLogUtils.getCount() + ")");

						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
						publishProgress(process + 1);
					}
					
				}

				if (types.contains(CallLog.Calls.MISSED_TYPE)) {
					for (int i=0; i < number ; i++,process++) {
						try {
							boolean isCallnumber=mIsCallnumber.isChecked();
							boolean isCalldate=mIsCalldate.isChecked();
							boolean isCalltime=mIsCalltime.isChecked();
							if(isCallnumber && !isCalldate && !isCalltime){//号码
								mCallLogUtils.insertCallLog(
										CallLog.Calls.MISSED_TYPE,
										CallLogActivity.this,customCallnumber,-1,-1,-1,-1,-1);
							}else if(!isCallnumber && isCalldate && !isCalltime){//日期
								mCallLogUtils.insertCallLog(
										CallLog.Calls.MISSED_TYPE,
										CallLogActivity.this,null,customYear,customMon,customDay,-1,-1);
							}else if(!isCallnumber && !isCalldate && isCalltime){//时间
								mCallLogUtils.insertCallLog(
										CallLog.Calls.MISSED_TYPE,
										CallLogActivity.this,null,-1,-1,-1,customHour,customMin);
							}else if(isCallnumber && isCalldate && !isCalltime){//号码 日期
								mCallLogUtils.insertCallLog(
										CallLog.Calls.MISSED_TYPE,
										CallLogActivity.this,customCallnumber,customYear,customMon,customDay,-1,-1);
							}else if(isCallnumber && !isCalldate && isCalltime){//号码 时间
								mCallLogUtils.insertCallLog(
										CallLog.Calls.MISSED_TYPE,
										CallLogActivity.this,customCallnumber,-1,-1,-1,customHour,customMin);
							}else if(!isCallnumber && isCalldate && isCalltime){//日期 时间
								mCallLogUtils.insertCallLog(
										CallLog.Calls.MISSED_TYPE,
										CallLogActivity.this,null,customYear,customMon,customDay,customHour,customMin);
							}else if(isCallnumber && isCalldate && isCalltime){//号码 日期 时间
								mCallLogUtils.insertCallLog(
										CallLog.Calls.MISSED_TYPE,
										CallLogActivity.this,customCallnumber,customYear,customMon,customDay,customHour,customMin);
							}else{
								mCallLogUtils.insertCallLog(
										CallLog.Calls.MISSED_TYPE,
										CallLogActivity.this);
							}
							
							

							calllogTitle.setText("通话记录(当前数量:"
									+ mCallLogUtils.getCount() + ")");

						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
						publishProgress(process + 1);
					}
					
				}
				

			result = true;

			setTaskFinish(true);
			// return params[0].intValue() + "";
			return result;

		}

		@Override
		protected void onPostExecute(Boolean result) {
			Log.i("look", "**************完成线程onPostExecute" );
			m_pDialog.setTitle("添加完成");
			m_pDialog.cancel();
		}

		
		@Override
		protected void onPreExecute() {
			Log.i("look", "**************开始线程onPreExecute" );
//			if (params[0]==1) {
//				addCallogProgressDialog("来电记录");
//			}
//			if (params[0]==2) {
//				addCallogProgressDialog("去电记录");
//			}
//			if (params[0]==3) {
//				addCallogProgressDialog("1");
//			}
			
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

	private void addCallogProgressDialog(String addTypes){
		m_pDialog = new ProgressDialog(CallLogActivity.this);
		m_pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		m_pDialog.setCanceledOnTouchOutside(false);
		m_pDialog.setTitle("正在添加"+addTypes);
		m_pDialog.setMax(number*types.size());
		m_pDialog.setIndeterminate(false);
		m_pDialog.setCancelable(true);
		m_pDialog.show();
		
	}
	/**
	 * 保存异步线程完成与否，通过SharedPreferences
	 * @param isTaskFinish
	 */
	private void setTaskFinish(boolean isCallLogTaskFinish) {
		Editor editor = prefs.edit();
		editor.putBoolean(IsCallLogTaskFinish, isCallLogTaskFinish);
		editor.commit();
		Log.i("look", "**************保存" );
	}
	
	/**
	 * 获取异步线程完成与否，通过SharedPreferences
	 * @return
	 */
	private boolean isCallLogTaskFinish() {
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean isCallLogTaskFinish=prefs.getBoolean(IsCallLogTaskFinish, true);
//		Log.i("look", "isCallLogTaskFinish:" + isCallLogTaskFinish);
		return isCallLogTaskFinish;
		
	}
	
	 @Override
	protected Dialog onCreateDialog(int id) {
		time.setToNow();
		// TODO Auto-generated method stub
		switch (id) {
		case 1:

			return new DatePickerDialog(this, myDateSetListener, time.year,
					time.month, time.monthDay);
		case 2:
			return new TimePickerDialog(this, new OnTimeSetListener() {

				@Override
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					// TODO Auto-generated method stub
					  time.setToNow();
					  if(customYear>=time.year && customMon>=time.month 
							  && customDay>=time.monthDay && hourOfDay>time.hour){
						  mCallLogUtils.DisplayToast("日期：请选以前的日期或小时数");
					  }else if(customYear>=time.year && customMon>=time.month 
							  && customDay>=time.monthDay && minute>time.minute){
						  mCallLogUtils.DisplayToast("日期：请选以前的日期或分钟数");
					} else {
						DecimalFormat f = new DecimalFormat("00");
						showCalltime.setText(f.format(hourOfDay) + ":"
								+ f.format(minute));
						customHour = hourOfDay;
						customMin = minute;
					}
					
				}
			}, time.hour, time.minute, true);

		}
		return null;
	}
	
	/**
	 * 查询数据库字段名称
	 */
	public void d(){
		ContentResolver CR = this.getContentResolver();
		Uri uri = CallLog.Calls.CONTENT_URI;
		Cursor cursor = CR.query(uri, null, null, null, null);
		String []cols=cursor.getColumnNames();
		for(int i=0;i<cols.length;i++){
			Log.i("look", cols[i]);
		}
		
	}
}
