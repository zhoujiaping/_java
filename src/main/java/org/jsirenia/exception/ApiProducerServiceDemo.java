package org.jsirenia.exception;
/**
 * api提供者(Demo模块)的业务异常处理
 */
public class ApiProducerServiceDemo {
	public String doService(){
		throw new ServiceException(DemoExceptionCode.USER_NOT_FOUND,"id为1的用户未找到");
	}
}