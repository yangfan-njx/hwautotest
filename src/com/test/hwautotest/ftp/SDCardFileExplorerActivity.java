package com.test.hwautotest.ftp;

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
	// 记录当前路径下的所有文件夹的文件数组
	static File[] currentFiles;
	static int enterPosition=-1;//记录返回目录位置
//	static ArrayList<Integer> enterPositionHistory=null;//之前目录位置历史记录
	private String old_uploadFilePath=null;//原本的上传需要文件完整路径
	private String old_uploadFileSize=null;//原本的上传需要文件大小
	


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
		old_uploadFilePath=intent.getStringExtra("uploadFilePath");
		Log.w("look", "原本上传文件的路径："+old_uploadFilePath);
		old_uploadFileSize=intent.getStringExtra("uploadFileSize");
		Log.w("look", "原本上传文件的大小："+old_uploadFileSize);
		// 获取系统的SDCard的目录
		File root = new File(Environment.getExternalStorageDirectory().toString());
		Log.w("look", "启动路径："+root);
		// 如果SD卡存在的话
		if (root.exists()) {
			currentParent = root;
			currentFiles = root.listFiles();
			// 使用当前目录下的全部文件、文件夹来填充ListView
			inflateListView(currentFiles);
		}
		//向上到存储位置选择的层
		// 获取上一级目录
		currentParent = currentParent.getParentFile();
		// 列出当前目录下的所有文件
		currentFiles = currentParent.listFiles();
		// 再次更新ListView
		inflateListView(currentFiles);
		
		lvFiles.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				enterPosition=position;
//				enterPositionHistory.add(position);
				File f=currentFiles[position];
				// 如果用户单击了文件，直接返回，不做任何处理
				if (f.isFile()) {
					if(f.length()>0){
						// 也可自定义扩展打开这个文件等
						String uploadFilePath = f.getAbsolutePath();
						DecimalFormat decimaFormat=new DecimalFormat(",###KB");
						String uploadFileSize=String.valueOf(decimaFormat.format((double)(f.length())/1000));
						
						ActivityUtil.goActivityWithString2(
								SDCardFileExplorerActivity.this,
								FtpDesignUpload.class, 
								"uploadFilePath",uploadFilePath,
								"uploadFileSize",uploadFileSize
								,true);
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
				ActivityUtil.goActivityWithString2(
						SDCardFileExplorerActivity.this,
						FtpDesignUpload.class, 
						"uploadFilePath",old_uploadFilePath,
						"uploadFileSize",old_uploadFileSize
						,true);
			}
		});

	}

	
	/**
	 * 根据文件夹填充ListView
	 * 
	 * @param files
	 */
	private void inflateListView(File[] files) {
		
		File[] sortedFiles=sortFiles(files);
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < sortedFiles.length; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			
			//添加图标
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
			f=new DecimalFormat(",###KB");
			if(files[i].isDirectory()){
				listItem.put("size","");
			}else{
				String fileSize=f.format((double)(sortedFiles[i].length())/1000);
				listItem.put("size","大小：" + fileSize);
			}
			
			
			listItems.add(listItem);
		}
		
//		//对数据排序
//		Collections.sort(listItems, new Comparator<Map<String, Object>>() {
//	     	@Override
//	     	public int compare(Map<String, Object> object1,Map<String, Object> object2) {
//			//根据文本排序
//	     		int compareResult=(object1.get("icon").toString()).compareTo(object2.get("icon").toString());
//	     		 if (compareResult== 0) {           //如果字段相同，再根据下一字段排序
//	     			compareResult=object1.get("filename").toString().compareTo(object2.get("filename").toString());
//	            }
//	          	return compareResult;
//	     	}    
//	    }); 
		// 定义一个SimpleAdapter
		SimpleAdapter adapter = new SimpleAdapter(
				SDCardFileExplorerActivity.this, listItems, R.layout.ftp_list_item,
				new String[] { "filename", "icon", "size" }, new int[] {
						R.id.file_name, R.id.file_icon,R.id.file_size});
		
		// 填充数据集
		lvFiles.setAdapter(adapter);
		
		try {
			tvpath.setText("当前路径为:" + currentParent.getCanonicalPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
//		Log.w("look", "onKeyUp ");
		if(keyCode==KeyEvent.KEYCODE_BACK){
			try {
				if (!currentParent.getCanonicalPath().equals("/")) {
					// 获取上一级目录
					currentParent = currentParent.getParentFile();
					// 列出当前目录下的所有文件
					currentFiles = currentParent.listFiles();
					// 再次更新ListView
					inflateListView(currentFiles);
					lvFiles.setSelection(enterPosition);//滚动到之前进入的位置
				}else{
//					Toast.makeText(SDCardFileExplorerActivity.this,
//							"到顶啦！", Toast.LENGTH_SHORT).show();
				}
				return false;//返回true说明你已经处理了这个事件并且它应该就此终止，如果返回false就表示此事件还需要继续传递下去
			} catch (IOException e) {
				e.printStackTrace();
				return true;
			}
		}
		return false;
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
		return currentFiles;
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
} 
