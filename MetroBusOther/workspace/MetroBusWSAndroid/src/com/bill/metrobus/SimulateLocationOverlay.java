package com.bill.metrobus;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;

import com.tencent.tencentmap.mapsdk.map.GeoPoint;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.Overlay;
import com.tencent.tencentmap.mapsdk.map.Projection;

public class SimulateLocationOverlay extends Overlay {
	
	GeoPoint geoPoint;
	Bitmap bmpMarker;
	float fAccuracy=0f;
	

	public SimulateLocationOverlay(Bitmap mMarker) {
	    bmpMarker = mMarker;
	}
	
	public void setGeoCoords(GeoPoint geoSimulateLoc)
	{
		if(geoPoint==null)
		{
			geoPoint=new GeoPoint(geoSimulateLoc.getLatitudeE6(),geoSimulateLoc.getLongitudeE6());
		}
		else
		{
			geoPoint.setLatitudeE6(geoSimulateLoc.getLatitudeE6());
			geoPoint.setLongitudeE6(geoSimulateLoc.getLongitudeE6());
		}
	}
	
	public void setAccuracy(float fAccur)
	{
		fAccuracy=fAccur;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView) {
		if(geoPoint==null)
		{
			return;
		}
		Projection mapProjection = mapView.getProjection();
		Paint paint = new Paint();
		Point ptMap = mapProjection.toPixels(geoPoint, null);
		paint.setColor(Color.BLUE);
		paint.setAlpha(8);
		paint.setAntiAlias(true);

		float fRadius=mapProjection.metersToEquatorPixels(fAccuracy);
		canvas.drawCircle(ptMap.x, ptMap.y, fRadius, paint);
		paint.setStyle(Style.STROKE);
		paint.setAlpha(200);
		canvas.drawCircle(ptMap.x, ptMap.y, fRadius, paint);

		if(bmpMarker!=null)
		{
			paint.setAlpha(255);
			canvas.drawBitmap(bmpMarker, ptMap.x - bmpMarker.getWidth() / 2, ptMap.y
					- bmpMarker.getHeight() / 2, paint);
		}
		
		super.draw(canvas, mapView);
	}
}