package com.test.hwautotest.ftp;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;

import com.test.hwautotest.R;

import android.os.AsyncTask;
import android.os.Handler;
import android.text.format.Time;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class FtpUtil {
	private static final String TAG = "look";
	
	private static String mFTPHost= "18.8.8.252";
	private static int mFTPPort=21;
	private static String mFTPUser="hwadmin";
	private static String mFTPPassword = "123456";
	public static FTPClient ftpClient=null;
	
	public static FTPClient getFtpClient() {
		return ftpClient;
	}

	public static void setFtpClient(FTPClient ftpClient) {
		FtpUtil.ftpClient = ftpClient;
	}

	public static void setmFTPHost(String mFTPHost) {
		FtpUtil.mFTPHost = mFTPHost;
	}

	public static void setmFTPPort(int mFTPPort) {
		FtpUtil.mFTPPort = mFTPPort;
	}

	public static void setmFTPUser(String mFTPUser) {
		FtpUtil.mFTPUser = mFTPUser;
	}

	public static void setmFTPPassword(String mFTPPassword) {
		FtpUtil.mFTPPassword = mFTPPassword;
	}
	
	
//	public void initFTP(){
//		String host=FtpGo.getmFTPHost();
//		int port=FtpGo.getmFTPPort();
//		String user=FtpGo.getmFTPUser();
//		String passward=FtpGo.getmFTPPassword();
//		if(host==null ||port==-1 || user==null || passward==null){//修改配置
//			
//			
//		}else{//默认配置
//			mFTPHost = "18.8.8.48";
//			mFTPPort=21;
//			mFTPUser = 
//			mFTPPassword
//		}
//		
//	}
	
	
	/**
	 * 连接FTP服务器
	 * 
	 * @param serverUrl
	 *            * @param port
	 * @param username
	 * @param password
	 * @return
	 * 
	 * @author Louis on 2010-11-27 & memoryCat V1.0
	 */

	public static FTPClient connectFtp() {
		
//		mFTPHost = "18.8.8.48";//黄圣权本机地址192.168.1.200 21 ftp  ftp //杨帆路由内网地址18.8.8.252 hwadmin 123456
//		//《下载》Hinet ftp.speed.hinet.net【】服务器192.168.110.93【1,700KB/手机；9.6MB电脑】路由18.8.8.37 【2,539KB/秒手机；8.69MB电脑】; 本机192.168.1.200 【1,088KB/秒手机；154MB/秒-本机】;
//		//上传： 服务器192.168.110.93【1.9MB/手机；8.45MB电脑】路由18.8.8.37 【1.9MB/手机；3.69MB电脑】本机192.168.1.200【1.35MB手机；184MB本机】
//		mFTPPort=21;
//		mFTPUser = "hwadmin";//yangfan gongyy ftp 
//		mFTPPassword = "123456";//gionee@2010 123456 ftp
		
		FTPClient mFTPClient=new FTPClient();

//			boolean errorAndRetry = false ;  //根据不同的异常类型，是否重新捕获
				String[] welcome=null;
				try {
					welcome = mFTPClient.connect(mFTPHost, mFTPPort);
					
					mFTPClient.login(mFTPUser, mFTPPassword);
					if (welcome != null) {
						for (String value : welcome) {
							Log.i("look " ,"v: "+value);
						}
					}else{
						Log.i("look " ,"none !!!");
					}
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.i("look " ,"IllegalStateException"+e);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.i("look " ,"IOException"+e);
				} catch (FTPIllegalReplyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.i("look " ,"FTPIllegalReplyException"+e);
				} catch (FTPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.i("look " ,"FTPException"+e);
				}
				return ftpClient=mFTPClient;
	
	}

	
	public  static void logv(String log) {
		Log.i(TAG, log);
	}
	
}
