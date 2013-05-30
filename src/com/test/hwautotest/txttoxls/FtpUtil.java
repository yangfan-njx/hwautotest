package com.test.hwautotest.txttoxls;


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

import android.os.AsyncTask;
import android.os.Handler;
import android.text.format.Time;
import android.util.Log;
import android.widget.TextView;

public class FtpUtil {
	private static final String TAG = "YANG";
	
//	protected  static double totolTransferred = 0;
//	protected  static double fileSize = -1;
//	
//	private static Time time=new Time();
//	private static long startTime=0;
//	private static long endTime=0;
//	private static double passTime=0;
//	private static float percent = 0;
//	
//	
//	public static String transSize;
//	public static String percent2 ;
//
//	// 耗时
//	public static String passTime2 ;
//
//	// 速度
//	public static String speed ;
//
//	// 余时
//	public static String leftTime ;
	
	
	
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

	public static FTPClient connectFtp(String[] params) {
		String mFTPHost;
		int mFTPPort;
		String mFTPUser;
		String mFTPPassword;
//		mFTPHost = "192.168.1.200";
//		mFTPUser = "ftp";//yangfan gongyy ftp 
//		mFTPPassword = "ftp";//gionee@2010 123456 ftp
		mFTPHost = params[0];
		mFTPPort=Integer.valueOf(params[1]);
		mFTPUser = params[2];//yangfan gongyy ftp 
		mFTPPassword = params[3];//gionee@2010 123456 ftp
		
		
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
				return mFTPClient;
	
	}

//	public static class HappenedFTPDataTransferListener implements
//			FTPDataTransferListener {
//
////		private int totolTransferred = 0;
////		private long fileSize = -1;
////		
////		Time time=new Time();
////		private long startTime=0;
////		private long endTime=0;
////		public long passTime=0;
////		public float percent = 0;
//		
//		public HappenedFTPDataTransferListener(long fileSize) {
//			if (fileSize <= 0) {
//				throw new RuntimeException(
//						"the size of file muset be larger than zero.");
//			}
//			FtpUtil.fileSize = fileSize;
//		}
//
//		@Override
//		public void aborted() {
//			// TODO Auto-generated method stub
//			logv("FTPDataTransferListener : aborted");
//			
////			percent = (float) (totolTransferred / FtpUtil.fileSize);
//			time.setToNow();
//			endTime=time.toMillis(false);
//			passTime=endTime-startTime;
////			logv(" 百分比:" + percent*100.00+"%");
////			logv(" 耗时s:"+passTime);
////			logv(" 速度:" + totolTransferred/passTime);
//			totolTransferred=0;//清空数据
//		}
//
//		@Override
//		public void completed() {
//			logv("FTPDataTransferListener : completed");
//			time.setToNow();
//			endTime=time.toMillis(false);
//			passTime=endTime-startTime;
//			totolTransferred=0;//清空数据
//			
//		}
//
//		@Override
//		public void failed() {
//			// TODO Auto-generated method stub
//			logv("FTPDataTransferListener : failed");
//			logResult();
//			
//			DecimalFormat f = null;
//
//			// 大小
//			f = new DecimalFormat(",###KB");
//			String transedSize = f.format(FtpUtil.totolTransferred / 1000);
//			String totalSize = f.format(FtpUtil.fileSize / 1000);
//			String transSize = "已传输：" + transedSize + "/" + totalSize;
//
//			f = new DecimalFormat("(#.##%)");
//			String percent = f.format(FtpUtil.totolTransferred
//					/ FtpUtil.fileSize);
//
//			// 耗时
//			f = new DecimalFormat("#.###秒");
//			String passTime = "耗时:" + f.format(FtpUtil.passTime / 1000.000);
//
//			// 速度
//			f = new DecimalFormat(",###KB/秒");
//			String speed = " 速度:"
//					+ f.format(FtpUtil.totolTransferred / FtpUtil.passTime);
//
//			// 余时
//			f = new DecimalFormat("#.###秒");
//			String leftTime = "余时:"
//					+ f.format((FtpUtil.totolTransferred - FtpUtil.fileSize)
//							/ (FtpUtil.totolTransferred / FtpUtil.passTime)
//							/ 1000.000);
////			TextView result = FtpDo.result;
////			result.setText(transSize + percent + "\n" + passTime + " "
////					+ leftTime + "\n" + speed);
//			
//			
//			totolTransferred=0;//清空数据
//		}
//
//		@Override
//		public void started() {
//			totolTransferred=0;//清空数据
//			// TODO Auto-generated method stub
//			logv("FTPDataTransferListener : started");
//			time.setToNow();
////			FtpUtil.startTime=time.toMillis(false);
//			FtpUtil.startTime=System.currentTimeMillis();//使用java的高精确度毫秒
//			
//			time.setToNow();
//			endTime=time.toMillis(false);
//			passTime=endTime-startTime;
//		}
//
//		@Override
//		public void transferred(int length) {
//			FtpDo.downTask.(FtpUtil.totolTransferred);
//			totolTransferred += length;
//			logResult();
//			
//			DecimalFormat f = null;
//
//			// 大小
//			f = new DecimalFormat(",###KB");
//			String transedSize = f.format(FtpUtil.totolTransferred / 1000);
//			String totalSize = f.format(FtpUtil.fileSize / 1000);
//			 transSize = "已传输：" + transedSize + "/" + totalSize;
//
//			f = new DecimalFormat("(#.##%)");
//			 percent2 = f.format(FtpUtil.totolTransferred
//					/ FtpUtil.fileSize);
//
//			// 耗时
//			f = new DecimalFormat("#.###秒");
//			 passTime2 = "耗时:" + f.format(FtpUtil.passTime / 1000.000);
//
//			// 速度
//			f = new DecimalFormat(",###KB/秒");
//			 speed = " 速度:"
//					+ f.format(FtpUtil.totolTransferred / FtpUtil.passTime);
//
//			// 余时
//			f = new DecimalFormat("#.###秒");
//			 leftTime = "余时:"
//					+ f.format((FtpUtil.totolTransferred - FtpUtil.fileSize)
//							/ (FtpUtil.totolTransferred / FtpUtil.passTime)
//							/ 1000.000);
//			//不能在后台执行UI操作
////			TextView result = FtpDo.result;
////			result.setText(transSize + percent + "\n" + passTime + " "
////					+ leftTime + "\n" + speed);
//		}
//	}
	
	public  static void logv(String log) {
		Log.i(TAG, log);
	}
	
//	public static void logResult(){
//		percent =  (float)totolTransferred/(float)fileSize;
//		
////		time.setToNow();
////		endTime=time.toMillis(false);
//		endTime=System.currentTimeMillis();
//		
//		passTime=endTime-startTime;
//		
//		DecimalFormat f = null;
//		
//		//大小
//		f=new DecimalFormat(",###KB");
//		logv("已传输： " + f.format(totolTransferred/1000));
//		logv("总大小： " + f.format(fileSize/1000));
//		
//		f = new DecimalFormat("#.##%");
//		logv(" 百分比:" + f.format(totolTransferred/fileSize));
//		
//		//耗时
//		f=new DecimalFormat("#.###秒");
//		logv(" 耗时:"+f.format(passTime/1000.000));
//		
//		//速度
//		f = new DecimalFormat(",###KB/秒");
//		Log.i(TAG, " 速度:" + f.format(totolTransferred/passTime));
//	}
	
}
