package com.test.hwautotest.ftp;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.test.hwautotest.R;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast; 

public class SDCardFileExplorerActivity extends Activity {
	private TextView tvpath;
	private ListView lvFiles;
	private Button btnBack;
	private Button btnForward;
	private Button btnClose;
	// 记录当前的父文件夹
	static File currentParent;
	static FTPFile currentParent_ftp;
	static String currentParentPath_ftp;
	// 记录当前路径下的所有文件夹的文件数组
	private static File[] currentFiles=null;
	private static FTPFile[] currentFiles_ftp=null; 
	static int enterPosition=-1;//记录返回目录位置
//	static ArrayList<Integer> enterPositionHistory=null;//之前目录位置历史记录
	private String old_uploadFilePath=null;//原本的上传需要文件完整路径
	private String old_uploadFileSize=null;//原本的上传需要文件大小
	private String old_downloadFilePath=null;//原本的下载需要文件完整路径
	private String old_downloadFileSize=null;//原本的下载需要文件大小
	
	private File topFiles=null;//默认打开位置和顶层位置，到达继续向上设计为退出
	private FTPFile topFiles_ftp=null;//默认打开位置和顶层位置，到达继续向上设计为退出
	
	private FTPFile root_ftp;
	private  String uploadFilePath;
	private  String uploadFileSize;
	private  String downloadPath="/";//用静态则加载初始路径错误
	private  String downloadFilePath ;
	private  String downloadFileSize ;
	public static FTPClient SDmFTPClient;
	
	public static int ftpType=-1;//显示目录类型
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ftp_sdmain);
		MyApplication.getInstance().addActivity(this);
		lvFiles = (ListView) this.findViewById(R.id.files);
		tvpath = (TextView) this.findViewById(R.id.tvpath);
		btnBack = (Button) this.findViewById(R.id.btnBack);
		btnForward = (Button) this.findViewById(R.id.btnForward);
		btnClose = (Button) this.findViewById(R.id.btnClose);
		//选择后获取原本的上传路径
		Intent intent=getIntent();
		ftpType=intent.getIntExtra("ftpType",ftpType);
		Log.w("look", "目录类型："+ftpType);
		old_uploadFilePath=intent.getStringExtra("uploadFilePath");
		Log.w("look", "原本上传文件的路径："+old_uploadFilePath);
		old_uploadFileSize=intent.getStringExtra("uploadFileSize");
		Log.w("look", "原本上传文件的大小："+old_uploadFileSize);
		
		// 选择后获取原本的下载路径
		old_downloadFilePath = intent.getStringExtra("downloadFilePath");
		Log.w("look", "原本下载文件的路径：" + old_downloadFilePath);
		old_downloadFileSize = intent.getStringExtra("downloadFileSize");
		Log.w("look", "原本下载文件的大小："+old_downloadFileSize);
				
		File root =null;
		FTPFile root_ftp=null;
//		for(FTPFile f:FtpDesignDownload.files){
//			Log.i(TAG,".."+f.getName());
//		}
		//获取目录
		if(ftpType==1){
			setTitle(R.string.name_chooseUpload);
			// 获取系统的SDCard的目录
			root = new File(Environment.getExternalStorageDirectory().toString());
			// 如果SD卡存在的话
			if (root.exists()) {
				currentParent = root;
				currentFiles = root.listFiles();
				// 使用当前目录下的全部文件、文件夹来填充ListView
				inflateListView(currentFiles);
				
				//向上到存储位置选择的层
				// 获取上一级目录
				currentParent = currentParent.getParentFile();
				// 列出当前目录下的所有文件
				currentFiles = currentParent.listFiles();
				// 再次更新ListView
				inflateListView(currentFiles);
			}
			topFiles=currentParent;//默认打开位置和顶层位置
			Log.w("look","顶层路径[存储位置上层]："+topFiles);
		}else if(ftpType==0){
			setTitle(R.string.name_chooseDownload);
			new ConnectTask().execute();
			
//			currentFiles = root.listFiles();
//			// 使用当前目录下的全部文件、文件夹来填充ListView
//			inflateListView(currentFiles);
		}
		
		
		lvFiles.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				enterPosition=position;
