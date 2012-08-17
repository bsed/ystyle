package org.love.create;

import java.sql.Connection;

import org.love.action.BaseAction;
import org.love.db.Session;
import org.love.db.SessionFactory;

public class CreateAction extends BaseAction {

	public String createPojos() {
		String packageName=request.getParameter("packageName");
		String tablename=request.getParameter("tablename");
		String srcdir=request.getParameter("srcdir");
		String superClass=request.getParameter("superClass");
		Session session=SessionFactory.getSession();
		Connection conn=session.getConnection();
		try {
		    CreateService.generateCode(packageName, null,tablename, srcdir, superClass,conn);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			SessionFactory.closeSession(session);
		}
		return "success";
	}

}
