package com.cnme.backend.redDevil.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import com.cnme.backend.redDevil.model.Log;
import com.cnme.backend.redDevil.model.Protocal;
import com.cnme.backend.redDevil.web.SQLConnectionPoolManager.DB_Type;

public class SQLConnectionPool {
	private int MinConnectionNo;
	private int MaxConnectionNo;
	private ArrayList<Connection> FreeConnections;
	private ArrayList<Connection> AllConnections;
	private SQLConnectionPoolManager.DB_Type type;

	private String url;
	private String username;
	private String password;

	public SQLConnectionPool(SQLConnectionPoolManager.DB_Type type,int MinConNo, int MaxConNo) {
		this.type=type;	
		this.MinConnectionNo = MinConNo;
		this.MaxConnectionNo = MaxConNo;
		initialConfig();
	}

	public void initialConfig() {
		switch (type) {
		case SQLServer: {
			url = Protocal.getConfiguration().getInitParameter("sqlServerPath");
			username = Protocal.getConfiguration().getInitParameter("sqlServerUsername");
			password = Protocal.getConfiguration().getInitParameter("sqlServerPassword");
			Log.info("initial SQLServer configuration done!");
			break;
		}
		default:
			break;
		}
	}

	public void inital() {
		FreeConnections = new ArrayList<Connection>();
		AllConnections = new ArrayList<Connection>();
		for (int i = 0; i < this.MinConnectionNo; i++) {
			try {
				Connection con = newConnection(type);
				FreeConnections.add(con);
				AllConnections.add(con);
			} catch (ClassNotFoundException | SQLException e) {
				Log.info("Inital Connection pool failed");
				Log.exception(e);
				break;
			}
		}
		Log.info("Inital Connection pool Done!");
	}

	public synchronized Connection getConnction() {
		Connection con = null;
		boolean result = true;
		if (FreeConnections.size() > 0) {
			con = FreeConnections.get(0);
			FreeConnections.remove(0);
		} else if (AllConnections.size() < MaxConnectionNo) {
			try {
				con = newConnection(type);
				AllConnections.add(con);
			} catch (ClassNotFoundException | SQLException e) {
				Log.info("get Connection from Connection pool failed");
				Log.exception(e);
				result = false;
			}
		} else {
			result = false;
		}
		if (result) {
			Log.info("One connection was applied for from Connection pool! Remain "
					+ FreeConnections.size() + " connections");
		}
		return con;
	}

	public synchronized void releaseConnection(Connection con) {
		FreeConnections.add(con);
		Log.info("One connection was released to Connection pool! Remain "
				+ FreeConnections.size() + " connections");
	}


	public void releaseAllConnections() {
		for (int i = 0; i < AllConnections.size(); i++) {
			try {
				AllConnections.get(i).close();
			} catch (SQLException e) {
				Log.info("Close one SQL server connection failed");
				Log.exception(e);
			}
		}
	}

	public Connection newConnection(DB_Type type)
			throws ClassNotFoundException, SQLException {
		switch (type) {
		case SQLServer:
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			return DriverManager.getConnection(url, username, password);
		default:
			return null;
		}
	}
}
