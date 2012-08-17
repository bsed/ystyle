package com.test.vo;

import java.io.Serializable;
import java.util.Date;

import org.love.Annotation.Column;
import org.love.Annotation.Entity;
import org.love.Annotation.Id;

@Entity(table="userinfo")
public class Testafei implements Serializable {
	
	@Id
	@Column(name="toid")
	private int uid;
	
	@Column(name="username")
	private String username;
	
	@Column(name="age")
	private int age;
	
	@Column(name="password")
	private String userpwd;
	
	private Date birthday;
	
	private String[] hobby;
	
	public String[] getHobby() {
		return hobby;
	}
	public void setHobby(String[] hobby) {
		this.hobby = hobby;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUserpwd() {
		return userpwd;
	}
	public void setUserpwd(String userpwd) {
		this.userpwd = userpwd;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
}
