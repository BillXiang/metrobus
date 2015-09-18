package com.bill.metrobus.entity;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;

public class BusUserManager {
	private static BusUserManager busUserManager = new BusUserManager();
	/**
	 * b1->S11 S12 S13 ... S1n
	 * b2->S21 S22 S23 ... S2n
	 * .
	 * .
	 * .
	 * bm->Sm1 Sm2 Sm3 ... Smn
	 * 
	 * 存储所有有用户的公交id，及与该公交关联的用户的连接信息
	 * 公交id作为HashMap的String键，所有在该公交上的用户连接信息存储在对应键的值域List中
	 */
	private HashMap<String, List<MessageInbound>> busUser ;
	
	/**
	 * 正在等待获取指定公交位置信息的用户连接信息队列
	 */
	private HashMap<String, List<MessageInbound>> busPositionWaitQueue;
	
	private BusUserManager(){
		busUser = new HashMap<String, List<MessageInbound>>();
		busPositionWaitQueue = new HashMap<String, List<MessageInbound>>();
	}
	
	public static BusUserManager getInstance(){
		return busUserManager;
	}
	/**
	 * 上车
	 * @param busId 公交id
	 * @param socket 用户连接信息
	 */
	public void getOn(String busId, MessageInbound socket){
		System.out.println("getOn:"+busId);
		if(busUser.containsKey(busId)){
			busUser.get(busId).add(socket);
		}else{
			List<MessageInbound> users = new ArrayList<MessageInbound>();
			users.add(socket);
			busUser.put(busId, users);
		}
	}
	/**
	 * 下车
	 * @param busId
	 * @param socket
	 */
	public void getOff(String busId, MessageInbound socket){
		System.out.println("getOff:"+busId);
		if(busUser.containsKey(busId)){
			busUser.get(busId).remove(socket);
		}else{
			return;
		}
	}
	/**
	 * 服务器向用户请求某个公交位置信息
	 * @param busId 公交id
	 * @param socket 发起该请求的用户连接信息
	 * @return
	 */
	public String requestBusPosition(String busId, MessageInbound socket){
		System.out.println("requestBusPosition:"+busId);
		if(busUser.containsKey(busId)){
			
			JSONObject requestBusPositionJson = new JSONObject();
			requestBusPositionJson.put("action", "requestBusPosition");
			requestBusPositionJson.put("busId", busId);
			
			List<MessageInbound> users = busUser.get(busId);
			//向该公交上所有用户发起信息位置请求
			for(int i=0; i<users.size(); i++){
				try {
					WsOutbound outbound = users.get(i).getWsOutbound();
					//TODO
					outbound.writeTextMessage(CharBuffer.wrap(requestBusPositionJson.toString()));
					outbound.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//将自己的连接信息加到该公交的位置信息等待队列中
			if(busPositionWaitQueue.containsKey(busId)){
				busPositionWaitQueue.get(busId).add(socket);
			}else{
				List<MessageInbound> waitUsers = new ArrayList<MessageInbound>();
				waitUsers.add(socket);
				busPositionWaitQueue.put(busId, waitUsers);
			}
			return "请稍后";
		}
		return "没有相关信息";
	}
	/**
	 * 服务器向用户报告某个公交位置信息
	 * @param busId
	 * @param latitudeE6
	 * @param longitudeE6
	 */
	public void updateBusPosition(String busId, int latitudeE6, int longitudeE6){
		System.out.println("updateBusPosition:"+busId+" "+latitudeE6+" "+longitudeE6 );
		if(busPositionWaitQueue.containsKey(busId)){
			
			JSONObject updateBusPositionJson = new JSONObject();
			updateBusPositionJson.put("action", "updateBusPosition");
			updateBusPositionJson.put("busId", busId);
			updateBusPositionJson.put("latitudeE6", latitudeE6);
			updateBusPositionJson.put("longitudeE6", longitudeE6);
			
			List<MessageInbound> waitUsers = busPositionWaitQueue.get(busId);
			//向等待该公交位置信息的每一个用户发送该公交位置信息
			for(int i=0; i<waitUsers.size(); i++){
				//TODO
				try {
					WsOutbound outbound = waitUsers.get(i).getWsOutbound();
					outbound.writeTextMessage(CharBuffer.wrap(updateBusPositionJson.toString()));
					outbound.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
