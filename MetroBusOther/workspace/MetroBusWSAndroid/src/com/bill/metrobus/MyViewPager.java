package com.bill.metrobus;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager{

	public MyViewPager(Context context) {
		super(context);
	}
	
	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		int currentItem = this.getCurrentItem();
		if(currentItem==0){
			((TabsAdapter)this.getAdapter()).getPrimaryItem().getView().dispatchTouchEvent(arg0);
			return true;
		}
		return super.onInterceptTouchEvent(arg0);
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		int currentItem = this.getCurrentItem();
		if(currentItem==0){
			((TabsAdapter)this.getAdapter()).getPrimaryItem().getView().dispatchTouchEvent(arg0);
			return true;
		}
		return super.onTouchEvent(arg0);
	}
}