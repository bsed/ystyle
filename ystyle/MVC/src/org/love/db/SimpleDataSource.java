package org.love.db;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.love.ProxyFactory.ConnProxyFactory;
import org.love.dbutil.DbUtils;

public class SimpleDataSource implements DataSource {
 
	private  String driverClassName = "com.mysql.jdbc.Driver";
	private  String username = "root";
	private  String password = "root";
	private  String url = "jdbc:mysql://localhost:3306/test?useUnicode=true&amp;characterEncoding=utf8";

	private  int minsize = 10;


	public int getMinsize() {
		return minsize;
	}

	public void setMinsize(int minsize) {
		this.minsize = minsize;
	}

	// 连接池
	private List<Connection> connPool = new ArrayList<Connection>();
	
	// 释放连接
	public  void closeConnection(Connection conn)
			throws SQLException {
		// 假如释放的是一个关闭的连接，则在连接池中删除这个连接
		if (conn.isClosed()) {
			connPool.remove(conn);
		} else {
			putPool(conn);
		}
	}

	/**
	 * 此方法必须保证还有可用连接
	 * 
	 * @return
	 */
	private  Connection searchConntion() {
		Connection conn = connPool.get(0);
		connPool.remove(conn);
		return conn;
	}

	/**
	 * 放入连接池
	 */
	private  void putPool(Connection conn) {
			connPool.add(conn);
	}

	/**
	 * 创建新的连接
	 * 
	 * @return
	 * @throws SQLException
	 */
	private Connection createConnection() throws SQLException {
		DbUtils.loadDriver(driverClassName);
		//Connection conn0 = DriverManager.getConnection(url, username, password);
		Connection conn=(Connection)new ConnProxyFactory(this).factory(DriverManager.getConnection(url, username, password),null);		
		return conn;
		
	}
	
	
	
	
	public Connection getConnection() throws SQLException {
		int poolSize = connPool.size();
		System.out.println("连接池还有" + connPool.size() + "个连接");
		if (poolSize >= minsize) {
			return searchConntion();
		} else {
			Connection conn = null;
			try {
				conn = createConnection();
			} catch (SQLException e) {
				throw new RuntimeException("不能得到数据库的连接" + e.getMessage());
			}
			putPool(conn);
			return conn;
		}

	}

	public Connection getConnection(String username, String password)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public PrintWriter getLogWriter() throws SQLException {
		throw new UnsupportedOperationException("getLogWriter");
	}

	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	public void setLogWriter(PrintWriter out) throws SQLException {
		throw new UnsupportedOperationException("setLogWriter");
	}

	public void setLoginTimeout(int seconds) throws SQLException {
		throw new UnsupportedOperationException("setLoginTimeout");
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return DataSource.class.equals(iface);
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return (T) this;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}


}
