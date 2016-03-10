package com.cnme.backend.redDevil.web;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import com.cnme.backend.redDevil.model.Log;

import net.sf.json.JSONObject;

@ServerEndpoint(value = "/echoendpoint")
public class EchoEndpoint {
	@OnOpen
	public void start(Session session) {
		try {
			Log.info("session " + session.getId() + " open.");
		} catch (NullPointerException e) {
			Log.exception(e);
		}
	}

	@OnMessage
	public void process(Session session, String message) {
		try {
			Log.info("session " + session.getId() + ">" + message);
			JSONObject msg = JSONObject.fromObject(message);
			Actions.handle(session, msg);
		} catch (Exception e) {
			Log.exception(e);
		}
	}

	@OnClose
	public void end(Session session) {
		Log.info("session " + session.getId() + " close.");
		try {
			RedisHelper.removeSession(session);
		} catch (NullPointerException e) {
			Log.exception(e);
		}
	}

	@OnError
	public void error(Session session, java.lang.Throwable throwable) {
		Log.error("session " + session.getId(), throwable);
		try {
			RedisHelper.removeSession(session);
		} catch (NullPointerException e) {
			Log.exception(e);
		}
	}
}
