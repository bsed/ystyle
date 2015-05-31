第二章 实现初步的控制层，实现各种配置和资源获取
> mvc框架最基础的功能就是跳转，struts2支持注解+xml配置跳转，我个人认为用注解来配置跳转不是一个好的做法，看似比较简单，但是action多了之后
查找起来比较不方便，而且把配置信息放在类里面实际上跟解耦理念是相悖的（不过每个人有自己喜好），所以在这里我打算把跳转层设计成xml配置的，其他层设计成注解的。
> 配置跳转需要用到的知识有：反射，xml读取。反射是实现动态装配的基础，它使我们的程序更具动态性，可扩展性，几乎所有流行的框架都以它为基础实现，
。xml读取基本都会采用dom4j完成。
> mvc实现跳转的过程：xml配置命名空间，Action处理类，请求的action方法和跳转的页面，在form提交请求后，被中心Servlet处理，解析出请求的路径，根据xml配置的各种信息，反射调用目标Action类的处理方法，并且根据xml配置的目标跳转页面进行跳转。

> 所以，我们提炼出的 核心配置有
  1. namcespace：命名空间，不同模块有不同的namespace，
> 2，name：form请求的名字。
> 3，method：name对应的Action处理方法名,会被反射调用
> 4,  class:Action处理类的全路径，用于在中心Servlet反射生成.
> 5,  result子标签：Action处理后的跳转页面，跳转方式为forward或redirect



> 我们新建一个web工程（Eclipse），取名MVC。
> 接着新建一个Servlet，取名MainServlet,做中心处理器用。
> src目录下新建control.xml当作跳转配置文件，control.xml如下
<? xml version ="1.0" encoding= "UTF-8" ?>
< actions>
> > 

&lt;global-results&gt;


> > > 

&lt;result name="userindex" type="redirect"&gt;

test/test1.action

&lt;/result&gt;


> > > 

&lt;/global-results&gt;



> > 

&lt;namespace name= "/test" &gt;


> > > <action name= "test1" method= "test1"
> > > > class= "com.test.action.TestAction" >
> > > > 

&lt;result name= "success" type= "forward" &gt;

success.jsp </result >
> > > > 

&lt;result name= "error" type= "redirect" &gt;

error.jsp </result >

> > > </ action>


> <action name= "test2" method= "test2"
> > class= "com.test.action.TestAction" >
> > 

&lt;result name= "success" type= "forward" &gt;

success.jsp </result >
> > 

&lt;result name= "error" type= "forward" &gt;

error.jsp </result >

> </ action>

> <action name= "test3" method= "test3"
> > class= "com.test.action.TestAction" >
> > 

&lt;result name= "success" type= "forward" &gt;

success.jsp </result >
> > 

&lt;result name= "error" type= "forward" &gt;

error.jsp </result >

> </ action>
> </namespace >

</ actions>
> 配置web.xml，使其拦截所有action结尾的请求，我的配置如下：



&lt;servlet&gt;


> 

&lt;servlet-name &gt;

mainservlet 

&lt;/servlet-name&gt;


> 

&lt;servlet-class &gt;

org.love.servlet.MainServlet 

&lt;/servlet-class&gt;




> 

&lt;init-param &gt;


> 

&lt;description &gt;

control action的配置文件路径 不配置，则默认为src根路径的control.xml文件
> > 

&lt;/description&gt;



> 

&lt;param-name &gt;

CONTROL\_CONFIG 

&lt;/param-name&gt;


> 

&lt;param-value &gt;

classpath:control.xml 

&lt;/param-value&gt;


> </init-param >

> 

&lt;init-param &gt;


> 

&lt;description &gt;

字符编码 

&lt;/description&gt;


> 

&lt;param-name &gt;

ENCODING 

&lt;/param-name&gt;


> 

&lt;param-value &gt;

UTF-8 

&lt;/param-value&gt;


> </init-param >

> </servlet >

> 

&lt;servlet-mapping &gt;


> 

