package com.test.hwautotest.ftp;



import java.util.ArrayList;

import com.test.hwautotest.R;

import it.sauronsoftware.ftp4j.FTPClient;
import android.os.Bundle;
import android.app.Activity;
import android.text.Selection;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * 界面布局
 * 
 * @author huangsq
 * 
 */
public class FtpGo extends Activity {
	Button downloadTypeBtn = null;
	Button uploadTypeBtn = null;
	ToggleButton toggleBtn_changeFTP=null;
	
	
	TextView tipHost=null;
	TextView tipPort=null;
	TextView tipUser=null;
	TextView tipPassward=null;
	
	EditText editHost=null;
	EditText editPort=null;
	EditText editUser=null;
	EditText editPassward=null;
	
	ArrayList<View> ftpInfos=new ArrayList<View>();
	
	private static String mFTPHost=null;
	private static int mFTPPort=-1;
	private static String mFTPUser=null;
	private static String mFTPPassword=null;
	public static String getmFTPHost() {
		return mFTPHost;
	}
	public static int getmFTPPort() {
		return mFTPPort;
	}
	public static String getmFTPUser() {
		return mFTPUser;
	}
	public static String getmFTPPassword() {
		return mFTPPassword;
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
	}
	/**
	 * 声明道具
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ftp_go);
		setTitle(R.string.tip_choose_testType);
		MyApplication.getInstance().addActivity(this);
		// 声明要添加代码的控件
		 downloadTypeBtn = (Button) findViewById(R.id.button_downloadType);
		 uploadTypeBtn = (Button) findViewById(R.id.button_uploadType);
		 toggleBtn_changeFTP=(ToggleButton)findViewById(R.id.toggleBtn_changeFTP);
		
		  tipHost=(TextView)findViewById(R.id.tip_ftpHost);
			 tipPort=(TextView)findViewById(R.id.tip_ftpPort);
			 tipUser=(TextView)findViewById(R.id.tip_ftpUser);
			 tipPassward=(TextView)findViewById(R.id.tip_ftpPassward);
			
			 editHost=(EditText)findViewById(R.id.ftpHost);
			 editPort=(EditText)findViewById(R.id.ftpPort);
			 editUser=(EditText)findViewById(R.id.ftpUser);
			 editPassward=(EditText)findViewById(R.id.ftpPassward);
			
			
			ftpInfos.add(tipHost);
			ftpInfos.add(tipPort);
			ftpInfos.add(tipUser);
			ftpInfos.add(tipPassward);
			ftpInfos.add(editHost);
			ftpInfos.add(editPort);
			ftpInfos.add(editUser);
			ftpInfos.add(editPassward);
		 
		downloadTypeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(toggleBtn_changeFTP.isChecked()){//提示保存
					toast("请点保存确认FTP配置");
				}else{
					ActivityUtil.goActivity(FtpGo.this,FtpDesignDownload.class);
//					ActivityUtil.toast(FtpGo.this,"选择下载文件");
				}
				
			}
		});

		uploadTypeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(toggleBtn_changeFTP.isChecked()){//提示保存
					toast("请点保存确认FTP配置");
				}else{
					ActivityUtil.goActivity(FtpGo.this,FtpDesignUpload.class);
//					ActivityUtil.toast(FtpGo.this,"选择上传文件");
				}
				
			}
		});
		
		toggleBtn_changeFTP.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(toggleBtn_changeFTP.isChecked()){//高亮状态
					for(View v:ftpInfos){
						v.setFocusableInTouchMode(true);
						v.setEnabled(true);
						
					}
//					editHost.requestFocusFromTouch();
				}else{//置灰状态
					if(editHost.getText().toString().isEmpty()
							|| editPort.getText().toString().isEmpty()
							|| editUser.getText().toString().isEmpty()
							|| editPassward.getText().toString().isEmpty()){
						toggleBtn_changeFTP.setChecked(true);
						toast("请输入完整再保存");
					}else{
						for(View v:ftpInfos){
							v.setFocusable(false);
							v.setEnabled(false);
						}
						mFTPHost=editHost.getText().toString();
						  mFTPPort=Integer.valueOf(editPort.getText().toString());
						   mFTPUser=editUser.getText().toString();
						   mFTPPassword=editPassward.getText().toString();
						   
						   FtpUtil.setmFTPHost(mFTPHost);
						   FtpUtil.setmFTPPort(mFTPPort);
						   FtpUtil.setmFTPUser(mFTPUser);
						   FtpUtil.setmFTPPassword(mFTPPassword);
						   
					}
					
				}
			}
			
			
		});

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ftp_main, menu);
		return true;
	}
	private void toast(String hint) {
		Toast.makeText(this, hint,Toast.LENGTH_SHORT).show();
	}
	private static final String TAG = "look";
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int selectId=item.getItemId();
		Log.i(TAG, "selectId:"+selectId);
		switch (selectId) {
		// 响应每个菜单项(通过菜单项的ID)
		case R.id.action_about://关于
			toast("负责人:黄圣权");
			break;
		case R.id.action_exit://退出
			toast("退出程序");
			// 退出
			MyApplication.getInstance().exit();
			break;
		default:
			// 对没有处理的事件，交给父类来处理
			return super.onOptionsItemSelected(item);

		}
		return super.onOptionsItemSelected(item);
	}




}
