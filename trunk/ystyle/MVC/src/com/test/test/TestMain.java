package com.test.test;

import java.math.BigInteger;
import java.util.Random;

public class TestMain {

	public static void main(String[] args) {
		/*
		 * 此时模拟action启动，在主控制servlet接收到请求的时候， 会初始化一个action，此时注入action所需要的service //
		 */
		// TestAction tm = new TestAction();
		//
		// //得到action的Class环境
		// Class testCls = tm.getClass();
		//		
		// /*
		// * 将action所有用到@Autowired这个注解的域进行解析并装配注入
		// */
		// AnnotationHelp.AutowiredSet(tm,testCls);
		//		
		// tm.test1();
		for (int i = 0; i < 10; i++) {
			System.out.println(generateGUID().length());
		}

	}

	private static final Random RANDOM = new Random();

	public static String generateGUID() {
		return new BigInteger(165, RANDOM).toString(36).toUpperCase();
	}

}
