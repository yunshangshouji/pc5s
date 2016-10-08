package zhuboss.pc2server.sengine.webserver.sessionmanager;

import io.netty.handler.codec.http.ClientCookieEncoder;
import io.netty.handler.codec.http.DefaultCookie;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.ServerCookieEncoder;

import java.util.Date;

import org.eclipse.jetty.http.HttpHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import zhuboss.framework.util.DesInstance;

public class CookieSessionManager implements InitializingBean{
	
	Logger logger = LoggerFactory.getLogger(CookieSessionManager.class);
	
	private static final String COOKIE_SID_NAME = "pc5s.sid";
	
	@Value("#{propertyConfigurer.getContextProperty('sengine.cookie.deskey')}")
	String desKey ;
	DesInstance desInstance;
	final int expireTime = 1000*60*60*12; //12h
	@Override
	public void afterPropertiesSet() throws Exception {
		desInstance = new DesInstance(desKey);
	}
	
	public boolean hasToken(MyFullHttpRequestWrapper req,String userDomain){
		Assert.isTrue(userDomain!=null);
		if(req.headers().get(HttpHeader.COOKIE.toString())==null){
			return false;
		}
		String header = req.headers().get(HttpHeader.COOKIE.toString()).toString();
		if(header==null){
			return false;
		}
		javax.servlet.http.Cookie[]  cookies = req.getCookies();
		String value = null;
		for(javax.servlet.http.Cookie c : cookies ){
			if(c.getName().equals(COOKIE_SID_NAME)){
				value = c.getValue();
				break;
			}
		}
		if(value == null) return false;
		
		String cookieUserDomain ="";
		long cookieDate = 0;
		try{
			String clearText = desInstance.decrypt(value);
			String[] ss = clearText.split(",");
			cookieUserDomain = ss[0];
			cookieDate = Long.parseLong(ss[1]);
		}catch(Exception e){
			logger.warn(e.getMessage());
			return false;
		}
		
		return userDomain.equals(cookieUserDomain) && (new Date().getTime() - cookieDate)<expireTime; //TODO
	}
	
	public void writeCookieToken(HttpResponse res,String userDomain){
		String secretStr = desInstance.encrypt(userDomain+","+new Date().getTime());
		DefaultCookie cookie = new DefaultCookie(COOKIE_SID_NAME, secretStr);
		cookie.setDomain(userDomain+".pc5s.cn"); //注意domain，不设domain，有可能子目录，实际有效是domain
		cookie.setPath("/");
//		cookie.setMaxAge(-1); //如果maxAge为负数，则表示该Cookie仅在本浏览器窗口以及本窗口打开的子窗口内有效，关闭窗口后该Cookie即失效。maxAge为负数的Cookie，为临时性Cookie，不会被持久化，不会被写到Cookie文件中。Cookie信息保存在浏览器内存中，因此关闭浏览器该Cookie就消失了。Cookie默认的maxAge值为-1。
		res.headers().set("Set-Cookie",
				ServerCookieEncoder.encode(cookie )
				);
	}

}
