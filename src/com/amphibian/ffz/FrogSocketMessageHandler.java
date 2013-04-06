package com.amphibian.ffz;

import org.json.JSONObject;

import android.util.Log;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIOException;

public class FrogSocketMessageHandler implements IOCallback {

	@Override
	public void on(String event, IOAcknowledge ack, Object... args) {

		if ("newfrog".equals(event)) {
			Log.i("ffz", "newfrog event happened! " + args[0]);
		}
		
	}

	@Override
	public void onConnect() {
		Log.i("ffz", "websocket connected!");
	}

	@Override
	public void onDisconnect() {
		Log.i("ffz", "websocket disconnected!");
	}

	@Override
	public void onError(SocketIOException arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessage(String arg0, IOAcknowledge arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessage(JSONObject arg0, IOAcknowledge arg1) {
		// TODO Auto-generated method stub

	}

}
