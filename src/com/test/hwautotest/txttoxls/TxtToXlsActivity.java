package com.test.hwautotest.txttoxls;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemProperties;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gionee.hwautotest.R;

public class TxtToXlsActivity extends Activity {

	private Button ft_btn;
	private Button st_btn;
	private ProgressBar mProgressBar;
	private String filePath = getSDPath() + "/TestReport/";
	private XlsOperate xls = new XlsOperate(filePath);
	private File dir;
	private File caseTemplate;// 测试模板
	private String version;
	private String testReportName;
	private ArrayList<String> dirNames;
	private boolean isExist;
	
	private FTPClient mFTPClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.txttoxsl);

		ft_btn = (Button) findViewById(R.id.button1);
		st_btn = (Button) findViewById(R.id.button2);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		version = SystemProperties.get("ro.gn.extvernumber");// 获得软件版本

		dir = new File(getSDPath() + "/TestReport");// 创建文件夹对象
		// 如果没有文件夹，创建文件夹
		if (!dir.exists()) {
			dir.mkdirs();
		}
		dir = new File(getSDPath() + "/dzsoftSmart/taskLogs/");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		dirNames = listAllFiles(TxtToXlsActivity.this, getSDPath() + "/dzsoftSmart/taskLogs/");

		ft_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				caseTemplate = new File(getSDPath()
						+ "/TestReport/ft_testCase.xls");// FT测试模板
				testReportName = version + "_AutoTestReport.xls";

				// 如果没有模板则拷贝模板
//				if (!caseTemplate.exists()) {
					getReportTemplate("ft_testCase");
