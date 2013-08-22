/*
 * Copyright (c) 2012-2013 NetEase, Inc. and other contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.test.hwautotest.emmagee.activity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.test.hwautotest.R;
import com.test.hwautotest.emmagee.utils.EncryptData;

/**
 * Setting Page of Emmagee
 * 
 */
public class SettingsActivity extends Activity {

	private static final String LOG_TAG = "Emmagee-"
			+ SettingsActivity.class.getSimpleName();

	private CheckBox chkFloat;
	private EditText edtTime;
	private EditText edtRecipients;
	private EditText edtSender;
	private EditText edtPassword;
	private EditText edtSmtp;
	private String time, sender;
	private String prePassword, curPassword;
	private String settingTempFile;
	private String recipients, smtp;
	private String[] receivers;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(LOG_TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.emmagee_settings);

		final EncryptData des = new EncryptData("emmagee");
		Intent intent = this.getIntent();
		settingTempFile = intent.getStringExtra("settingTempFile");

		
		edtTime = (EditText) findViewById(R.id.time);
		edtSender = (EditText) findViewById(R.id.sender);
		edtPassword = (EditText) findViewById(R.id.password);
		edtSmtp = (EditText) findViewById(R.id.smtp);
		edtRecipients = (EditText) findViewById(R.id.recipients);
		chkFloat = (CheckBox) findViewById(R.id.floating);

		/*屏蔽邮件发送功能*/
		edtSender.setVisibility(View.INVISIBLE);
		edtPassword.setVisibility(View.INVISIBLE);
		edtSmtp.setVisibility(View.INVISIBLE);
		edtRecipients.setVisibility(View.INVISIBLE);
		
		
		Button btnSave = (Button) findViewById(R.id.save);
		boolean floatingTag = true;

		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream(settingTempFile));
			String interval = properties.getProperty("interval").trim();
			String isfloat = properties.getProperty("isfloat").trim();
			sender = properties.getProperty("sender").trim();
			prePassword = properties.getProperty("password").trim();
			recipients = properties.getProperty("recipients").trim();
			time = "".equals(interval) ? "5" : interval;
			floatingTag = "false".equals(isfloat) ? false : true;
			recipients = properties.getProperty("recipients");
			smtp = properties.getProperty("smtp");
		} catch (FileNotFoundException e) {
			Log.e(LOG_TAG, "FileNotFoundException: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(LOG_TAG, "IOException: " + e.getMessage());
			e.printStackTrace();
		}
		edtTime.setText(time);
		chkFloat.setChecked(floatingTag);
		edtRecipients.setText(recipients);
		edtSender.setText(sender);
		edtPassword.setText(prePassword);
		edtSmtp.setText(smtp);

		// edtTime.setInputType(InputType.TYPE_CLASS_NUMBER);
		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				time = edtTime.getText().toString().trim();
				sender = edtSender.getText().toString().trim();
//				if (!"".equals(sender) && !checkMailFormat(sender)) {
//					Toast.makeText(SettingsActivity.this, "发件人邮箱格式不正确",
//							Toast.LENGTH_LONG).show();
//					return;
//				}
//				recipients = edtRecipients.getText().toString().trim();
//				receivers = recipients.split("\\s+");
//				for (int i = 0; i < receivers.length; i++) {
//					if (!"".equals(receivers[i])
//							&& !checkMailFormat(receivers[i])) {
//						Toast.makeText(SettingsActivity.this,
//								"收件人邮箱" + receivers[i] + "格式不正确",
//								Toast.LENGTH_LONG).show();
//						return;
//					}
//				}
//				curPassword = edtPassword.getText().toString().trim();
//				smtp = edtSmtp.getText().toString().trim();
//				if (checkMailConfig(sender, recipients, smtp, curPassword) == -1) {
//					Toast.makeText(SettingsActivity.this, "邮箱配置不完整，请完善所有信息",
//							Toast.LENGTH_LONG).show();
//					return;
//				}
				if (!isNumeric(time)) {
					Toast.makeText(SettingsActivity.this, "输入数据无效，请重新输入",
							Toast.LENGTH_LONG).show();
					edtTime.setText("");
				} else 
				if ("".equals(time) || Long.parseLong(time) == 0) {
					Toast.makeText(SettingsActivity.this, "输入数据为空,请重新输入",
							Toast.LENGTH_LONG).show();
					edtTime.setText("");
				} else 
					if (Integer.parseInt(time) > 600) {
					Toast.makeText(SettingsActivity.this, "数据超过最大值600，请重新输入",
							Toast.LENGTH_LONG).show();
				} else {
					try {
						Properties properties = new Properties();
						properties.setProperty("interval", time);
						properties.setProperty("isfloat",
								chkFloat.isChecked() ? "true" : "false");
						properties.setProperty("sender", sender);
						Log.d(LOG_TAG, "sender=" + sender);
						try {
							// FIXME 注释
							properties.setProperty(
									"password",
									curPassword.equals(prePassword) ? curPassword
											: ("".equals(curPassword) ? ""
													: des.encrypt(curPassword)));
							Log.d(LOG_TAG, "curPassword=" + curPassword);
							Log.d(LOG_TAG,
									"encrtpt=" + des.encrypt(curPassword));
						} catch (Exception e) {
							properties.setProperty("password", "");
						}
						properties.setProperty("recipients", recipients);
						properties.setProperty("smtp", smtp);
						FileOutputStream fos = new FileOutputStream(
								settingTempFile);
						properties.store(fos, "Setting Data");
						fos.close();
						Toast.makeText(SettingsActivity.this, "保存成功",
								Toast.LENGTH_LONG).show();
						Intent intent = new Intent();
						setResult(Activity.RESULT_FIRST_USER, intent);
//						intent.setClass( SettingsActivity.this,MainPageActivity.class);//修改为上一界面
						SettingsActivity.this.finish();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	public void finish() {
		super.finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

//	private int checkMailConfig(String sender, String recipients, String smtp,
//			String curPassword) {
//		if (!"".equals(curPassword) && !"".equals(sender)
//				&& !"".equals(recipients) && !"".equals(smtp)) {
//			return 1;
//		} else if ("".equals(curPassword) && "".equals(sender)
//				&& "".equals(recipients) && "".equals(smtp)) {
//			return 0;
//		} else
//			return -1;
//	}
//
//	/**
//	 * 检查邮件格式正确性
//	 */
//	private boolean checkMailFormat1(String mail) {
//		String strPattern = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*"
//				+ "[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
//		Pattern p = Pattern.compile(strPattern);
//		Matcher m = p.matcher(mail);
//		return m.matches();
//	}

	/**
	 * is input a number.
	 * 
	 * @param inputStr
	 *            input string
	 * @return true is numeric
	 */
	private boolean isNumeric(String inputStr) {
		for (int i = inputStr.length(); --i >= 0;) {
			if (!Character.isDigit(inputStr.charAt(i))) {
				return false;
			}
		}
		return true;
	}

}
