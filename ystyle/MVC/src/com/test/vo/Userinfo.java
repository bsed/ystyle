package com.test.vo;

import org.love.Annotation.UploadFile;
import org.love.po.FilePo;

//@Entity(name="userinfo")
public class Userinfo implements java.io.Serializable {


    // @Id
    private Integer toid;
    private String username;
    private Integer age;
    private Integer[] ageTest;
    private String password;
    private String web;
    private String userid;
    
    @UploadFile(path="uploadfiles/${param.folderPath}/")
    private FilePo[] myimg;
    
    @UploadFile(path="uploadfiles/${param.folderPath}/")
    private FilePo myimg0;
    
    private String[] hobby;
    private String hobbyTest;

	public String getHobbyTest() {
		return hobbyTest;
	}

	public void setHobbyTest(String hobbyTest) {
		this.hobbyTest = hobbyTest;
	}

	public Integer getToid() {
        return toid;
    }

    public void setToid(Integer in) {
        this.toid = in;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String in) {
        this.username = in;
    }



	public String getPassword() {
        return password;
    }

    public void setPassword(String in) {
        this.password = in;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String in) {
        this.web = in;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String in) {
        this.userid = in;
    }


	public String[] getHobby() {
		return hobby;
	}

	public void setHobby(String[] hobby) {
		this.hobby = hobby;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Integer[] getAgeTest() {
		return ageTest;
	}

	public void setAgeTest(Integer[] ageTest) {
		this.ageTest = ageTest;
	}

	public FilePo[] getMyimg() {
		return myimg;
	}

	public void setMyimg(FilePo[] myimg) {
		this.myimg = myimg;
	}

	public FilePo getMyimg0() {
		return myimg0;
	}

	public void setMyimg0(FilePo myimg0) {
		this.myimg0 = myimg0;
	}


	

}