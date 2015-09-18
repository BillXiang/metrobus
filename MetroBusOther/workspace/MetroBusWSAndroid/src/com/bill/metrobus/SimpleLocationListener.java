package com.bill.metrobus;

import com.tencent.tencentmap.lbssdk.TencentMapLBSApi;
import com.tencent.tencentmap.lbssdk.TencentMapLBSApiListener;
import com.tencent.tencentmap.lbssdk.TencentMapLBSApiResult;

public class SimpleLocationListener extends TencentMapLBSApiListener{

	private String busId;
	
	public SimpleLocationListener(int reqGeoType, int reqLevel,	int reqDelay, String busId) {
		super(reqGeoType, reqLevel, reqDelay);
		this.busId = busId;
	}
	
	@Override
	public void onLocationUpdate(TencentMapLBSApiResult arg0) {
		TencentMapLBSApi.getInstance().removeLocationUpdate();
		int latitudeE6 = (int) (arg0.Latitude * 1e6);
		int longitudeE6 = (int) (arg0.Longitude * 1e6);
		BusUserManager.getInstance().updateBusPosition(busId, latitudeE6, longitudeE6);
	}
}
