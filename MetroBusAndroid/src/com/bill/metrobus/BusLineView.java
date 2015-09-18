package com.bill.metrobus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BusLineView{
	private Context context;
	private LayoutInflater inflater;
	private View busLineView;
	private TextView headView;
	private TextView start;
	private TextView end;
	private LinearLayout middle;
	public BusLineView(Context context) {
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		busLineView = inflater.inflate(R.layout.detail_bus_line, null);
		headView = (TextView) busLineView.findViewById(R.id.bus_line_head);
		start = (TextView) busLineView.findViewById(R.id.bus_line_start);
		end = (TextView) busLineView.findViewById(R.id.bus_line_end);
		middle = (LinearLayout) busLineView.findViewById(R.id.bus_line_middle);
		//addStop("middle");
	}
	
	public void setName(String name){
		headView.setText(name);
	}
	
	public void setStart(String name){
		start.setText(name);
	}
	
	public void setEnd(String name){
		end.setText(name);
	}
	
	public void addStop(String name){
		View busStopView = inflater.inflate(R.layout.bus_stop, null);
		TextView busStop = (TextView) busStopView.findViewById(R.id.bus_stop);
		busStop.setText(name);
		middle.addView(busStopView);
	}
	
	public View getBusLineView(){
		return busLineView;
	}
}
