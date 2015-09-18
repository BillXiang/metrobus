package com.bill.metrobus.websocket;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;
import com.bill.metrobus.FragmentTabsPager;
import com.bill.metrobus.RouteHandler;
import com.bill.metrobus.SimpleLocationListener;
import com.bill.metrobus.util.ContextUtil;
import com.tencent.tencentmap.lbssdk.TencentMapLBSApi;

public class WSClient extends WebSocketClient {
	private static WSClient wsClient = null;
	
	private static BusPositionChangeListener busPositionChangeListener;
	
	private static Handler handler;
	
	public static WSClient getInstance(){
		try {
			wsClient = new WSClient(new URI(FragmentTabsPager.WEBSOCKET));
			if(!wsClient.isOpen()){
				wsClient.connectBlocking();
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return wsClient;
	}
	
	public static WSClient getInstance(BusPositionChangeListener mBusPositionChangeListener){
		busPositionChangeListener = mBusPositionChangeListener;
		return getInstance();
	}
	public static WSClient getInstance(Handler mHandler){
		handler = mHandler;
		return getInstance();
	}
	
	public WSClient(URI serverUri, Draft draft) {
		super(serverUri, draft);
	}

	public WSClient(URI serverURI) {
		super(serverURI);
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		System.out.println("opened connection");
		// if you plan to refuse connection based on ip or httpfields overload:
		// onWebsocketHandshakeReceivedAsClient
	}

	@Override
	public void onMessage(String message) {
		System.out.println(message);
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
			try {
				latitudeE6 = jsonObject.getInt("latitudeE6");
				longitudeE6 = jsonObject.getInt("longitudeE6");
				Log.i("updateBusPosition", "公交："+busId+"位置："+latitudeE6+" "+longitudeE6);
//				FragmentTabsPager.tabsAdapter.navigateTo("nearby");
//				MapFragment.routeHandler.obtainMessage(RouteHandler.BUS_POSITION, jsonObject).sendToTarget();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(handler!=null){
				handler.obtainMessage(RouteHandler.BUS_POSITION, jsonObject).sendToTarget();
			}
			if (busPositionChangeListener!=null) {
				busPositionChangeListener.busPositionChanged(busId, latitudeE6, longitudeE6);
			}
		}
	}

	@Override
	public void onFragment(Framedata fragment) {
		System.out.println("received fragment: "
				+ new String(fragment.getPayloadData().array()));
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		// The codecodes are documented in class
		// org.java_websocket.framing.CloseFrame
		System.out.println("Connection closed by "
				+ (remote ? "remote peer" : "us"));
	}

	@Override
	public void onError(Exception ex) {
		ex.printStackTrace();
		// if the error is fatal then onClose will be called additionally
	}
	
	public interface BusPositionChangeListener{
		public void busPositionChanged(String busId, int latitudeE6, int longitudeE6);
	}
}
