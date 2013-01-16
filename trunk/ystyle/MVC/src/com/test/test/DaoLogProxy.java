package com.test.test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.love.ProxyFactory.DefaultProxyFactory;

public class DaoLogProxy extends DefaultProxyFactory {
	private Log logger = LogFactory.getLog(DaoLogProxy.class);
	
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
        try{
        	if(isInclude){
            	//是属于包含的方法
            	String showargs="";
            	for(Object arg:args){
            		showargs=showargs+arg+"  ";
            	}
            	logger.debug("执行"+this.getTargetObject().getClass()+"的"+method.getName()+"方法，参数为"+showargs+proxy);
            	returnValue=method.invoke(this.getTargetObject(), args);        	
            }else{
            	returnValue=method.invoke(this.getTargetObject(), args);
            }
        }catch(Exception ex){
        	Exception e0=new Exception();
        	e0.initCause(ex);
        	throw e0;
        }
        
		
		return returnValue;
	}
	public static void main(String[] args) {
		List<String> list=(List<String>)new DaoLogProxy().factory(new ArrayList<String>(),"includeMethods:add,get");
		list.add("test0");
		list.get(0);
		list.remove(0);
	}

}
