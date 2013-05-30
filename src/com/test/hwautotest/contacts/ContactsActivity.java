package com.test.hwautotest.contacts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gionee.hwautotest.R;
import com.gionee.hwautotest.R.id;
import com.gionee.hwautotest.R.layout;
import com.test.utils.*;

public class ContactsActivity extends Activity {
	public static final int EMAIL = 0;
    public static final int IM = 1;
    public static final int COMPANY = 2;
    public static final int ADDRESS = 3;
    public static final int NOTES = 4;
    public static final int NICKNAME = 5;
    public static final int WEBSITE = 6;
  
    
	private Button addContacts;
	private Button clearContacts;
	private EditText contactNumber;
	private TextView contactAmount;

	RadioGroup name;
	RadioButton chineseName;
	RadioButton englishName;
	ProgressDialog m_pDialog;
	ListView lv;
	
	String chioce[] = { "添加Email", "添加IM", "添加公司", "添加地址", "添加备注", "添加昵称", "添加网站" }; 
	 
	ArrayList<String> listStr = null; 
	private List<HashMap<String, Object>> list = null; 
	private MyAdapter adapter; 
	String mNumber;
	int number = 0;
	int m_count = 0;
	int amount = 0;
	boolean addEmail = false;
	boolean addNotes = false;
	boolean addAddress = false;
	boolean addCompany = false;
	boolean addIM = false;
	boolean addNickName = false;
	boolean addWebsite = false;
	String language = "chinese";
	ContactsUtils mContactsUtils = new ContactsUtils(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts);

		addContacts = (Button) this.findViewById(R.id.addContacts);
		clearContacts = (Button) this.findViewById(R.id.clearContacts);
		contactAmount = (TextView) this.findViewById(R.id.contactTitle);
		contactNumber = (EditText) this.findViewById(R.id.contactNumber);
		lv = (ListView)findViewById(R.id.lv);
		name = (RadioGroup) this.findViewById(R.id.name);
		chineseName = (RadioButton)this.findViewById(R.id.chineseName);
		englishName = (RadioButton)this.findViewById(R.id.englishName);
		showCheckBoxListView(); 
		amount = mContactsUtils.getCount();
		contactAmount.setText("电话本(当前数量:" + amount + ")");
		// 添加联系人按钮
		Log.i("look", contactNumber+"");
		name.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(checkedId == englishName.getId()){
					language = "english";
				}else{
					language = "chinese";
				}
			}
		});
		
		
		addContacts.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					
					mNumber = contactNumber.getText().toString();
					number = Integer.parseInt(mNumber);
