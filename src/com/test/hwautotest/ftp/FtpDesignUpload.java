package com.test.hwautotest.ftp;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;

import com.test.hwautotest.R;





import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.ClipboardManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 界面布局
 * 
 * @author huangsq
 * 
 */
public class FtpDesignUpload extends Activity {
	private static final String TAG = "look";
	
	private TextView uploadName=null;
	private Button btn_chooseUpload=null;
	private TextView size_upload=null;
	private Button btn_finishUploadChoose=null;
	private Button btn_reChoose=null;
	
	private final int ftpType=1;//下载为0，上传为1
	private static String uploadFilePath=null;//上传需要文件完整路径
	private static String uploadFileSize=null;//上传需要文件大小
	
	public static String getUploadFileSize() {
		return uploadFileSize;
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
		setContentView(R.layout.ftp_design_upload);
		
		MyApplication.getInstance().addActivity(this);
		// 声明要添加代码的控件
		uploadName = (TextView) findViewById(R.id.uploadName);
		btn_chooseUpload = (Button) findViewById(R.id.btn_chooseUpload);
		size_upload= (TextView) findViewById(R.id.size_upload);
		btn_finishUploadChoose= (Button) findViewById(R.id.btn_finishUploadChoose);
		btn_reChoose=(Button)findViewById(R.id.btn_reChoose);

		
		//选择后获取上传路径
		Intent intent=getIntent();
		uploadFilePath=intent.getStringExtra("uploadFilePath");
		Log.w("look", "获取要上传文件的路径："+uploadFilePath);
		uploadFileSize=intent.getStringExtra("uploadFileSize");
		Log.w("look", "获取要上传文件的大小："+uploadFileSize);
		
		//显示获取上传文件路径和大小到两处文本框
		if(uploadFilePath!=null){
			uploadName.setText(uploadFilePath);
			size_upload.setText(uploadFileSize);
		}
		
		
		// 绑定触发事件
		
		btn_chooseUpload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivityUtil.goActivityWithString2(
						FtpDesignUpload.this,
						SDCardFileExplorerActivity.class, 
						"uploadFilePath",uploadFilePath,
						"uploadFileSize",uploadFileSize);
//				ActivityUtil.toast(FtpDesignUpload.this, "选择上传的文件");
			}
		});
		btn_finishUploadChoose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.w("look", "上传文件路径：" + uploadFilePath);
				if(uploadFilePath!=null){
					ActivityUtil.goActivityWithDoData(FtpDesignUpload.this,
							FtpDo.class, ftpType, uploadFilePath);
//					ActivityUtil.toast(FtpDesignUpload.this, "设置完成");
				}else{
					ActivityUtil.toast(FtpDesignUpload.this, "请点“浏览”选择上传文件");
				}
				
			}
		});
		
		btn_reChoose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivityUtil.goActivity(FtpDesignUpload.this,
						FtpGo.class);
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