&lt;servlet-name &gt;

mainservlet 

&lt;/servlet-name&gt;


> 

&lt;url-pattern &gt;

**.action

&lt;/url-pattern&gt;


> </servlet-mapping >
之所以把CONTROL\_CONFIG 配制成
classpath:control.xml是因为更好的读取不同路径下的文件。**

这样，所有.action的请求都会进入MainServlet这个中心处理器，并且把配置文件一并传入，在servlet初始化(init()方法会在第一次请求时调用
)阶段读取配置文件信息，并缓存起来。
> 一般来说，资源管理类都可以设计成单例，即满足应用需要，又节省内存，并且代码看起来更加清晰，所以我这里新建ControlXml类，用来专门读取control.xml文件
> 在实现此类之前，还需要把配置文件各元素对应成不同javabean，类对应如下

Namespace持有ActionVo列表，ActionVo持有Result列表，ControlXml持有Namespace的map结构就行了。
ControlXML代码如下：

package org.love.servlet.util;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.love.converter.DateConverter;
import org.love.converter.FileConverter;
import org.love.converter.IntegerConverter;
import org.love.converter.TypeConverter;

/
  * 用来解析控制器的xml文件
  * 
  * @author Administrator
  * 
  * 
public class ControlXML {

> public final static String CONTROL\_CONFIG = "control.xml";

> private static ControlXML controlXml = new ControlXML();

> private Map<String, Namespace> namespaces = new HashMap<String, Namespace>();

> private Map<String,TypeConverter> convertMap= new HashMap<String, TypeConverter>();

> private Map<String,Result> globalResults= new HashMap<String, Result>();

> public Map<String, Result> getGlobalResults() {
> > return globalResults;

> }

> private ControlXML() {
> > /**convertMap.put("java.util.Date", new DateConverter());
> > convertMap.put("java.lang.Integer", new IntegerConverter());
> > convertMap.put("org.love.po.FilePo", new FileConverter());**/

> }

> public static ControlXML getInstance() {
> > return controlXml;

> }

> public void readXml(String xmlurl) throws DocumentException, InstantiationException, IllegalAccessException, ClassNotFoundException {
> > /**读取xml文件**/
> > SAXReader reader = new SAXReader();
> > Document document = reader.read( new File(xmlurl));
> > Element rootElement = document.getRootElement();


> /**读取namespace配置（包含result配置） start**/
> List

&lt;Element&gt;

 namespaces\_list = rootElement.elements("namespace" );
> for (Element namespace : namespaces\_list) {
> > /**读取action后的集合**/
> > Map<String, ActionVo> actions = new HashMap<String, ActionVo>();


> /**读取action配置（包含result配置） start**/
> List actions\_list = namespace.elements("action" );
> for (Iterator it = actions\_list.iterator(); it.hasNext();) {
> > Element action = (Element) it.next();
> > ActionVo avo = new ActionVo();
> > avo.setName(action.attributeValue( "name"));
> > avo.setMethod(action.attributeValue( "method"));
> > avo.setClassName(action.attributeValue( "class"));


> List

&lt;Element&gt;

 avo\_results = action.elements("result" );
> Map<String,Result> list\_result = new HashMap<String, Result>();
> > for (Element result : avo\_results) {
> > > Result rs = new Result();
> > > rs.setName(result.attributeValue( "name"));
> > > rs.setType(result.attributeValue( "type"));
> > > rs.setUrltext(result.getText().trim());
> > > list\_result.put(rs.getName(),rs);

> }
> avo.setResults(list\_result);
> actions.put(avo.getName(), avo);
> }
> > /**读取action配置（包含result配置） end**/


> namespaces.put(namespace.attributeValue( "name"), new Namespace(
> > namespace.attributeValue( "name"), actions));

> }
> > /**读取namespace配置（包含result配置） end**/


> /**读取converter配置 start**/
> /**List**

&lt;Element&gt;

 converter\_list = rootElement.elements("converter");
> for(Element convertElement:converter\_list){
> > String type=convertElement.attributeValue("type");
> > String handle=convertElement.attributeValue("handle");
> > TypeConverter tc=(TypeConverter)(Class.forName(handle).newInstance());
> > convertMap.put(type,tc);

> }**/
> > /**读取converter配置 end**/**


> /**读取 global-results start**/
> List 

&lt;Element&gt;

 global\_results\_list = rootElement.elements("global-results");
> if(global\_results\_list!=null && global\_results\_list.size()>0){
> > Element global\_results=global\_results\_list.get(0);
> > > List

&lt;Element&gt;

 results=global\_results.elements("result");

> > for(Element result:results){
> > > Result rs = new Result();
> > > rs.setName(result.attributeValue("name"));
> > > rs.setType(result.attributeValue("type"));
> > > rs.setUrltext(result.getText().trim());
> > > globalResults.put(rs.getName(),rs);

> > }

> }


> /**读取 global-results end**/

> /**后续会加上其他配置**/

> }

> public ActionVo getAction(String namespacename, String actionname) {
> > if ( namespaces == null || namespaces.isEmpty()) {
> > > throw new RuntimeException( "请确保之前调用了readXml(xml)方法" );

> > }
> > Namespace ns = namespaces.get(namespacename);
> > ActionVo avo = null;
> > > if (ns != null) {
> > > > avo=ns.getListActions().get(actionname);

> > }


> return avo;
> }



> public Map<String, TypeConverter> getConvertMap() {
> > return convertMap;

> }

}

