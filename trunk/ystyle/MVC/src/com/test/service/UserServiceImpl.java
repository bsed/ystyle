package com.test.service;

import java.util.List;

import org.love.Annotation.Autowired;
import org.love.Annotation.Proxy;
import org.love.Annotation.Service;
import org.love.Annotation.SingleTon;
import org.love.Annotation.Transactional;

import com.test.dao.UserDAO;
import com.test.dao.UserDAOImpl;
import com.test.test.DaoLogProxy;
import com.test.vo.Userinfo;

@SingleTon
@Service
@Proxy(proxyFactoryClass=DaoLogProxy.class,params="includeMethods:save,update")
public class UserServiceImpl implements UserService {

	@Autowired(iocClass = UserDAOImpl.class)
	private UserDAO userDAO;

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Transactional
	public void save(Userinfo user) {
		userDAO.save(user);
	}

	@Transactional
	public void update(Userinfo user) {
		userDAO.update(user);
	}

	public Userinfo getUserById(int id) {
		return userDAO.getUserById(id);
	}

	public List findAll() {
		
		return null;
	}

}
