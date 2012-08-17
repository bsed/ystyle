package org.love.ProxyFactory;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.love.Annotation.Transactional;
import org.love.db.Session;
import org.love.db.SessionFactory;
import org.love.db.TransactionManager;
import org.love.servlet.MainServlet;

/**
 * service层代理的工厂
 * 
 * @author Administrator
 * 
 */
public class ServiceProxyFactory extends DefaultProxyFactory {
	private Log logger=LogFactory.getLog(ServiceProxyFactory.class);
	
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object returnValue = null;
		/* 处理事务注解 start */
		Class targetClass = this.getTargetObject().getClass();
		Method targetMethod = targetClass.getMethod(method.getName(), method
				.getParameterTypes());
		Transactional tran = targetMethod.getAnnotation(Transactional.class);
		if (tran != null) {
			/* 说明此方法已使用了Transactional的注解，则需要在方法完成后提交事务 */
			logger.debug("开启事务  :"+targetClass.getName()+"."+method.getName());
			Session session=SessionFactory.getSession();
			TransactionManager transactionManager=session.beginTransaction();
			try {
				returnValue = method.invoke(this.getTargetObject(), args);
				transactionManager.commit();
				logger.debug("事务已提交");
			} catch (Exception ex) {
				ex.printStackTrace();
				logger.debug("事务已回滚");
				transactionManager.rollback();
			}
			SessionFactory.closeSession(session);
		}else{
			returnValue = method.invoke(this.getTargetObject(), args);
		}
		/* 处理事务注解 end */

		return returnValue;
	}

}
