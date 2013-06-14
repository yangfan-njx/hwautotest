package com.test.utils;

import java.io.File;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

public class Utils {
	
	public Context mContext;
	
	public Utils(Context mContext){
		this.mContext = mContext;
	}
	
	/**
	 * 显示Toast
	 * @param string
	 */
	public void DisplayToast(String string) {
		// TODO Auto-generated method stub
		Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
	}
	
	public String getSdPath(){
		return Environment.getExternalStorageDirectory().getPath();
	}
	
	public String getInternalPath(){
		String InternalPath;
		if(isSdExists()){
			if(isContain("mnt", getSdPath())){
				InternalPath = "/mnt/sdcard2";
			}else{
				InternalPath = "/storage/sdcard1";
			}
		}else{
			InternalPath = getSdPath();
		}
		
		return InternalPath;
		
		
	}
	
	
	public String getPath(){
		
		String InternalPath;
		if(isContain("mnt", getSdPath())){
			InternalPath = "/mnt/sdcard2";
		}else{
			InternalPath = "/storage/sdcard1";
		}
		return InternalPath;
	}
	
	/**
	 * 
	 * 判断路径中是否包含字符
	 * @param word
	 * @param sentence
	 * @return
	 */
	public boolean isContain(String word,String sentence){
		return Pattern.compile(word).matcher(sentence).find();
	}
	
	
	public boolean isSdExists(){
		return Environment.isExternalStorageRemovable();
	}
	
	
	
	
}
