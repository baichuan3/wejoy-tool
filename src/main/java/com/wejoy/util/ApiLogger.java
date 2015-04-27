package com.wejoy.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ApiLogger {
	
	private static Logger log = Logger.getLogger("api");
	private static Logger infoLog = Logger.getLogger("info");
	private static Logger warnLog = Logger.getLogger("warn");
	private static Logger errorLog = Logger.getLogger("error");
	
	static{
		Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                LogManager.shutdown();              
            }
        });
	}
	
	public static boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}

	public static void trace(Object msg) {
		log.trace(msg);
	}

	public static void debug(Object msg) {
		if (log.isDebugEnabled()) {
			log.debug(msg);
		}
	}
	
	public static void debug(Object msg, Throwable e) {
		if (log.isDebugEnabled()) {
			log.debug(msg, e);
		}
	}
	
	public static void info(Object msg) {
		if (infoLog.isInfoEnabled()) {
			infoLog.info(msg);
		}
	}

    public static void info(Object msg, Throwable e) {
        if (infoLog.isInfoEnabled()) {
            infoLog.info(msg, e);
        }
    }

	public static void warn(Object msg) {
		warnLog.warn(msg);
	}

	public static void warn(Object msg, Throwable e) {
		warnLog.warn(msg, e);
	}

	public static void error(Object msg) {
		errorLog.error(msg);
	}

	public static void error(Object msg, Throwable e) {
		errorLog.error(msg, e);
	}
}
