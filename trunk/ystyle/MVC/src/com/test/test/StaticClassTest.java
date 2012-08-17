package com.test.test;

public class StaticClassTest {

	{
		System.out.println("我是--动态块");
	}
	static{
		System.out.println("我是static静态块");
	}
	
	public static void test(){
		System.out.println("执行静态方法");
	}
}
