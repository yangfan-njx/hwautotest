package com.test.hwautotest.filemanager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class FilemanagerUtils {
	private long TotalSize = 0;
	private long UsedSize = 0;
	private Context mContext;
	
	public FilemanagerUtils(Context mContext){
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
	
	/**
	 * 创建指定目录
	 * @param path
	 * @return
	 */

	public File creatPath(String path,int i){
		String PATH = path + "/" + "HWAutoTest";
		File road =  new File(PATH);
		if(!road.exists()){
			road.mkdirs();
		}
		File file = new File(PATH + "/"+getDate()+i);
		return file;
	}
	
	/**
	 * 获取所要添加文件大小
	 * @param seekBar
	 * @param path
	 * @return
	 */
	public int getSize(SeekBar seekBar,String path){
		return (int) (seekBar.getProgress() - (usedSize(path)/1024/1024));
	}
	/**
	 * 添加1M大小的文件至指定目
	 * @param path
	 * @param size
	 * @throws IOException 
	 */
	public void addFile(File file) throws IOException{
		FileOutputStream fos = new FileOutputStream(file);
		try{
			byte[] buffer = new byte[1024 * 1024];
			fos.write(buffer);
			fos.close();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			fos.close();
			
		}
		
	}
	
	/**
	 * 获取当前时间 用于文件名
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public String getDate(){
		Date currentDate = new Date(System.currentTimeMillis());;//获取当前时间
        SimpleDateFormat date = new SimpleDateFormat("yyyyMMddhhmmss");
        return date.format(currentDate);
        
	}
	
	/**
	 * 设置拖动条的初始值
	 * @param path
	 * @param seekBar
	 */
	public void setDefault(String path,SeekBar seekBar,TextView textView){
		TotalSize = totalSize(path);
		UsedSize = usedSize(path);
		
		Log.i("look",TotalSize + " "+ UsedSize);
		int max = (int)(TotalSize/1024/1024);
		int use = (int) (UsedSize/1024/1024);
		seekBar.setMax(max);
		seekBar.setProgress(use);
		textView.setText(getText(TotalSize,UsedSize));
	}
	
	
	/**
	 * 获取textView上显示的文本
	 * @param used
	 * @param total
	 * @return
	 */
	public String getText(long total,long used){
		String text =  null;
		text = formatSize(used)+"/"+formatSize(total);
		System.out.println(text);
		return text;
	}
	
	/**
	 * 获取指定路径容量的总大小
	 * @param path
	 * @return
	 */
    public long totalSize(String path){
    	StatFs stat = new StatFs(path); 
    	long blockSize = stat.getBlockSize(); // 获得block的大小
		long totalBlocks = stat.getBlockCount(); // 获得总容量
		long total = blockSize * totalBlocks;
		return total;
    }
    
    /**
     * 获取指定路径的可用容量
     * @param path
     * @return
     */
    public long availableSize(String path){
    	StatFs stat = new StatFs(path); 
    	long blockSize = stat.getBlockSize(); // 获得block的大小
    	long availableBlock=stat.getAvailableBlocks();
		long avail = blockSize * availableBlock;
		return avail;
    }
    
    /**
     * 获取指定路径的已用容量
     * @param path
     * @return
     */
    public long usedSize(String path){
    	return totalSize(path)-availableSize(path);
    }
    
    /**
     * 格式化所获取的容量大小
     * @param size
     * @return
     */
    public String formatSize(long size){
    	String formatSize = null;
    	if (size / 1024 / 1024 < 1000) {
			int intSize = (int) (size / 1024 / 1024);
			formatSize = intSize+"MB"; 
		} else {
			formatSize = formatSize(size, mContext); 
		}
		System.out.println(formatSize);
		return formatSize;
    }
    
   /**
    *  格式化 转化为.MB格式
    * @param size
    * @param mContext
    * @return
    */
	public String formatSize(long size, Context mContext) {
		return Formatter.formatShortFileSize(mContext, size);
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
	
}
