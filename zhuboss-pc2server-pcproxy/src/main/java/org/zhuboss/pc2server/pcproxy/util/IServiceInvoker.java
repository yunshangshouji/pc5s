package org.zhuboss.pc2server.pcproxy.util;

import java.util.List;

import org.apache.http.NameValuePair;
public interface IServiceInvoker {
	
	<T> T get(String path, Class<T> cls) ;
	
	<T> List<T> getList(String path, Class<T> cls) ;
	
	<T> T get(String path, List<NameValuePair> parameters, Class<T> cls);
	
	<T> List<T> getList(String path, List<NameValuePair> parameters, Class<T> cls);
	
	String post(String path, List<NameValuePair> parameters, Object obj) ;

	String put(String path, Object obj) ;
	
	String put(String path, String content) ;

	String delete(String path) ;
	
	String delete(String path, List<NameValuePair> parameters) ;

}
