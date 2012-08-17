package org.love.db;

import java.sql.Connection;
import java.util.List;

public interface Session {
	public TransactionManager beginTransaction();

	public Connection getConnection();

	public boolean isConnected();

	public void close();

	public int update(String sql, Object... params);
	

	public <T> List<T> query(String sql, Class cls, Object... params);
	
	public Long queryCount(String sql,Object... params);
}
