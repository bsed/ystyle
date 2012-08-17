package org.love.action;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

/**
 * 提供基础action，用于被业务action继承
 * 本类提供的功能有 1,设置:
 *                 request
 *                 response
 *                 session
 *                 servletContext; 
 *                2,提供java对象与json的转化功能 
 * @author duyunfei
 *
 */
public class BaseAction implements ContextAction {

	public final static String SUCCESS="success";
	public final static String ERROR="error";
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected HttpSession session;
	protected ServletContext servletContext;

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void setServletContext(ServletContext context) {
		this.servletContext = context;
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}

	/**
	 * 处理ajax需要转换对象与json
	 * @param list 对象列表���Դ���list����
	 * @param config 
	 * @throws Exception
	 */
	protected void doAjaxJson(List list, JsonConfig config)
			throws Exception {
		String data = JSONArray.fromObject(list, config).toString();
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		response.getWriter().write(data);
		response.flushBuffer();
	}

	/**
	 * 处理ajax需要转换对象与json
	 * @param instance 对象���Դ���list����
	 * @param config 
	 * @throws Exception
	 */
	protected void doAjaxJson(Object instance, JsonConfig config)
			throws Exception {
		String data = JSONArray.fromObject(instance, config).toString();
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		response.getWriter().write(data);
		response.flushBuffer();
	}
}
