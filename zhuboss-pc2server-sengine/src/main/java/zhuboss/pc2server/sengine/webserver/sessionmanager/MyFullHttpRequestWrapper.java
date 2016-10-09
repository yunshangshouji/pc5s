package zhuboss.pc2server.sengine.webserver.sessionmanager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import javax.servlet.http.Cookie;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.CookieCutter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zhuboss.framework.spring.CustomizedPropertyPlaceholderConfigurer;
import zhuboss.pc2server.sengine.StartSEngine;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

public class MyFullHttpRequestWrapper implements FullHttpRequest  {
	Logger logger = LoggerFactory.getLogger(MyFullHttpRequestWrapper.class);
	
	static final CharSequence HOST_NAME = "Host";
	
	FullHttpRequest fullHttpRequest ;
	
	public MyFullHttpRequestWrapper(FullHttpRequest fullHttpRequest){
		this.fullHttpRequest = fullHttpRequest;
	}
	public FullHttpRequest getFullHttpRequest(){
		return this.fullHttpRequest;
	}
	
	private String userDomain = null;
	private Map<String,String> paramMap = null;
	private CookieCutter _cookies;
	private boolean _cookiesExtracted = false;
	
	/**
	 * 
	 * @return 取域名失败返回null
	 */
	public String getUserDomain(){
		if(userDomain == null){
			String host = fullHttpRequest.headers().get(HOST_NAME).toString().toLowerCase();
			try{
				String rootDomain = (String)StartSEngine.getApplicationContext().getBean(CustomizedPropertyPlaceholderConfigurer.class).getContextProperty("sengine.domain");
			userDomain = host.substring(0,host.indexOf(rootDomain));
			}catch(Exception e){
				logger.warn(e.getMessage() +":"+ host);
				return null;
			}
		}
		return userDomain;
	}
	
	public String getPath(){
		int queryStringIndex = fullHttpRequest.uri().indexOf("?");
		if(queryStringIndex == -1){
			return fullHttpRequest.uri();
		}else{
			return fullHttpRequest.uri().substring(0, queryStringIndex);
		}
	}
	
	/**
	 * 返回未decode的字符串
	 * @return
	 */
	public String getRawQuery(){
		//new URI() not error /state?path={[{.}]}
		int queryStringIndex = fullHttpRequest.uri().indexOf("?");
		if(queryStringIndex == -1){
			return "";
		}else{
			return fullHttpRequest.uri().substring(queryStringIndex+1);
		}
	}
	
	public Map<String,String> getQueryParams(){
		if(paramMap == null){
			paramMap = new HashMap<String,String>();
			List<NameValuePair> nvpList;
			try {
				nvpList = URLEncodedUtils.parse(new URI(fullHttpRequest.uri()), "UTF-8");
			} catch (URISyntaxException e) {
				logger.warn(e.getMessage()+":"+fullHttpRequest.uri());
				return null;
			}
			for(NameValuePair nvp : nvpList){
				paramMap.put(nvp.getName(), nvp.getValue());
			}
		}
		return paramMap;
	}
	
	public Cookie[] getCookies()
    {
        if (_cookiesExtracted)
        {
            if (_cookies == null || _cookies.getCookies().length == 0)
                return null;
            
            return _cookies.getCookies();
        }

        _cookiesExtracted = true;

        Enumeration<?> enm = this.getValues(HttpHeader.COOKIE.toString());

        // Handle no cookies
        if (enm != null)
        {
            if (_cookies == null)
                _cookies = new CookieCutter();

            while (enm.hasMoreElements())
            {
                String c = (String)enm.nextElement();
                _cookies.addCookieField(c);
            }
        }

        //Javadoc for Request.getCookies() stipulates null for no cookies
        if (_cookies == null || _cookies.getCookies().length == 0)
            return null;
        
        return _cookies.getCookies();
    }
	
