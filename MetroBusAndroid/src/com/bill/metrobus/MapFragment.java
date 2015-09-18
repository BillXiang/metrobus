package com.bill.metrobus;

import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.tencent.tencentmap.lbssdk.TencentMapLBSApi;
import com.tencent.tencentmap.lbssdk.TencentMapLBSApiListener;
import com.tencent.tencentmap.lbssdk.TencentMapLBSApiResult;
import com.tencent.tencentmap.mapsdk.map.GeoPoint;
import com.tencent.tencentmap.mapsdk.map.MapController;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.route.QPlaceInfo;
import com.tencent.tencentmap.mapsdk.route.QRouteSearchResult;
import com.tencent.tencentmap.mapsdk.route.RouteSearch;

public class MapFragment extends Fragment {
	public static String NATION = null;// 国家，无效时为null，查询不到为Unknown
	public static String PROVINCE = null;// 省，无效时为null，查询不到为Unknown
	public static String CITY = null;// 市，无效时为null，查询不到为Unknown
	public static String DISTRICT = null; // 区，无效时为null，查询不到为Unknown
	public static String TOWN = null; // 镇，无效时为null，查询不到为Unknown
	public static String VILLAGE = null; // 村，无效时为null，查询不到为Unknown
	public static String STREET = null; // 街道，无效时为null，查询不到为Unknown
	public static String STREET_NO = null; // 门号，无效时为null，查询不到为Unknown
	private View view;
	private MapView mapView;

	public static RouteHandler routeHandler;

	public static SharedPreferences preferences;
	public static SharedPreferences.Editor editor;

	private Context context;

	private TencentMapLBSApi tencentMapLBSApi;

	private MyTencentMapLBSApiListener myTencentMapLBSApiListener;

