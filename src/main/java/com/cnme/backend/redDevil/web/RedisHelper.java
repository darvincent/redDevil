package com.cnme.backend.redDevil.web;

import java.util.Hashtable;
import java.util.List;

import javax.websocket.Session;

import com.cnme.backend.redDevil.model.Log;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;

public class RedisHelper {
	private static Hashtable<String, Session> UserSession;
	private static Jedis Jedis;
	public static Jedis getJedis() {
		return Jedis;
	}

	private static String USERNOTFOUND;
	private static String Msg_suffix;
	private static String Sessions_key;

	public static void intial() {
		UserSession = new Hashtable<String, Session>();
		Jedis = new Jedis("127.0.0.1");
		USERNOTFOUND = "userNotFound";
		Msg_suffix = "_m";
		Sessions_key = "sessions";
		Log.info("Redis intial done!");
	}

	public static void putSession(String username, Session session)
			throws NullPointerException {
		Jedis.hset(Sessions_key, session.toString(), username);
		UserSession.put(username, session);
	}

	public static void removeSession(Session session)
			throws NullPointerException {
		String username = getUsername(session);
		UserSession.remove(username);
		Jedis.hdel(Sessions_key, session.toString());
	}

	public static String getUsername(Session session)
			throws NullPointerException {
		String key = session.toString();
		return Jedis.hexists(Sessions_key, key) ? Jedis.hget(Sessions_key, key)
				: USERNOTFOUND;
	}

	public static void loginSucceed(String msgID, String username,
			JSONObject msg) {
		Jedis.lrem(USERNOTFOUND, 0, msgID);
		putMsg(msgID, username, msg);
	}

	public static void loginFailed(String msgID) {
		Jedis.lrem(USERNOTFOUND, 0, msgID);
	}

	public static void putMsg(String msgID, String username, JSONObject msg) {
		boolean result = false;
		String key = username + Msg_suffix;
		if (Jedis.set(msgID, msg.toString()).equals("OK")) {
			try {
				Jedis.lpush(key, msgID);
				result = true;
			} catch (Exception e) {
				Log.exception(e);
				removeMsg(msgID, username);
			}
		}
		if (!result) {
			// TODO
		}
	}

	public static void removeMsg(String msgID, String username) {
		String key = username + Msg_suffix;
		Jedis.lrem(key, 0, msgID);
		Jedis.del(msgID);
		Log.info(msgID+" was removed from Redis");
	}

	public static void removeMsgs(String[] msgIDs, String username) {
		String key = username + Msg_suffix;
		for (int i = 0; i < msgIDs.length; i++) {
			Jedis.lrem(key, 0, msgIDs[i]);
			Jedis.del(msgIDs[i]);
		}
	}

	public static JSONObject getMsg(String msgID) {
		try {
			return JSONObject.fromObject(Jedis.get(msgID));
		} catch (Exception e) {
			Log.exception(e);
			return null;
		}
	}

	public static String combineStr(List<String> items) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < items.size() - 1; i++) {
			sb.append(items.get(i)).append(",");
		}
		sb.append(items.get(items.size() - 1));
		return sb.toString();
	}

	public static JSONObject getSyncReturnMsg(String username, String msgID) {
		String key = username + Msg_suffix;
		long remain;
		List<String> msgIDs;
		if (Jedis.llen(key) < 5) {
			msgIDs = Jedis.lrange(key, 0, -1);
			remain = 0;
		} else {
			msgIDs = Jedis.lrange(key, 0, 4);
			remain = Jedis.llen(key) - 5;
		}
		int count = msgIDs.size();
		JSONArray msgs = new JSONArray();
		for (int i = 0; i < count; i++) {
			msgs.add(getMsg(msgIDs.get(i)));
		}
		JSONObject returnMsg = new JSONBuilder(msgID, "sync", "replied", "0")
				.body(msgs).remainToSync(String.valueOf(remain)).getMsg();
		return returnMsg;
	}

	public static void confirmSyncMsg(String username, String msgID) {
		String key = username + Msg_suffix;
		JSONObject msg = JSONObject.fromObject(getMsg(msgID));
		long remain = Long.parseLong(getMsg(msgID).getString("remain"));
		if (remain == 0) {
			// TODO
			Jedis.del(key);
		} else {
			Jedis.ltrim(key, 5, -1);
		}
		removeSyncedMsgs(msg.getJSONArray("body"));
	}

	private static void removeSyncedMsgs(JSONArray msgs) {
		for (int i = 0; i < msgs.size(); i++) {
			Jedis.del(msgs.getJSONObject(i).getString("msgID"));
		}
	}

}
