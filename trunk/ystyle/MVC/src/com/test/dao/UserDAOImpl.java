package com.test.dao;

import java.util.List;

import org.love.Annotation.Proxy;
import org.love.db.Session;
import org.love.db.SessionFactory;

import com.test.test.TestProxy;
import com.test.vo.Userinfo;

@Proxy(proxyFactoryClass=TestProxy.class)
public class UserDAOImpl implements UserDAO {

	public void save(Userinfo user) {
		Session session=SessionFactory.getSession();
		session.update("insert into userinfo(username,age,password) values(?,?,?)",user.getUsername(),user.getAge(),user.getPassword());
		//SessionFactory.closeSession(session);
	}

	public void update(Userinfo user) {
		System.out.println("我正在修改user");
	}

	public Userinfo getUserById(int id) {
		Session session=SessionFactory.getSession();
		List<Userinfo> list=session.query("select * from userinfo u where u.toid = ?",Userinfo.class,id);
		Userinfo user=list.get(0);
		SessionFactory.closeSession(session);
		return user;
		
	}

	public List findAll() {
		
		return null;
	}

}