//				enterPositionHistory.add(position);
				if(ftpType==1){/*SD卡目录*/
					File f=currentFiles[position];
					// 如果用户单击了文件，直接返回，不做任何处理
					if (f.isFile()) {
						if (f.length() > 0) {
							// 也可自定义扩展打开这个文件等
							 uploadFilePath = f.getAbsolutePath();
							DecimalFormat decimaFormat = new DecimalFormat(
									",###KB");
							 uploadFileSize = String.valueOf(decimaFormat
									.format((double) (f.length()) / 1000));

							ActivityUtil.goActivityWithString2(
									SDCardFileExplorerActivity.this,
									FtpDesignUpload.class, "uploadFilePath",
									uploadFilePath, "uploadFileSize",
									uploadFileSize, true);
						}else if(f.length()<=0){
							toast("请另选占用空间大于0的文件");
						}
						
					} else {
						// 获取用户点击的文件夹 下的所有文件
						File[] tem = f.listFiles();

						if (tem == null || tem.length == 0) {
							Toast.makeText(SDCardFileExplorerActivity.this,
									"无权限或无文件", Toast.LENGTH_SHORT).show();
						} else {
							// 获取用户单击的列表项对应的文件夹，设为当前的父文件夹
							currentParent = f;
							// 保存当前的父文件夹内的全部文件和文件夹
							currentFiles = tem;
							// 再次更新ListView
							inflateListView(currentFiles);
						}
					}
				}else if(ftpType==0){/*FTP目录*/
					FTPFile f=currentFiles_ftp[position];
					// 如果用户单击了文件，直接返回，不做任何处理
					if (f.getType()==FTPFile.TYPE_FILE) {
						if(f.getSize()>0){
							// 也可自定义扩展打开这个文件等
							new GetFTPTask().execute(f);
							String fileName=f.getName();
							if(downloadPath.equals("/")){
								 downloadFilePath = downloadPath+fileName;
							}else{
								downloadFilePath = downloadPath+"/"+fileName;
							}
							
							DecimalFormat decimaFormat=new DecimalFormat(",###KB");
							 downloadFileSize=String.valueOf(decimaFormat.format((double)(f.getSize())/1000));
							
							ActivityUtil.goActivityWithString2(
									SDCardFileExplorerActivity.this,
									FtpDesignDownload.class, 
									"downloadFilePath",downloadFilePath,
									"downloadFileSize",downloadFileSize
									,true);
						}else if(f.getSize()<=0){
							toast("请另选占用空间大于0的文件");
						}
						
					} else {
						// 获取用户点击的文件夹 下的所有文件
//						FTPFile[] tem;
						
//							FtpUtil.getFtpClient().changeDirectory(FtpUtil.getFtpClient().currentDirectory()+"/"+f.getName());
//							if (tem == null || tem.length == 0) {
//								Toast.makeText(SDCardFileExplorerActivity.this,
//										"无权限或无文件", Toast.LENGTH_SHORT).show();
//							} 
//							else {
								// 获取用户单击的列表项对应的文件夹，设为当前的父文件夹
//								currentParent_ftp = f;
								// 保存当前的父文件夹内的全部文件和文件夹
//								currentFiles_ftp = FtpUtil.getFtpClient().list();
//								// 再次更新ListView
//								inflateListView(currentFiles_ftp);
								new GetFTPTask().execute(f);
//							}

						
					}
				
				}

			}
		});
		
		
		// 获取上一级目录（返回按钮）
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if (!currentParent.getCanonicalPath().equals("/")) {
						// 获取上一级目录
						currentParent = currentParent.getParentFile();
						// 列出当前目录下的所有文件
						currentFiles = currentParent.listFiles();
						// 再次更新ListView
						inflateListView(currentFiles);
						lvFiles.setSelection(enterPosition);// 滚动到之前进入的位置
					} else {
//						Toast.makeText(SDCardFileExplorerActivity.this, "到顶啦！",
//								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
				}
			}
		});
		// 获取下一级目录（前进按钮）
		btnForward.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if (!currentParent.getCanonicalPath().equals("/")) {
						// 获取上一级目录
						currentParent = currentParent.getParentFile();
						// 列出当前目录下的所有文件
						currentFiles = currentParent.listFiles();
						// 再次更新ListView
						inflateListView(currentFiles);
						lvFiles.setSelection(enterPosition);// 滚动到之前进入的位置
					} else {
//						Toast.makeText(SDCardFileExplorerActivity.this, "到顶啦！",
//								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
				}
			}
		});
		// 退出选择（关闭按钮）
		btnClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (ftpType) {
				case 0:
					ActivityUtil.goActivityWithString2(
							SDCardFileExplorerActivity.this,
							FtpDesignDownload.class, 
							"downloadFilePath",old_downloadFilePath,
							"downloadFileSize",old_downloadFileSize
							,true);
					break;
				case 1:
					Log.w(TAG,"oldUpload "+ old_uploadFilePath+"/"+old_uploadFileSize);
					ActivityUtil.goActivityWithString2(
							SDCardFileExplorerActivity.this,
							FtpDesignUpload.class, 
							"uploadFilePath",old_uploadFilePath,
							"uploadFileSize",old_uploadFileSize
							,true);
					break;
				}
				
			}
		});

	}
	public static File[] sortFiles(File[] files){
		List<File>fList=new ArrayList<File>();
		for(File f:files){
			fList.add(f);
		}
		//对数据排序
		Collections.sort(fList, new Comparator<File>() {
	     	@Override
	     	public int compare(File object1,File object2) {
			//根据文本排序
	     		int compareResult=object1.getName().compareTo(object2.getName());
	          	return compareResult;
	     	}    
	    }); 
		for(File f:fList){
//			Log.w("look2", fList.indexOf(f)+"/");
			currentFiles[fList.indexOf(f)]=f;
		}
		for(int i=0;i<currentFiles.length;i++){
//			Log.w("look2","currentFiles"+i+currentFiles[i].getName());
//			currentFiles_ftp[fList.indexOf(f)]=f;
		}
		return currentFiles;
	}
	
	public static FTPFile[] sortFiles(FTPFile[] files){
		List<FTPFile>fList=new ArrayList<FTPFile>();
		for(FTPFile f:files){
			fList.add(f);
		}
		//对数据排序
		Collections.sort(fList, new Comparator<FTPFile>() {
	     	@Override
	     	public int compare(FTPFile object1,FTPFile object2) {
			//根据文本排序
	     		int compareResult=object1.getName().compareTo(object2.getName());
	          	return compareResult;
	     	}    
	    }); 
		for(FTPFile f:fList){
//			Log.w("look2", fList.indexOf(f)+"/"+f.getName());
			currentFiles_ftp[fList.indexOf(f)]=f;
		}
//		for(int i=0;i<currentFiles_ftp.length;i++){
//			Log.w("look2","currentFiles_ftp"+i+currentFiles_ftp[i].getName());
////			currentFiles_ftp[fList.indexOf(f)]=f;
//		}
		return currentFiles_ftp;
	}
	
	/**
	 * 根据文件夹填充ListView
	 * 
	 * @param files
	 */
	private void inflateListView(File[] files) {

		File[] sortedFiles = sortFiles(files);
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < sortedFiles.length; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();

			// 添加图标
			if (sortedFiles[i].isDirectory()) {
				// 如果是文件夹就显示的图片为文件夹的图片
				listItem.put("icon", R.drawable.folder);
			} else {
				listItem.put("icon", R.drawable.file);
			}

			// 添加文件名称
			listItem.put("filename", sortedFiles[i].getName());

			// 获取文件大小
			DecimalFormat f = null;
			f = new DecimalFormat(",###KB");
			if (files[i].isDirectory()) {
				listItem.put("size", "");
			} else {
				String fileSize = f
						.format((double) (sortedFiles[i].length()) / 1000);
				listItem.put("size", "大小：" + fileSize);
			}

			listItems.add(listItem);
			// 定义一个SimpleAdapter
			SimpleAdapter adapter = new SimpleAdapter(
					SDCardFileExplorerActivity.this, listItems,
					R.layout.ftp_list_item, new String[] { "filename", "icon",
							"size" }, new int[] { R.id.file_name,
							R.id.file_icon, R.id.file_size });

			// 填充数据集
			lvFiles.setAdapter(adapter);

			try {
				tvpath.setText("当前路径为:" + currentParent.getCanonicalPath());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
		/**
		 * 根据文件夹填充ListView
		 * 
		 * @param files
		 */
	public void inflateListView(FTPFile[] files) {

		FTPFile[] sortedFiles = sortFiles(files);
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < sortedFiles.length; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();

			// 添加图标
			if (sortedFiles[i].getType() == FTPFile.TYPE_DIRECTORY) {
				// 如果是文件夹就显示的图片为文件夹的图片
				listItem.put("icon", R.drawable.folder);
			} else {
				listItem.put("icon", R.drawable.file);
			}

			// 添加文件名称
			listItem.put("filename", sortedFiles[i].getName());

			// 获取文件大小
			DecimalFormat f = null;
			f = new DecimalFormat(",###KB");
			if (files[i].getType() == FTPFile.TYPE_DIRECTORY) {
				listItem.put("size", "");
			} else {
				String fileSize = f
						.format((double) (sortedFiles[i].getSize()) / 1000);
				listItem.put("size", "大小：" + fileSize);
			}

			listItems.add(listItem);
		}

		// 定义一个SimpleAdapter
		SimpleAdapter adapter = new SimpleAdapter(
				SDCardFileExplorerActivity.this, listItems,
				R.layout.ftp_list_item, new String[] { "filename", "icon",
						"size" }, new int[] { R.id.file_name, R.id.file_icon,
						R.id.file_size });

		// 填充数据集
		lvFiles.setAdapter(adapter);
		try {
//			tvpath.setText("当前路径为:" + FtpUtil.getFtpClient().currentDirectory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
//		Log.w("look", "onKeyUp ");
		if(keyCode==KeyEvent.KEYCODE_BACK){
			try {
//				if (!currentParent.getCanonicalPath().equals("/")) {
				switch (ftpType) {
				case 0:
					if (!downloadPath.equals("/")) {//顶层继续向上设计为退出
						// 获取上一级目录
						new GetUpFTPTask().execute();
						lvFiles.setSelection(enterPosition);//滚动到之前进入的位置
					}else{
//						Toast.makeText(SDCardFileExplorerActivity.this,
//								"到顶啦！", Toast.LENGTH_SHORT).show();
						ActivityUtil.goActivityWithString2(
								SDCardFileExplorerActivity.this,
								FtpDesignDownload.class, 
								"downloadFilePath",old_downloadFilePath,
								"downloadFileSize",old_downloadFileSize
								,true);
					}
					break;
				case 1:
					if (!currentParent.getCanonicalPath().equals(topFiles.getCanonicalPath())) {//顶层继续向上设计为退出
						// 获取上一级目录
						currentParent = currentParent.getParentFile();
						// 列出当前目录下的所有文件
						currentFiles = currentParent.listFiles();
						// 再次更新ListView
						inflateListView(currentFiles);
						lvFiles.setSelection(enterPosition);//滚动到之前进入的位置
					}else{
//						Toast.makeText(SDCardFileExplorerActivity.this,
//								"到顶啦！", Toast.LENGTH_SHORT).show();
						ActivityUtil.goActivityWithString2(
								SDCardFileExplorerActivity.this,
								FtpDesignUpload.class, 
								"uploadFilePath",old_uploadFilePath,
								"uploadFileSize",old_uploadFileSize
								,true);
					}
				
					break;
				}
				return false;//返回true说明你已经处理了这个事件并且它应该就此终止，如果返回false就表示此事件还需要继续传递下去
			} catch (IOException e) {
				e.printStackTrace();
				return true;
			}
		}
		return false;
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
//		Log.i(TAG, "selectId:"+selectId);
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
			FtpUtil.connectFtp();
			currentParentPath_ftp="/";
			Log.i(TAG,"client: "+FtpUtil.getFtpClient().toString());
			
			
			try {
//				root_ftp=new FTPFile();
				try {
					FTPFile[] files=FtpUtil.getFtpClient().list();
					Log.i(TAG,"初始工作目录"+FtpUtil.getFtpClient().currentDirectory());
					currentFiles_ftp = FtpUtil.getFtpClient().list();
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
				} catch (FTPDataTransferException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPAbortedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPListParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return SDmFTPClient;
		}
		@Override
		protected void onPostExecute(FTPClient result) {
			inflateListView(currentFiles_ftp);
			tvpath.setText("当前路径为:" + downloadPath);
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

public class GetFTPTask extends AsyncTask<FTPFile,Integer,String> {
	
	
	@Override
	protected String doInBackground(FTPFile... params) {
		Log.i(TAG,"getFTP!");
		try {
//			root_ftp=new FTPFile();
			try {
				Log.i(TAG,"初始工作目录"+FtpUtil.getFtpClient().currentDirectory());
//				String filePre="";
				if(params[0].getType()==FTPFile.TYPE_DIRECTORY){//文件夹展开
					if(FtpUtil.getFtpClient().currentDirectory().equals("/")){
						currentParentPath_ftp="/";
						downloadPath="/"+params[0].getName();
						
					}else{
//						filePre=params[0].getName().substring(0,params[0].getName().indexOf("."));
						currentParentPath_ftp=FtpUtil.getFtpClient().currentDirectory();
						downloadPath=FtpUtil.getFtpClient().currentDirectory()+"/"+params[0].getName();
//						currentParentPath_ftp = downloadPath.substring(0,downloadPath.lastIndexOf("/"));
						
						
					}
					FtpUtil.getFtpClient().changeDirectory(downloadPath);
					currentParent_ftp=params[0];
				}
				else{//文件
						downloadPath=FtpUtil.getFtpClient().currentDirectory();
				}
				Log.i(TAG,"点击后的工作目录"+"downloadPath:"+downloadPath);
				Log.i(TAG,"点击后的上层工作目录"+"currentParentPath_ftp:"+currentParentPath_ftp);
//				if (tem == null || tem.length == 0) {
//					Toast.makeText(SDCardFileExplorerActivity.this,
//							"无权限或无文件", Toast.LENGTH_SHORT).show();
//				} 
//				else {
					// 获取用户单击的列表项对应的文件夹，设为当前的父文件夹
//					currentParent_ftp = f;
					// 保存当前的父文件夹内的全部文件和文件夹
					currentFiles_ftp = FtpUtil.getFtpClient().list();
				
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
			} catch (FTPDataTransferException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPAbortedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPListParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return downloadPath;
	}
	@Override
	protected void onPostExecute(String path) {
		inflateListView(currentFiles_ftp);
		tvpath.setText("当前路径为:" + downloadPath);
		// TODO Auto-generated method stub
//		toast(mFTPClient.isConnected() ? "连接成功" : "连接失败");
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

public class GetUpFTPTask extends AsyncTask<FTPFile,Integer,String> {
	
	
	@Override
	protected String doInBackground(FTPFile... params) {
		Log.i(TAG,"getFTP!");
		try {
//			root_ftp=new FTPFile();
			try {
				Log.i(TAG,"初始工作目录"+FtpUtil.getFtpClient().currentDirectory());
//				String filePre="";
//				if(params[0].getType()==FTPFile.TYPE_DIRECTORY){//文件夹展开
					if(FtpUtil.getFtpClient().currentDirectory().equals("/")){
						
					}else{
//						filePre=params[0].getName().substring(0,params[0].getName().indexOf("."));
						downloadPath=downloadPath.substring(0,downloadPath.lastIndexOf("/"));
						if(downloadPath.length()<1){
							downloadPath="/";
							currentParentPath_ftp = "/";
						}else{
							if(currentParentPath_ftp.length()<1){
								downloadPath="/";
								currentParentPath_ftp ="/";
							}else{
								currentParentPath_ftp= downloadPath.substring(0,downloadPath.lastIndexOf("/"));
							}
							
						}
						
						
					}
					FtpUtil.getFtpClient().changeDirectory(downloadPath);
//					currentParent_ftp=currentFiles_ftp[enterPosition];
//				}
//				else{//文件
//						downloadPath=FtpUtil.getFtpClient().currentDirectory();
//				}
				Log.i(TAG,"点击后的工作目录"+"downloadPath:"+downloadPath);
				Log.i(TAG,"点击后的上层工作目录"+"currentParentPath_ftp:"+currentParentPath_ftp);
//				if (tem == null || tem.length == 0) {
//					Toast.makeText(SDCardFileExplorerActivity.this,
//							"无权限或无文件", Toast.LENGTH_SHORT).show();
//				} 
//				else {
					// 获取用户单击的列表项对应的文件夹，设为当前的父文件夹
//					currentParent_ftp = f;
					// 保存当前的父文件夹内的全部文件和文件夹
					currentFiles_ftp = FtpUtil.getFtpClient().list();
				
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
			} catch (FTPDataTransferException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPAbortedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPListParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return downloadPath;
	}
	@Override
	protected void onPostExecute(String path) {
		inflateListView(currentFiles_ftp);
		tvpath.setText("当前路径为:" + downloadPath);
		// TODO Auto-generated method stub
//		toast(mFTPClient.isConnected() ? "连接成功" : "连接失败");
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

} 
