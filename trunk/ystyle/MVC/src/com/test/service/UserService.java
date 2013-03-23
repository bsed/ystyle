package com.test.service;

import java.util.List;

import com.test.vo.Userinfo;


public interface UserService {

	public void save(Userinfo user);
	
	public void update(Userinfo user);
	
	public Userinfo getUserById(int id);
	
	public List findAll();
}
