package com.test.hwautotest.mms;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.gionee.hwautotest.R;
import com.test.hwautotest.mms.MMSUtils.AttachmentType;
import com.test.hwautotest.mms.Telephony.Mms;
import com.test.utils.ViewHolder;

public class MMSActivity extends Activity {
	private String[] title = {"彩信附件格式","彩信位置"}; 
	private String[] item = {"图片","收件箱"};
	private ListView mmsSetting;
	private Dialog mDialog; 
	private Button addMms;
	private int msgBoxType = Mms.MESSAGE_BOX_INBOX; 
	private AttachmentType TYPE = AttachmentType.IMAGE;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mms);
//		mmsSetting = (ListView)findViewById(R.id.mmsSetting);
//		addMms = (Button)findViewById(R.id.addMms);
//		
//		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>(); 
//		for (int i = 0; i < title.length; i++) { 
//			HashMap<String, String> map = new HashMap<String, String>(); 
//			map.put("ItemTitle", title[i]); 
//			map.put("ItemText", item[i]); 
//			mylist.add(map); 
//		}
//		
//		 SimpleAdapter mSchedule = new SimpleAdapter(this,mylist,R.layout.mainlist,
//		    		new String[] {"ItemTitle", "ItemText"},new int[] {R.id.ItemTitle,R.id.ItemText});  
//		  
//		 mmsSetting.setAdapter(mSchedule); 
//		 
//		 mmsSetting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,int position,long id) {
//				// TODO Auto-generated method stub
//				final LayoutInflater factory = LayoutInflater.from(MMSActivity.this);   
//				final View customdialog2view = factory.inflate(R.layout.mmslist,null);  
//                final String[] type = new String[]{"图片","声音","视频"};
//                final String[] local = new String[]{"收件箱","发件箱","草稿箱"};
//                ListView listView = (ListView) customdialog2view.findViewById(R.id.choice);  
//                ArrayList<HashMap<String, String>> mylist1 = new ArrayList<HashMap<String, String>>();
//                
//				switch (position) {
//				
//				case 0:
//					  
//	                 for (int i = 0; i < type.length; i++) { 
//	         			HashMap<String, String> map = new HashMap<String, String>(); 
//	         			map.put("choiceButton", type[i]); 
//	         			mylist1.add(map); 
//	         		}
//	         		
//	         		SimpleAdapter m_Schedule = new SimpleAdapter(MMSActivity.this,mylist1,R.layout.choicelist,
//	         		    		new String[] {"choiceButton"},new int[] {R.id.choiceButton});    
//	         		listView.setAdapter(m_Schedule);
//	         		final AlertDialog progressBuilder = new AlertDialog.Builder(MMSActivity.this).setTitle("选择附件格式").setView(customdialog2view).show();
//					
//	                listView.setOnItemClickListener(new OnItemClickListener() {  
//	                     @Override  
//	                     public void onItemClick(AdapterView<?> parent, View view,  
//	                             int position, long id) {  
//	                          switch(position) {
//	                          case 0:
//	                        	  TYPE = AttachmentType.IMAGE;
//	                        	  break;
//	                          case 1:
//	                        	  TYPE = AttachmentType.AUDIO;
//	                        	  break;
//	                          case 2:
//	                        	  TYPE = AttachmentType.VIDEO;
//	                        	  break;
//	                          }
//	                          progressBuilder.dismiss();
//	                         
//	                     }  
//	                 });  
//	                  
//	                 break;  
//					
//				
//
//				default:
//					break;
//				}
//			}
//		});
//		 
//		
//	   
//		 
//		 addMms.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				MMSUtils mMMSUtils = new MMSUtils(MMSActivity.this);
//				mMMSUtils.insert(Mms.MESSAGE_BOX_INBOX, AttachmentType.IMAGE, "1558552231");
//			}
//		});
//	}
//	
////	 class editItem extends AsyncTask<String,Integer,String>{
////         @Override
////         protected String doInBackground(String... params) {
////        	 mylist.set(Integer.parseInt(params[0]),params[1]);
////                 //params得到的是一个数组，params[0]在这里是"0",params[1]是"第1项"
////                 //Adapter.notifyDataSetChanged();
////                 //执行添加后不能调用 Adapter.notifyDataSetChanged()更新UI，因为与UI不是同线程
////                 //下面的onPostExecute方法会在doBackground执行后由UI线程调用
////             return null;    
////         }
////  
////         protected void onPostExecute(String result) {
////             // TODO Auto-generated method stub
////             super.onPostExecute(result);
////             Adapter.notifyDataSetChanged();
////             //执行完毕，更新UI
////         }
////  
     }
//	
	
}
