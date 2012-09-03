package org.love.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.love.dbutil.QueryRunner;
import org.love.dbutil.handlers.BeanListHandler;
import org.love.dbutil.handlers.ScalarHandler;

public class DefaultSession implements Session {

	private Connection connection;
	private TransactionManager transaction;

	public DefaultSession() {
		connection = ConnectionPool.instance().getConnection();
		transaction = new TransactionManager(this);
	}

	public TransactionManager beginTransaction() {
		transaction.begin();
		return transaction;
	}

	public Connection getConnection() {
		return connection;
	}

	public boolean isConnected() {
		boolean isconnec = false;
		try {
			isconnec = connection.isClosed();
		} catch (SQLException e) {
		}
		return isconnec;
	}

	public void close() {
		try {
			connection.close();
			connection = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	public int update(String sql, Object... params) {
		QueryRunner qr = new QueryRunner();
		int count=0;
		try {
			count = qr.update(connection, sql, params);
		} catch (SQLException e) {
			e.printStackTrace();
			count=0;
		}
		return count;
	}

	
	public <T> List<T> query(String sql, Class cls, Object... params) {
		QueryRunner qr = new QueryRunner();
		try {
			List<T> list = qr.query(connection,sql,
					new BeanListHandler<T>(cls),params);
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Long queryCount(String sql, Object... params) {
		QueryRunner qr = new QueryRunner();
		try {
			Object obj=qr.query(connection,sql,new ScalarHandler(),params);
			return (Long)obj;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	

	




	

}
