第三章 完善控制层，提供自动注入和注解上传等功能
> 当表单提交的内容过多 ，让懒惰的程序员一个个getParameter()是很让人抓狂的，所以自动注入表单域是mvc不可或缺的功能，另外，文件上传也是一个特殊的表单域，你想看到程序员发觉上传只需要注入就能完成功能时的那种欣喜吗  ？ 我们一起做做看。
> 我们依然从最简单的开始做，慢慢的润色。
> 注入表单的思路比较简单：
  1. 在form里面的name需要设置成诸如userinfo.username这类的,userinfo表示注入的目标对象，username表示userinfo对象的属性。这个对象必须是Action里面声明的
> 2，MainServlet在接收表单时，从getParameterMap（）得到所有表单域，拆分出目标对象和属性，通过反射执行set方法

注意：由于每个请求都会产生一个Action的新实例，所以在Action类的属性不会被多个请求共享，是线程安全的。

实现方式如下：
1，打开MainServlet，首先声明
Map<String,Object[.md](.md)> paramMap = request.getParameterMap();

//此map对象用来缓存单页面的目标注入对象，比如此页面有多个Userinfo的属性需要注入，不可能每次注入都要生成Userinfo对象，肯定得在同一个对象中注入（小细节）
Map<String, Object> fieldMap = new HashMap<String, Object>();

得到请求信息后进行迭代
Set<Entry<String,Object[.md](.md)>> paramSet = paramMap.entrySet();
for (Entry<String,Object[.md](.md)> ent : paramSet) {
> String paramName = (String) ent.getKey();

> Object[.md](.md) paramValue = ent.getValue();

> handField(fieldMap,paramName,paramValue,action);
}
handField方法用来处理注入功能。
方法体和详细注释如下：

> //.这个字符是不能直接用正则的，需要转义
> String[.md](.md) paramVos = paramName.split("\\.");
> //这里只支持 对象.属性的表单注入，对于多级的大家可以自行实现，相信不是难事儿。
> > if (paramVos. length == 2) {
> > > Class actionClass = action.getClass();
> > > Object fieldObj = fieldMap.get(paramVos[0](0.md));
> > > //从你的action得到目标注入对象
> > > Field field  = actionClass.getDeclaredField(paramVos[0](0.md));;
> > > > if (fieldObj == null) {
> > > > > //假如是第一次注入，为空，则实例化目标对象
> > > > > Class fieldClass = field.getType();
> > > > > fieldObj = fieldClass.newInstance();
> > > > > //放入缓存，第二次直接从缓存取，保证同一个form注入的是同一个对象
> > > > > fieldMap.put(paramVos[0](0.md), fieldObj);

> > > }
> > > > //构造目标属性的set方法
> > > > String setMethod = "set"
> > > > > + paramVos[1](1.md).substring(0, 1).toUpperCase()
> > > > > + paramVos[1](1.md).substring(1);

> > > > Field fieldField = null;
> > > > fieldField = fieldObj.getClass().getDeclaredField(
> > > > > paramVos[1](1.md));



> if(realValue!= null){
> > InvocakeHelp. invokeMethod(fieldObj, setMethod,
> > > new Object[.md](.md) { paramValue });

> }


> }

到此，基本的注入功能就有了，测试一下.
我们编写一个Userinfo对象，属性为username，email等，并在TestAction里声明Userinfo对象，名为user。
然后form表单里写好表单


&lt;input type="text" name="user.username" /&gt;




&lt;input type="text" name="user.email" /&gt;


