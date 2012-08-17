package org.love.ProxyFactory;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 默认的代理工厂，假如可以扩展，继承之，实现自己的invoke方法即可
 * @author duyunfei
 *
 */
public abstract class DefaultProxyFactory implements ProxyFactory {

	private Object params;
	
	private Object targetObject;
	
	public Object factory(Object targetObject,Object params) {
		this.targetObject=targetObject;
		this.params=params;
		
		Class cls = targetObject.getClass();
		List<Class> listInterfaces=new ArrayList<Class>();
		Class[] selfInterfaces=cls.getInterfaces();
		for(Class inter:selfInterfaces){
			listInterfaces.add(inter);
		}
		Class superClass=cls.getSuperclass();
		while(!superClass.getName().equals("java.lang.Object")){
			selfInterfaces=superClass.getInterfaces();
			for(Class inter:selfInterfaces){
				listInterfaces.add(inter);
			}	
			superClass=superClass.getSuperclass();
		}
		
		selfInterfaces=listInterfaces.toArray(selfInterfaces);

		return Proxy.newProxyInstance(cls.getClassLoader(),
				selfInterfaces,this);
	}


	public Object getTargetObject() {
		return targetObject;
	}


	public Object getParams() {
		return params;
	}


}
