package com.test.hwautotest.ftp;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
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
import java.text.DecimalFormat;
import java.util.Date;

import com.test.hwautotest.R;
import com.test.hwautotest.ftp.FtpDesignDownload.CmdDownLoad;
import com.test.hwautotest.ftp.FtpDesignDownload.CmdUpload;
import com.test.hwautotest.ftp.FtpDesignDownload.ConnectTask;
import com.test.hwautotest.ftp.FtpDesignDownload.CmdUpload.HappenedFTPDataTransferListener;





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
	
	
	public FTPClient mFTPClient;
	Button doBtn;
	public static TextView result;
	
	private final static int ftpType=1;//下载为0，上传为1
	private static String uploadFilePath=null;//上传需要文件完整路径
	private static String uploadFileSize=null;//上传需要文件大小
	
	public String fileName="";//下载实质只需要文件名
	protected static String downloadSize=null;
	
	public boolean isDoing=false;
	AsyncTask<String, Double, Boolean> transTask=null;
	
	private  double allTransferred = 0;
	private double allPassTime = 0;
	private static String fileSize;
	//平均速度
	private double speedAve=0;
	private String speedAveStr;
	//最大速度
	private double speedMax=0;
	private String speedMaxStr;
	//最小速度
	private double speedMin=0;
	private String speedMinStr;
	
	//测试次数
	private int round=0;
	private String roundStr;
	
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

		doBtn = (Button) findViewById(R.id.button_do);
		result = (TextView) findViewById(R.id.result);
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
						"uploadFileSize",uploadFileSize
						,true,"ftpType",ftpType);
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
		
		// 绑定触发事件
 		doBtn.setOnClickListener(new OnClickListener() {
 			
 			@Override
 			public void onClick(View v) {
 				Log.w("look", "上传文件路径：" + uploadFilePath);
				if(uploadFilePath!=null){
					Log.w(TAG, "transTask："+transTask+" isDoing："+isDoing);
	 				if(!isDoing && transTask==null){//未启动状态
	 					new ConnectTask().execute();//连接
	 					switch (ftpType) {
	 					case 0:
//	 						transTask=new CmdDownLoad().execute(fileName);//下载
	 						break;
	 					case 1:
	 						new CmdUpload().execute(uploadFilePath);//上传
	 						break;
	 					}
	 					isDoing=!isDoing;//改变状态
	 				}else if(isDoing && transTask!=null){//可停止状态，强制停止
	 					Date data=new Date();
	 					transTask.cancel(false);
	 							toast("终止中...");
//	 					new DisConnectTask().execute();
	 					isDoing=!isDoing;//改变状态
	 				}
				}else{
					ActivityUtil.toast(FtpDesignUpload.this, "请点“浏览”选择上传文件");
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

public class ConnectTask extends AsyncTask<String,Integer,FTPClient> {
		
		@Override
		protected FTPClient doInBackground(String... params) {
		    
			// TODO Auto-generated method stub
			mFTPClient=FtpUtil.connectFtp();
			Log.i(TAG,mFTPClient.toString());
			return mFTPClient;
		}
		@Override
		protected void onPostExecute(FTPClient result) {
			// TODO Auto-generated method stub
//			toast(mFTPClient.isConnected() ? "连接成功" : "连接失败");
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			
		}
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}
		
	}
	
	public class CmdUpload extends AsyncTask<String, Double, Boolean> {
		protected double totalTransferred = 0;
		protected double fileSize = -1;
		public String path;
		
		public long startTime = 0;
		public long endTime = 0;
		public double passTime = 0;
		public double percent = 0;

		public String transSizeShow;
		public String percentShow;
		// 耗时
		public String passTimeShow;
		// 速度
		public String speedShow;
		// 余时
		public String leftTimeShow;

		public CmdUpload() {
			round++;
		}

		@Override
		protected Boolean doInBackground(String... params) {
			path = params[0];
			String workDir = "/autoTestTemp/upload";// /测试组/autoTestTemp/download
			try {
				
				mFTPClient.setCharset("GB2312");// 金立服务器中文路径需要设置中文GB2312字符集
				mFTPClient.changeDirectory(workDir);
				Log.i(TAG, "currentDirectory:" + mFTPClient.currentDirectory());

				File file = new File(path);
				mFTPClient.upload(file, new HappenedFTPDataTransferListener(
						file.length()));
			} catch (Exception ex) {
				Log.i(TAG, "CmdUpload e:" + ex);
				ex.printStackTrace();
				return false;
			}
			finally{//清理上传的文件
				Log.i(TAG, "删除FTP文件:" + (workDir+path.substring(params[0].lastIndexOf("/"))));
				try {
					mFTPClient.deleteFile(workDir+path.substring(params[0].lastIndexOf("/")));
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPIllegalReplyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return true;
		}


		protected void onProgressUpdate(Double... progress) {
//			String[] p = new String[] { transSizeShow, percentShow,
//					passTimeShow, leftTimeShow, speedShow };
//			result.setText(p[0] + p[1] + "\n" + p[2] + " " + p[3] + "\n" + p[4]);
			result.setText(
					roundStr+"\n"
					+FtpDesignUpload.fileSize+"\n"
					+"[综合速度] "+"\n"
					+speedMaxStr+"\n"
					+speedMinStr+"\n"
					+speedAveStr+"\n"
					+"\n"
					+transSizeShow  + "\n" 
					+ passTimeShow + "\n" 
					+leftTimeShow + "\n" 
					+ speedShow);
		}

		protected void onPostExecute(Boolean result1) {
			transTask = null;
			doBtn.setText(R.string.button_start);
			toast(result1 ? "上传成功" : "上传失败");
			isDoing=!isDoing;//改变状态
			btn_chooseUpload.setEnabled(true);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			doBtn.setText(R.string.button_stop);
			result.setText("(未执行，准备开始！)");
			btn_chooseUpload.setEnabled(false);
		}

		@Override
		protected void onCancelled() {
			transTask = null;
			doBtn.setText(R.string.button_start);
			if(transTask==null){
				toast("上传中断成功");
				btn_chooseUpload.setEnabled(true);
			}else{
				toast("上传中断失败");
			}
			isDoing=!isDoing;//改变状态

		}
		public class HappenedFTPDataTransferListener implements
				FTPDataTransferListener {

			public HappenedFTPDataTransferListener(long fileSizeP) {
				if (fileSizeP < 0) {
					throw new RuntimeException(
							"the size of file muset be larger than zero.");
				}
				fileSize = fileSizeP;
			}

			@Override
			public void started() {
				FtpUtil.logv("FTPDataTransferListener : started");
				totalTransferred = 0;// 清空数据
				startTime = System.currentTimeMillis();
				showResult();
			}

			@Override
			public void transferred(int length) {
				totalTransferred += length;
				showResult();
				if(round==1 && passTime>0 && passTime<500){
					speedMin=speedAve;
				}
			}

			@Override
			public void completed() {
				FtpUtil.logv("FTPDataTransferListener : completed");
				showResult();
				allTransferred+=totalTransferred;//统计到总体数据
				allPassTime+=passTime;//统计到总体用时
				totalTransferred = 0;// 清空当前次数的数据
				transTask = null;
			}

			@Override
			public void aborted() {
				FtpUtil.logv("FTPDataTransferListener : aborted");

				showResult();
				totalTransferred = 0;// 清空数据
				transTask = null;
			}

			@Override
			public void failed() {
				FtpUtil.logv("FTPDataTransferListener : failed");

				showResult();
				totalTransferred = 0;// 清空数据
				transTask = null;
			}

		}

		public void showResult() {
			percent =  (double)totalTransferred/(double)fileSize;
			endTime=System.currentTimeMillis();// 使用java的高精确度毫秒
			passTime=endTime-startTime;
			
			DecimalFormat f = null;
			//百分比
			f = new DecimalFormat("(0.00%)");
			percentShow = f.format(percent);
			// 大小
			f = new DecimalFormat(",##0.000KB");
			String transedSize = f.format(totalTransferred / 1000);
			String totalSize = f.format(fileSize / 1000);
			transSizeShow = "[已传输]" + percentShow+transedSize + "/" + totalSize;

			

			// 耗时
			f = new DecimalFormat("0.000秒");
			passTimeShow = "[耗时]" + f.format(passTime / 1000.000);

			// 速度
			f = new DecimalFormat(",##0KB/秒");
			speedShow = "[速度]" + f.format(totalTransferred / passTime);
			

			// 余时
			f = new DecimalFormat("0.000秒");
			leftTimeShow = "[余时]"
					+ f.format((fileSize - totalTransferred)
							/ (totalTransferred / passTime) / 1000.000);

			// 次数
			roundStr="[测试次数]"+round+"次";
				
			// 综合速度
			///平均
			f = new DecimalFormat(",##0KB/秒");
			speedAve=(allTransferred+totalTransferred) /(allPassTime+ passTime);
			speedAveStr="[平均]"+ f.format(speedAve);

			///最大
			if(speedAve>speedMax){
				speedMax=speedAve;
			}
			speedMaxStr="[最大]"+f.format(speedMax);
			///最小
			if(speedAve<speedMin){
				speedMin=speedAve;
			}
			speedMinStr="[最小]"+f.format(speedMin);
			
			//上传大小
			FtpDesignUpload.fileSize="[文件大小]"+FtpDesignUpload.getUploadFileSize();
			
			publishProgress(totalTransferred);
		}
	}
	
//	public class CmdDownLoad extends AsyncTask<String, Double, Boolean> {
//		protected double totalTransferred = 0;
//		protected double fileSize = -1;
//
//		public long startTime = 0;
//		public long endTime = 0;
//		public double passTime = 0;
//		public double percent = 0;
//
//		public String transSizeShow;
//		public String percentShow;
//		// 耗时
//		public String passTimeShow;
//		// 速度
//		public String speedShow;
//		// 余时
//		public String leftTimeShow;
//		
//
//		public CmdDownLoad() {
//			round++;//统计执行次数
//		}
//
//		@Override
//		protected Boolean doInBackground(String... params) {
//
//			String workDir = "/autoTestTemp/download";// /测试组/autoTestTemp/download
//			String downFileName = params[0];// "test_001m.zip";//CR00715254.rar
//											// G.apk
//			String localPath = Environment.getExternalStorageDirectory()
//					.toString();
//			localPath += "/autoTestTemp/download";
//			try {
//				mFTPClient.setCharset("GB2312");// 金立服务器中文路径需要设置中文GB2312字符集
//				mFTPClient.changeDirectory(workDir);// //测试组/autoTestTemp/download/
//				Log.i(TAG, "currentDirectory:" + mFTPClient.currentDirectory());
//
//				createDir(localPath);
//				mFTPClient.download(downFileName, new File(localPath + "/"
//						+ downFileName), new HappenedFTPDataTransferListener(
//						mFTPClient.fileSize(workDir + "/" + downFileName)));
//
//			} catch (Exception ex) {
//				Log.i(TAG, "CmdDownload e:" + ex);
//				ex.printStackTrace();
//				return false;
//			} finally {
//				clearDownloadFile(localPath);
//			}
//			return true;
//		}
//
//		protected void onProgressUpdate(Double... progress) {
////			String[] p = new String[] { transSizeShow, percentShow,
////					passTimeShow, leftTimeShow, speedShow };
////			result.setText(p[0] + p[1] + "\n" + p[2] + " " + p[3] + "\n" + p[4]);
//			result.setText(
//					roundStr+"\n"
//					+FtpDesignDownload.fileSize+"\n"
//					+"[综合速度] "+"\n"
//					+speedMaxStr+"\n"
//					+speedMinStr+"\n"
//					+speedAveStr+"\n"
//					+"\n"
//					+transSizeShow  + "\n" 
//					+ passTimeShow + "\n" 
//					+leftTimeShow + "\n" 
//					+ speedShow);
//		}
//
//		protected void onPostExecute(Boolean result1) {
//			transTask = null;
//			doBtn.setText(R.string.button_start);
//			toast(result1 ? "下载成功" : "下载失败");
//			isDoing=!isDoing;//改变状态
//			choose_download.setEnabled(true);
//			
//		}
//
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//			doBtn.setText(R.string.button_stop);
//			result.setText("(未执行，准备开始！)");
//			choose_download.setEnabled(false);
//		}
//
//		@Override
//		protected void onCancelled() {
//			transTask = null;
//			doBtn.setText(R.string.button_start);
//			if(transTask==null){
//				toast("下载中断成功");
//				choose_download.setEnabled(true);
//			}else{
//				toast("下载中断失败");
//			}
//			isDoing=!isDoing;//改变状态
//
//		}
//
//		public class HappenedFTPDataTransferListener implements
//				FTPDataTransferListener {
//
//			public HappenedFTPDataTransferListener(long fileSizeP) {
//				if (fileSizeP <0) {
//					throw new RuntimeException(
//							"the size of file muset be larger than zero.");
//				}
//				fileSize = fileSizeP;
//			}
//			@Override
//			public void started() {
//				FtpUtil.logv("FTPDataTransferListener : started");
//				totalTransferred = 0;// 清空数据
//				startTime=System.currentTimeMillis();
//				showResult();
//				
//			}
//
//			@Override
//			public void transferred(int length) {
//				totalTransferred += length;
//				showResult();
//				if(round==1 && passTime>0 && passTime<500){
//					speedMin=speedAve;
//				}
//				
//			}
//			@Override
//			public void completed() {
//				FtpUtil.logv("FTPDataTransferListener : completed");
//				showResult();
//				allTransferred+=totalTransferred;//统计到总体数据
//				allPassTime+=passTime;//统计到总体用时
//				totalTransferred = 0;// 清空当前次数的数据
//				transTask = null;
//			}
//			@Override
//			public void aborted() {
//				FtpUtil.logv("FTPDataTransferListener : aborted");
//
//				showResult();
//				totalTransferred = 0;// 清空数据
//				transTask=null;
//			}
//
//			@Override
//			public void failed() {
//				FtpUtil.logv("FTPDataTransferListener : failed");
//				
//				showResult();
//				totalTransferred = 0;// 清空数据
//				transTask=null;
//			}
//
//			
//		}
//		public  void showResult(){
//			percent =  (double)totalTransferred/(double)fileSize;
//			endTime=System.currentTimeMillis();// 使用java的高精确度毫秒
//			passTime=endTime-startTime;
//			
//			DecimalFormat f = null;
//			//百分比
//			f = new DecimalFormat("(0.00%)");
//			percentShow = f.format(percent);
//			// 大小
//			f = new DecimalFormat(",###.000KB");
//			String transedSize = f.format(totalTransferred / 1000);
//			String totalSize = f.format(fileSize / 1000);
//			transSizeShow = "[已传输]" + percentShow+transedSize + "/" + totalSize;
//
//
//			// 耗时
//			f = new DecimalFormat("0.000秒");
//			passTimeShow = "[耗时]" + f.format(passTime / 1000.000);
//
//			// 速度
//			f = new DecimalFormat(",###KB/秒");
//			speedShow = "[速度]" + f.format(totalTransferred / passTime);
//			
//
//			// 余时
//			f = new DecimalFormat("0.000秒");
//			leftTimeShow = "[余时]"
//					+ f.format((fileSize - totalTransferred)
//							/ (totalTransferred / passTime) / 1000.000);
//
//			// 次数
//			roundStr="[测试次数]"+round+"次";
//				
//			// 综合速度
//			///平均
//			f = new DecimalFormat(",###KB/秒");
//			speedAve=(allTransferred+totalTransferred) /(allPassTime+ passTime);
//			speedAveStr="[平均]"+ f.format(speedAve);
//
//			///最大
//			if(speedAve>speedMax){
//				speedMax=speedAve;
//			}
//			speedMaxStr="[最大]"+f.format(speedMax);
//			///最小
//			if(speedAve<speedMin){
//				speedMin=speedAve;
//			}
//			speedMinStr="[最小]"+f.format(speedMin);
//			
//			//下载大小
//			FtpDesignDownload.fileSize="[文件大小]"+FtpDesignDownload.getDownloadSize();
//			
//			publishProgress(totalTransferred);
//		}
//	}
}
