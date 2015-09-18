package com.bill.metrobus;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import com.tencent.tencentmap.mapsdk.map.GeoPoint;
import com.tencent.tencentmap.mapsdk.map.ItemizedOverlay;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.OverlayItem;
import com.tencent.tencentmap.mapsdk.map.Projection;

public class BusPositionOverlay extends ItemizedOverlay<OverlayItem> {

	private List<OverlayItem> itemList = new ArrayList<OverlayItem>();
	// private Context mContext;
	private OnTapListener tapListener = null;

	public BusPositionOverlay(Drawable marker, Context context) {
		super(boundCenterBottom(marker));

//		// 构造OverlayItem的三个参数依次为：item的位置，标题文本，文字片段
//		itemList.add(new OverlayItem(p1, "1", "已选中第一个点"));
//		OverlayItem itemNntDrag = new OverlayItem(p2, "2", "该点无法拖拽");
//		itemNntDrag.setDragable(false);
//		itemList.add(itemNntDrag);
//		itemList.add(new OverlayItem(p3, "3", "已选中第三个点"));
//		populate(); // createItem(int)方法构造item。一旦有了数据，在调用其它方法前，首先调用这个方法
	}
	
	public void addItem(String busId, String title, int latitudeE6, int longitudeE6){
		itemList.add(new OverlayItem(new GeoPoint(latitudeE6, longitudeE6), title, busId));
		populate();
	}

	@Override
	public void draw(Canvas canvas, MapView mapView) {

		// Projection接口用于屏幕像素点坐标系统和地球表面经纬度点坐标系统之间的变换
		Projection projection = mapView.getProjection();
		for (int index = size() - 1; index >= 0; index--) { // 遍历GeoList
			OverlayItem overLayItem = getItem(index); // 得到给定索引的item

			String title = overLayItem.getTitle();
			// 把经纬度变换到相对于MapView左上角的屏幕像素坐标
			Point point = projection.toPixels(overLayItem.getPoint(), null);

			Paint paintCircle = new Paint();
			paintCircle.setColor(Color.RED);
			canvas.drawCircle(point.x, point.y, 5, paintCircle); // 画圆

			Paint paintText = new Paint();
			paintText.setColor(Color.BLACK);
			paintText.setTextSize(15);
			canvas.drawText(title, point.x, point.y - 25, paintText); // 绘制文本
		}

		super.draw(canvas, mapView);
	}

	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return itemList.get(i);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return itemList.size();
	}

	@Override
	protected boolean onTap(int i) {
		OverlayItem itemSelect = itemList.get(i);
		setFocus(itemSelect);
		if (tapListener != null) {
			tapListener.onTap(itemSelect);
		}
		return true;
	}

	@Override
	public void onEmptyTap(GeoPoint pt) {
		// TODO Auto-generated method stub
		super.onEmptyTap(pt);

		if (tapListener != null) {
			tapListener.onEmptyTap(pt);
		}
	}

	interface OnTapListener {
		void onTap(OverlayItem itemTap);

		void onEmptyTap(GeoPoint pt);
	}

	public void setOnTapListener(OnTapListener listnerTap) {
		tapListener = listnerTap;
	}
}