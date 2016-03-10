package com.cnme.backend.redDevil.web;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cnme.backend.redDevil.model.DBSchema;

import net.sf.json.JSONObject;

public class JSONBodyBuilder {

	public static JSONObject buildBody(ResultSet rs, DBSchema schema) throws SQLException {
		JSONObject msg = new JSONObject();
		for (int i = 0; i < schema.QuerySets.length; i++) {
			msg.put(schema.QuerySets[i], rs.getString(i+1).trim());
		}
		return msg;
	}
}
