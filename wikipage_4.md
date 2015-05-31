第四章 容器该帮我们做什么？（非常的重点）
注解注入
我们知道，Spring只有一个角色：工厂。这个工厂可以生产出任何你想要的对象或依赖，并且在出厂前后可以无限制的增强功能。
Spring最基础的功能就是注入，其中注解注入的方式消除了文件配置的繁琐，让人爱不释手。我们做的这个小框架虽然没法完全依照
它的实现，但是我们可以发挥小而精的特色，做好同样的功能。
首先新建注入注解Autowired
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD )
public @interface Autowired {
> /
    * 注入依赖的类 必须为可以实例化的类
    * 不能是抽象类或者接口
    * @return
    * 
> public Class iocClass();

}

只有一个参数，提供实现类。

接着创建容器BeanContainer，这个容器需要实现读取目标类被注解@Autowired注释的元素，提供注入功能，并且能缓存所有注入类。
处理Autowired注解的过程比较简单：读取Autowire,提取iocClass，实例化一个iocClass对象，通过反射调用setter方法，完成注入

for (Field f : fields) {
> // 循环判断是否有Autowired的自动注入field
> Autowired au = f.getAnnotation(Autowired.class );
> //System.out.println("========================="+f.getName()+" : "+au+"======================\n");
> if (au != null) {
> > // 假如此属性被注解为Autowired
> > // 得到需要注入的实例class
> > Class ioc = au.iocClass();
> > > // 构造set方法

> > String setName = "set"
> > > + f.getName().substring(0, 1).toUpperCase()
> > > + f.getName().substring(1);


> // 得到action set的方法以及它的参数类型，注意此时不能是参数子类的类型。
> Method setMethod = cls.getMethod(setName, new Class[.md](.md) { f
> > .getType() });


> Object iocObj = ioc.newInstance();
> System. out.println( "处理" + ioc.getName());
> AutowiredSet(iocObj);
> Object setObject = iocObj;
> > setMethod.invoke(obj, setObject);

> }

> }


AutowiredSet方法是此代码体的最外围方法体，里面进行了递归调用，实现了被注入类以下所有Autowired属性的注入。
单例注解
我们往往用单例模式来保证每个应用程序只产生一个某类的对象，但是用容器做单例不同于编程式单例模式，前者依赖与容器的缓存功能，后者通过约束构造方法为私有来实现，不要弄混淆哦。
首先创建单例注解：
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE )
public @interface SingleTon {

}
没有任何参数属性，仅仅用来标识此类是否单例。

在容器类BeanContainer创建Map
private static Map< String, Object> autoObjMap = new ConcurrentHashMap<String, Object>();
BeanContainer设计成单例，ConcurrentHashMap可以保证多线程下的线程安全问题，当然 用HashTable也可以。
改造上面代码，加入两句话：
if (autoObjMap .containsKey(ioc.getName())) {
> System. out.println( "已在缓存中找到" + ioc.getName());
> iocObj=getBean(ioc.getName());
> setMethod.invoke(obj, iocObj);
> > continue;
}

if (checkSingle(iocObj)) {

> autoObjMap.put(iocObj.getClass().getName(),
> > setObject);
}
checkSingle用来判断是否用了单例注解
public boolean checkSingle(Object obj) {

> SingleTon single = obj.getClass().getAnnotation(SingleTon .class);
> if (single == null) {
> > return false;

> }
> > return true;
}

OK，就这么简单。
上面的注解注入仅仅完成了最基本的功能，还不能无缝的增强原有功能，比如现在有个需求：为特定方法做日志处理，怎么办？每个方法都加上logger？显然不是，最理想的就是
可以直接为这个事务处理类提供功能扩展，并且不影响原有代码。代理模式正是为此类问题而生的。


代理模式
伟大的Spring是设计模式运用的最佳实践，包含工厂模式，代理模式，策略模式，单例模式等等。
而代理模式的运用更是让spring框架充满活力，
毫不夸张的说，正是代理模式的运用让spring看起来无比强大。spring提供两种方式实现代理：jdk动态代理，CGLIB包。
代理模式有这样几个小概念：抽象接口，代理实现类，真实实现类。前者是后面两个的公共接口，是暴露给最终调用者的唯一接口。
首先我们模拟一下代理模式的简单例子。
1，创建抽象接口：
public interface UserService {

> public void save ();
}

2，创建真实实现类，实现UserService接口：
public class UserServiceImpl implements UserService {

> @Override
> public void save() {
> > System. out.println( "插入用户");

> }

}
3，创建代理实现类，实现UserService接口：
public class UserServiceProxy implements UserService {

> private UserService userService;
> public UserServiceProxy(){
> > //创建真实实现类
> > userService= new UserServiceImpl();

> }
> > @Override
> > public void save() {
> > > System. out.println( "事务开始");
> > > > userService.save();

> > > System. out.println( "事务提交");

> > }

}

