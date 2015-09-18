package com.bill.metrobus.websocket;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

public class MessageManager {
	private static MessageManager instance = new MessageManager();

	private List<MyWebSocket> socketList;

	private MessageManager() {
		this.socketList = new ArrayList<MyWebSocket>();
	}

	public static MessageManager getInstance() {
		return instance;
	}

	public void addMessageInbound(MyWebSocket myWebSocket) {
		socketList.add(myWebSocket);
	}

	public void removeMessageInbound(MyWebSocket myWebSocket) {
		socketList.remove(myWebSocket);
	}

	public void broadcast(CharBuffer msg) throws IOException {
		for (MyWebSocket myWebSocket : socketList) {
			myWebSocket.sendMessage("broadcasting:" + msg);
		}
	}
}
