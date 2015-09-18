package com.bill.metrobus.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;

import com.bill.metrobus.listener.SysListener;

import net.sf.json.JSONObject;

public class BusUserManager {
	private static BusUserManager busUserManager = new BusUserManager();
	/**
	 * b1->S11 S12 S13 ... S1n 
	 * b2->S21 S22 S23 ... S2n 
	 * bm->Sm1 Sm2 Sm3 ... Smn
	 * 
	 * 存储所有有用户的公交id，及与该公交关联的用户的连接信息
	 * 公交id作为HashMap的String键，所有在该公交上的用户连接信息存储在对应键的值域List中
	 */
	private HashMap<String, List<String>> busUser;

	/**
	 * 正在等待获取指定公交位置信息的用户连接信息队列
	 */
	private HashMap<String, List<String>> busPositionWaitQueue;

	private BusUserManager() {
		busUser = new HashMap<String, List<String>>();
		busPositionWaitQueue = new HashMap<String, List<String>>();
	}

	public static BusUserManager getInstance() {
		return busUserManager;
	}

	/**
	 * 上车
	 * 
	 * @param busId
	 *            公交id
	 * @param user
	 *            用户信息
	 */
	public void getOn(String busId, String user) {
		System.out.println("getOn:" + busId);
		if (busUser.containsKey(busId)) {
			busUser.get(busId).add(user);
		} else {
			List<String> users = new ArrayList<String>();
			users.add(user);
			busUser.put(busId, users);
		}
	}
	 
	/**
	 * 下车
	 * 
	 * @param busId
	 * @param user
	 */
	public void getOff(String busId, String user) {
		System.out.println("getOff:" + busId);
		if (busUser.containsKey(busId)) {
			busUser.get(busId).remove(user);
		} else {
			return;
		}
	}

	/**
	 * 服务器向用户请求某个公交位置信息
	 * 
	 * @param busId
	 *            公交id
	 * @param user
	 *            发起该请求的用户信息
	 * @return
	 */
	public String requestBusPosition(String busId, String user) {
		System.out.println("requestBusPosition:" + busId);
		if (busUser.containsKey(busId)) {

			JSONObject requestBusPositionJson = new JSONObject();
			requestBusPositionJson.put("action", "requestBusPosition");
			requestBusPositionJson.put("busId", busId);

			List<String> users = busUser.get(busId);
			// 向该公交上所有用户发起信息位置请求
			for (int i = 0; i < users.size(); i++) {
				// TODO
				// WebSocket.Connection connection =
				// users.get(i).getConnection();
				// connection.sendMessage(requestBusPositionJson.toString());
				SysListener.jpushClient.sendPush(
						PushPayload.newBuilder()
							.setPlatform(Platform.android())
							.setAudience(Audience.newBuilder()
									.addAudienceTarget(
										AudienceTarget.alias(users.get(i)))
									.build())
							.setMessage(Message.newBuilder()
			                        .setMsgContent(requestBusPositionJson.toString())
			                        .build())
							.build());
				
			}
			// 将自己的连接信息加到该公交的位置信息等待队列中
			if (busPositionWaitQueue.containsKey(busId)) {
				busPositionWaitQueue.get(busId).add(user);
			} else {
				List<String> waitUsers = new ArrayList<String>();
				waitUsers.add(user);
				busPositionWaitQueue.put(busId, waitUsers);
			}
			return "请稍后";
		}
		return "没有相关信息";
	}

	/**
	 * 服务器向用户报告某个公交位置信息
	 * 
	 * @param busId
	 * @param latitudeE6
	 * @param longitudeE6
	 */
	public void updateBusPosition(String busId, int latitudeE6, int longitudeE6, String remoteUser) {
		System.out.println("updateBusPosition:" + busId + " " + latitudeE6
				+ " " + longitudeE6);
		if (busPositionWaitQueue.containsKey(busId)) {

			JSONObject updateBusPositionJson = new JSONObject();
			updateBusPositionJson.put("action", "updateBusPosition");
			updateBusPositionJson.put("busId", busId);
			updateBusPositionJson.put("latitudeE6", latitudeE6);
			updateBusPositionJson.put("longitudeE6", longitudeE6);
			updateBusPositionJson.put("remoteUser", remoteUser);

			List<String> waitUsers = busPositionWaitQueue.get(busId);
			// 向等待该公交位置信息的每一个用户发送该公交位置信息
			for (int i = 0; i < waitUsers.size(); i++) {
				// TODO
				// WebSocket.Connection connection = waitUsers.get(i)
				// .getConnection();
				// connection.sendMessage(updateBusPositionJson.toString());
				SysListener.jpushClient.sendPush(
						PushPayload.newBuilder()
							.setPlatform(Platform.android())
							.setAudience(Audience.newBuilder()
									.addAudienceTarget(
										AudienceTarget.alias(waitUsers.get(i)))
									.build())
							.setMessage(Message.newBuilder()
			                        .setMsgContent(updateBusPositionJson.toString())
			                        .build())
							.build());
			}
		}
	}
	//获取指定公交所有在线用户
	public List<String> getBusUser(String busId){
		return busUser.get(busId);
	}
	//获取所有在线用户
	public List<String> getAllUser(){
		List<String> result = new ArrayList<String>();
		Iterator iterator = busUser.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry entry = (Map.Entry) iterator.next();
			result.addAll((List<String>)entry.getValue());
		}
		return result;
	}
}
