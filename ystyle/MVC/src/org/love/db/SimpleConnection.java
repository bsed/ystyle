package org.love.db;

import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import com.mysql.jdbc.JDBC4Connection;

public class SimpleConnection extends JDBC4Connection {

	public SimpleConnection(String hostToConnectTo, int portToConnectTo,
			Properties info, String databaseToConnectTo, String url)
			throws SQLException {
		super(hostToConnectTo, portToConnectTo, info, databaseToConnectTo, url);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public void close(){
		
	}

}
