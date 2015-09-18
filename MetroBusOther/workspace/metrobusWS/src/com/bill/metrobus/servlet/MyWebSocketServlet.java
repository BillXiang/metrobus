package com.bill.metrobus.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;

import com.bill.metrobus.websocket.MyMessageInbound;

/**
 * Servlet implementation class MyWebSocketServlet
 */
public class MyWebSocketServlet extends WebSocketServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected StreamInbound createWebSocketInbound(String subProtocol,
			HttpServletRequest request) {
		// System.out.println("##########client login#########");
		return new MyMessageInbound();
	}
}
