package com.cnme.backend.redDevil.web;

import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.cnme.backend.redDevil.model.Log;
import com.cnme.backend.redDevil.model.Protocal;

/**
 * Application Lifecycle Listener implementation class Initial
 *
 */
@WebListener
public class Initial implements ServletContextListener {

	/**
	 * Default constructor.
	 */
	public Initial() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		SQLConnectionPoolManager.getPool(
				SQLConnectionPoolManager.DB_Type.SQLServer)
				.releaseAllConnections();
		Log.info("Server shut down!");
	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		ServletContext conf = arg0.getServletContext();
		Protocal.initial(conf);
		Log.initial(conf);
		SQLConnectionPoolManager.initial(conf);
		RedisHelper.intial();
		try {
			RedisHelper.getJedis().set(
					Protocal.Redis_Key.FirstTenDeployments.toString(),
					Actions.getDeploymentsByPage(1, 10).toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.exception(e);
		}
		Log.info("service started!");
	}

}