//					number = 1;
					Log.i("look",number+"");
					m_pDialog = new ProgressDialog(ContactsActivity.this);
					m_pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					m_pDialog.setTitle("正在添加");
					m_pDialog.setMax(number);
					m_pDialog.setIndeterminate(false);
					m_pDialog.setCancelable(true);
					m_pDialog.show();
					ProgressDailogAsyncTask asyncTask = new ProgressDailogAsyncTask(language ,number,addEmail,addNotes ,addAddress ,
					addCompany , addIM ,addNickName,addWebsite);
					asyncTask.execute(1000);
					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					if (mNumber != "") {
						mContactsUtils.DisplayToast("请输入整数");
					}

				
				}

			}
		});

		// 清空联系人按钮
		clearContacts.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					// 清除联系人
					ContentResolver CR = getContentResolver();
					ContactsContract.RawContacts.ACCOUNT_NAME.toString();
					CR.delete(Uri
							.parse(ContactsContract.RawContacts.CONTENT_URI
									.toString()
									+ "?"
									+ ContactsContract.CALLER_IS_SYNCADAPTER
									+ "=true"),
							ContactsContract.RawContacts._ID + ">0", null);
					int amount = mContactsUtils.getCount();
					mContactsUtils.DisplayToast("清空电话本完成");
					contactAmount.setText("电话本(当前数量:" + amount + ")");
				} catch (Exception e) {
					// TODO:handle exception
					e.printStackTrace();
				}
			}

		});

	}
	
	/**
	 * 异步线程
	 * @author gongyangyang
	 *
	 */
	private class ProgressDailogAsyncTask extends AsyncTask<Integer, Integer, String> {
				
				String language = null;
				int number = 0;
				boolean addEmail = false;
				boolean addNotes = false;
				boolean addAddress = false;
				boolean addCompany = false;
				boolean addIM = false;
				boolean addNickName = false;
				boolean addWebsite = false;
				
				public ProgressDailogAsyncTask(String language, int number, boolean addEmail,boolean addNotes ,
						boolean addAddress ,boolean addCompany ,boolean addIM ,boolean addNickName,boolean addWebsite) {
					super();
					this.language = language;
					this.number = number;
					this.addEmail = addEmail;
					this.addNotes = addNotes;
					this.addAddress = addAddress;
					this.addCompany = addCompany;
					this.addIM = addIM;
					this.addNickName = addNickName;
					this.addWebsite = addWebsite;
				}
				
				@Override
				protected String doInBackground(Integer... params) {
					// TODO Auto-generated method stub
					int i = 0;
					Random mRandom = new Random();
					for (i = 0; i < number; i++) {
				
						try {
							mContactsUtils.addContacts(language,addEmail,addNotes ,addAddress ,
									addCompany , addIM ,addNickName,addWebsite);

				
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
						publishProgress(i + 1);
				
					}
					return params[0].intValue() + "";
				
				}
				
				@Override
				protected void onPostExecute(String result) {
					m_pDialog.setTitle("添加完成");
					amount = mContactsUtils.getCount();
					contactAmount.setText("电话本(当前数量:" + amount + ")");
					m_pDialog.cancel();
				}
				
				// 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
				@Override
				protected void onPreExecute() {
					// m_pDialog.setTitle("正在添加");
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
	
	private static class MyAdapter extends BaseAdapter {

		 private static HashMap<Integer, Boolean> isSelected;
	     private Context context = null;
	     private LayoutInflater inflater = null;
	     private List<HashMap<String, Object>> list = null;
	     private String keyString[] = null;
	     private String itemString = null; // 记录每个item中textview的值
	     private int idValue[] = null;// id值

	     private MyAdapter(Context context, List<HashMap<String, Object>> list,
	             int resource, String[] from, int[] to) {
	         this.context = context;
	         this.list = list;
	         keyString = new String[from.length];
	         idValue = new int[to.length];
	         System.arraycopy(from, 0, keyString, 0, from.length);
	         System.arraycopy(to, 0, idValue, 0, to.length);
	         inflater = LayoutInflater.from(context);
	         init();
	     }

	     // 初始化 设置所有checkbox都为未选择
	     private void init() {
	         isSelected = new HashMap<Integer, Boolean>();
	         for (int i = 0; i < list.size(); i++) {
	             isSelected.put(i, false);
	         }
	     }

	     @Override
	     public int getCount() {
	         return list.size();
	     }

	     @Override
	     public Object getItem(int arg0) {
	         return list.get(arg0);
	     }

	     @Override
	     public long getItemId(int arg0) {
	         return 0;
	     }

	     @Override
	     public View getView(int position, View view, ViewGroup arg2) {
	         ViewHolder holder = null;
	         if (holder == null) {
	             holder = new ViewHolder();
	             if (view == null) {
	                 view = inflater.inflate(R.layout.con_list, null);
	             }
	            
	             holder.cb = (CheckBox) view.findViewById(R.id.app_select);
	             holder.tv = (TextView) view.findViewById(R.id.item_tv);
	             view.setTag(holder);
	         } else {
	             holder = (ViewHolder) view.getTag();
	         }
	         HashMap<String, Object> map = list.get(position);
	         if (map != null) {
	             itemString = (String) map.get(keyString[0]);
	             holder.tv.setText(itemString);
	         }
	         holder.cb.setChecked(isSelected.get(position));
	         return view;
	     }

	}
	
	// 显示带有checkbox的listview 
	private void showCheckBoxListView() { 
		list = new ArrayList<HashMap<String, Object>>(); 
		for (int i = 0; i < chioce.length; i++) { 
		 
			HashMap<String, Object> map = new HashMap<String, Object>(); 
			map.put("item_tv", chioce[i]); 
			map.put("app_select", false); 
			list.add(map); 
		} 
			adapter = new MyAdapter(this, list, R.layout.con_list, 
			new String[] { "item_tv", "item_cb" }, new int[] { 
			R.id.item_tv, R.id.app_select }); 
			lv.setAdapter(adapter); 
			listStr = new ArrayList<String>(); 
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() { 
			 
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) { 
				ViewHolder holder = (ViewHolder) view.getTag(); 
				 
				holder.cb.toggle();// 在每次获取点击的item时改变checkbox的状态 
				MyAdapter.isSelected.put(position, holder.cb.isChecked()); // 同时修改map的值保存状态
				switch(position){
				case EMAIL:
					if (holder.cb.isChecked() == true) { 
						addEmail = true;
					}
					break;
				case IM:
					if (holder.cb.isChecked() == true) { 
						addIM = true;
					}
					break;
				case COMPANY:
					if (holder.cb.isChecked() == true) { 
						addCompany = true;
					}
					break;
				case ADDRESS:
					if (holder.cb.isChecked() == true) { 
						addAddress = true;
					}
					break;
				case NOTES:
					if (holder.cb.isChecked() == true) { 
						addNotes = true;
					}
					break;
				case NICKNAME:
					if (holder.cb.isChecked() == true) { 
						addNickName = true;
					}
					break;
				case WEBSITE:
					if (holder.cb.isChecked() == true) { 
						addWebsite = true;
					}
					break;
				}

			}
		 
		}); 
		 
	}

}
