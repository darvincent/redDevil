package com.cnme.backend.redDevil.web;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import com.cnme.backend.redDevil.model.DBSchema;
import com.cnme.backend.redDevil.model.Log;

public class SQLTransaction {
	private Connection connection;
	private ArrayList<Statement> statements;
	private ArrayList<ResultSet> resultSets;
	private long startTime = 0;

	public SQLTransaction() {
	}

	public boolean inital() {
		connection = SQLConnectionPoolManager.getPool(
				SQLConnectionPoolManager.DB_Type.SQLServer).getConnction();
		if (connection != null) {
			statements = new ArrayList<Statement>();
			resultSets = new ArrayList<ResultSet>();
			startTime = System.currentTimeMillis();
			return true;
		} else {
			return false;
		}
	}

	private Statement createStatement(int type, int concurrency)
			throws SQLException {
		try {
			Statement stmt = connection.createStatement(type, concurrency);
			statements.add(stmt);
			return stmt;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Statement createDefaultStatement() throws SQLException {
		Statement stmt = connection.createStatement();
		statements.add(stmt);
		return stmt;
	}

	public ResultSet query(int statement_type, int statement_concurrency,
			String cmd) throws SQLTimeoutException, SQLException {
		ResultSet set = SQL_CmdBuilder.query(
				createStatement(statement_type, statement_concurrency), cmd);
		resultSets.add(set);
		return set;

	}

	public ResultSet query(int statement_type, int statement_concurrency,
			DBSchema schema) throws SQLTimeoutException, SQLException {
		ResultSet set = SQL_CmdBuilder.query(
				createStatement(statement_type, statement_concurrency), schema);
		resultSets.add(set);
		return set;
	}

	public ResultSet query(int statement_type, int statement_concurrency,
			JSONObject msg, DBSchema schema) throws SQLException,
			NullPointerException {
		ResultSet set = SQL_CmdBuilder.query(
				createStatement(statement_type, statement_concurrency), msg,
				schema);
		resultSets.add(set);
		return set;
	}

	public ResultSet query(int statement_type, int statement_concurrency,
			HttpServletRequest request, DBSchema schema) throws SQLException,
			NullPointerException {
		ResultSet set = SQL_CmdBuilder.query(
				createStatement(statement_type, statement_concurrency),
				request, schema);
		resultSets.add(set);
		return set;
	}

	public void addRecordBySQL(JSONObject msg, DBSchema schema)
			throws SQLException, NullPointerException {
		SQL_CmdBuilder.addRecordBySQL(createDefaultStatement(), msg, schema);
	}

	public void addRecordBySQL(HttpServletRequest request, DBSchema schema)
			throws SQLException, NullPointerException {
		SQL_CmdBuilder
				.addRecordBySQL(createDefaultStatement(), request, schema);
	}

	public void editRecordBySQL(JSONObject msg, DBSchema schema)
			throws SQLException, NullPointerException {
		SQL_CmdBuilder.editRecordBySQLBySQL(createDefaultStatement(), msg,
				schema);
	}

	public void editRecordBySQL(HttpServletRequest request, DBSchema schema)
			throws SQLException, NullPointerException {
		SQL_CmdBuilder.editRecordBySQLBySQL(createDefaultStatement(), request,
				schema);
	}

	public void deleteRecordBySQL(JSONObject msg, DBSchema schema)
			throws SQLException, NullPointerException {
		SQL_CmdBuilder.deleteRecord(createDefaultStatement(), msg, schema);
	}

	public void deleteRecordBySQL(HttpServletRequest request, DBSchema schema)
			throws SQLException, NullPointerException {
		SQL_CmdBuilder.deleteRecord(createDefaultStatement(), request, schema);
	}

	public void releaseResource() {
		if (resultSets != null) {
			for (int i = 0; i < resultSets.size(); i++) {
				try {
					resultSets.get(i).close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (statements != null) {
			for (int i = 0; i < statements.size(); i++) {
				try {
					statements.get(i).close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (connection != null) {
			SQLConnectionPoolManager.getPool(
					SQLConnectionPoolManager.DB_Type.SQLServer)
					.releaseConnection(connection);
		}
		if (startTime != 0) {
			Log.info("Transaction cost time: "
					+ (System.currentTimeMillis() - startTime));
		}
	}
}
