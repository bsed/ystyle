package com.test.test.xxx;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.love.db.Session;
import org.love.db.SessionFactory;

import com.test.vo.Userinfo;

public class TestMain {

	
	public static void main(String[] args) {
		ExecutorService pool=Executors.newFixedThreadPool(101);
		
		for(int i=0;i<100;i++){
			Runnable t1 = new Runnable(){

				@Override
				public void run() {
					try {
						Session session=SessionFactory.getSession();
						List<Userinfo> list=session.query("select * from userinfo u where u.toid = ?",Userinfo.class,"256");
						//Thread.sleep(1000);
						SessionFactory.closeSession(session);
					} catch (Exception e) {
						e.printStackTrace();
					}
				
				}
				
			};
			
			// 将线程放入池中进行执行
			pool.execute(t1);
		}
		
		System.out.println("准备结束");
		// 关闭线程池
		pool.shutdown();

		
		
		
	}
	
	

}
