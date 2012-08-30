package com.test.action;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.love.db.ConnectionPool;

public class AppListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		ConnectionPool.instance();
	}

}
