package com.test.dao;

import java.util.List;

import com.test.vo.Userinfo;

public interface UserDAO {

	public void save(Userinfo user);
	public void update(Userinfo user);
	public Userinfo getUserById(int id);
	public List findAll();
}
