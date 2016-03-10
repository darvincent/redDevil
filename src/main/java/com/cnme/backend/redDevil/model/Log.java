package com.cnme.backend.redDevil.model;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Log {
	private static Logger logger;
	private static boolean isDebug;

	public static void initial(ServletContext conf) {
		logger = Logger.getLogger("logger");
		isDebug = conf.getInitParameter("isDebug").toUpperCase().equals("Y") ? true
				: false;
		PropertyConfigurator.configure(conf
				.getInitParameter("log4jPropertiesPath"));
		logger.getAppender("info");
	}

	public static void info(String content) {
		logger.info(content);
	}

	public static void error(String error, Throwable t) {
		logger.warn(error, t);
	}

	public static void exception(Exception e) {
		logger.error(e);
	}

	public static void debug(String msg) {
		if (isDebug) {
			logger.info(msg);
		}
	}
}
