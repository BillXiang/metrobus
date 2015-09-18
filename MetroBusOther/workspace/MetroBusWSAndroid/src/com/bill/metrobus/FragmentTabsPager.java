package com.bill.metrobus;

import java.lang.reflect.Field;

import cn.jpush.android.api.JPushInterface;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TabHost;

public class FragmentTabsPager extends FragmentActivity {
	public static String WEBSOCKET = null;

	private TabHost tabHost;
	private ViewPager viewPager;
	public static TabsAdapter tabsAdapter = null;
	private Resources res;
	private View view;
	private LayoutInflater inflater;

	private String html = "同行<img src='" + R.drawable.bus + "'/>";
	private CharSequence charSequence1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fragment_tabs_pager);
		inflater = getLayoutInflater();
		view = inflater.inflate(R.layout.tab_map, null);

		res = getResources();

		WEBSOCKET = res.getString(R.string.websocket);

		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();

		viewPager = (MyViewPager) findViewById(R.id.pager);

		tabsAdapter = new TabsAdapter(this, tabHost, viewPager);

		tabsAdapter.addTab(
				tabHost.newTabSpec("nearby").setIndicator(/* "附近" */null,
						res.getDrawable(R.drawable.map)),// (view),
				MapFragment.class, null);
		tabsAdapter.addTab(tabHost.newTabSpec("bus").setIndicator("道合"),
				LoaderCursorSupport.CursorLoaderListFragment.class, null);
		tabsAdapter.addTab(
				tabHost.newTabSpec("route").setIndicator("线路",
						res.getDrawable(R.drawable.route)),
				BusLineSearch.BusListFragment.class, null);
		tabsAdapter.addTab(
				tabHost.newTabSpec("user").setIndicator("我",
						res.getDrawable(R.drawable.user)),
				UserFragment.class, null);

		// ImageView iv = (ImageView)
		// mTabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.icon);
		// TextView tv = (TextView)
		// mTabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
		// iv.setImageDrawable(getResources().getDrawable(R.drawable.map));
		// iv.setVisibility(View.VISIBLE);
		// tv.setText("附近");
		// tv.setVisibility(View.VISIBLE);

		if (savedInstanceState != null) {// 根据传入信息设置当前显示的tab
			tabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}

		// getActionBar().setBackgroundDrawable(res.getDrawable(R.drawable.action_bar_background));

		// int titleId = Resources.getSystem().getIdentifier("action_bar_title",
		// "id", "android");
		// TextView textView = (TextView) findViewById(titleId);
		// textView.setTextColor(Color.BLACK);
		// getOverflowMenu();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", tabHost.getCurrentTabTag());
	}

	/**
	 * 让有实体菜单键的设备显示右上角菜单
	 */
	private void getOverflowMenu() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(this);
	}
	
}
