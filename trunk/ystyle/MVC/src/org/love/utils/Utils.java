package org.love.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;

import com.sun.org.apache.commons.beanutils.BeanUtils;

public class Utils {

	private static final String PLACEHOLDER_START = "${";
	public static void copyFile(File source,File des){
		   FileInputStream fis = null;
		   FileOutputStream fos = null;
		   try {
		    fis = new FileInputStream(source);
		    fos = new FileOutputStream(des);

		    byte[] bb = new byte[ (int) source.length()];
		    fis.read(bb);
		    fos.write(bb);

		   } catch (IOException e) {
		    e.printStackTrace();
		   } finally {
		    try {
		     fis.close();
		     fos.close();
		    } catch (IOException e) {
		     e.printStackTrace();
		    }
		   }

	}
	
	public static String join(String[] arr,String delims){
		if(arr==null || arr.length==0){
			return "";
		}
		if(delims==null){
			delims=",";
		}
		String result="";
		for(String s:arr){
			if("".equals(result)){
				result=s;
			}else{
				result=result+delims+s;
			}
		}
		return result;
	}
	
	
	/**
	 * 打印每个对象的值，用来测试
	 */
	public static void printFields(Object obj) {
		Class cls = obj.getClass();
		Field[] fields = cls.getDeclaredFields();
		System.out.println("----------------" + cls.getName()
				+ "---------------");
		try {
			for (Field field : fields) {
				String[] fieldvalue=null;
				if(field.getType().isArray()){
					fieldvalue= BeanUtils.getArrayProperty(obj,field.getName());
				}else{
					fieldvalue=new String[1];
					fieldvalue[0]=BeanUtils.getProperty(obj, field.getName());
				}
				System.out.println(field.getName() + " : " + join(fieldvalue,","));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * 打印列表中每个对象的值，用来测试
	 */

	public static void printListFields(Collection list) {
		for (Object obj : list) {
			System.out
					.println("===============================================");
			if (obj instanceof String) {
				System.out.println(obj);
			} else {
				printFields(obj);
			}
		}
	}
	
	/**
	 * 解析占位符具体操作
	 * @param property
	 * @return
	 */
	public static String resolvePlaceHolder(String property,ReplaceHolder rh) {
		if ( property.indexOf( PLACEHOLDER_START ) < 0 ) {
			return property;
		}
		StringBuffer buff = new StringBuffer();
		char[] chars = property.toCharArray();
		for ( int pos = 0; pos < chars.length; pos++ ) {
			if ( chars[pos] == '$' ) {
				if ( chars[pos+1] == '{' ) {
					String propertyName = "";
					int x = pos + 2;
					for (  ; x < chars.length && chars[x] != '}'; x++ ) {
						propertyName += chars[x];
						if ( x == chars.length - 1 ) {
							throw new IllegalArgumentException( "unmatched placeholder start [" + property + "]" );
						}
					}
					String systemProperty = rh.extract( propertyName );
					buff.append( systemProperty == null ? "" : systemProperty );
					pos = x + 1;
					if ( pos >= chars.length ) {
						break;
					}
				}
			}
			buff.append( chars[pos] );
		}
		String rtn = buff.toString();
		return isEmpty( rtn ) ? null : rtn;
	}
	
	/**
	 * 判断字符串的空(null或者.length=0)
	 * @param string
	 * @return
	 */
	public static boolean isEmpty(String string) {
		return string == null || string.trim().equals("");
	}
}
