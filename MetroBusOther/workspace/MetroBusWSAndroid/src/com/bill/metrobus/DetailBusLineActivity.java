package com.bill.metrobus;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.bill.metrobus.websocket.WSClient;
import com.bill.metrobus.websocket.WSClient.BusPositionChangeListener;
import com.tencent.tencentmap.mapsdk.search.BusInfoDetails;
import com.tencent.tencentmap.mapsdk.search.BusSearch;
import com.tencent.tencentmap.mapsdk.search.BusStation;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class DetailBusLineActivity extends Activity {
	private TextView detailBusLineView;
	private BusLineView busLineView;
	private ProgressDialog progressDialog = null;
	
	private static final int MENU_LIKE = 0;
	private static final int MENU_GETON = 1;
	private static final int MENU_LOCATE = 2;
	
	private BusInfoDetails  busInfoDetails = null;
	
	private String busId;
	private String busName;
	private String busStart;
	private String busEnd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		busId = intent.getStringExtra("bus_id");
		busName = intent.getStringExtra("bus_name");
		busStart = intent.getStringExtra("bus_start");
		busEnd = intent.getStringExtra("bus_end");
		busLineView = new BusLineView(this);
		busLineView.setName(busName);
		busLineView.setStart(busStart);
		busLineView.setEnd(busEnd);
		
		setContentView(busLineView.getBusLineView());
		
		getActionBar().setDisplayHomeAsUpEnabled(true);//显示左上角返回按钮
		
		if(progressDialog==null){
			progressDialog = new ProgressDialog(this);
			// 设置进度条风格，风格为圆形，旋转的
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            // 设置ProgressDialog 的进度条是否不明确
            progressDialog.setIndeterminate(false);
            progressDialog.setMessage("正在获取线路详细信息...");
            progressDialog.show();
		}
		
		BusLineSearchThread busLineSearchThread = new BusLineSearchThread(this, 
				new BusLineSearchHanlder(this, progressDialog, busLineView), busId);
		busLineSearchThread.start();
		//setContentView(R.layout.detail_bus_line);
		//detailBusLineView = (TextView) findViewById(R.id.detailBusLineView);
		//detailBusLineView.setText(busUid);
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem itemLove = menu.add(0, MENU_LIKE, MENU_LIKE, "收藏");
		itemLove.setIcon(R.drawable.like_grey);
		MenuItemCompat.setShowAsAction(itemLove,
				MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
		
		MenuItem itemLocate = menu.add(0, MENU_LOCATE, MENU_LOCATE, "定位");
		itemLocate.setIcon(R.drawable.locate_grey);
		MenuItemCompat.setShowAsAction(itemLocate,
				MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
		
		MenuItem itemAboard = menu.add(0, MENU_GETON, MENU_GETON, "上车");
		MenuItemCompat.setShowAsAction(itemAboard,
				MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch( item.getItemId() ){
		case MENU_LIKE:
			//TODO
			Toast.makeText(this, "like", Toast.LENGTH_SHORT).show();
			break;
		case MENU_LOCATE:
			BusUserManager.getInstance().requestBusPosition(this, busId, new BusLineSearchHanlder());
			break;
		case MENU_GETON:
			FragmentTabsPager.tabsAdapter.navigateTo("nearby");
			MapFragment.routeHandler.obtainMessage(RouteHandler.ROUTE_PARAMETER, busInfoDetails).sendToTarget();
			BusUserManager.getInstance().getOn(busId);
			finish();
			break;
			
		case android.R.id.home:
			finish();
            return true;
        default:
        	break;
        }
		return super.onOptionsItemSelected(item);
	}
	class BusLineSearchThread extends Thread{
		
		private Context context;
		private String uid; 
		private Handler handler;
		
		public BusLineSearchThread(Context context, Handler handler, String uid){
			this.context = context;
			this.handler = handler;
			this.uid = uid;
		}
		
		@Override
		public void run() {
			searchBusLine();
			super.run();
		}
		
		public void searchBusLine(){
			BusSearch busSearch = new BusSearch(context);
			try {
				busInfoDetails = busSearch.searchBusInfomation(uid);
			} catch (Exception e) {
				handler.obtainMessage(BusLineSearchHanlder.FAILED, busInfoDetails);
				e.printStackTrace();
			}
			if(busInfoDetails!=null){
				handler.obtainMessage(BusLineSearchHanlder.SUCCEED, busInfoDetails).sendToTarget();
			}else{
				handler.obtainMessage(BusLineSearchHanlder.FAILED, busInfoDetails).sendToTarget();
			}
		}
	}
	
	class BusLineSearchHanlder extends Handler{
		private Context context;
		private ProgressDialog progressDialog;
		private BusLineView busLineView;
		private BusInfoDetails  busInfoDetails = null;
		
		public static final int SUCCEED = 0;
		public static final int FAILED = 1;
		//public static final int BUS_POSITION = 2;
		
		public BusLineSearchHanlder(){
		}
		
		public BusLineSearchHanlder(Context context, ProgressDialog progressDialog, BusLineView busLineView) {
			this.context = context;
			this.progressDialog = progressDialog;
			this.busLineView = busLineView;
		}
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SUCCEED:
				busInfoDetails = (BusInfoDetails) msg.obj;
				int i=0;
				for(BusStation busStation: busInfoDetails.busstations){
					if(i>0 && i<busInfoDetails.busstations.size()-1){//去掉起点和终点
						busLineView.addStop(busStation.name);
					}
					i++;
				}
				break;
			case FAILED:
				Toast.makeText(context, "未找到线路详细信息", Toast.LENGTH_SHORT).show();
				break;
			
			case RouteHandler.BUS_POSITION:
				JSONObject jsonObject = (JSONObject)msg.obj;
				FragmentTabsPager.tabsAdapter.navigateTo("nearby");
				HashMap<String, Object> parameters = new HashMap<String, Object>();
				try {
					parameters.put("busId", jsonObject.get("busId"));
					parameters.put("latitudeE6", Integer.parseInt(jsonObject.getString("latitudeE6")));
					parameters.put("longitudeE6", Integer.parseInt(jsonObject.getString("longitudeE6")));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				MapFragment.routeHandler.obtainMessage(RouteHandler.BUS_POSITION, parameters).sendToTarget();
				DetailBusLineActivity.this.finish();
				break;
			default:
				break;
			}
			if(progressDialog!=null)
				progressDialog.dismiss();
		}
	}
}
