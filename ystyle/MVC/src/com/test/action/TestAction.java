package com.test.action;

import java.lang.reflect.Array;
import java.util.logging.Logger;

import org.apache.commons.fileupload.FileItem;
import org.love.Annotation.Autowired;
import org.love.action.BaseAction;
import org.love.utils.Utils;

import com.test.service.NewsService;
import com.test.service.NewsServiceImpl;
import com.test.service.UserService;
import com.test.service.UserServiceImpl;
import com.test.vo.News;
import com.test.vo.Userinfo;

public class TestAction extends BaseAction {
	private static Logger log = Logger.getLogger("");

	private Userinfo testAfei;

	@Autowired(iocClass = UserServiceImpl.class)
	private UserService userService;

	@Autowired(iocClass = NewsServiceImpl.class)
	private NewsService newsService;

	public void setNewsService(NewsService newsService) {
		this.newsService = newsService;
	}

	public String test1() {

		/*
		 * String username=request.getParameter("username"); String
		 * userpwd=request.getParameter("userpwd"); Testafei user=new
		 * Testafei(); user.setUsername(username); user.setUserpwd(userpwd);
		 * userService.save(user);
		 */
		FileItem[] fis = (FileItem[]) request.getParameterMap().get("testAfei.myimg0");
		System.out.println(fis.length);
		Utils.printFields(testAfei);
		userService.save(testAfei);
		return "success";
	}

	public String test2() {
		String uid = request.getParameter("uid");
		Userinfo ta = userService.getUserById(Integer.parseInt(uid));
		request.setAttribute("paramSuccess", ta);
		return "success";
	}

	public String test3() {
		String uid = request.getParameter("uid");
		String title = request.getParameter("title");
		News news = new News();
		news.setUid(Integer.parseInt(uid));
		news.setTitle(title);
		newsService.save(news);
		return "success";
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public Userinfo getTestAfei() {
		return testAfei;
	}

	public void setTestAfei(Userinfo testAfei) {
		this.testAfei = testAfei;
	}

	public static void main(String[] args) throws Exception {
		String[] param={"1","2","3","4","5"};
		Object[] xx=(Object[])Array.newInstance(param[0].getClass(),param.length+1);
		System.out.println(xx.getClass()+" : "+xx.length);
	}
	
}
