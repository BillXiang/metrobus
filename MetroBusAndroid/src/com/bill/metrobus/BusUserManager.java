package com.bill.metrobus;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.bill.metrobus.util.ContextUtil;
import com.bill.metrobus.util.HttpClientHelper;

import android.content.Context;
import android.os.Handler;

public class BusUserManager {
	private String user = null;
	private static BusUserManager busUserManager = new BusUserManager();
	public static BusUserManager getInstance(String user){
		busUserManager.user = user;
		return busUserManager;
	}
	/**
	 * 上车
	 * @param busId 公交id
	 */
	public void getOn(String busId){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("action", "getOn");
			jsonObject.put("busId", busId);
			jsonObject.put("user", UserFragment.userName);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//WSClient.getInstance().send(jsonObject.toString());
		HttpClientHelper.sendMessage(user, jsonObject.toString(), ContextUtil.getInstance(), null);
	}
	/**
	 * 下车
	 * @param busId
	 */
	public void getOff(String busId){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("action", "getOff");
			jsonObject.put("busId", busId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//WSClient.getInstance().send(jsonObject.toString());
		HttpClientHelper.sendMessage(user, jsonObject.toString(), ContextUtil.getInstance(), null);
	}
	/**
	 * 用户向服务器请求某个公交位置信息
	 * @param busId 公交id
	 * @return
	 */
	public void requestBusPosition(Context context, String busId, Handler handler){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("action", "requestBusPosition");
			jsonObject.put("busId", busId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//WSClient.getInstance(handler).send(jsonObject.toString());
		HttpClientHelper.sendMessage(user, jsonObject.toString(), ContextUtil.getInstance(), null);
	}
	/**
	 * 用户向服务器报告某个公交位置信息
	 * @param busId
	 * @param latitudeE6
	 * @param longitudeE6
	 */
	public void updateBusPosition(String busId, int latitudeE6, int longitudeE6){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("action", "updateBusPosition");
			jsonObject.put("busId", busId);
			jsonObject.put("latitudeE6", latitudeE6);
			jsonObject.put("longitudeE6", longitudeE6);
			jsonObject.put("user", user);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//WSClient.getInstance().send(jsonObject.toString());
		HttpClientHelper.sendMessage(user, jsonObject.toString(), ContextUtil.getInstance(), null);
	}
	
	public void getUsers(String user){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("action", "getUsers");
			jsonObject.put("user", user);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpClientHelper.sendMessage(user, jsonObject.toString(), ContextUtil.getInstance(), null);
	}
}
