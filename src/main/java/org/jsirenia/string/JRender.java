package org.jsirenia.string;

import java.util.HashMap;
import java.util.Map;

public class JRender {
	 private String openToken;
	  private String closeToken;
	  private NullValueStrategy nullValueStrategy  = NullValueStrategy.ThrowException;
	  private Map<String,Object> variables;
	public static enum NullValueStrategy{
		ThrowException,ReturnEmptyString,ReturnNullString
	}
	public JRender(String openToken, String closeToken){
		 this.openToken = openToken;
		    this.closeToken = closeToken;
	}
	public JRender(String openToken, String closeToken,NullValueStrategy nullValueStrategy){
		 this.openToken = openToken;
		    this.closeToken = closeToken;
		    this.nullValueStrategy = nullValueStrategy;
	}
	public JRender withVariables(Map<String,Object> variables){
		this.variables = variables;
		return this;
	}
	public JRender withVariable(String key,Object value){
		if(variables==null){
			variables = new HashMap<>();
		}
		variables.put(key, value);
		return this;
	}
	public String render(String text){
		return render(text,variables);
	}
	public String render(String text,Map<String,Object> variables){
		GenericTokenParser tokenParser = new GenericTokenParser(openToken, closeToken, token->{
			Object v = variables.get(token.trim());
			if(v==null ){
				switch(nullValueStrategy){
				case ThrowException:
					throw new RuntimeException("value of "+token+" is null");
				case ReturnEmptyString:
					return "";
				case ReturnNullString:
					return "null";
				default:
					throw new RuntimeException("this is imposible");
				}
			}else{
				return v.toString();
			}
		});
		return tokenParser.parse(text);
	}
	public static void main(String[] args) {
		String text = "i am {name},\r\nhello {yourname}";
		JRender jrender = new JRender("{", "}");
		String res = jrender.withVariable("name", "zhou").withVariable("yourname", "sirenia").render(text);
		System.out.println(res);
	}
}
