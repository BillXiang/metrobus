package com.bill.metrobus.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;

public class ExampleClient extends WebSocketClient {

	public ExampleClient(URI serverUri, Draft draft) {
		super(serverUri, draft);
	}

	public ExampleClient(URI serverURI) {
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
		System.out.println("received: " + message);
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

	public static void main(String[] args) throws URISyntaxException {
		ExampleClient c = new ExampleClient(new URI(
				"ws://localhost:8080/MetroBus/metrobus.ws"), new Draft_17());
		WebSocket webSocket = null;
		try {
			c.connectBlocking();
			webSocket = c.getConnection();
			Scanner sc = new Scanner(System.in);
			while(true){
				webSocket.send(sc.next());
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
