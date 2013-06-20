package com.test.hwautotest.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;

import com.test.hwautotest.R;

public class updateOnline extends Activity{
	private HashMap<String, String> hashMap;
	private String updateVersionXMLPath;
	private boolean isUpdate = false;
	private String serverVerName = "";// 新版本名称
	private int serverCode = -1;// 新版本号
	private ProgressDialog pd = null;
	private String UPDATE_SERVERAPK;
	private String updateApkPath;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update);

		updateVersionXMLPath = "http://192.168.1.44/ver.xml";
		UPDATE_SERVERAPK = "ApkUpdateAndroid.apk";
		new creatTestReport().execute();
		
	}

	class creatTestReport extends AsyncTask<String, Integer, String> {
		/**
		 * AsyncTask的3个参数： 第一个是doInBackground的传入参数类型 第二个是onProgressUpdate的传入参数类型
		 * 第三个是doInBackground的返回值类型，此返回值是onPostExecute的传入参数
		 */
		protected void onPreExecute() {
			Log.i("YANG", "onpreexecute");
		}

		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			isUpdate = isUpdate(updateOnline.this);
			return null;
		}

		protected void onProgressUpdate(Integer... progress) {
		}

		protected void onPostExecute(String result) {
			if (isUpdate) {
				doNewVersionUpdate();
			} 
			else {
				notNewVersionUpdate();
			}
		}

		protected void onCancelled() {
		}
	}

	/**
	 * 判断是否可以升级
	 * 
	 * @param context
	 * @return
	 */
	private boolean isUpdate(Context context) {
		int versionCode = getVerCode(context);
		try {
			// 把version.xml放到网络上，然后获取文件信息
			URL url = new URL(updateVersionXMLPath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(5 * 1000);
			conn.setRequestMethod("GET");// 必须要大写
			InputStream inputStream = conn.getInputStream();
			// 解析XML文件。
			ParseXmlService service = new ParseXmlService();
			hashMap = service.parseXml(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null != hashMap) {
			serverCode = Integer.valueOf(hashMap.get("versionCode"));
			serverVerName = hashMap.get("fileName");
			updateApkPath = hashMap.get("loadUrl");
			// 版本判断
			if (serverCode > versionCode) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获得版本号
	 */
	public int getVerCode(Context context) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo(
					"com.test.hwautotest", 0).versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			Log.e("版本号获取异常", e.getMessage());
		}
		return verCode;
	}

	/**
	 * 获得版本名称
	 */
	public String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(
					"com.test.hwautotest", 0).versionName;
		} catch (NameNotFoundException e) {
			Log.e("版本名称获取异常", e.getMessage());
		}
		return verName;
	}

	/**
	 * 不更新版本
	 */
	public void notNewVersionUpdate() {
		int verCode = this.getVerCode(this);
		String verName = this.getVerName(this);
		StringBuffer sb = new StringBuffer();
//		sb.append("当前版本：");
//		sb.append(verName);
//		sb.append(" Code:");
//		sb.append(verCode);
		sb.append("已是最新版本，无需更新");
		Dialog dialog = new AlertDialog.Builder(this).setTitle("软件更新")
				.setMessage(sb.toString())
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						finish();
					}
				}).create();
		dialog.show();
	}

	/**
	 * 更新版本
	 */
	public void doNewVersionUpdate() {
		int verCode = this.getVerCode(this);
		String verName = this.getVerName(this);
		StringBuffer sb = new StringBuffer();
		sb.append("当前版本：V");
		sb.append(verName);
//		sb.append(" Code:");
//		sb.append(verCode);
		sb.append("，发现新版本：V");
		sb.append(serverVerName);
//		sb.append(" Code:");
//		sb.append(verCode);
		sb.append("，是否更新？");
		Dialog dialog = new AlertDialog.Builder(this)
				.setTitle("软件更新")
				.setMessage(sb.toString())
				.setPositiveButton("更新", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						pd = new ProgressDialog(updateOnline.this);
						pd.setTitle("正在下载");
						pd.setMessage("请稍后。。。");
						pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						downFile(updateApkPath);
					}
				})
				.setNegativeButton("暂不更新",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								finish();
							}
						}).create();
		// 显示更新框
		dialog.show();
	}

	/**
	 * 下载apk
	 */
	public void downFile(final String url) {
		pd.show();
		new Thread() {
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					long length = entity.getContentLength();
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is != null) {
						File file = new File(
								Environment.getExternalStorageDirectory(),UPDATE_SERVERAPK);
						fileOutputStream = new FileOutputStream(file);
						byte[] b = new byte[1024];
						int charb = -1;
						int count = 0;
						while ((charb = is.read(b)) != -1) {
							fileOutputStream.write(b, 0, charb);
							count += charb;
						}
					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
					down();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			pd.cancel();
			update();
		}
	};

	/**
	 * 下载完成，通过handler将下载对话框取消
	 */
	public void down() {
		new Thread() {
			public void run() {
				Message message = handler.obtainMessage();
				handler.sendMessage(message);
			}
		}.start();
	}

	/**
	 * 安装应用
	 */
	public void update() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(Environment
				.getExternalStorageDirectory(), UPDATE_SERVERAPK)),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}
}
