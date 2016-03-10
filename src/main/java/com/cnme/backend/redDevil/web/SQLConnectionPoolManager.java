package com.cnme.backend.redDevil.web;

import javax.servlet.ServletContext;

import com.cnme.backend.redDevil.model.Log;

public class SQLConnectionPoolManager {
	public static enum DB_Type {
		MySQL, SQLServer
	};

	public static SQLConnectionPool SQLServerPool;

	public static void initial(ServletContext conf) {
		try {
			SQLServerPool = new SQLConnectionPool(DB_Type.SQLServer,10, 30);
			SQLServerPool.inital();
		} catch (Exception e) {
			Log.exception(e);
		}
	}

	public static SQLConnectionPool getPool(DB_Type type) {
		switch (type) {
		case MySQL:
			break;
		case SQLServer:
			return SQLServerPool;
		default:
			break;
		}
		return null;
	}

}