	private MapController mapController;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		context = this.getActivity();
		tencentMapLBSApi = TencentMapLBSApi.getInstance();
		myTencentMapLBSApiListener = new MyTencentMapLBSApiListener(
				TencentMapLBSApi.GEO_TYPE_GCJ02,
				TencentMapLBSApi.LEVEL_ADMIN_AREA,
				TencentMapLBSApi.DELAY_NORMAL, mapView, context);
		tencentMapLBSApi.requestLocationUpdate(context,
				myTencentMapLBSApiListener);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.mapview, container, false);
		mapView = (MapView) view.findViewById(R.id.mapviewtraffic);
		mapController = mapView.getController(); // 得到mapView的控制权,可以用它控制和驱动平移和缩放
		// 获取只能被本应用程序读、写的SharedPreferences对象
		preferences = getActivity().getSharedPreferences("MetroBus",
				Context.MODE_PRIVATE);
		// 获得修改器
		editor = preferences.edit();

		int latitude = preferences.getInt("latitude", (int) (39.95923 * 1E6));
		int longitude = preferences.getInt("longitude",
				(int) (116.437428 * 1E6));// 默认北京

		GeoPoint geoPoint = new GeoPoint(latitude, longitude);
		mapController.animateTo(geoPoint);
		mapController.setZoom(17);

		MapFragment.NATION = preferences.getString("NATION", null);
		MapFragment.PROVINCE = preferences.getString("PROVINCE", null);
		MapFragment.CITY = preferences.getString("CITY", null);
		MapFragment.DISTRICT = preferences.getString("DISTRICT", null);
		MapFragment.TOWN = preferences.getString("TOWN", null);
		MapFragment.VILLAGE = preferences.getString("VILLAGE", null);
		MapFragment.STREET = preferences.getString("STREET", null);
		MapFragment.STREET_NO = preferences.getString("STREET_NO", null);

		UserFragment.userName = preferences.getString("USER", null);
		if(UserFragment.userName!=null){
			JPushInterface.setAlias(getActivity(), UserFragment.userName, new TagAliasCallback() {
				
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
						Toast.makeText(getActivity(), "设置用户名失败", Toast.LENGTH_LONG).show();
					}
				}
			});
		}
		
		routeHandler = new RouteHandler(getActivity(), null, mapView);
		/*
		 * btnRouteSearch = (Button) view.findViewById(R.id.btnBusRoute);
		 * btnRouteSearch.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub //searchBusRoute(); // searchDriveRoute();
		 * if(progressDialog==null){ progressDialog = new
		 * ProgressDialog(context); // 设置进度条风格，风格为圆形，旋转的
		 * progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); //
		 * 设置ProgressDialog 标题 //progressDialog.setTitle("提示"); //
		 * 设置ProgressDialog 提示信息 progressDialog.setMessage("正在搜索，请稍后..."); //
		 * 设置ProgressDialog 标题图标 //progressDialog.setIcon(R.drawable.wait); //
		 * 设置ProgressDialog 的进度条是否不明确 progressDialog.setIndeterminate(false); }
		 * progressDialog.show(); RouteSearchThread routeSearchThread = new
		 * RouteSearchThread(context, new RouteSearchHandler(context,
		 * progressDialog, mapView)); routeSearchThread.start(); } });
		 */

		mapView.setBuiltInZoomControls(true); // 设置启用内置的缩放控件

		mapView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {//高度变化监听
					@Override
					public void onGlobalLayout() {
						// TODO Auto-generated method stub
						Log.i("change hight", mapView.getHeight() + "");
					}

				});

		// Log.i("onCreateView hight", mapView.getHeight()+"");
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem item0 = menu.add(0, 0, 0, "打开卫星影像");
		MenuItem item1 = menu.add(0, 1, 0, "打开实时交通");
		MenuItem item2 = menu.add(0, 2, 0, "定位");
		// MenuItem item3 = menu.add(0, 3, 0, "搜索");
		// item.setIcon(android.R.drawable.ic_menu_search);
		MenuItemCompat.setShowAsAction(item0,
				MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		MenuItemCompat.setShowAsAction(item1,
				MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		MenuItemCompat.setShowAsAction(item2,
				MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
		// super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			boolean boSatellite = mapView.isSatellite();
			if (boSatellite == true) {
				mapView.setSatellite(false);
				item.setTitle("打开卫星影像");
			} else {
				mapView.setSatellite(true);
				item.setTitle("关闭卫星影像");
			}
			break;
		case 1:
			boolean boTraffic = mapView.isTraffic();
			if (boTraffic == false) {
				int iCurrentLevel = mapView.getZoomLevel();
				if (iCurrentLevel < 10) // 实时交通在10级以上才显示
				{
					mapView.getController().setZoom(10);
				}
				mapView.setTraffic(true);
				item.setTitle("关闭实时交通");
			} else {
				mapView.setTraffic(false);
				item.setTitle("打开实时交通");
			}
			break;
		case 2:
			tencentMapLBSApi.requestLocationUpdate(context,
					myTencentMapLBSApiListener);
			// Log.i("tencentMapLBSApi hight", mapView.getHeight()+"");
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPause() {
		Log.i("map fragment", "onPause");
		if(mapView!=null){
			if(mapView.isTraffic()){
				
			}
			mapView.onPause();
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		Log.i("map fragment", "onResume");
		Bundle bundle = getArguments();
		if(bundle!=null){
			String busName = bundle.getString("busName");
		}
		if(mapView!=null)
			mapView.onResume();
		super.onResume();
	}

	@Override
	public void onStop() {
		Log.i("map fragment", "onStop");
		if(mapView!=null)
			mapView.onStop();
		super.onStop();
	}

	@Override
	public void onDestroy() {
		Log.i("map fragment", "onDestroy");
		if(mapView!=null)
			mapView.onDestroy();
		//WSClient.getInstance().close();
		super.onDestroy();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		
		super.onSaveInstanceState(outState);
	}
}

class MyTencentMapLBSApiListener extends TencentMapLBSApiListener {

	private MapController mapController;
	private MapView mapView;
	private SimulateLocationOverlay simuOvelay;
	private Bitmap bmpMarker = null;
	private Context context;

	public MyTencentMapLBSApiListener(int reqGeoType, int reqLevel,
			int reqDelay, MapView mapView, Context context) {
		super(reqGeoType, reqLevel, reqDelay);
		this.mapController = mapView.getController();
		this.mapView = mapView;

		Resources res = context.getResources();
		bmpMarker = BitmapFactory.decodeResource(res, R.drawable.mark_location);
		this.context = context;
	}

	@Override
	public void onLocationUpdate(TencentMapLBSApiResult arg0) {
		if (arg0.ErrorCode != TencentMapLBSApi.LOC_ERROR_NOERROR
				|| arg0.Info == TencentMapLBSApi.LEVEL_NONE) {
			return;
		}
		int latitude = (int) (arg0.Latitude * 1e6);
		int longitude = (int) (arg0.Longitude * 1e6);
		MapFragment.editor.putInt("latitude", latitude);
		MapFragment.editor.putInt("longitude", longitude);
		MapFragment.editor.commit();

		GeoPoint geoPoint = new GeoPoint(latitude, longitude);
		mapController.animateTo(geoPoint);
		TencentMapLBSApi.getInstance().removeLocationUpdate();
		
		//com.tencent.tencentmap.streetviewsdk.map.basemap.GeoPoint gp = 
		//		new com.tencent.tencentmap.streetviewsdk.map.basemap.GeoPoint(latitude, longitude);
		//StreetViewShow.getInstance().showStreetView(context, gp, 100, null, -170f, 0f);
		
		if (simuOvelay == null) {
			simuOvelay = new SimulateLocationOverlay(bmpMarker);
			mapView.addOverlay(simuOvelay);
		}

		simuOvelay.setGeoCoords(geoPoint);
		simuOvelay.setAccuracy((int) arg0.Accuracy);
		mapController.setZoom(17);

		if (arg0.Info == TencentMapLBSApi.LEVEL_ADMIN_AREA) {

			MapFragment.NATION = arg0.Nation;
			MapFragment.PROVINCE = arg0.Province;
			MapFragment.CITY = arg0.City;
			MapFragment.DISTRICT = arg0.District;
			MapFragment.TOWN = arg0.Town;
			MapFragment.VILLAGE = arg0.Village;
			MapFragment.STREET = arg0.Street;
			MapFragment.STREET_NO = arg0.StreetNo;

			MapFragment.editor.putString("NATION", arg0.Nation);
			MapFragment.editor.putString("PROVINCE", arg0.Province);
			MapFragment.editor.putString("CITY", arg0.City);
			MapFragment.editor.putString("DISTRICT", arg0.District);
			MapFragment.editor.putString("TOWN", arg0.Town);
			MapFragment.editor.putString("VILLAGE", arg0.Village);
			MapFragment.editor.putString("STREET", arg0.Street);
			MapFragment.editor.putString("STREET_NO", arg0.StreetNo);
			MapFragment.editor.commit();
		}
		super.onLocationUpdate(arg0);
	}

	@Override
	public void onStatusUpdate(int arg0) {
		// TODO Auto-generated method stub
		super.onStatusUpdate(arg0);
	}
}

class RouteSearchThread extends Thread {

	private Context context;
	private QRouteSearchResult busRouteResult = null;
	private Handler handler;

	public RouteSearchThread(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	@Override
	public void run() {
		searchBusRoute();
	}

	private void searchBusRoute() {
		RouteSearch routeSearch = new RouteSearch(context);
		QPlaceInfo placeStart = new QPlaceInfo();
		placeStart.point = new GeoPoint((int) (39.981857 * 1e6),
				(int) (116.306364 * 1e6));
		QPlaceInfo placeEnd = new QPlaceInfo();
		placeEnd.point = new GeoPoint((int) (39.900732 * 1e6),
				(int) (116.433547 * 1e6));

		try {
			busRouteResult = routeSearch.searchBusRoute("北京", placeStart,
					placeEnd);
		} catch (Exception e) {
			handler.obtainMessage(RouteHandler.ROUTE_SRARCH_FAILED, null)
					.sendToTarget();
			e.printStackTrace();
		}
		if (busRouteResult == null) {
			handler.obtainMessage(RouteHandler.ROUTE_SRARCH_FAILED, null)
					.sendToTarget();
			return;
		}
		handler.obtainMessage(RouteHandler.ROUTE_SRARCH_SUCCEED, busRouteResult)
				.sendToTarget();
	}

	// private void searchDriveRoute() {
	// RouteSearch routeSearch = new RouteSearch(context);
	// QPlaceInfo placeStart = new QPlaceInfo();
	// placeStart.point = new GeoPoint((int) (39.981857 * 1e6),
	// (int) (116.306364 * 1e6));
	// QPlaceInfo placeEnd = new QPlaceInfo();
	// placeEnd.point = new GeoPoint((int) (39.900732 * 1e6),
	// (int) (116.433547 * 1e6));
	//
	// QRouteSearchResult driveSearchResult = null;
	// try {
	// driveSearchResult = routeSearch.searchDriveRoute("北京", placeStart,
	// "北京", placeEnd);
	// } catch (Exception e) {
	// e.printStackTrace();
	// return;
	// }
	// if (driveSearchResult == null) {
	// return;
	// }
	// }
}