4,创建客户端测试代码：
public class ClientMain {



> public static void main(String[.md](.md) args) {
> > UserService userService= new UserServiceProxy();
> > userService.save();

> }

}
最终输出：

事务开始
插入用户
事务提交。


当调用save方法时，会调用最终实现类的save方法，并且让你有机会在方法前后进行代码增强。
想做出通用点的代理实现 显然上面的用法是不够的。为了重现代码改造的过程，我先稍微改动一下上面的代码，
UserServiceProxy类添加一个构造方法：
public UserServiceProxy(String realclass){
> try {
> > userService=(UserService)Class. forName(realclass).newInstance();
> > } catch (Exception e) {
> > > e.printStackTrace();

> > }

> }
调用代码改为：
UserService userService= new UserServiceProxy("com.javapatterns.proxy.mytest.UserServiceImpl" );
userService.save();

新加的构造方法看起来像一个工厂，外面传入不同实现类，都可以做代理。但是这样有两个明显的缺点：
1，代理工厂需要实现所有接口方法，当方法很多时，你会感到很毛的。
2，当你需要为另一个接口做代理时，需要重新写一个Proxy，并且工厂代码一句也不能少。
你要知道我们面对的是刁钻的程序员们，他们总是不能忍受编写冗余的代码。

从JDK1.3开始，java提供了InvocationHandler接口和Proxy类，用于创建动态代理。
jdk动态代理的强制条件：实现类必须实现一个或多个接口（CGLIB包就没有此限制）
InvocationHandler只提供一个方法
public Object invoke (Object proxy, Method method, Object[.md](.md) args)。
当抽象接口调用方法时，会自动调用invoke里面的方法，给我们机会diy任何附加功能。
为了把工厂代码隐藏起来，并且能够很方便的扩展，我们需要设计一个代理工厂，这个工厂的具体设计目标是：
1，提供统一的工厂方法生成代理对象，比如通过传入原始类，或者其他参数，让工厂自动创造出代理类。
2，扩展不同的代理工厂时，可以很方便的提供加工代码。
一般做法是：
1，定义工厂公共接口，继承InvocationHandler接口
/
  * 代理工厂总接口
  * 所有自定义代理工厂必须实现此接口
  * @author 杜云飞
  * 
  * 
public interface ProxyFactory extends InvocationHandler {
> > public Object factory(Object targetObject,Object params);
> > public Object getTargetObject();
> > public Object getParams();
}

ProxyFactory提供工厂方法factory，也继承了调用方法invoke。表明实现此接口可以完成创造代理类，并添加代理功能，
params是为了满足有些特殊的传参需求。
2,提供一个默认的代理工厂实现，最好满足大部分需求，再次看到常用的缺省模式。
/
  * 默认的代理工厂，假如可以扩展，继承之，实现自己的invoke方法即可
  * @author duyunfei
  * 
  * 
public abstract class DefaultProxyFactory implements ProxyFactory {


> private Object params;

> private Object targetObject;

> public Object factory(Object targetObject,Object params) {
> > this. targetObject=targetObject;
> > this. params=params;


> Class cls = targetObject.getClass();
> //用set的原因是，接口列表不能重复
> Set

&lt;Class&gt;

 listInterfaces =new HashSet

&lt;Class&gt;

();
> Class[.md](.md) selfInterfaces=cls.getInterfaces();
> > for(Class inter:selfInterfaces){
> > > listInterfaces.add(inter);

> }
> Class superClass=cls.getSuperclass();
> > while(!superClass.getName().equals( "java.lang.Object")){
> > > selfInterfaces=superClass.getInterfaces();
> > > > for(Class inter:selfInterfaces){
> > > > > listInterfaces.add(inter);

> > > }
> > > superClass=superClass.getSuperclass();

> }

> selfInterfaces=listInterfaces.toArray(selfInterfaces);

> return Proxy.newProxyInstance(cls.getClassLoader(),
> > selfInterfaces, this);

> }


> public Object getTargetObject() {
> > return targetObject;

> }


> public Object getParams() {
> > return params;

> }

}
这里有几点值得注意，首先把此类设成abstract是因为，工厂方法factory是所有工厂的共通代码，再没有抽象的必要，所以实现之，而从ProxyFactory中获得的
invoke方法是对每个工厂都需要有不同的实现，所以继续抽象之。
另外，Factory方法中的代码按常理说，可以直接用下面的几句话搞定
Class cls = targetObject.getClass();
Proxy.newProxyInstance(cls.getClassLoader(),
cls.getInterfaces(), this);
但是我这为了保证 即使出现一些类的层级比较复杂的情况下也能很好的生成代理类，把类的父类以上接口都纳入进来。
3，通过继承DefaultProxyFactory,实现自己的代理工厂。接下来我们一起实现一个日志处理的代理。
这个代理实现的功能是，在被代理方法前执行相应的日志输出，通过一个参数配置受控方法。
首先创建DaoLogProxy继承DefaultProxyFactory：
public class DaoLogProxy extends DefaultProxyFactory {
> private static Logger log = Logger. getLogger("");

> public Object invoke(Object proxy, Method method, Object[.md](.md) args)
> > throws Throwable {
> > String params=(String) this.getParams();
> > String[.md](.md) param=params.split( ":");
> > String[.md](.md) includeMethods=param[1](1.md).split( ",");
> > boolean isInclude= false;
> > for(String inmethod:includeMethods){
> > > if(method.getName().equals(inmethod)){
> > > > isInclude= true;

> > > }

> > }
> > Object returnValue= null;
> > if(isInclude){
> > > //是属于包含的方法
> > > String showargs= "";
> > > > for(Object arg:args){
> > > > > showargs=showargs+arg+ "  ";

> > > }
> > > log .info("执行"+ this.getTargetObject().getClass()+ "的"+method.getName()+"方法，参数为" +showargs);
> > > > returnValue=method.invoke( this.getTargetObject(), args);

> > } else{
> > > returnValue=method.invoke( this.getTargetObject(), args);

> > }


> return returnValue;
> }

}

