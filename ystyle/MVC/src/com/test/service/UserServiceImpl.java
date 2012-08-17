package com.test.service;

import org.love.Annotation.Autowired;
import org.love.Annotation.Service;
import org.love.Annotation.SingleTon;
import org.love.Annotation.Transactional;

import com.test.dao.UserDAO;
import com.test.dao.UserDAOImpl;
import com.test.vo.Userinfo;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

@SingleTon
@Service
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
