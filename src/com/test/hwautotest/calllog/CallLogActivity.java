package com.test.hwautotest.calllog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gionee.hwautotest.R;

public class CallLogActivity extends Activity {

	private TextView calllogTitle;
	private Button clearCallLog;
	private Button addCallLog;
	private EditText calllogNumber;
	private RadioGroup mType;
	private RadioButton mReceive;
	private RadioButton mOutgoing;
	private RadioButton mMissed;
	private String mNumber;
	private int type;
	private int number;
	private ProgressDialog m_pDialog;
	
	

	CallLogUtils mCallLogUtils = new CallLogUtils(this);

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calllog);
		calllogTitle = (TextView) findViewById(R.id.calllogTitle);
		clearCallLog = (Button) findViewById(R.id.clearCallLog);
		addCallLog = (Button) findViewById(R.id.addCallLog);
		calllogNumber = (EditText) findViewById(R.id.calllogNumber);

		mType = (RadioGroup) findViewById(R.id.Type);
		mReceive = (RadioButton) findViewById(R.id.Received);
		mOutgoing = (RadioButton) findViewById(R.id.Outgoing);
		mMissed = (RadioButton) findViewById(R.id.Missed);

		calllogTitle.setText("通话记录(当前数量:" + mCallLogUtils.getCount() + ")");

		mType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == mReceive.getId()) {
					type = CallLog.Calls.INCOMING_TYPE;
				} else if (checkedId == mOutgoing.getId()) {
					type = CallLog.Calls.OUTGOING_TYPE;
				} else if (checkedId == mMissed.getId()) {
					type = CallLog.Calls.MISSED_TYPE;
				} else {
					type = CallLog.Calls.INCOMING_TYPE;
				}
			}
		});

		addCallLog.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				try {
					mNumber = calllogNumber.getText().toString();
					number = Integer.parseInt(mNumber);
					m_pDialog = new ProgressDialog(CallLogActivity.this);
					m_pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					m_pDialog.setTitle("正在添加");
					m_pDialog.setMax(number);
					m_pDialog.setIndeterminate(false);
					m_pDialog.setCancelable(true);
					m_pDialog.show();
					ProgressDailogAsyncTask asyncTask = new ProgressDailogAsyncTask(
							type, number);
					asyncTask.execute(1000);
					// TODO Auto-generated method stub
				} catch (Exception e) {
					// TODO: handle exception
					if (mNumber != "") {
						mCallLogUtils.DisplayToast("请输入整数");
					}
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
			AsyncTask<Integer, Integer, String> {

		int type = CallLog.Calls.INCOMING_TYPE;
		int number = 0;

		private ProgressDailogAsyncTask(int type, int number) {
			super();
			this.type = type;
			this.number = number;
		}

		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			int i = 0;
			for (i = 0; i < number; i++) {

				try {
					mCallLogUtils.insertCallLog(type, CallLogActivity.this);
					calllogTitle.setText("通话记录(当前数量:"
							+ mCallLogUtils.getCount() + ")");

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				publishProgress(i + 1);

			}
			return params[0].intValue() + "";

		}

		@Override
		protected void onPostExecute(String result) {
			m_pDialog.setTitle("添加完成");
			m_pDialog.cancel();
		}

		
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


}
