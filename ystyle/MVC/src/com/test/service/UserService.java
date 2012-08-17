package com.test.service;

import com.test.vo.Userinfo;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;


public interface UserService {

	public void save(Userinfo user);
	
	public void update(Userinfo user);
	
	public Userinfo getUserById(int id);
	
	public List findAll();
}
