package com.bill.metrobus.message;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

public class MessageManager {
	private static MessageManager instance = new MessageManager();

	private List<String> userList;

	private MessageManager() {
		this.userList = new ArrayList<String>();
	}

	public static MessageManager getInstance() {
		return instance;
	}

	public void addUser(String user) {
		userList.add(user);
	}

	public void removeUser(String user) {
		userList.remove(user);
	}

	public void broadcast(CharBuffer msg) throws IOException {
		for (String myWebSocket : userList) {
			//myWebSocket.sendMessage("broadcasting:" + msg);
		}
	}
}
