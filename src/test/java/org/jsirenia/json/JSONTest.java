package org.jsirenia.json;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsirenia.reflect.MethodUtil;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;

public class JSONTest {
	private String userClass = User.class.getName();
    @Test
    public void testToJSONStringWithType() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
    	Object[] users = new Object[2];
        users[0] = new User("john");
        users[1] = new User("lucy");
    	String text = JSONUtil.toJSONStringWithType(users);
    	System.out.println(text);
    	Class<?> clazz = new Object[]{}.getClass();
        Object res = JSONUtil.parseObjectWithType(text, clazz);
        System.out.println(res);
        /*
         [{"@type":"org.jsirenia.json.User","name":"lucy"},null]
		[Ljava.lang.Object;@6bf256fa 
         */
    }
    private void print(Object o){
		String s = JSON.toJSONString(o,SerializerFeature.WriteClassName);
		System.out.println(s);
    }
    /**
     */
    @Test
    public void testPrimitive() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
    	Object[] args = new Object[]{"hello",100,'z'};
    	String text = JSON.toJSONString(args);
    	Method method = MethodUtil.getMethodByName(userClass, "testPrimitive");
    	Object[] res = MethodUtil.parseJSONForArgs(method, text);
    	Object obj = method.invoke(new User(), res);
    	print(obj);
    }
    /**
     * class 带泛型的时候，没有泛型信息fastjson也无能为力
     */
    @Test
    public void testMap() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
    	Map<String,User> map = new HashMap<>();
    	map.put("john", new User("john"));
    	String str = "str";
    	Object[] args = new Object[]{map,str};
    	String text = JSON.toJSONString(args);
    	Method method = MethodUtil.getMethodByName(userClass, "testMap");
    	Object[] res = MethodUtil.parseJSONForArgs(method, text);
    	Object obj = method.invoke(new User(), res);
    	print(obj);
    }
    /**
     * type 传class实际上会调用传type的方法,Class是Type的子类型
     */
    @Test
    public void testMapNest() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
    	Map<String,Res<List<User>>> map = new HashMap<>();
    	map.put("res", new Res(Lists.asList(new User("john"), new User[]{new User("lucy")})));
    	String str = "str";
    	Object[] args = new Object[]{map,str};
    	String text = JSON.toJSONString(args);
    	Method method = MethodUtil.getMethodByName(userClass, "testMapNest");
    	Object[] res = MethodUtil.parseJSONForArgs(method, text);
    	Object obj = method.invoke(new User(), res);
    	print(obj);
    }
    /**
     */
    @Test
    public void testGenericNest() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
    	Res res =  new Res(Lists.asList(new User("john"), new User[]{new User("lucy")}));
    	Object[] args = new Object[]{res};
    	String text = JSON.toJSONString(args);
    	Method method = MethodUtil.getMethodByName(userClass, "testGenericNest");
    	Object[] arr = MethodUtil.parseJSONForArgs(method, text);
    	Object obj = method.invoke(new User(), arr);
    	print(obj);
    }
    @Test
    public void testGenericArray() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
    	Map<String,Res<List<User>>> map = new HashMap<>();
    	map.put("res", new Res(Lists.asList(new User("john"), new User[]{new User("lucy")})));
    	Object[] args = new Object[]{new Map[]{map}};
    	String text = JSON.toJSONString(args);
    	Method method = MethodUtil.getMethodByName(userClass, "testGenericArray");
    	Object[] res = MethodUtil.parseJSONForArgs(method, text);
    	Object obj = method.invoke(new User(), res);
    	print(obj);
    }
    @Test
    public void testGenericArrayArray() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
    	Map<String,Res<List<User>>> map = new HashMap<>();
    	map.put("res", new Res(Lists.asList(new User("john"), new User[]{new User("lucy")})));
    	Object[] args = new Object[]{new Map[]{map}};
    	String text = JSON.toJSONString(new Object[]{args});
    	Method method = MethodUtil.getMethodByName(userClass, "testGenericArrayArray");
    	Object[] res = MethodUtil.parseJSONForArgs(method, text);
    	Object obj = method.invoke(new User(), res);
    	print(obj);
    }
    @Test
    public void testGenericArrayArray2() throws Exception{
    	List<String> list = new ArrayList<>();
    	list.add("robin");
    	Object[] args = new Object[]{new List[][]{new List[]{list}}};
    	String text = JSON.toJSONString(args);
    	Class<?> clazz = User.class;
    	Method method = clazz.getMethod("testGenericArrayArray2", List[][].class);
    	Type[] types = method.getGenericParameterTypes();
    	List<Object> argList = JSON.parseArray(text, types);
    	Object res = new User().testGenericArrayArray2((List[][]) argList.get(0));
    	System.out.println(res);
    }
    @Test
    public void testArrayArray() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
    	Object[] args = new String[][]{new String[]{"zoro","lufy"}};
    	String text = JSON.toJSONString(new Object[]{args});
    	Method method = MethodUtil.getMethodByName(userClass, "testArrayArray");
    	Object[] res = MethodUtil.parseJSONForArgs(method, text);
    	Object obj = method.invoke(new User(), res);
    	print(obj);
    }
    /**
     */
    @Test
    public void testArray() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
    	Object[] args = new Object[]{new Object[]{new User("john"), new User("lucy")}};
    	String text = JSON.toJSONString(args);
    	Method method = MethodUtil.getMethodByName(userClass, "testArray");
    	Object[] res = MethodUtil.parseJSONForArgs(method, text);
    	Object obj = method.invoke(new User(), res);
    	print(obj);
    }
    @Test
    public void testArrayArray2() throws Exception{
    	Object[] args = new Object[]{new String[][]{new String[]{"zoro","lufy"}}};
    	String text = JSON.toJSONString(args);
    	Class<?> clazz = User.class;
    	Method method = clazz.getMethod("testArrayArray", String[][].class);
    	Type[] types = method.getGenericParameterTypes();
    	List<Object> argList = JSON.parseArray(text, types);
    	Object res = new User().testArrayArray((String[][]) argList.get(0));
    	System.out.println(res);
    }
    @Test
    public void testArray2() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
    	Object[] args = new Object[]{new Object[]{new User("john"), new User("lucy")}};
    	String text = JSON.toJSONString(args);
    	Class<?> clazz = User.class;
    	Method method = clazz.getMethod("testArray", User[].class);
    	Type[] types = method.getGenericParameterTypes();
    	List<Object> argList = JSON.parseArray(text, types);
    	Object res = new User().testArray((User[]) argList.get(0));
    	System.out.println(res);
    }
    /**
     */
    @Test
    public void testEnum() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
    	Object args = new Object[]{UserStatus.VALID};
    	String text = JSON.toJSONString(args);
    	print(text);
    	Method method = MethodUtil.getMethodByName(userClass, "testEnum");
    	Object[] res = MethodUtil.parseJSONForArgs(method, text);
    	Object obj = method.invoke(new User(), res);
    	print(obj);
    }
    @Test
    public void testJSON() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
    	JSONArray jsonArray = new JSONArray();
    	JSONObject json = new JSONObject();
    	json.put("hello", "world");
    	JSONObject json2 = new JSONObject();
    	json2.put("123", "456");
    	jsonArray.add(json2);
    	Object args = new Object[]{json,jsonArray};
    	String text = JSON.toJSONString(args);
    	print(text);
    	Method method = MethodUtil.getMethodByName(userClass, "testJSON");
    	Object[] res = MethodUtil.parseJSONForArgs(method, text);
    	Object obj = method.invoke(new User(), res);
    	print(obj);
    }
}