，这里我们通过自己的逻辑判断拦截方法。
然后写测试代码：
> public static void main(String[.md](.md) args) {
> > List

&lt;String&gt;

 list=(List

&lt;String&gt;

) new DaoLogProxy().factory(new ArrayList

&lt;String&gt;

(),"includeMethods:add,get" );
> > list.add( "test0");
> > list.get(0);
> > list.remove(0);

> }

非常方便，控制台显示如下信息表示成功：
2012-8-21 16:44:45 java.util.logging.LogManager$RootLogger log
信息: 执行class java.util.ArrayList的add方法，参数为test0
2012-8-21 16:44:45 java.util.logging.LogManager$RootLogger log
信息: 执行class java.util.ArrayList的get方法，参数为0

假如你是客户端程序员，你期望怎样的代理配置？
我的期望是：本框架为注入类提供一个注解，配置代理工厂，让容器读取注解信息，调用工厂代码生成代理类，然后注入，这样我的客户端接口调用
的任何方法都会被自己扩展的代理类拦截。
好吧，新建Proxy注解，
/
  * 通过此注解 可以为任意类创建jdk代理
  * @author duyf
  * 
  * 
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE )
public @interface Proxy {
> > /*** 必须指定自定义代理工厂 并且此工厂必须是ProxyFactory的子类
      * 
> > public Class proxyFactoryClass ();**


> /*** 传递任意参数 让自定义代理工厂自行处理
    * 
> > public String params() default "";**

}
注解的目标“客户”是类（Type）。proxyFactoryClass强制规定提供的代理工厂实现ProxyFactory接口，这里我们可以继承DefaultProxyFactory就ok了。
打开容器类BeanContainer,加上Proxy处理代码
Proxy proxy = iocObj.getClass().getAnnotation(Proxy .class);
if (proxy != null ) {

> Object proxyFactoryClass = proxy.proxyFactoryClass().newInstance();
> String params=proxy.params();
> setObject = setProxyObject(proxyFactoryClass, setObject,params);
}

public Object setProxyObject(Object proxyFactoryClass, Object obj,Object params) {
> checkProxy(proxyFactoryClass);
> ProxyFactory pf = (ProxyFactory) proxyFactoryClass;
> > return pf.factory(obj,params);
}

public void checkProxy(Object proxyFactoryClass) {

> if (!(proxyFactoryClass instanceof ProxyFactory)) {
> > throw new RuntimeException( "所提供的代理类[" + proxyFactoryClass
> > > + "]必须实现ProxyFactory接口" );

> }
}
最后直接注入setObject到目标对象就行了。
测试一下：
1，创建一个测试接口UserService,UserServiceImpl实现接口，并用@Proxy注释
@Proxy(proxyFactoryClass=DaoLogProxy.class,params= "includeMethods:save,update" )
2，在action里面配置自动注入：
@Autowired(iocClass = UserServiceImpl.class)
private UserService userService;
并给出setter方法。
3，配置Action，调用save方法，会打印出
执行class $Proxy9的save方法。
有了Proxy注解，以后任何人想扩展自己的代理将易如反掌
具体实现代码请参考源码。
















