package com.bill.metrobus.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import com.bill.metrobus.FragmentTabsPager;
import com.bill.metrobus.MainActivity;
import com.bill.metrobus.MapFragment;
import com.bill.metrobus.RouteHandler;
import com.bill.metrobus.SimpleLocationListener;
import com.bill.metrobus.util.ContextUtil;
import com.tencent.tencentmap.lbssdk.TencentMapLBSApi;

import cn.jpush.android.api.JPushInterface;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MessageReceiver extends BroadcastReceiver {

	private String TAG = "MessageReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Bundle bundle = intent.getExtras();
        Log.d(TAG, "onReceive - " + intent.getAction());
         
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
             
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	// 自定义消息不会展示在通知栏，完全要开发者写代码去处理
        	String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            System.out.println("收到了自定义消息。消息内容是：" + message);
            JSONObject jsonObject = null;
    		try {
    			jsonObject = new JSONObject(message);
    		} catch (JSONException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
    		if(jsonObject==null){
    			return;
    		}
    		String action = null;
    		String busId = null;
    		try {
    			action = jsonObject.getString("action");
    			busId = jsonObject.getString("busId");
    		} catch (JSONException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} 
    		if("requestBusPosition".equals(action)){//服务器向用户请求位置信息
    			//用户向服务器提交公交位置信息
    			TencentMapLBSApi.getInstance().requestLocationUpdate(ContextUtil.getInstance(), 
    					new SimpleLocationListener(TencentMapLBSApi.GEO_TYPE_GCJ02,
    												TencentMapLBSApi.LEVEL_ADMIN_AREA,
    												TencentMapLBSApi.DELAY_NORMAL, busId));
    		}else if("updateBusPosition".equals(action)){//服务器向用户发布指定公交位置信息
    			//用户更新本地指定公交位置信息
    			int latitudeE6 = 0;
    			int longitudeE6 = 0;
    			String remoteUser = null;
    			try {
    				latitudeE6 = jsonObject.getInt("latitudeE6");
    				longitudeE6 = jsonObject.getInt("longitudeE6");
    				remoteUser = jsonObject.getString("remoteUser");
    				Log.i("updateBusPosition", "公交："+busId+"位置："+latitudeE6+" "+longitudeE6+" remoteUser:"+remoteUser);
    				FragmentTabsPager.tabsAdapter.navigateTo("nearby");
    				MapFragment.routeHandler.obtainMessage(RouteHandler.BUS_POSITION, jsonObject).sendToTarget();
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
//    			if(handler!=null){
//    				handler.obtainMessage(RouteHandler.BUS_POSITION, jsonObject).sendToTarget();
//    			}
//    			if (busPositionChangeListener!=null) {
//    				busPositionChangeListener.busPositionChanged(busId, latitudeE6, longitudeE6);
//    			}
    		}

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            System.out.println("收到了通知");
            // 在这里可以做些统计，或者做些其他工作
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            System.out.println("用户点击打开了通知");
            // 在这里可以自己写代码去定义用户点击后的行为
            Intent i = new Intent(context, MainActivity.class);  //自定义打开的界面
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
	}
}
