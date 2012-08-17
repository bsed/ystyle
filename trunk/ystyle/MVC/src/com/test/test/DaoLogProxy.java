package com.test.test;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import org.love.ProxyFactory.DefaultProxyFactory;

public class DaoLogProxy extends DefaultProxyFactory {
	private static Logger log = Logger.getLogger("");
	
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
        String params=(String)this.getParams();
        String[] param=params.split(":");
        String[] includeMethods=param[1].split(",");
        boolean isInclude=false;
        for(String inmethod:includeMethods){
        	if(method.getName().equals(inmethod)){
        		isInclude=true;
        	}
        }
        Object returnValue=null;
        if(isInclude){
        	//是属于包含的方法
        	String showargs="";
        	for(Object arg:args){
        		showargs=showargs+arg+"  ";
        	}
        	log.info("执行"+this.getTargetObject()+"的"+method.getName()+"方法，参数为"+showargs);
        	returnValue=method.invoke(this.getTargetObject(), args);        	
        }else{
        	returnValue=method.invoke(this.getTargetObject(), args);
        }
		
		return returnValue;
	}

}