代码中注释的部分以后会有详细讲解，事实是只要代码结构清晰，增删功能都比较容易。
下面就可以在MainServlet的方法中进行读取了，核心代码如下：
ControlXML controlXml = ControlXML.getInstance();
> String control\_config = sc.getInitParameter("CONTROL\_CONFIG" );

> if (control\_config == null || control\_config.trim().equals("" )) {
> > control\_config = Thread.currentThread().getContextClassLoader()
> > > .getResource(ControlXML. CONTROL\_CONFIG).getFile();

> } else if (control\_config.startsWith("classpath:" )) {
> > control\_config = control\_config.split(":" )[1](1.md);
> > control\_config = Thread.currentThread().getContextClassLoader()
> > > .getResource(control\_config).getFile();

> } else {
> > control\_config = sc.getServletContext().getRealPath("WEB-INF" )
> > > +File. separator+ control\_config;

> }
> try {
> > controlXml.readXml(control\_config);
> > } catch (Exception e) {
> > > e.printStackTrace();

> > }
control.xml不一定放在src下，也有可能是web-inf下，所以这里需要多做几次判断，保证在常用目录下可以找到文件，这里默认为src下。
上面是所有解析工作，下一步：编写MainServlet，使其处理各种请求。

解析请求的核心工作分这几步：
1，从requestUri解析namespace,action
2，在配置资源类ControlXml中获得此Namcespace下的action信息，生成Action实例。
3，执行对应的Action方法，根据返回值得到此Action配置的result，跳转到页面。

实现如下：
String requestURI = request.getRequestURI();
//假如以"/"结尾，则截取url，方便后面解析
if (requestURI.endsWith("/" )) {

> requestURI = requestURI.substring(0, requestURI.length() - 1);
}
String namespace = requestURI.substring(0, requestURI.lastIndexOf("/" ));
String actionname = requestURI.substring(
> > requestURI.lastIndexOf( "/") + 1, requestURI.lastIndexOf("." ));

得到相应Action
ControlXML controlXml = ControlXML.getInstance();
ActionVo avo = controlXml.getAction(namespace, actionname);
Object action = InvocakeHelp.newInstance(avo.getClassName(), null);

反射调用Action的方法，并返回字符串
Object actionValue = InvocakeHelp.invokeMethod(action, avo.getMethod(),

> null);

首先根据globalResult配置判断是否有此Result，不存在则从各个action的result里获取
Result result = controlXml.getGlobalResults().get(actionValue);
if(result== null){
> result = avo.getResults().get(actionValue);
}


