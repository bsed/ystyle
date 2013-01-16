package com.test.test;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.love.ProxyFactory.DefaultProxyFactory;

public class TestProxy extends DefaultProxyFactory {

	private Log logger = LogFactory.getLog(TestProxy.class);
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object returnValue=null;
		logger.debug(this.getClass()+"开始"+this.getTargetObject()+"-->"+method);
		returnValue = method.invoke(this.getTargetObject(), args);
		logger.debug(this.getClass()+"结束");
		return returnValue;
	}



}
