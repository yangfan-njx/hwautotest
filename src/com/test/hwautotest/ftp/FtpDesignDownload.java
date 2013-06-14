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
import android.os.Process;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 界面布局
 * 
 * @author huangsq
 * 
 */
public class FtpDesignDownload extends Activity {
	private static final String[] downloadFileNames = { "1MB", "10MB", "100MB", "500MB"
//		, "2GB" 
		};
	private TextView size_download;
	private Spinner choose_download;
	private Button btn_finishDownloadChoose;
	private Button btn_reChoose;
	private ArrayAdapter<String> adapter;

	public final int ftpType=0;//下载为0，上传为1
	public String fileName;//下载实质只需要文件名
	protected static String downloadSize;
	
	public static String getDownloadSize() {
		return downloadSize;
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
		setContentView(R.layout.ftp_design_download);
		
		MyApplication.getInstance().addActivity(this);
		// 声明要添加代码的控件
		size_download=(TextView)findViewById(R.id.size_download);
		choose_download = (Spinner) findViewById(R.id.choose_download);  
		btn_finishDownloadChoose=(Button)findViewById(R.id.btn_finishDownloadChoose);
		btn_reChoose=(Button)findViewById(R.id.btn_reChoose);
		
        //将可选内容与ArrayAdapter连接起来  
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,downloadFileNames);  
          
        //设置下拉列表的风格  
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
          
        //将adapter 添加到spinner中  
        choose_download.setAdapter(adapter);  
          
        //添加事件Spinner事件监听    
        choose_download.setOnItemSelectedListener(new SpinnerSelectedListener());  
          
        //设置默认值  
        choose_download.setVisibility(View.VISIBLE);  

		// 绑定触发事件
        btn_finishDownloadChoose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.w("look", "下载文件名："+fileName);
				ActivityUtil.goActivityWithDoData(FtpDesignDownload.this, FtpDo.class, ftpType, fileName);
//				ActivityUtil.toast(FtpDesignDownload.this,"设置完成");
			}
		});
        btn_reChoose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivityUtil.goActivity(FtpDesignDownload.this,
						FtpGo.class);
			}
		});

	}
	 //使用数组形式操作  
    class SpinnerSelectedListener implements OnItemSelectedListener{  
    	
        public void onItemSelected(AdapterView<?> arg0, View arg1, int order,  
                long arg3) {  
        	fileName=downloadFileNames[order]+".rar";
//        	int endIndex=downloadFileNames[order].indexOf(".");
//        	String size=downloadFileNames[order].substring(0,endIndex);
        	size_download.setText(downloadFileNames[order]); 
        	downloadSize=downloadFileNames[order];
        }  
  
        public void onNothingSelected(AdapterView<?> arg0) {
        }  
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