	 private Enumeration<String> getValues(final String name)
	    {
		 final Iterator<Entry<CharSequence, CharSequence>> iterator = fullHttpRequest.headers().iterator();
		 while(iterator.hasNext()){
			 final Entry<CharSequence, CharSequence> entry = iterator.next();
			 if (entry.getKey().toString().equalsIgnoreCase(name) && entry.getValue()!=null){
	                return new Enumeration<String>()
	                {
	                	Entry<CharSequence, CharSequence> next = entry;

	                    @Override
	                    public boolean hasMoreElements()
	                    {
	                        return next != null;
	                    }

	                    @Override
	                    public String nextElement() throws NoSuchElementException
	                    {
	                        if (hasMoreElements())
	                        {
	                        	String result = next.getValue().toString();
	                        	next = null;
	                        	while(iterator.hasNext()){
	                        		Entry<CharSequence, CharSequence> entry = iterator.next();
	                        		if (entry.getKey().toString().equalsIgnoreCase(name) && entry.getValue()!=null){
	                        			next = entry;
	                        		}
	                        	}
	                            return result;
	                        }
	                        throw new NoSuchElementException();
	                    }

	                };
	            
			 }
		 }

	        List<String> empty=Collections.emptyList();
	        return Collections.enumeration(empty);
	    }
	
	////////////////////////////////////////////
	@Override
	public HttpHeaders trailingHeaders() {
		return fullHttpRequest.trailingHeaders();
	}

	@Override
	public ByteBuf content() {
		
		return fullHttpRequest.content();
	}

	@Override
	public int refCnt() {
		
		return fullHttpRequest.refCnt();
	}

	@Override
	public FullHttpRequest retain() {
		
		return fullHttpRequest.retain();
	}

	@Override
	public FullHttpRequest retain(int increment) {
		
		return fullHttpRequest.retain(increment);
	}

	@Override
	public FullHttpRequest touch() {
		
		return fullHttpRequest.touch();
	}

	@Override
	public FullHttpRequest touch(Object hint) {
		
		return fullHttpRequest.touch(hint);
	}

	@Override
	public boolean release() {
		
		return fullHttpRequest.release();
	}

	@Override
	public boolean release(int decrement) {
		
		return fullHttpRequest.release(decrement);
	}

	@Override
	public FullHttpRequest setProtocolVersion(HttpVersion version) {
		
		return fullHttpRequest.setProtocolVersion(version);
	}

	@Override
	public FullHttpRequest setMethod(HttpMethod method) {
		
		return fullHttpRequest.setMethod(method);
	}

	@Override
	public FullHttpRequest setUri(String uri) {
		
		return fullHttpRequest.setUri(uri);
	}

	@Override
	public FullHttpRequest copy(ByteBuf newContent) {
		
		return fullHttpRequest.copy(newContent);
	}

	@Override
	public FullHttpRequest copy() {
		
		return fullHttpRequest.copy();
	}

	@Override
	public FullHttpRequest duplicate() {
		
		return fullHttpRequest.duplicate();
	}

	@Override
	public int hashCode() {
		
		return fullHttpRequest.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		
		return fullHttpRequest.equals(o);
	}

	@Override
	public String toString() {
		
		return fullHttpRequest.toString();
	}

	@Override
	public HttpMethod method() {
		
		return fullHttpRequest.method();
	}

	@Override
	public String uri() {
		
		return fullHttpRequest.uri();
	}

	@Override
	public HttpHeaders headers() {
		
		return fullHttpRequest.headers();
	}

	@Override
	public HttpVersion protocolVersion() {
		
		return fullHttpRequest.protocolVersion();
	}

	@Override
	public DecoderResult decoderResult() {
		
		return fullHttpRequest.decoderResult();
	}

	@Override
	public void setDecoderResult(DecoderResult decoderResult) {
		
		fullHttpRequest.setDecoderResult(decoderResult);
	}

}