提交表单，发觉已经被注入了,测试成功。
但是，这里只是测试的字符串类的表单域，假如Userinfo里面还有个age属性，Integer型的，该怎么办呢？Integer类型的接收String型的肯定是不行的。
有人说，那就在MainServlet直接判断呗，直接得到属性type，判断假如是Integer，就Integer.parseInt一下。
但是假如是double类型的呢？假如是更多类型的呢，假如是自定义的类型呢？ 所以这里显然得做成可扩展的。
回想一下直接判断是个怎样的过程：
1，获取注入属性的类型（type）
2，根据不同类型，用不同方式转换值，比如Java.lang.Integer对应parseInt,Double对应parseDouble。
3，注入转换后的值
假如做成可扩展的，那么转换的类型是程序员自定义的，另外根据不同类型，配置不同的转换器，然后让MainServlet读取并自动转换。
那么我们的配置看起来应该是这样的：

> 

&lt;converter type= "java.lang.Integer" handle= "org.love.converter.IntegerConverter" &gt;


> </converter >
让IntegerConverter实现你设定好的接口，让MainServlet统一接口调用。
接口代码如下：
public interface TypeConverter {

> /
    * 
    * @param value 将要转换的值
    * @param field 将要转换的属性 元数据包含了更多的信息
    * @return 得到被转换后的对象
    * 
> public Object convertValue(Object value, Field field);
}


IntegerConverter实现代码如下：

public class IntegerConverter implements TypeConverter {

> public Object convertValue(Object value,Field field) {

> if(value== null || value.equals( "")){
> > return null;

> }
> //假如这里是数组，那么组装Integer数组并返回
> > if(field.getType().isArray()){
> > > String[.md](.md) intStr=(String[.md](.md))value;
> > > Integer[.md](.md) returnInt= new Integer[intStr.length];
> > > > for( int i=0;i<intStr. length;i++){
> > > > > if(intStr[i](i.md)!= null&&!(intStr[i](i.md).trim().equals( ""))){
> > > > > > returnInt[i](i.md)=Integer. parseInt(intStr[i](i.md));

> > > > > }


> }
> > return returnInt;

> } else{
> > return Integer. parseInt(value.toString());

> }


> }

}
，然后打开ControlXML类，加上
private Map<String,TypeConverter> convertMap= new HashMap<String, TypeConverter>();
读取 converter元素并装配到convertMap.(详细代码就不贴了，可以对照源代码看)
现在可以在MainServlet里调用了，首先获得注入属性的类型
//得到被反射的属性的类别名称，假如是数组，也返回原始类型
String fieldTypeClassName = (fieldField.getType().isArray()?fieldField.getType().getComponentType().getName():fieldField.getType().getName());

这句代码比较长，我们没有简单的getType().getName(),原因是，假如注入属性的类别是Integer数组（或者其他类别数组），用getType()是得不到原始类别的（因为数组也是个特殊的类别），所以这里判断假如是数组就得到原始类别，方便后面做判断。

//接收原始值
> Object realValue =paramValue;


