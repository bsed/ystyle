<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="org.love.create.CreateService"%>
<%@page import="org.love.create.Table"%>
<%
String path = request.getContextPath();
Set<Table> tables=CreateService.getAllTables();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  <title>创建pojo和html</title>
    <script type="text/javascript">
     function selectAll(checkall){
       var checkboxs=document.getElementsByName("checkbox");
       var tvval="";
       for(var i=0;i<checkboxs.length;i++){
            checkboxs[i].checked=checkall.checked;
            if(checkall.checked){
              var tnvalue=checkboxs[i].value;
              if(tvval==''){
                tvval=tnvalue;
              }else{
                tvval=tvval+","+tnvalue;
              }
            }
       }
       document.getElementById("tablename").value=tvval;
       
     }
     function selectSingle(){
       var checkboxs=document.getElementsByName("checkbox");
       var tvval="";
       for(var i=0;i<checkboxs.length;i++){
            if(checkboxs[i].checked){
              var tnvalue=checkboxs[i].value;
              if(tvval==''){
                tvval=tnvalue;
              }else{
                tvval=tvval+","+tnvalue;
              }
            }
       }
       document.getElementById("tablename").value=tvval;
     }
    </script>
 </head>
  
  <body>
  
  <table border="1">
  <tr>
     <td><input type="checkbox" name="checkall" value="1" onclick="selectAll(this)"/></td><td>表名</td><td>类型</td><td>主键</td><td>数据库</td>
  </tr>
  <%for(Table table:tables){%>
  <tr>
     <td><input type="checkbox" name="checkbox" value="<%=table.getTableName()%>" onclick="selectSingle()"/></td>
     <td><%=table.getTableName()%></td><td><%=table.getType()%></td><td><%=table.getT2()%></td><td><%=table.getDatabase()%></td>
  </tr>
  <%}%>
  
  </table>
  
    <form action="<%=path%>/pojo/createPojos.love" method="post"> 
     <table>
     <tr>
     <td>表名</td><td><input type="text" name="tablename" id="tablename" readonly/></td><td></td>
     </tr>
     <tr>
     <td>java文件目录</td><td><input type="text" name="srcdir"/></td><td></td>
     </tr>
     <tr>
     <td>包名（可以包含.）</td><td><input type="text" name="packageName"/></td><td></td>
     </tr>
     <tr>
     <td>pojo父类</td><td><input type="text" name="superClass"/></td><td></td>
     </tr>
     <tr>
     <td><input type="submit" value="创建"></td><td></td>
     <td></td>
     </tr>
     </table>
    </form>   
  </body>
</html>
