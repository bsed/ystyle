package com.test.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.love.Annotation.SingleTon;
import org.love.db.Session;
import org.love.db.SessionFactory;
import org.love.dbutil.handlers.ArrayListHandler;
import org.love.dbutil.handlers.ResultSetHandler;

import com.test.vo.News;

@SingleTon
public class NewsDaoImpl implements NewsDAO {

	public void save(News news) {
		Session session=SessionFactory.getSession();
		Connection conn=session.getConnection();
        PreparedStatement ps=null;
		try {
			ps = conn.prepareStatement("insert into news(uid,title) values(?,?)");
			ps.setInt(1,news.getUid());
			ps.setString(2,news.getTitle());
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("执行prepareStatement出错"+e);
		}finally{
			if(ps!=null){
				try {
					ps.close();
				} catch (SQLException e) {
				    e.printStackTrace();
				}
			}
			SessionFactory.closeSession(session);
		}
	}

	public void update(News news) {
		ResultSetHandler rsh=new ArrayListHandler();
//		QueryRunner qr=new QueryRunner(DbUtils.g);
		
	}

}