/*** 假如传递过来的是字符串数组（比如多选）并且被注入的元素类别是非数组
  * 那么会被以逗号分割拼接，假如是数组就不用处理，直接用数组接收
  * 
> > if(paramValue instanceof String[.md](.md)){
> > > if(!fieldField.getType().isArray()){
> > > > realValue = Utils.join((String[.md](.md))paramValue,",");
> > > > }

> }**

最后调用
if (realValue!=null&&convertMap.containsKey(fieldTypeClassName)) {
> realValue = convertMap.get(fieldTypeClassName)
> > .convertValue(realValue, fieldField);

> }

realValue就是转换后的值，反射注入搞定！

接口的设计为程序的扩展提供无限可能，我们趁热打铁，假如注入属性是Date类型呢？很好办。
创建DateConverter类实现TypeConverter，并且在control.xml里配置


&lt;converter type ="java.util.Date" handle= "org.love.converter.DateConverter"&gt;


</converter >
实现过程也比较简单，想必都会，我在这里写了个稍微功能强一点的Date转换器，代码如下,仅供参考:


public class DateConverter implements TypeConverter {

> private static final String[.md](.md) FORMAT = {
> > "HH",  // 2
> > "yyyy", // 4
> > "HH:mm", // 5
> > "yyyy-MM", // 7
> > "HH:mm:ss", // 8
> > "yyyy-MM-dd", // 10
> > "yyyy-MM-dd HH", // 13
> > "yyyy-MM-dd HH:mm", // 16
> > "yyyy-MM-dd HH:mm:ss" // 19

> };

> private static final DateFormat[.md](.md) ACCEPT\_DATE\_FORMATS = new DateFormat[FORMAT.length]; //支持转换的日期格式

> static {
> > for( int i=0; i< FORMAT. length; i++){
> > > ACCEPT\_DATE\_FORMATS[i](i.md) = new SimpleDateFormat(FORMAT[i](i.md));

> > }

> }

> public Object convertValue(Object value,Field field) {

> if(value== null||value.equals( "")){
> > return null;

> }
> //String[.md](.md) params = (String[.md](.md))value;
> String dateString = (String)value; //获取日期的字符串
> int len = dateString != null ? dateString.length() : 0;
> int index   = -1;

> if (len > 0) {
> > for ( int i = 0; i < FORMAT. length; i++) {
> > > if (len == FORMAT[i](i.md).length()) {
> > > > index = i;

> > > }

> > }
> > }



> if(index >= 0){
> > try {
> > > return ACCEPT\_DATE\_FORMATS[index](index.md).parse(dateString);
> > > } catch (ParseException e) {
> > > > return null;

> > > }

> }
> return null;
> }

}
也就是说支持数组中的各种时间格式。


设计程序时需要不断调整和重构，当我发现这两个转换器属于程序必备品，为什么还需要每次配置在control.xml里面呢？所以干掉这两个converter配置
，直接在ControlXML中加入代码：
convertMap .put("java.util.Date" , new DateConverter());
convertMap.put("java.lang.Integer" , new IntegerConverter());
到目前位置，C层的开发貌似已经有模有样了，可以放心大胆的测试了。

我们回到本章的开头，假设一个表单里面不仅有文本，还有文件上传，那么用这个框架肯定是搞不定的，因为你没法同时接收到文本数据和二进制流，而上传实在是一个烂大街的功能，所以我们必须搞定它。
用过struts2的都知道它是通过common-fileupload组件接收数据流，产生临时文件，然后绑定到注入的File属性，程序员只需要copy一下到自己的路径就行了，这里我不打算按照这个方式实现，
直接通过注解配置路径，自动上传。在这里依然要用到common-fileupload组件，它依赖common-io.jar。
当form上传文件时必须设置enctype="multipart/form-data"，我们在MainServlet通过request的contentType来判断是否二进制请求流，假如为true，
则通过common-fileupload得到所有文件和文本对象，很自然而然的，代码就成了这样：

> Map<String,Object[.md](.md)> paramMap = new HashMap<String,Object[.md](.md)>();
> String contentType=request.getContentType();

> //假如是带有上传，那么利用common fileupload封装表单
> if(contentType!= null && contentType.startsWith("multipart/form-data" )){
> > //文件项工厂
> > > FileItemFactory factory = new DiskFileItemFactory();
> > > ServletFileUpload upload = new ServletFileUpload(factory);
> > > //得到所有表单项
> > > List

&lt;FileItem&gt;

 items = upload.parseRequest(request);
> > > ...

> paramMap=fileParamMap.put(表单域名,FileItem[.md](.md))

> }else{
> > paramMap=request.getParameterMap();

> }



注意，这里的items不光是上传的文件数据，也包含文本数据。
现在paramMap里不仅装有文本数据，也有上传的文件流数据，下一步可以根据类别的不同做不同的注入了。但是这里有个问题，你仍然没有办法在自己编写的action中用
getParameter()或者getParameterMap()的方式得到提交的表单信息，所以这里需要改造一下。
> Java Web中提供一种装饰模式，让HttpServletRequest请求被处理之前改造自身。
> 首先创建一个MulRequestWraper,继承HttpServletRequestWrapper，并且需要提供带HttpServletRequest参数的构造方法，然后重写一些重要的方法，代码如下：

public class MulRequestWraper extends HttpServletRequestWrapper {

> private Map<String, Object[.md](.md)> paramMap = new HashMap<String, Object[.md](.md)>();

> public MulRequestWraper(HttpServletRequest request) {
> > super(request);


> try {
> > FileItemFactory factory = new DiskFileItemFactory();
> > ServletFileUpload upload = new ServletFileUpload(factory);
> > List

&lt;FileItem&gt;

 items = upload.parseRequest(request);
> > Iterator

&lt;FileItem&gt;

 iter = items.iterator();
> > String  encoding=(request.getCharacterEncoding()==null ?"UTF-8" :request.getCharacterEncoding());
> > > while (iter.hasNext()) {
> > > > FileItem item = (FileItem) iter.next();


> String fieldName = item.getFieldName();
> > if ( paramMap.containsKey(fieldName)) {
> > > Object[.md](.md) paramValue = paramMap.get(fieldName);


> // 构造同类数组
> Object[.md](.md) paramValueTemp = (Object[.md](.md)) Array.newInstance(
> > paramValue[0](0.md).getClass(), paramValue. length + 1);
> > for ( int i = 0; i < paramValue.length; i++) {
> > > paramValueTemp[i](i.md) = paramValue[i](i.md);

> }

> if (item.isFormField()) {
> > paramValueTemp[paramValueTemp. length - 1] = item
> > > .getString(encoding);

> } else {
> > if (item.getSize() > 0) {
> > > paramValueTemp[paramValueTemp. length - 1] = item;

> > }

> }

> paramMap.put(fieldName, paramValueTemp);
> } else {
> > if (item.isFormField()) {
> > > paramMap.put(fieldName,
> > > > new String[.md](.md) { item.getString(encoding) });

> > } else {
> > > if (item.getSize() > 0) {
> > > > paramMap.put(fieldName, new FileItem[.md](.md) { item });

> > > }

> > }


> }

> }
> } catch (Exception e) {
> > e.printStackTrace();

> }

> }

> public String getParameter (String name) {
> > Object[.md](.md) values= paramMap.get(name);
> > > if(values. length>0){
> > > > return (String)values[0](0.md);

> > }
> > > return super.getParameter(name);

> }

> public String[.md](.md) getParameterValues (String name) {
> > Object[.md](.md) values= paramMap.get(name);
> > > if(values!= null){
> > > > return (String[.md](.md))values;

> > }
> > > return super.getParameterValues(name);

> }



> public Map getParameterMap () {
> > paramMap.putAll( super.getParameterMap());
> > return paramMap;

> }


因为HttpServletRequestWrapper类是实现HttpServletRequest接口的，所以可以在外面直接接收，外面的判断代码可以改造成如下：
//假如是带有上传，那么利用common fileupload封装表单
> if(contentType!= null && contentType.startsWith("multipart/form-data" )){
> > request= new MulRequestWraper(request);

> }

> paramMap=request.getParameterMap();

逻辑更加清晰了，并且在后面的Action里都可以得到所有的表单信息（二进制的或者文本的）。

jdk5引入的注解不光可以简化各种配置信息，也逐渐成为了程序功能的一个部分
用注解实现上传配置的大致流程如下：
1，框架提供文件信息的Bean类：FilePo
2，自定义注解，(Field类型)
3，MainServlet通过转换FilePo，读取注解信息，实现上传

FilePo主要属性有：
> // 文件名 带后缀
> private String filename;

> // 相对于web的路径
> private String webpath;

> // 实际文件
> private File file;

> // 类型
> private String contentType;

> //大小 kb
> private double size;

，自定义注解的关键字是：@interface，代码如下
/
  * 自动上传，这个注解用在FilePo对象上
  * @author 杜云飞
  * 
  * 
@Retention(RetentionPolicy.RUNTIME)
//表示本注解用在属性上
@Target(ElementType.FIELD )
public @interface UploadFile {

> /
    * 上传的路径，默认上传到根目录
    * 
> public String path() default "";

> /
    * 文件名 为""表示用原文件名
    * 
> public String name() default "";
}

暂时只用这几个属性，其实还可以加上传限制或者后缀等。
还记得之前我们做的TypeConverter么，现在咱们需要一个拦截FilePo的转换器，
新建FileConverter，继承TypeConverter，读取Field上的注解并且获取path和name信息，通过common-fileupload上传，就这么简单
具体实现如下：
public Object convertValue(Object value, Field field) {
> if(!(value instanceof FileItem) && !(value instanceof FileItem[.md](.md))){
> > return null;

> }
> > UploadFile uf = field.getAnnotation(UploadFile.class );

> HttpServletRequest request = ActionContext.getRequest();
> > try {


> if (uf != null) {
> > String path = uf.path();
> > > logger.debug( "path: " + path);

> > //path =Utils.resolvePlaceHolder(path , ActionReplaceHolder.getInstance());
> > String name = uf.name();
> > String realpath = request.getRealPath(path);
> > > logger.debug( "realpath: " + realpath);

> > File dsk = new File(realpath);
> > > if (!dsk.exists()) {
> > > > dsk.mkdirs();

> > }


> FilePo[.md](.md) fps= new FilePo[0](0.md);
> > if (field.getType().isArray()) {


> // 假如是数组，那么保存在同一个注释的文件夹里面，并且文件名为源文件的名字
> FileItem[.md](.md) fis = (FileItem[.md](.md)) value;
> > for ( int i = 0; i < fis. length; i++) {
> > > FileItem item = fis[i](i.md);
> > > > long filesize=item.getSize();

> > > String filename = item.getName();
> > > > if(filename.indexOf(File. separator)>=0){
> > > > > filename=filename.substring(filename.lastIndexOf(File. separator)+1);

> > > }
> > > > logger.debug( "filesize: "+filesize);

> > > File file = new File(realpath + File.separator
> > > > + filename);


> item.write(file);

> FilePo[.md](.md) fps\_temp=fps;
> fps= new FilePo[fps.length+1];
> > for( int j=0;j<fps\_temp.length;j++){
> > > fps[j](j.md)=fps\_temp[j](j.md);

> }
> FilePo fp= new FilePo();
> fp.setFile(file);
> fp.setFilename(filename);
> fp.setWebpath(path+filename);
> fp.setContentType(item.getContentType());
> fp.setSize(filesize/1024);
> fps[fps. length-1]=fp;
> }
> > if(fps!= null && fps. length>0){
> > > return fps;

> }
> > return null;


> } else {
> > FileItem[.md](.md) items=(FileItem[.md](.md))value;
> > FileItem item=items[0](0.md);
> > > long filesize=item.getSize();
> > > if(filesize==0){
> > > > return null;

> > }
> > String filename = item.getName();
> > > if(!name.equals( "")){
> > > > String exts = filename.substring(filename
> > > > > .lastIndexOf( "."));

> > > > filename=name+exts ;

> > } else{
> > > filename=filename.substring(filename.lastIndexOf(File. separator)+1);

> > }
> > File file = new File(realpath + File.separator
> > > + filename);

> > item.write(file);
> > FilePo fp= new FilePo();
> > fp.setFile(file);
> > fp.setFilename(filename);
> > fp.setWebpath(path+filename);
> > fp.setContentType(item.getContentType());
> > fp.setSize(filesize/1024);
> > > return fp;

> }
> } else {
> > // 没有注解的暂时不作任何处理
> > return null;

> }

> } catch (Exception ex) {
> > ex.printStackTrace();
> > > logger.error( "上传模块出现错误:" +ex.getMessage());

> }

> return null;
> }

有时候我们希望path路径是从上下文资源里面取到的，而不是“死”的，比如从${requestScope.xxx}，${sessionScope.xxx}或者${param.xxx}等地方获取需要的路径信息，注释的那段代码正是此功能的实现。
path=Utils.resolvePlaceHolder(path, ActionReplaceHolder.getInstance());（策略模式的运用）
ActionReplaceHolder是一个单例类，实现了ReplaceHolder接口，用于产生替换值，核心代码如下：
public String extract(String value) {
> if(value.indexOf( "requestScope.")!=-1){
> > String prop=value.substring(value.indexOf("requestScope." )+"requestScope." .length());
> > > return (String)ActionContext.getRequest().getAttribute(prop);

> } else if(value.indexOf( "sessionScope.")!=-1){
> > String prop=value.substring(value.indexOf("sessionScope." )+"sessionScope." .length());
> > > return (String)ActionContext.getRequest().getSession().getAttribute(prop);

> } else if(value.indexOf( "param.")!=-1){
> > String prop=value.substring(value.indexOf("param." )+"param." .length());
> > > return ActionContext.getRequest().getParameter(prop);

> }
> > return value;

> }

resolvePlaceHolder方法用于解析${}这类占位符，参考实现如下：

/
  * 解析占位符具体操作
  * @param property
  * @return
  * 
> public static String resolvePlaceHolder(String property,ReplaceHolder rh) {
> > if ( property.indexOf( PLACEHOLDER\_START ) < 0 ) {
> > > return property;

> > }
> > StringBuffer buff = new StringBuffer();
> > > char[.md](.md) chars = property.toCharArray();
> > > for ( int pos = 0; pos < chars. length; pos++ ) {
> > > > if ( chars[pos](pos.md) == '$' ) {
> > > > > if ( chars[pos+1] == '{' ) {
> > > > > > String propertyName = "";
> > > > > > > int x = pos + 2;
> > > > > > > for (  ; x < chars. length && chars[x](x.md) != '}'; x++ ) {
> > > > > > > > propertyName += chars[x](x.md);
> > > > > > > > > if ( x == chars. length - 1 ) {
> > > > > > > > > > throw new IllegalArgumentException( "unmatched placeholder start [" + property + "]" );

> > > > > > > > }

> > > > > > }
> > > > > > String systemProperty = rh.extract( propertyName );
> > > > > > buff.append( systemProperty == null ? "" : systemProperty );
> > > > > > pos = x + 1;
> > > > > > > if ( pos >= chars. length ) {
> > > > > > > > break;

> > > > > > }

> > > > > }

> > > > }
> > > > buff.append( chars[pos](pos.md) );

> > }
> > String rtn = buff.toString();
> > > return isEmpty( rtn ) ? null : rtn;

> }
这也是hibernate中的标准解析方式。
详细可以参考本项目源码。
最后记得注册这个转换器，
convertMap .put( "org.love.po.FilePo", new FileConverter());
那么以后上传就可以直接注解FilePo属性就行了
> @UploadFile(path="uploadfiles/${param.folderPath}/" )
> private FilePo[.md](.md) myimg;

> @UploadFile(path="uploadfiles/${sessionScope.folderPath}/" )
> private FilePo myimg0;

> @UploadFile(path="uploadfiles/xxx/")
> private FilePo myimg1;

当然，假如有人希望不用注解上传，也依然可以在Action里得到上传信息（因为之前request已经被包装过了，已经存有此类对象。）
比如你可以这样做：
FileItem[.md](.md) fis = (FileItem[.md](.md)) request.getParameterMap().get("myimg");
然后通过api自己实现上传。
是不是灰常方便啊。
到现在为止，我们控制层的大部分功能都已实现，但毕竟是一个演示项目，有很多代码需要优化，功能也有很多需要完善并且改进的地方,不过我觉得，只要思路清晰，整体框架没有偏离基准，添砖加瓦是很容易的事情。
下一章开始讲解业务逻辑容器。