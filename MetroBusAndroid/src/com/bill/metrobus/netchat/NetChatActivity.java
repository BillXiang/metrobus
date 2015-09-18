package com.bill.metrobus.netchat;

import com.bill.metrobus.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NetChatActivity extends Activity implements OnClickListener{

	private LinearLayout talkContentHolder = null;
	private ImageButton talkSendBtn = null;
	private LayoutInflater inflater = null;
	private EditText talkContentEdit = null;
	
	private static final int MESSAGE_ITEM_FRIENDS = 0;
	private static final int MESSAGE_ITEM_ME = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.talk);
		talkContentEdit = (EditText) findViewById(R.id.talkContentEdit);
		talkContentEdit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				if(talkContentEdit.getText().length()>0){
					if(!talkSendBtn.isClickable()){
						talkSendBtn.setClickable(true);
						talkSendBtn.setImageResource(R.drawable.new_send_text_bg);
					}
				}else{
					talkSendBtn.setClickable(false);
					talkSendBtn.setImageResource(R.drawable.new_send_text_dispose);
				}
				
			}
		});
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		talkSendBtn = (ImageButton) findViewById(R.id.talkSendBtn);
		talkSendBtn.setOnClickListener(this);
		talkSendBtn.setClickable(false);
		talkContentHolder = (LinearLayout) findViewById(R.id.talkContentHolder);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.talkSendBtn:
			addTalkItem(MESSAGE_ITEM_ME, talkContentEdit.getText().toString());
			talkContentEdit.setText("");
			talkContentEdit.requestFocus();
			break;
			default:
				break;
		}
	}
	
	private void addTalkItem(int messageFrom, String message){
		View container	= null;
		switch(messageFrom){
		case MESSAGE_ITEM_FRIENDS:
			container = (View) inflater.inflate(R.layout.default_friends_talk_item, null);
			break;
		case MESSAGE_ITEM_ME:
			container = (View) inflater.inflate(R.layout.default_me_talk_item, null);
			break;
			default:
				break;
		}
		if(container!=null){
			TextView talkContent = (TextView) container.findViewById(R.id.talkContent);
			talkContent.setText(message);
			container.setFocusable(true);
			container.setFocusableInTouchMode(true);
			container.requestFocus();
			talkContentHolder.addView(container, 0);
		}
	}
}
