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
package com.test.hwautotest.emmagee.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.net.TrafficStats;
import android.util.Log;

/**
 * information of network traffic
 * 
 */
public class TrafficInfo {

	private static final String LOG_TAG = "Emmagee-"
			+ TrafficInfo.class.getSimpleName();

	private String uid;

	public TrafficInfo(String uid) {
		this.uid = uid;
	}

	/**
	 * get total network traffic, which is the sum of upload and download
	 * traffic.
	 * 
	 * @return total traffic include received and send traffic
	 */
	public long getTrafficInfo() {
		Log.i(LOG_TAG+"look", "get traffic information UID:"+uid);
		long rcvTraffic = -1;
		long sndTraffic = -1;

		rcvTraffic=rUid();
		sndTraffic=sUid();
		
//		RandomAccessFile rafRcv = null, rafSnd = null;
//		String rcvPath = "/proc/uid_stat/" + uid + "/tcp_rcv";
//		String sndPath = "/proc/uid_stat/" + uid + "/tcp_snd";
//		
//		try {
//			rafRcv = new RandomAccessFile(rcvPath, "r");
//			rafSnd = new RandomAccessFile(sndPath, "r");
//			rcvTraffic = Long.parseLong(rafRcv.readLine());
//			sndTraffic = Long.parseLong(rafSnd.readLine());
//		} catch (FileNotFoundException e) {
//			rcvTraffic = -1;
//			sndTraffic = -1;
//		} catch (NumberFormatException e) {
//			Log.e(LOG_TAG, "NumberFormatException: " + e.getMessage());
//			e.printStackTrace();
//		} catch (IOException e) {
//			Log.e(LOG_TAG, "IOException: " + e.getMessage());
//			e.printStackTrace();
//		} finally {
//			try {
//				if (rafRcv != null) {
//					rafRcv.close();
//				}
//				if (rafSnd != null)
//					rafSnd.close();
//			} catch (IOException e) {
//				Log.i(LOG_TAG,
//						"close randomAccessFile exception: " + e.getMessage());
//			}
//		}
		Log.i(LOG_TAG+"look", "流量"+rcvTraffic+"/"+sndTraffic);
		if (rcvTraffic == -1 || sndTraffic == -1) {
			return -1;
		} else
			return rcvTraffic + sndTraffic;
	}
	
//	/** 获取手机通过 2G/3G 接收的字节流量总数 */
//    TrafficStats.getMobileRxBytes();
//
//    /** 获取手机通过 2G/3G 接收的数据包总数 */
//    TrafficStats.getMobileRxPackets();
//
//    /** 获取手机通过 2G/3G 发出的字节流量总数 */
//    TrafficStats.getMobileTxBytes();
//
//    /** 获取手机通过 2G/3G 发出的数据包总数 */
//    TrafficStats.getMobileTxPackets();
//
//    /** 获取手机通过所有网络方式接收的字节流量总数(包括 wifi) */
//    TrafficStats.getTotalRxBytes();
//
//    /** 获取手机通过所有网络方式接收的数据包总数(包括 wifi) */
//    TrafficStats.getTotalRxPackets();
//
//    /** 获取手机通过所有网络方式发送的字节流量总数(包括 wifi) */
//    TrafficStats.getTotalTxBytes();
//
//    /** 获取手机通过所有网络方式发送的数据包总数(包括 wifi) */
//    TrafficStats.getTotalTxPackets();
	
	/** 获取手机指定 UID 对应的应程序用通过所有网络方式接收的字节流量总数(包括 wifi) */
	private long rUid(){
		
		return TrafficStats.getUidRxBytes(Integer.valueOf(uid));
	}
	/** 获取手机指定 UID 对应的应程序用通过所有网络方式接收的字节流量总数(包括 wifi) */
	private long sUid(){
		
		return TrafficStats.getUidRxBytes(Integer.valueOf(uid));
	}
	
	
}
