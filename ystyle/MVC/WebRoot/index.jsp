<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
	<title>My JSP 'index.jsp' starting page</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	</head>

	<body>
		<form action="<%=path%>/test/test1.love" method="post" enctype="multipart/form-data">
			姓名:<input type="text" name="testAfei.username" />
			<br/>
			密码:<input type="text" name="testAfei.password" />
			<br/>
			年龄1:<input type="text" name="testAfei.age" />
			<br/>
			年龄2:<input type="text" name="testAfei.ageTest" />
			<br/>
			年龄3:<input type="text" name="testAfei.ageTest" />
			<br/>
			语言1：<input name="testAfei.hobby" type="checkbox" value="java"/>java
			<input name="testAfei.hobby" type="checkbox" value=".net"/>.net
			<input name="testAfei.hobby" type="checkbox" value="c/c++"/>c/c++
			<br/>
			
			语言2：<input name="testAfei.hobbyTest" type="checkbox" value="java"/>java
			<input name="testAfei.hobbyTest" type="checkbox" value=".net"/>.net
			<input name="testAfei.hobbyTest" type="checkbox" value="c/c++"/>c/c++
			<br/>
			<input type="text" name="folderPath">
			<input type="file" name="testAfei.myimg"/>
			<input type="file" name="testAfei.myimg"/>  
			<input type="submit" value="提交到成功页面" />
		</form>

		<form action="<%=path%>/test/test3.love" method="post">
			<input type="text" name="uid" value="62"/>
			<input type="text" name="title" value="title" />
			<input type="submit" value="提交到添加新闻页面" />
		</form>

		<form action="<%=path%>/test/test2.love" method="post">
			<input type="text" name="uid" value="63" />
			<input type="submit" value="提交到查询页面" />
		</form>
	</body>
</html>
