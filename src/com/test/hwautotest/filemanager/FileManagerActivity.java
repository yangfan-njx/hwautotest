package com.test.hwautotest.filemanager;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gionee.hwautotest.R;

public class FileManagerActivity extends Activity implements SeekBar.OnSeekBarChangeListener {
	
	private File SDRoad = null;
	private SeekBar SDCardSeek;
	private SeekBar InternalSeek;
	private TextView SDCardAvailaleSize;
	private TextView InternalAvailaleSize;
	private Button SDAddFile;
	private Button InternalAddFile;
	private ProgressDialog m_pDialog;
	private String SDCardPath = Environment.getExternalStorageDirectory().getPath();
	private String InternalPath = "/mnt/sdcard2";
	private long TotalSize = 0;
	private long UsedSize = 0;
	private int SDAddSize = 0;
	private int InternalAddSize = 0;
	
	FilemanagerUtils mFilemanagerUtils = new FilemanagerUtils(FileManagerActivity.this);
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filemanager);
		SDCardSeek = (SeekBar) findViewById(R.id.SDCardSeek);
		Log.i("look", SDCardSeek + "");
		Log.i("look",SDCardPath);
		
		
		
		
		if(mFilemanagerUtils.isContain("mnt", SDCardPath)){
			InternalPath = "/mnt/sdcard2";
		}else{
			InternalPath = "/storage/sdcard1";
		}
			
		
		SDCardSeek.setOnSeekBarChangeListener(this);
		InternalSeek = (SeekBar) findViewById(R.id.InternalSeek);
		InternalSeek.setOnSeekBarChangeListener(this);
		SDCardAvailaleSize = (TextView) findViewById(R.id.SDAvailaleSize);
		InternalAvailaleSize = (TextView) findViewById(R.id.InternalAvailaleSize);
		SDAddFile = (Button)findViewById(R.id.SDAddFile);
		InternalAddFile = (Button)findViewById(R.id.InternalAddFile);

		if(Environment.isExternalStorageRemovable()){
			mFilemanagerUtils.setDefault(InternalPath, InternalSeek,InternalAvailaleSize);
			mFilemanagerUtils.setDefault(SDCardPath, SDCardSeek,SDCardAvailaleSize);
		}else{
			SDCardAvailaleSize.setText("SD卡不可用");
			InternalPath = SDCardPath ;
			mFilemanagerUtils.setDefault(InternalPath, InternalSeek,InternalAvailaleSize);
		}
		
		SDAddFile.setOnClickListener(new View.OnClickListener() {
			
		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SDAddSize = mFilemanagerUtils.getSize(SDCardSeek, SDCardPath);
				Log.i("look",SDAddSize+"");
				m_pDialog = new ProgressDialog(FileManagerActivity.this);
				m_pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				m_pDialog.setTitle("正在添加");
				m_pDialog.setMax(SDAddSize);
				m_pDialog.setIndeterminate(false);
				m_pDialog.setCancelable(true);
				m_pDialog.show();
				
				ProgressDailogAsyncTask asyncTask = new ProgressDailogAsyncTask(SDCardPath,SDAddSize);  
	            asyncTask.execute(1000); 
			}
		});
		
		

	

	InternalAddFile.setOnClickListener(new View.OnClickListener() {
		
	
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			InternalAddSize = mFilemanagerUtils.getSize(InternalSeek, InternalPath);
			Log.i("look",InternalAddSize+"");
			m_pDialog = new ProgressDialog(FileManagerActivity.this);
			m_pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			m_pDialog.setTitle("正在添加");
			m_pDialog.setMax(InternalAddSize);
			m_pDialog.setIndeterminate(false);
			m_pDialog.setCancelable(true);
			m_pDialog.show();
			
			ProgressDailogAsyncTask asyncTask = new ProgressDailogAsyncTask(InternalPath,InternalAddSize);  
            asyncTask.execute(1000); 
		}
	});
	
	
}
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		switch (seekBar.getId()) {
		
		case R.id.SDCardSeek:
			
			if(Environment.isExternalStorageRemovable()){
				SDCardAvailaleSize.setText(mFilemanagerUtils.formatSize(progress)+"/"+mFilemanagerUtils.formatSize(mFilemanagerUtils.totalSize(SDCardPath)));
			}else{
				mFilemanagerUtils.DisplayToast("SD不可用");
			}
			
			break;
		case R.id.InternalSeek:
			
				Log.i("look",progress+"");
				Log.i("look",(long)progress*1024*1024+"");
				Log.i("look",mFilemanagerUtils.formatSize((long)progress*1024*1024));
				InternalAvailaleSize.setText(mFilemanagerUtils.formatSize((long)progress*1024*1024)+"/"+mFilemanagerUtils.formatSize(mFilemanagerUtils.totalSize(InternalPath)));
			
			break;
		}

	}


	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
	
	private class ProgressDailogAsyncTask extends AsyncTask<Integer, Integer, String> {
		String path;
		int size = 0;
		File file;
		
		private ProgressDailogAsyncTask(String path,int size) {  
	        super();  
	        this.path = path;  
	        this.size = size;  
	    }  
	  
		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			int i = 0;  
	        for (i = 0; i < size; i++) {  
	        	
				try {
					file = mFilemanagerUtils.creatPath(path,i);
					mFilemanagerUtils.addFile(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	publishProgress(i+1); 
	        	
	        }  
	        return params[0].intValue() + "";  
			
		}

      
	
	
	 @Override  
	    protected void onPostExecute(String result) {  
		 	m_pDialog.setTitle("添加完成"); 
		 	m_pDialog.cancel();
	    }  
	  
	  
	    //该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置  
	    @Override  
	    protected void onPreExecute() {  
//	        m_pDialog.setTitle("正在添加");  
	    }  
	  
	  
	    /**  
	     * 这里的Intege参数对应AsyncTask中的第二个参数  
	     * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行  
	     * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作  
	     */  
	    @Override  
	    protected void onProgressUpdate(Integer... values) {  
	        int vlaue = values[0];  
	        m_pDialog.setProgress(vlaue);  
	    }  
	}
}
