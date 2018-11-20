package org.jsirenia.bean;

import java.util.HashMap;
import java.util.Map;

import groovy.lang.GroovyObject;

public class GroovyScriptHelper {
	/**
	 * 
	 * @param classname
	 *            比如"hellogroovy.Hello"
	 * @return
	 */
	private static Map<String,GroovyObject> groovyClassCache = new HashMap<>();
	public static GroovyObject loadGroovyScript(String classname) throws Exception {
		return loadGroovyScript(classname,false);
	}
	/**
	 * 
	 * @param classname
	 * @param useCache 使用缓存
	 * @return
	 * @throws Exception
	 */
	public static GroovyObject loadGroovyScript(String classname,boolean useCache) throws Exception {
		if(useCache){
			GroovyObject o = groovyClassCache.get(classname);
			if(o!=null){
				return o;
			}
		}
		GroovyObject groovyObject = (GroovyObject) GroovyScriptHelper.class.getClassLoader().loadClass(classname)
				.newInstance();
		groovyClassCache.put(classname, groovyObject);
		return groovyObject;
	}
	public static Object invokeMethod(GroovyObject groovyObject, String method, Object args) throws Exception {
		Object res = groovyObject.invokeMethod(method, args);
		return res;
	}
	public static void main(String[] args) {
	}
}
