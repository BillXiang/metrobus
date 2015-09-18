package com.bill.metrobus.servlet;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

import com.bill.metrobus.websocket.MyWebSocket;

/**
 * Servlet implementation class MyWebSocketServlet
 */
public class MyWebSocketServlet extends WebSocketServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public WebSocket doWebSocketConnect(HttpServletRequest arg0, String arg1) {
		// TODO Auto-generated method stub
		return new MyWebSocket();
	}
}
