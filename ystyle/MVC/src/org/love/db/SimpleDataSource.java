package org.love.db;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

import org.love.ProxyFactory.ConnProxyFactory;
import org.love.dbutil.DbUtils;

public class SimpleDataSource implements DataSource {

	// 当前正在使用的连接数
	private transient int currentCount = 0;

	/**
	 * 当前可用连接数 初始化为@see
	 */
	private transient int aviliableCount = 0;

	// 同时连接最大并发数量
	private int maxactive = 8;

	// 初始化连接数
	private int initsize = 10;

	// 最大可用连接数
	private int maxsize = 30;

	// 连接不成功次数
	private transient AtomicInteger errcount = new AtomicInteger(0);
	private String driverClassName = "com.mysql.jdbc.Driver";
	private String username = "root";
	private String password = "root";
	private String url = "jdbc:mysql://localhost:3306/test?useUnicode=true&amp;characterEncoding=utf8";

	// 连接代理工厂
	private ConnProxyFactory cpf = new ConnProxyFactory(this);

	protected final ReentrantLock lock = new ReentrantLock(true);

	// 连接池
	private List<Connection> connPool = new ArrayList<Connection>();

	public SimpleDataSource() {
		init();
	}

	public void init() {
		DbUtils.loadDriver(driverClassName);
		for (int i = 0; i < initsize; i++) {
			connPool.add(createConnection());
		}
	}

	// 释放连接
	public void closeConnection(Connection conn) throws SQLException {
		// 假如释放的是一个关闭的连接，则在连接池中删除这个连接
		if (conn.isClosed()) {
			lock.lock();
			try {
				connPool.remove(conn);
			} finally {
				lock.unlock();
			}

		} else {
			System.out.println("释放到连接池");
			putPool(conn);
		}
	}

	/**
	 * 此方法必须保证还有可用连接
	 * 
	 * @return
	 */
	private Connection searchConntion() {
		lock.lock();
		try {
			Connection conn = connPool.get(0);
			connPool.remove(conn);
			return conn;
		} finally {
			lock.unlock();
		}

	}

	/**
	 * 放入连接池
	 */
	private void putPool(Connection conn) {
		lock.lock();
		try {
			connPool.add(conn);
		} finally {
			lock.unlock();
		}

	}

	/**
	 * 创建新的连接
	 * 
	 * @return
	 * @throws SQLException
	 */
	private Connection createConnection() {
		Connection conn = null;
		try {
			lock.lock();
			try{
				conn = (Connection) cpf.factory(DriverManager.getConnection(url,
						username, password), null);	
			}finally{
				lock.unlock();
			}
			
		} catch (SQLException e) {
			errcount.incrementAndGet();
            System.out.println("连接错误次数: "+errcount); 
            if(errcount.get()>=5){
				throw new RuntimeException(" cannot connect the database!");
			}
			try {
				Thread.sleep(5000);
				conn = createConnection();
				if(conn!=null){
					errcount=new AtomicInteger(0);
					return conn;
				}
				
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

		}
		return conn;

	}

	public Connection getConnection() throws SQLException {
		int poolSize = 0;
		lock.lock();
		try {
			poolSize = connPool.size();
		} finally {
			lock.unlock();
		}

		System.out.println("连接池还有" + poolSize + "个连接");

		if (poolSize < maxsize || poolSize == 0) {
			Connection conn = createConnection();
			putPool(conn);
		}
		return searchConntion();

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
