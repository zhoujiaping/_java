package org.jsirenia.groovy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.ResourceUtils;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
//https://blog.csdn.net/a906998248/article/details/46444967
public class GroovyScriptFileRunner {
	private String filepath = "";//目录
	private GroovyScriptEngine  engine ;
	public GroovyScriptFileRunner(String filepath){
		try {
			this.filepath = ResourceUtils.getFile(filepath).getAbsolutePath();
			this.engine =  new GroovyScriptEngine(this.filepath);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public Object run(String scriptName,Map<String,Object> variables){
		//groovy代码文件名
		Object res;
		try {
			res = engine.run(scriptName, new Binding(variables));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return res;
	}
	public String getFilepath() {
		return filepath;
	}
	public static void main(String[] args) throws FileNotFoundException{
		Map<String, Object> variables = new HashMap<>();
		String filename = 	"classpath:./";
		Object res = new GroovyScriptFileRunner(filename).run("groovy/Hello.groovy", variables);
		System.out.println(res);
	}
}
