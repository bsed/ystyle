> 第一章  为什么要堆出自己的mvc？

> MVC是一种分层式的开发方式，是前人总结的最佳实践，这里不打算讨论mvc的各种基本概念，但是有必要了解一下在mvc之前，我们是怎样开发java
web 项目的。java web开发的关键词有：jsp,servlet,jdbc等。
> 比如你要做一个表单提交，需要新建servlet，并且在web.xml配置

> 

&lt;servlet &gt;


> > 

&lt;servlet-name &gt;

mainservlet 

&lt;/servlet-name&gt;


> > 

&lt;servlet-class&gt;

org.love.servlet.MainServlet

&lt;/servlet-class&gt;



> 

&lt;servlet-mapping &gt;


> 

&lt;servlet-name &gt;

mainservlet 

&lt;/servlet-name&gt;


> > 

&lt;url-pattern &gt;

/mainservlet

&lt;/url-pattern&gt;



> </servlet-mapping >

> 新建index.jsp，把表单action指向/mainservlet,然后编写MainServlet中的处理代码，最后forward或者redirect到success.jsp页面。ok，很简单，但是，假如有100个这样的请求呢？ 你会发觉你会需要100个这样的配置和跳转代码。

> ssh三大框架彻底解决了这些问题，简单的配置和足够依赖的功能解放了无数javaer，继续详细讲解ssh的各种知识只会让砖头越来越多，看到这篇文章的人已经有相关的经验了,但是也有必要回顾一下大致的开发过程：
    * ,导入jar包（初学者容易冲突）
    * ,编写struts,spring,hibernate配置文件，并且把依赖注入到spring
    * ,配置web.xml
    * ,建立分层的package，建立service，用注解，事务，异常，拦截器，。。。
好吧，随着我们用的越多，知识面越广，越觉得编程是一个简单工作，
在外人看来很牛逼的你其实早已获取通向成功的捷径，然后你越来越飘渺，越来越迷茫，越来越没自信，越来越妄自菲薄，终于你跌落凡间，幡然醒悟：我tm其实就是一码农？
> 当然不是，你只是在ssh的大树下玩的太久，是时候该砍了它，自己栽树了。
> ssh的成功不是偶然，它是集当今所有优秀设计模式为一体的重量组合，但它更是一"堆"优秀设计思想凝聚成的代码。我们在使用它的过程中，有想过他们是怎样实现的么？有想过它为什么非得这么实现？有想过自己设计一套完整的mvc么？答案就在这里。
接下来我们一起看看该怎样实现一个稍微完整的mvc，并逐步探索ssh框架的设计模式和原理。