//跳转到配置的页面
> if ("redirect" .equals(result.getType())) {
> > response.sendRedirect(request.getContextPath() + "/"
> > > + result.getUrltext());

> } else {
> > request.getRequestDispatcher( "/" + result.getUrltext()).forward(
> > > request, response);

> }

InvocakeHelp这个是反射工具类，实现起来不复杂，核心代码如下



> public static Object newInstance(String className, Object[.md](.md) args) {
> > try {
> > > Class newClass = Class. forName(className);
> > > > if (args == null || args. length == 0) {
> > > > > return newClass.newInstance();

> > > } else {
> > > > Class[.md](.md) argsClasses = new Class[args.length];
> > > > > for ( int i = 0; i < args. length; i++) {
> > > > > > argsClasses[i](i.md) = args[i](i.md).getClass();

> > > > }
> > > > Constructor cons = newClass.getConstructor(argsClasses);
> > > > > return cons.newInstance(args);

> > > }


> } catch (ClassNotFoundException e) {
> > e.printStackTrace();

> } catch (Exception ex) {
> > ex.printStackTrace();

> }
> > return null;

> }

> public static Object invokeMethod(Object owner, String methodName,
> > Object[.md](.md) args) {
> > Class ownerClass = owner.getClass();
> > Class[.md](.md) argsClass = null;
> > > if (args != null && args. length != 0) {
> > > > argsClass = new Class[args. length];
> > > > > for ( int i = 0; i < args. length; i++) {
> > > > > > argsClass[i](i.md) = args[i](i.md).getClass();

> > > > }

> > }
> > > try {
> > > > Method method = ownerClass.getMethod(methodName, argsClass);
> > > > > return method.invoke(owner, args);

> > } catch (SecurityException e) {
> > > e.printStackTrace();

> > } catch (NoSuchMethodException e) {
> > > e.printStackTrace();

> > } catch (Exception ex) {
> > > ex.printStackTrace();

> > }


> return null;
> }

> /
    * 调用对象的set方法
    * 
    * @param obj
    * @param fieldName
    * @param value
    * @param fieldType
    * 
> public static void callSetMethod(Object owner, String fieldName,
> > Object value) {
> > String setName = "set" + fieldName.substring(0, 1).toUpperCase()
> > > + fieldName.substring(1);

> > Class ownerClass = owner.getClass();
> > > try {
> > > > Field field=ownerClass.getField(fieldName);
> > > > Method method = ownerClass.getMethod(setName,field.getType());
> > > > method.invoke(owner,value);

> > } catch (Exception e) {
> > > e.printStackTrace();

> > }



> }






看起来大致功能都有了，是不是可以测试下，但是？。。。貌似自己的Action里面没有任何request,response的资源？
现在有两种方式可以得到：
  1. 实现接口（或继承）
> 2，API得到
有人说第二种方式肯定好点，因为很解耦哦，我觉得不一定，因为我们做任何框架的目标不是为了解耦，只要用着顺手就行，固执的考虑低耦合是效率低下的原因之一，当然，作为一个有理想 有思想的框架，这两种方式都得提供。
其实struts2也遇到过类似问题，解决方式之一是实现资源接口，比如
ServletRequestAwarehue接口，实现它可以获得request,另外还有他的兄弟如ServletResponseAware，SessionAware，ServletContextAware，实现他们可以分别得到response,session,servletContext。
首先我们创建
ContextAction接口，代码如下：
/
  * 为action提供应用程序，请求，会话等相关的资源
  * @author duyf
  * 
  * 
public interface ContextAction {
> > public void setRequest(HttpServletRequest request);
> > public void setResponse(HttpServletResponse response);
> > public void setSession(HttpSession session);
> > public void setServletContext(ServletContext context);
}

调出MainServlet，在得到action对象后加入设置代码


