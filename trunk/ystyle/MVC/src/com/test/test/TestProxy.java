package com.test.test;

import java.lang.reflect.Method;

import org.love.ProxyFactory.DefaultProxyFactory;

public class TestProxy extends DefaultProxyFactory {

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object returnValue=null;
		System.out.println(this.getClass()+"开始");
		returnValue = method.invoke(this.getTargetObject(), args);
		System.out.println(this.getClass()+"结束");
		return returnValue;
	}



}
