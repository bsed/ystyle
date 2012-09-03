package com.test.test.xxx;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;
import org.love.db.ConnectionPool;
import org.love.db.Session;
import org.love.db.SessionFactory;

import com.test.vo.Userinfo;



public class TestMain {

	@Before
	public void initDatabase()  throws Exception {
		ConnectionPool.instance();	
		System.out.println("数据库已经初始化连接");
	}
	@Test
	public void databaseT() throws Exception {
		/*ConnectionPool.instance();
		Thread.sleep(5000);*/
		ExecutorService pool=Executors.newFixedThreadPool(105);
		final CountDownLatch countDownLatch=new CountDownLatch(100);
		long starttime=System.currentTimeMillis();
		for(int i=0;i<100;i++){
			Runnable t1 = new Runnable(){

				@Override
				public void run() {
					try {
						Session session=SessionFactory.getSession();
						List<Userinfo> list=session.query("select * from userinfo u where u.toid = ?",Userinfo.class,"256");
						SessionFactory.closeSession(session);
						//Thread.sleep(1000);
						countDownLatch.countDown();
					} catch (Exception e) {
						e.printStackTrace();
					}
				
				}
				
			};
			
			// 将线程放入池中进行执行
			pool.execute(t1);
		}
		
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// 关闭线程池
		pool.shutdown();
		long endtime=System.currentTimeMillis();
		System.out.println("准备结束"+(endtime-starttime));
		
		

		
		
		
	}
	
	

}
