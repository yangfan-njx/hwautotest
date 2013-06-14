package com.test.hwautotest.ftp;



import com.test.hwautotest.R;

import it.sauronsoftware.ftp4j.FTPClient;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 界面布局
 * 
 * @author huangsq
 * 
 */
public class FtpGo extends Activity {
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
		Button downloadTypeBtn = (Button) findViewById(R.id.button_downloadType);
		Button uploadTypeBtn = (Button) findViewById(R.id.button_uploadType);

		downloadTypeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ActivityUtil.goActivity(FtpGo.this,FtpDesignDownload.class);
//				ActivityUtil.toast(FtpGo.this,"选择下载文件");
			}
		});

		uploadTypeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivityUtil.goActivity(FtpGo.this,FtpDesignUpload.class);
//				ActivityUtil.toast(FtpGo.this,"选择上传文件");
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