> if (!(action instanceof ContextAction)) {
> > throw new RuntimeException( "当前版本需要实现ContextAction接口" );
> > }
> > ContextAction ca = (ContextAction) action;
> > ca.setRequest(request);
> > ca.setResponse(response);
> > ca.setSession(request.getSession());
> > ca.setServletContext( servletContext);

调出测试Action，声明HttpServletRequest等各种资源对象，实现接口并接收资源，核心代码如下：

> protected HttpServletRequest request ;
> protected HttpServletResponse response ;
> protected HttpSession session;
> protected ServletContext servletContext;

> public void setRequest(HttpServletRequest request) {
> > this. request = request;

> }

> public void setResponse(HttpServletResponse response) {
> > this. response = response;

> }

> public void setServletContext(ServletContext context) {
> > this. servletContext = context;

> }

> public void setSession(HttpSession session) {
> > this. session = session;

> }

。有木有发觉以后每个action都有这些与业务没什么关系的代码，所以一般做法是，做一个BaseAction类实现ContextAction接口，然后让你的Action继承BaseAction，干掉了冗余代码。这也是java模式中常用的缺省模式。

在实现API获取web资源之前，我们仔细想一下tomcat是怎样响应请求的。
请求tomcat时，tomcat从线程池分配一个空闲线程给此请求，经过数据的包装，会在第一次执行MainServlet的时候，执行init方法（执行一次），并且以后所有请求都会执行同一个MainServlet对象，这样就容易造成Servlet的线程安全问题，（HttpServletRequest线程安全的），所以为了在任何地方都能正确的得到请求资源，我们采用ThreadLocal来存储,ThreadLocal本质上是一个 当前线程和值的键值对，所以保证了在当前任何时候访问此对象都能排除多线程变量的干扰，另外在一般情景下会用static修饰ThreadLocal。
实现方式比较简单：
public class ActionContext {

> public final static String HTTPREQUEST = "httprequest";
> public final static String HTTPRESPONSE = "httpresponse";
> private static ThreadLocal

&lt;ActionContext&gt;

 threadLocal = new ThreadLocal

&lt;ActionContext&gt;

();

> public static ActionContext getContext() {
> > return (ActionContext) threadLocal.get();

> }

> public static void setContext(ActionContext context) {
> > threadLocal.set(context);

> }

> public static HttpServletRequest getRequest() {
> > return (HttpServletRequest) getContext().get(HTTPREQUEST);

> }

> public static HttpServletResponse getResponse() {
> > return (HttpServletResponse) getContext().get(HTTPRESPONSE);

> }



> public ActionContext() {

> }

> public ActionContext(Map<String, Object> context) {
> > this. context = context;

> }

> /**对象属性 start**/
> private Map<String, Object> context = new HashMap<String, Object>();

> /**对象属性 end**/

> /**对象方法 start**/
> public Object get(String key) {
> > return context.get(key);

> }

> public Object put(String key, Object value) {
> > return context.put(key, value);

> }
> /**对象方法 end**/
}

这里仅仅存储了request和response，你还可以自行存储国际化信息，容器信息，或者执行Action的上下文信息等.
再一次说明的是request和response是线程安全的,但是你要达到不通过继承或者传参等方式可以在任何地方都能获得request资源，用threadLocal是最好的选择。

调用方式如下（请对照源代码）
> //注入Action Context
> Map<String, Object> contextMap= new HashMap<String, Object>();
> contextMap.put(ActionContext. HTTPREQUEST, request);
> contextMap.put(ActionContext. HTTPRESPONSE,response);
> ActionContext. setContext(new ActionContext(contextMap));

ThreadLocal存储会耗费不小的资源，所以用完必须清除掉，为了保险起见，在finally块里调用清除代码：
finally{
> //释放上下文资源
> ActionContext. setContext(null);
> }

以后在任何请求范围内都可以通过ActionContext.getRequest()和ActionContext.getResponse()得到请求资源或响应
到此我们基本的配置跳转已算完成，可以自行测试一下了。