//				}
				// 检查数据源
				isExist = true;
				for(int i = 0; i < dirNames.size(); i++){
					isExist = isExist && fileIsExists(dirNames.get(i) + "/taskSummary.txt") 
							&& fileIsExists(getSDPath() + "/TestReport/ft_testCase.xls");
					if(!isExist){
						break;
					}
				}	
				if (isExist) {
					new creatTestReport().execute("ft_testCase");// 异步写入数据
				} else {
					Toast.makeText(TxtToXlsActivity.this, "缺少生成报告的数据文件",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		st_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				caseTemplate = new File(getSDPath()
						+ "/TestReport/st_testCase.xls");// FT测试模板
				testReportName = version + "_StableTestReport.xls";

				// 如果没有模板则拷贝模板
//				if (!caseTemplate.exists()) {//每次动态更新，无论有无
					getReportTemplate("st_testCase");
//				}

				// 检查数据源
				isExist = true;
				for(int i = 0; i < dirNames.size(); i++){
					isExist = isExist && fileIsExists(dirNames.get(i) + "/taskSummary.txt") 
							&& fileIsExists(getSDPath() + "/TestReport/st_testCase.xls");
					Log.i("YANG", fileIsExists(dirNames.get(i) + "/taskSummary.txt") + "");
					if(!isExist){
						break;
					}
				}	
				if (isExist) {
					new creatTestReport().execute("st_testCase");// 异步写入数据
				} else {
					Toast.makeText(TxtToXlsActivity.this, "缺少生成报告的数据文件",
							Toast.LENGTH_SHORT).show();
				}	
				Log.i("YANG", "---------------end-------------------");
			}
		});

	}

	class creatTestReport extends AsyncTask<String, Integer, String> {
		/**
		 * AsyncTask的3个参数： 第一个是doInBackground的传入参数类型 第二个是onProgressUpdate的传入参数类型
		 * 第三个是doInBackground的返回值类型，此返回值是onPostExecute的传入参数
		 */
		@Override
		protected void onPreExecute() {
			mProgressBar.setProgress(0);// 进度条复位
			Toast.makeText(TxtToXlsActivity.this, "开始生成报告", Toast.LENGTH_SHORT)
					.show();
			mProgressBar.setVisibility(View.VISIBLE);
			mProgressBar.setIndeterminate(true);// 设置为不可控进度条
		}

		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			publishProgress(0);
			
			for(int i = 0; i < dirNames.size(); i++){
				Log.i("YANG", "@@@@@@@@@@@@@@@@@@");
				ArrayList<String> taskSummarys = xls.ReadTxtFile(dirNames.get(i)
						+ "/taskSummary.txt");
				HashSet<String> set = xls.fillResult(taskSummarys, params[0]);
				xls.removeSheetRow(set, params[0]);
			}
			// 重命名文件
			new File(getSDPath() + "/TestReport/" + params[0] + ".xls")
					.renameTo(new File(getSDPath() + "/TestReport/"
							+ testReportName));
			
			publishProgress(100);
			return null;
		}

		protected void onProgressUpdate(Integer... progress) {
			mProgressBar.setProgress(progress[0]);
		}

		protected void onPostExecute(String result) {
			Toast.makeText(TxtToXlsActivity.this, "完成", Toast.LENGTH_SHORT)
					.show();
			mProgressBar.setVisibility(View.GONE);
		}

		protected void onCancelled() {
			mProgressBar.setProgress(0);
		}
	}

	public String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		// new Environment().isExternalStorageRemovable();
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
		}
		return sdDir.toString();
	}

	public boolean fileIsExists(String path) {
		File f = new File(path);
		if (!f.exists()) {
			return false;
		} else {
			return true;
		}
	}

	public void getReportTemplate(String templateName) {
		//FTP操作
		String[] connectInfo=new String[]{"y383840206.oicp.net","21","hwtest","123456"};//FTP地址、端口、用户、密码
		String[] downloadInfo=new String[]{"/TestReport",templateName+".xls"};//FTP下载路径、下载文件名
		new ConnectFTP().execute(connectInfo);//连接
		new DownLoadFTP().execute(downloadInfo);//下载
		
//		try {
//			InputStream inputStream = getAssets().open(templateName + ".xls");
//			FileOutputStream fos = new FileOutputStream(getSDPath()
//					+ "/TestReport/" + templateName + ".xls");
//			int count = 0;
//			byte[] buffer = new byte[1024];
//			while ((count = inputStream.read(buffer)) > 0) {
//				fos.write(buffer, 0, count);
//			}
//			fos.close();
//			inputStream.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	public ArrayList<String> listAllFiles(Context context,String dirName){// 列出目录下所有的文件夹
		ArrayList<String> dirNames = new ArrayList<String>();
		// 如果dir不以文件分隔符结尾，自动添加文件分隔符
		if (!dirName.endsWith(File.separator)) {
			dirName = dirName + File.separator;
		}
		File dirFile = new File(dirName);
		if (!dirFile.isDirectory()) {
			Toast.makeText(context, "找不到目录：" + dirName, Toast.LENGTH_SHORT).show();
		}

		// 列出文件夹下所有的文件
		File[] files = dirFile.listFiles();

		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				dirNames.add(files[i].toString());
				Log.i("YANG", files[i].toString());
			}
		}
		return dirNames;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * 异步连接FTP
	 * @author huangsq
	 *	【参数】1个字符数组
	 *	【参数内容】
	 *	1、地址"192.168.1.200" 
	 *	2、端口"21"
	 *	3、用户名"ftp"
	 *	4、密码"ftp"
	 */
	class ConnectFTP extends AsyncTask<String,Integer,FTPClient> {
		@Override
		protected FTPClient doInBackground(String... params) {
			// TODO Auto-generated method stub
			mFTPClient=FtpUtil.connectFtp(params);
			Log.i("YANG",mFTPClient.toString());
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
	
	/**
	 * <连接成功前提下>异步下载FTP
	 * @author huangsq
	 *
	 */
	public class DownLoadFTP extends AsyncTask<String, Double, Boolean> {
		protected double totolTransferred = 0;
		protected double fileSize = -1;

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

		public DownLoadFTP() {
		}

		@Override
		protected Boolean doInBackground(String... params) {

			String workDir = params[0];// /测试组/autoTestTemp/download
			String downFileName = params[1];// "test_001m.zip";//CR00715254.rar
											// G.apk
			String localPath = Environment.getExternalStorageDirectory()
					.toString();
			localPath += workDir;
			try {
				mFTPClient.setCharset("GB2312");// 金立服务器中文路径需要设置中文GB2312字符集
				mFTPClient.changeDirectory(workDir);// //测试组/autoTestTemp/download/
				Log.i("YANG", "currentDirectory:" + mFTPClient.currentDirectory());

				createDir(localPath);
				mFTPClient.download(downFileName, new File(localPath + "/"
						+ downFileName), new HappenedFTPDataTransferListener(
						mFTPClient.fileSize(workDir + "/" + downFileName)));

			} catch (Exception ex) {
				Log.i("YANG", "CmdDownload e:" + ex);
				ex.printStackTrace();
				return false;
			} finally {
//				clearDownloadFile(localPath);
			}
			return true;
		}

		protected void onProgressUpdate(Double... progress) {
//			String[] p = new String[] { transSizeShow, percentShow,
//					passTimeShow, leftTimeShow, speedShow };
//			result.setText(p[0] + p[1] + "\n" + p[2] + " " + p[3] + "\n" + p[4]);
		}

		protected void onPostExecute(Boolean result1) {
//			transTask = null;
//			doBtn.setText(R.string.button_start);
//			toast(result1 ? "下载成功" : "下载失败");
//			isDoing=!isDoing;//改变状态
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
//			doBtn.setText(R.string.button_stop);
//			result.setText("(未执行，准备开始！)");
		}

		@Override
		protected void onCancelled() {
//			transTask = null;
//			doBtn.setText(R.string.button_start);
//			if(transTask==null){
//				toast("下载中断成功");
//			}else{
//				toast("下载中断失败");
//			}
//			isDoing=!isDoing;//改变状态

		}

		public class HappenedFTPDataTransferListener implements
				FTPDataTransferListener {

			public HappenedFTPDataTransferListener(long fileSizeP) {
				if (fileSizeP <0) {
					throw new RuntimeException(
							"the size of file muset be larger than zero.");
				}
				fileSize = fileSizeP;
			}
			@Override
			public void started() {
				FtpUtil.logv("FTPDataTransferListener : started");
				totolTransferred = 0;// 清空数据
				startTime=System.currentTimeMillis();
//				showResult();
			}

			@Override
			public void transferred(int length) {
				totolTransferred += length;
//				showResult();
			}
			@Override
			public void completed() {
				FtpUtil.logv("FTPDataTransferListener : completed");
//				showResult();
				totolTransferred = 0;// 清空数据
//				transTask=null;
			}
			@Override
			public void aborted() {
				FtpUtil.logv("FTPDataTransferListener : aborted");

//				showResult();
				totolTransferred = 0;// 清空数据
//				transTask=null;
			}

			@Override
			public void failed() {
				FtpUtil.logv("FTPDataTransferListener : failed");
				
//				showResult();
				totolTransferred = 0;// 清空数据
//				transTask=null;
			}

			
		}
//		public  void showResult(){
//			percent =  (double)totolTransferred/(double)fileSize;
//			endTime=System.currentTimeMillis();// 使用java的高精确度毫秒
//			passTime=endTime-startTime;
//			
//			DecimalFormat f = null;
//			// 大小
//			f = new DecimalFormat(",###.000KB");
//			String transedSize = f.format(totolTransferred / 1000);
//			String totalSize = f.format(fileSize / 1000);
//			transSizeShow = "已传输：" + transedSize + "/" + totalSize;
//
//			f = new DecimalFormat("(#.##%)");
//			percentShow = f.format(percent);
//
//			// 耗时
//			f = new DecimalFormat("0.000秒");
//			passTimeShow = "耗时:" + f.format(passTime / 1000.000);
//
//			// 速度
//			f = new DecimalFormat(",###KB/秒");
//			speedShow = "速度:" + f.format(totolTransferred / passTime);
//
//			// 余时
//			f = new DecimalFormat("0.000秒");
//				leftTimeShow = "余时:"
//					+ f.format((fileSize-totolTransferred)
//							/ (totolTransferred / passTime) / 1000.000);
//			
//			publishProgress(totolTransferred);
//		}
	}
	
	/**
	 * 在手机上创建文件夹
	 * @param writePath
	 */
	public static void createDir(String writePath){
		File txtFileAll = new File(writePath);// 创建文件夹
		if (!txtFileAll.exists()) {// 文件夹已有
			Log.w("YANG", "创建文件夹" + txtFileAll.mkdirs());
		}
	}
	/**
	 * 在手机上删除文件夹
	 * @param path
	 */
	public static void clearDownloadFile(String path) { 
		File file=new File(path);
	    if (!file.exists())  
	        return;  
	    if (file.isFile()) {  
	    	Log.w("YANG", "删除文件"+file.delete());  
	        return;  
	    }  
	    File[] files = file.listFiles();  
	    for (int i = 0; i < files.length; i++) {
	    	clearDownloadFile(files[i].getPath());//递归删除
	    }  
	    Log.w("YANG", "删除文件夹"+file.delete());  
	}
}
