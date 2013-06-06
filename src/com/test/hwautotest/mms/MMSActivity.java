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

import com.test.hwautotest.R;
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
		
		
	}
}
