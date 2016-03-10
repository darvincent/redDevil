package com.cnme.backend.redDevil.web;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import com.cnme.backend.redDevil.model.DBSchema;
import com.cnme.backend.redDevil.model.Protocal;

public class SQL_CmdBuilder {

	public static ResultSet query(Statement stmt, String cmd)
			throws SQLException, SQLTimeoutException {
		stmt.execute(cmd);
		return stmt.getResultSet();
	}

	public static ResultSet query(Statement stmt, DBSchema schema)
			throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		int set_Length = schema.QuerySets.length - 1;
		for (int i = 0; i < set_Length; i++) {
			sb.append(Protocal.KV_Data.get(schema.QuerySets[i])).append(",");
		}
		sb.append(Protocal.KV_Data.get(schema.QuerySets[set_Length]));
		sb.append(" from ").append(schema.Name);

		if (schema.FilterCondition != null) {
			sb.append(" ").append(schema.FilterCondition);
		}
		stmt.execute(sb.toString());
		return stmt.getResultSet();
	}

	public static ResultSet query(Statement stmt, JSONObject msg,
			DBSchema schema) throws SQLException, NullPointerException {
		StringBuilder sb = new StringBuilder();
		if (schema.IsStoredProcedures) {
			sb.append("{call ");
			sb.append(schema.Name).append("(");
			int length = schema.ColumnKeys == null ? -1
					: schema.ColumnKeys.length - 1;
			if (length >= 0) {
				for (int i = 0; i < length; i++) {
					sb.append("'")
							.append(getValueFromJSONObject(msg,
									schema.ColumnKeys[i])).append("',");
				}
				sb.append("'").append(
						getValueFromJSONObject(msg, schema.ColumnKeys[length]));
			}
			sb.append("')}");
		} else {
			sb.append("select ");
			int set_Length = schema.QuerySets.length - 1;
			for (int i = 0; i < set_Length; i++) {
				sb.append(Protocal.KV_Data.get(schema.QuerySets[i]))
						.append(",");
			}
			sb.append(Protocal.KV_Data.get(schema.QuerySets[set_Length]));

			sb.append(" from ").append(schema.Name);
			if (schema.FilterCondition != null) {
				sb.append(" ").append(schema.FilterCondition);
			} else {
				int PK_length = schema.PrimaryKeys == null ? -1
						: schema.PrimaryKeys.length - 1;
				if (PK_length >= 0) {
					sb.append(" where ");
					for (int i = 0; i < PK_length; i++) {
						sb.append(Protocal.KV_Data.get(schema.PrimaryKeys[i]))
								.append("='")
								.append(getValueFromJSONObject(msg,
										schema.PrimaryKeys[i]))
								.append("' and ");
					}
					sb.append(
							Protocal.KV_Data.get(schema.PrimaryKeys[PK_length]))
							.append("='")
							.append(getValueFromJSONObject(msg,
									schema.PrimaryKeys[PK_length])).append("'");
				}
			}
		}
		stmt.execute(sb.toString());
		return stmt.getResultSet();
	}

	public static ResultSet query(Statement stmt, HttpServletRequest request,
			DBSchema schema) throws SQLException, NullPointerException {
		StringBuilder sb = new StringBuilder();
		if (schema.IsStoredProcedures) {
			sb.append("{call ");
			sb.append(schema.Name).append("(");
			int length = schema.ColumnKeys == null ? -1
					: schema.ColumnKeys.length - 1;
			if (length >= 0) {
				for (int i = 0; i < length; i++) {
					sb.append("'")
							.append(getValueFromHttpServletRequest(request,
									schema.ColumnKeys[i])).append("',");
				}
				sb.append("'").append(
						getValueFromHttpServletRequest(request,
								schema.ColumnKeys[length]));
			}
			sb.append("')}");
		} else {
			sb.append("select ");
			if (schema.QuerySets != null && schema.QuerySets.length > 0) {
				int set_Length = schema.QuerySets.length - 1;
				for (int i = 0; i < set_Length; i++) {
					sb.append(Protocal.KV_Data.get(schema.QuerySets[i]))
							.append(",");
				}
				sb.append(Protocal.KV_Data.get(schema.QuerySets[set_Length]));
			} else {
				sb.append("* ");
			}
			sb.append(" from ").append(schema.Name);
			if (schema.FilterCondition != null) {
				sb.append(" ").append(schema.FilterCondition);
			} else {
				int PK_length = schema.PrimaryKeys == null ? -1
						: schema.PrimaryKeys.length - 1;
				if (PK_length >= 0) {
					sb.append(" where ");
					for (int i = 0; i < PK_length; i++) {
						sb.append(Protocal.KV_Data.get(schema.PrimaryKeys[i]))
								.append("='")
								.append(getValueFromHttpServletRequest(request,
										schema.PrimaryKeys[i]))
								.append("' and ");
					}
					sb.append(
							Protocal.KV_Data.get(schema.PrimaryKeys[PK_length]))
							.append("='")
							.append(getValueFromHttpServletRequest(request,
									schema.PrimaryKeys[PK_length])).append("'");
				}
			}
		}
		stmt.execute(sb.toString());
		return stmt.getResultSet();
	}

	public static void addRecordBySQL(Statement stmt, JSONObject msg,
			DBSchema schema) throws SQLException, NullPointerException {
		if (schema.ColumnKeys != null) {
			int length = schema.ColumnKeys.length - 1;
			StringBuilder sb = new StringBuilder("insert into ");
			sb.append(schema.Name).append(" Values ('");
			for (int i = 0; i < length; i++) {
				sb.append(getValueFromJSONObject(msg, schema.ColumnKeys[i]))
						.append("','");
			}
			sb.append(getValueFromJSONObject(msg, schema.ColumnKeys[length]))
					.append("')");
			stmt.executeUpdate(sb.toString());
		}
	}

	public static void addRecordBySQL(Statement stmt,
			HttpServletRequest request, DBSchema schema) throws SQLException,
			NullPointerException {
		if (schema.ColumnKeys != null) {
			int length = schema.ColumnKeys.length - 1;
			StringBuilder sb = new StringBuilder("insert into ");
			sb.append(schema.Name).append(" Values ('");
			for (int i = 0; i < length; i++) {
				sb.append(
						getValueFromHttpServletRequest(request,
								schema.ColumnKeys[i])).append("','");
			}
			sb.append(
					getValueFromHttpServletRequest(request,
							schema.ColumnKeys[length])).append("')");
			stmt.executeUpdate(sb.toString());
		}
	}

	public static void editRecordBySQLBySQL(Statement stmt, JSONObject msg,
			DBSchema schema) throws SQLException, NullPointerException {
		if (schema.ColumnKeys != null) {
			int set_Len = schema.ColumnKeys.length - 1;
			StringBuilder sb = new StringBuilder("update ");
			sb.append(schema.Name).append(" set ");
			for (int i = 0; i < set_Len; i++) {
				if (msg.containsKey(schema.ColumnKeys[i])) {
					sb.append(Protocal.KV_Data.get(schema.ColumnKeys[i]))
							.append("='")
							.append(getValueFromJSONObject(msg,
									schema.ColumnKeys[i])).append("',");
				}
			}
			if (msg.containsKey(schema.ColumnKeys[set_Len])) {
				sb.append(Protocal.KV_Data.get(schema.ColumnKeys[set_Len]))
						.append("='")
						.append(getValueFromJSONObject(msg,
								schema.ColumnKeys[set_Len]));
			}
			if (schema.PrimaryKeys != null) {
				sb.append("' where ");
				int pk_Len = schema.PrimaryKeys.length - 1;
				for (int i = 0; i < pk_Len; i++) {
					sb.append(Protocal.KV_Data.get(schema.PrimaryKeys[i]))
							.append("='")
							.append(getValueFromJSONObject(msg,
									schema.PrimaryKeys[i])).append("' and ");
				}
				sb.append(Protocal.KV_Data.get(schema.PrimaryKeys[pk_Len]))
						.append("='")
						.append(getValueFromJSONObject(msg,
								schema.PrimaryKeys[pk_Len])).append("'");
			}
			stmt.executeUpdate(sb.toString());
		}
	}

	public static void editRecordBySQLBySQL(Statement stmt,
			HttpServletRequest request, DBSchema schema) throws SQLException,
			NullPointerException {
		if (schema.ColumnKeys != null) {
			int set_Len = schema.ColumnKeys.length - 1;
			StringBuilder sb = new StringBuilder("update ");
			sb.append(schema.Name).append(" set ");
			for (int i = 0; i < set_Len; i++) {
				if (!getValueFromHttpServletRequest(request,
						schema.ColumnKeys[i]).equals("")) {
					sb.append(Protocal.KV_Data.get(schema.ColumnKeys[i]))
							.append("='")
							.append(getValueFromHttpServletRequest(request,
									schema.ColumnKeys[i])).append("',");
				}
			}
			if (!getValueFromHttpServletRequest(request,
					schema.ColumnKeys[set_Len]).equals("")) {
				sb.append(Protocal.KV_Data.get(schema.ColumnKeys[set_Len]))
						.append("='")
						.append(getValueFromHttpServletRequest(request,
								schema.ColumnKeys[set_Len]));
			}
			if (schema.PrimaryKeys != null) {
				sb.append("' where ");
				int pk_Len = schema.PrimaryKeys.length - 1;
				for (int i = 0; i < pk_Len; i++) {
					sb.append(Protocal.KV_Data.get(schema.PrimaryKeys[i]))
							.append("='")
							.append(getValueFromHttpServletRequest(request,
									schema.PrimaryKeys[i])).append("' and ");
				}
				sb.append(Protocal.KV_Data.get(schema.PrimaryKeys[pk_Len]))
						.append("='")
						.append(getValueFromHttpServletRequest(request,
								schema.PrimaryKeys[pk_Len])).append("'");
			}
			stmt.executeUpdate(sb.toString());
		}
	}

	public static void deleteRecord(Statement stmt, JSONObject msg,
			DBSchema schema) throws SQLException, NullPointerException {
		if (schema.PrimaryKeys == null) {
			return;
		}
		StringBuilder sb = new StringBuilder("delete from ");
		int length = schema.PrimaryKeys.length - 1;
		sb.append(schema.Name).append(" where ");
		for (int i = 0; i < length; i++) {
			sb.append(Protocal.KV_Data.get(schema.PrimaryKeys[i])).append("='")
					.append(getValueFromJSONObject(msg, schema.PrimaryKeys[i]))
					.append("' and ");
		}
		sb.append(Protocal.KV_Data.get(schema.PrimaryKeys[length]))
				.append("='")
				.append(getValueFromJSONObject(msg, schema.PrimaryKeys[length]))
				.append("'");
		stmt.executeUpdate(sb.toString());
	}

	public static void deleteRecord(Statement stmt, HttpServletRequest request,
			DBSchema schema) throws SQLException, NullPointerException {
		if (schema.PrimaryKeys == null) {
			return;
		}
		StringBuilder sb = new StringBuilder("delete from ");
		int length = schema.PrimaryKeys.length - 1;
		sb.append(schema.Name).append(" where ");
		for (int i = 0; i < length; i++) {
			sb.append(Protocal.KV_Data.get(schema.PrimaryKeys[i]))
					.append("='")
					.append(getValueFromHttpServletRequest(request,
							schema.PrimaryKeys[i])).append("' and ");
		}
		sb.append(Protocal.KV_Data.get(schema.PrimaryKeys[length]))
				.append("='")
				.append(getValueFromHttpServletRequest(request,
						schema.PrimaryKeys[length])).append("'");
		stmt.executeUpdate(sb.toString());
	}

	private static String modifyToInsertMySQL(String cmd)
			throws NullPointerException {
		String result = cmd.replaceAll("\'", "''");
		return result;
	}

	public static String getValueFromHttpServletRequest(
			HttpServletRequest request, String key) throws NullPointerException {
		Object value = request.getAttribute(key);
		if (value == null) {
			value = request.getParameter(key);
		}
		return modifyToInsertMySQL(value.toString());
	}

	public static String getValueFromJSONObject(JSONObject msg, String key)
			throws NullPointerException {
		return modifyToInsertMySQL(msg.getString(key));
	}
}
