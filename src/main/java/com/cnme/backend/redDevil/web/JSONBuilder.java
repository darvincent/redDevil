package com.cnme.backend.redDevil.web;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JSONBuilder {
	private final String MsgID;
	private final String Action;
	private final String Status;
	private final String ErrorCode;
	private JSONObject BodyMsg;
	private JSONArray BodyMsgs;
	private String RemainToSync;
	private String Remark;
	private JSONObject Msg;
	private String SyncedMsgIDs;

	public JSONBuilder(String msgID, String action, String status,
			String errorCode) {
		this.MsgID = msgID;
		this.Action = action;
		this.Status = status;
		this.ErrorCode = errorCode;
	}

	public JSONBuilder body(JSONObject bodyMsg) {
		this.BodyMsg = bodyMsg;
		return this;
	}

	public JSONBuilder body(JSONArray bodyMsgs) {
		this.BodyMsgs = bodyMsgs;
		return this;
	}

	public JSONBuilder remainToSync(String remainToSync) {
		this.RemainToSync = remainToSync;
		return this;
	}

	public JSONBuilder syncedMsgIDs(String syncedMsgIDs) {
		this.SyncedMsgIDs = syncedMsgIDs;
		return this;
	}

	public JSONBuilder remark(String remark) {
		this.Remark = remark;
		return this;
	}

	public JSONObject getMsg() {
		Msg = new JSONObject();
		Msg.put("msgID", MsgID);
		Msg.put("action", Action);
		Msg.put("status", Status);
		Msg.put("errorCode", ErrorCode);
		if (BodyMsg != null) {
			Msg.put("bodyLength", BodyMsg.toString().length());
			Msg.put("body", BodyMsg);
		}
		if (BodyMsgs != null) {
			Msg.put("bodyLength", BodyMsgs.toString().length());
			Msg.put("body", BodyMsgs);
		}
		if (RemainToSync != null) {
			Msg.put("remainToSync", RemainToSync);
		}
		if (Remark != null) {
			Msg.put("remark", Remark);
		}
		if (SyncedMsgIDs != null) {
			Msg.put("syncedMsgIDs", SyncedMsgIDs);
		}
		return Msg;
	}
}
