package org.jsirenia.http;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jsirenia.json.JSONUtil;
import org.jsirenia.reflect.MethodUtil;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.alibaba.fastjson.JSONArray;

/**
 * 将本地方法暴露为http接口
 * 
 * 收到http请求，获取 接口名、接口方法 参数
 * 不支持方法重载
 * 不支持泛型方法
 * 读取暴露hessian接口的配置（beans-hessian.xml），获取暴露的接口
 * 从spring上下文获取注入的接口实现
 * 获取方法
 * 执行方法
 * 返回结果
 */
@Controller
@RequestMapping("/http-exporter/{className}/{methodName}")
public class HttpExporter{
	@RequestMapping
	@ResponseBody
	public Object callHessianApi(@RequestBody String body,
			@PathVariable("className")String className,
			@PathVariable("methodName")String methodName,
			HttpServletRequest req) throws ClassNotFoundException, BeansException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(req.getSession(true).getServletContext());
		Class<?> type = Class.forName(className);
		Object target = context.getBean(type);
		if(target==null){
			throw new RuntimeException("没有找到"+className+"的实例");
		}
		Method[] methods = type.getDeclaredMethods();
		Method method = null;
		if(methods!=null){
			for(Method m : methods){
				if(m.getName().equals(methodName)){
					method = m;
					break;
				}
			}
		}
		if(method==null){
			throw new RuntimeException("没有找到方法"+methodName);
		}
		Class<?>[] parameterTypes = method.getParameterTypes();
		Type[] genericParameterTypes = method.getGenericParameterTypes();
		List<Object> argList = JSONArray.parseArray(body);
		Object[] args = new Object[parameterTypes.length];
		for(int i=0;i<parameterTypes.length;i++){
			args[i] = JSONUtil.toJavaObject(argList.get(i), parameterTypes[i],genericParameterTypes[i]);
		}
		Object res = method.invoke(target, args );
		return res;
	}
	public static void main(String[] args0) throws Exception{
		File f = ResourceUtils.getFile("classpath:test2.json");
		String body = StreamUtils.copyToString(new FileInputStream(f), Charset.forName("utf-8"));
		Object[] args = MethodUtil.parseArgs("org.jsirenia.http.MyTest","test1",body);
		System.out.println(args);
	}
}