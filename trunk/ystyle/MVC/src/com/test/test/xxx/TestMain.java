package com.test.test.xxx;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestMain {

	
	public static void main(String[] args) {
          Result<Date> re1=new GetDate();
          Date d1=re1.get();
          
          Result<String> re2=new GetString();
          String d2=re2.get();
          
          List<String> list=new GetListString().get();
          List<String> list1=new ArrayList<String>();
          Result<List<Date>> r=new GetBean<Date>();
          r.get();
          new GetBean<Date>().get();
          
	}

}
