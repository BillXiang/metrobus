package com.bill.metrobus;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.Set;

import com.bill.metrobus.util.Util;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 用户信息界面
 * 
 * @author Bill
 * 
 */
public class UserFragment extends Fragment implements OnClickListener, TagAliasCallback{
	private SharedPreferences mShare;
	private Editor editor;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// MenuItem populateItem = menu.add(Menu.NONE, POPULATE_ID, 0,
		// "Populate");
		// MenuItemCompat.setShowAsAction(populateItem,
		// MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
		// MenuItem clearItem = menu.add(Menu.NONE, CLEAR_ID, 0, "Clear");
		// MenuItemCompat.setShowAsAction(clearItem,
		// MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final ContentResolver cr = getActivity().getContentResolver();

		switch (item.getItemId()) {

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private Button userNameCommitBtn = null;
	private EditText userNameEditText = null;
	public static String userName = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View userView = inflater.inflate(R.layout.userview, container, false);
		userNameCommitBtn = (Button)userView.findViewById(R.id.userNameCommitBtn);
		userNameCommitBtn.setOnClickListener(this);
		userNameEditText = (EditText)userView.findViewById(R.id.userNameEditText);
		
		mShare = getActivity().getSharedPreferences("MetroBus", Context.MODE_PRIVATE);
		editor = mShare.edit();
		userName = mShare.getString("userName", "");
		if(!Util.isEmpty(userName)){
			userNameEditText.setText(userName);
			JPushInterface.setAlias(getActivity(), userName, this);
		}
		
		return userView;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.userNameCommitBtn:
			userName = userNameEditText.getText().toString();
			JPushInterface.setAlias(getActivity(), userName+Util.getDeviceId(getActivity()), this);
			editor.putString("userName", userName);
			break;
		default:
			break;
		}
	}

	@Override
	public void gotResult(int arg0, String arg1, Set<String> arg2) {
		// TODO Auto-generated method stub
		if(arg0!=0){
			/*Code	描述	详细解释
			6001	无效的设置，tag/alias 不应参数都为 null	 
			6002	设置超时	建议重试
			6003	alias 字符串不合法	有效的别名、标签组成：字母（区分大小写）、数字、下划线、汉字。
			6004	alias超长。最多 40个字节	中文 UTF-8 是 3 个字节
			6005	某一个 tag 字符串不合法	有效的别名、标签组成：字母（区分大小写）、数字、下划线、汉字。
			6006	某一个 tag 超长。一个 tag 最多 40个字节	中文 UTF-8 是 3 个字节
			6007	tags 数量超出限制。最多 100个	这是一台设备的限制。一个应用全局的标签数量无限制。
			6008	tag/alias 超出总长度限制。总长度最多 1K 字节*/
			//Toast.makeText(getActivity(), "设置用户名失败", Toast.LENGTH_LONG).show();
		}
	}
}