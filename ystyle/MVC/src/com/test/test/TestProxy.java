package com.test.test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.love.ProxyFactory.ProxyFactory;

public class TestProxy implements ProxyFactory {

	private Object targetObject;
	private Object params;
	public Object factory(Object targetObject, Object params) {
		this.targetObject=targetObject;
		this.params=params;
		Class cls=this.targetObject.getClass();
		return Proxy.newProxyInstance(cls.getClassLoader(),
				cls.getInterfaces(),this);
	}

	public Object getParams() {		
		return params;
	}

	public Object getTargetObject() {
		return targetObject;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		
	    System.out.println("自定义代理工厂 测试前 参数是"+params);
	    Object returnValue=method.invoke(this.getTargetObject(),args);
	    System.out.println("自定义代理工厂 测试后");
		return returnValue;
	}

}
