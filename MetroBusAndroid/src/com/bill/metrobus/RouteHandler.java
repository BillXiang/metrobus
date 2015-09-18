package com.bill.metrobus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.tencent.tencentmap.mapsdk.map.GeoPoint;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.RouteOverlay;
import com.tencent.tencentmap.mapsdk.route.QRouteSearchResult;
import com.tencent.tencentmap.mapsdk.search.BusInfoDetails;
import com.tencent.tencentmap.mapsdk.search.BusStation;
import com.tencent.tencentmap.mapsdk.search.PoiItem;

public class RouteHandler extends Handler {

	public static final int ROUTE_SRARCH_SUCCEED = 0;
	public static final int ROUTE_SRARCH_FAILED = 1;
	public static final int ROUTE_PARAMETER = 2;// 外部传入线路信息
	public static final int BUS_POSITION = 3;// 公交位置信息

	private Context context;
	private ProgressDialog progressDialog = null;
	private MapView mapView;
	private RouteOverlay busRouteOverlay = null;
	private RouteOverlay driveRouteOverlay = null;
	private BusPositionOverlay busPositionOverlay = null;
	private QRouteSearchResult busRouteResult = null;
	private Paint paint;

	public RouteHandler(Context context, ProgressDialog progressDialog,
			MapView mapView) {
		this.context = context;
		this.progressDialog = progressDialog;
		this.mapView = mapView;
		mapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);// 无硬件加速，否则路线显示有问题
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case ROUTE_SRARCH_SUCCEED:
			busRouteResult = (QRouteSearchResult) msg.obj;
			if (busRouteOverlay == null) {
				busRouteOverlay = new RouteOverlay();
				mapView.addOverlay(busRouteOverlay);
			}
			busRouteOverlay
					.setBusRouteInfo(busRouteResult.busRoutePlanInfo.routeList
							.get(0));
			busRouteOverlay.showInfoWindow(0);
			zoomToSpan(busRouteResult.busRoutePlanInfo.routeList.get(0).routeNodeList);
			break;
		case ROUTE_SRARCH_FAILED:
			Toast.makeText(context, "未找到公交路线", Toast.LENGTH_SHORT).show();
			break;
		case ROUTE_PARAMETER:
			BusInfoDetails busInfoDetails = (BusInfoDetails) msg.obj;
			if (busRouteOverlay == null) {
				busRouteOverlay = new RouteOverlay();
				mapView.addOverlay(busRouteOverlay);
			}
			busRouteOverlay.setRoutePoints(busInfoDetails.points);

			List<PoiItem> poiList = new ArrayList<PoiItem>();
			for (BusStation busStation : busInfoDetails.busstations) {
				PoiItem poiItem = new PoiItem();
				poiItem.point = busStation.point;
				poiItem.pInfo = busStation.name;
				poiItem.name = busStation.name;
				poiItem.poitype = busStation.poitype;
				poiList.add(poiItem);
			}
			busRouteOverlay.setPoiItems(poiList);

			busRouteOverlay.showInfoWindow(0);
			zoomToSpan(busInfoDetails.points);
			break;

		case BUS_POSITION:
//			HashMap<String, Object> parameters = (HashMap<String, Object>) msg.obj;
			JSONObject parameters = (JSONObject) msg.obj;
			String busId = null;
			int latitudeE6 = 0;
			int longitudeE6 = 0;
			String remoteUser = null;
			try {
				busId = (String) parameters.get("busId");
				latitudeE6 = (Integer) parameters.get("latitudeE6");
				longitudeE6 = (Integer) parameters.get("longitudeE6");
				remoteUser = parameters.getString("remoteUser");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			busPositionOverlay = new BusPositionOverlay(context.getResources()
					.getDrawable(R.drawable.markpoint), context);
			busPositionOverlay.addItem(busId, remoteUser, latitudeE6, longitudeE6);
			mapView.addOverlay(busPositionOverlay);
			mapView.invalidate();
			break;
		default:
			break;
		}
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	/**
	 * 把地图视图缩放到刚好显示路线
	 * 
	 * @param listPts
	 */
	private void zoomToSpan(List<GeoPoint> listPts) {
		if (listPts == null) {
			return;
		}
		int iPtSize = listPts.size();
		if (iPtSize <= 0) {
			return;
		}

		GeoPoint geoPtLeftUp = null;
		GeoPoint geoPtRightDown = null; // 获取路径点的左上角点，和右下角点

		GeoPoint geoPt = null;
		for (int i = 0; i < iPtSize; i++) {
			geoPt = listPts.get(i);
			if (geoPt == null) {
				continue;
			}

			if (geoPtLeftUp == null) {
				geoPtLeftUp = new GeoPoint(geoPt.getLatitudeE6(),
						geoPt.getLongitudeE6());
			} else {
				if (geoPtLeftUp.getLatitudeE6() < geoPt.getLatitudeE6()) {
					geoPtLeftUp.setLatitudeE6(geoPt.getLatitudeE6());
				}
				if (geoPtLeftUp.getLongitudeE6() > geoPt.getLongitudeE6()) {
					geoPtLeftUp.setLongitudeE6(geoPt.getLongitudeE6());
				}
			}

			if (geoPtRightDown == null) {
				geoPtRightDown = new GeoPoint(geoPt.getLatitudeE6(),
						geoPt.getLongitudeE6());
			} else {
				if (geoPtRightDown.getLatitudeE6() > geoPt.getLatitudeE6()) {
					geoPtRightDown.setLatitudeE6(geoPt.getLatitudeE6());
				}
				if (geoPtRightDown.getLongitudeE6() < geoPt.getLongitudeE6()) {
					geoPtRightDown.setLongitudeE6(geoPt.getLongitudeE6());
				}
			}

		}

		if (geoPtLeftUp == null || geoPtRightDown == null) {
			return;
		}
		mapView.getController().zoomToSpan(geoPtLeftUp, geoPtRightDown);
	}
}
