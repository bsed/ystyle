package com.test.test.xxx;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.love.db.Session;
import org.love.db.SessionFactory;

import com.test.vo.Userinfo;

public class TestMain {

	
	public static void main(String[] args) {
		ExecutorService pool=Executors.newFixedThreadPool(3);
		Runnable t1 = new Runnable(){

			@Override
			public void run() {
				try {
					for(int i=0;i<1000;i++){
						Session session=SessionFactory.getSession();
						List<Userinfo> list=session.query("select * from userinfo u where u.toid = ?",Userinfo.class,"256");
						SessionFactory.closeSession(session);	
						Thread.sleep(1000);
					
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			
			}
			
		};
		
		Runnable t2 = new Runnable(){

			@Override
			public void run() {
				try {
					for(int i=0;i<1000;i++){
						Session session=SessionFactory.getSession();
						List<Userinfo> list=session.query("select * from userinfo u where u.toid = ?",Userinfo.class,"256");
						SessionFactory.closeSession(session);	
						Thread.sleep(1000);
					
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			
			}
			
		};
		
		Runnable t3 = new Runnable(){

			@Override
			public void run() {
				try {
					for(int i=0;i<1000;i++){
						Session session=SessionFactory.getSession();
						List<Userinfo> list=session.query("select * from userinfo u where u.toid = ?",Userinfo.class,"256");
						SessionFactory.closeSession(session);	
						Thread.sleep(1000);
					
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			
			}
			
		};
		
		Runnable t4 = new Runnable(){

			@Override
			public void run() {
				try {
					for(int i=0;i<1000;i++){
						Session session=SessionFactory.getSession();
						List<Userinfo> list=session.query("select * from userinfo u where u.toid = ?",Userinfo.class,"256");
						SessionFactory.closeSession(session);	
						Thread.sleep(1000);
					
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			
			}
			
		};
		
		Runnable t5 = new Runnable(){

			@Override
			public void run() {
				try {
					for(int i=0;i<1000;i++){
						Session session=SessionFactory.getSession();
						List<Userinfo> list=session.query("select * from userinfo u where u.toid = ?",Userinfo.class,"256");
						SessionFactory.closeSession(session);	
						Thread.sleep(1000);
					
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			
			}
			
		};
		
		// 将线程放入池中进行执行
		pool.execute(t1);
		pool.execute(t2);
		pool.execute(t3);
		pool.execute(t4);
		pool.execute(t5);
		// 关闭线程池
		pool.shutdown();

		
		
		
	}
	
	

}
