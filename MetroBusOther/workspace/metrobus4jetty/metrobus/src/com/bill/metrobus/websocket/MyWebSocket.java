package com.bill.metrobus.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Date;

import org.eclipse.jetty.websocket.WebSocket;

import net.sf.json.JSON;
import net.sf.json.JSONObject;

import com.bill.metrobus.entity.BusUserManager;

public class MyWebSocket implements WebSocket, 
								WebSocket.OnControl,
								WebSocket.OnBinaryMessage,
								WebSocket.OnFrame,
								WebSocket.OnTextMessage{
	private Connection connection;
	
	public Connection getConnection(){
		return this.connection;
	}
	
	public void sendMessage(String text){
		if(connection!=null){
			try {
				connection.sendMessage(text);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onClose(int arg0, String arg1) {
		// TODO Auto-generated method stub
		System.out.println("onClose");
	}

	@Override
	public void onOpen(Connection connection) {
		// TODO Auto-generated method stub
		System.out.println("onOpen");
		this.connection = connection;
	}

	@Override
	public boolean onControl(byte arg0, byte[] arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		System.out.println("onControl");
		return false;
	}


	@Override
	public void onMessage(byte[] arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		System.out.println(new String(arg0));
	}

	@Override
	public boolean onFrame(byte arg0, byte arg1, byte[] arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		System.out.println("onFrame");
		return false;
	}

	@Override
	public void onHandshake(FrameConnection arg0) {
		// TODO Auto-generated method stub
		System.out.println("onHandshake");
	}

	@Override
	public void onMessage(String arg0) {
		// TODO Auto-generated method stub
		System.out.println(arg0);
	}
}
