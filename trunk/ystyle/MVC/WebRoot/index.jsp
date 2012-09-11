<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	response.sendRedirect(path+"/main.action");
   // request.getRequestDispatcher("main.action").forward(request,response);
%>
