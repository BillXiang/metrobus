package com.bill.metrobus;

import java.io.Serializable;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.tencent.tencentmap.mapsdk.search.BusItem;

/**
 * This class holds the per-item data in our Loader.
 */
public class BusEntry implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final BusItem busInfo;
	private CharSequence label;
	private Drawable mIcon;
	private Context context;

	public BusEntry(BusItem info, Context context) {
		busInfo = info;
		this.context = context;
	}

	public BusItem getBusInfo() {
		return busInfo;
	}

	public CharSequence getLabel() {
		return label;
	}

	public Drawable getIcon() {
		if(mIcon==null){
			Resources res = context.getResources();
			mIcon = res.getDrawable(R.drawable.bus);
		}
		return mIcon;
	}

	@Override
	public String toString() {
		return label.toString();
	}

	void loadLabel() {
		//label = busInfo.name + ":" + busInfo.from + " 至 " + busInfo.to;
		label = android.text.Html.fromHtml("<b><big>"+busInfo.name+"</big></b><br>"+busInfo.from + " 至 " + busInfo.to);
	}
}
