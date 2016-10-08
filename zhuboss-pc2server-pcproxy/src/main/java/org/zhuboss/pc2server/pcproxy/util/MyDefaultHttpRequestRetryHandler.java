package org.zhuboss.pc2server.pcproxy.util;

import java.io.IOException;

import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyDefaultHttpRequestRetryHandler extends
		DefaultHttpRequestRetryHandler {
	Logger logger = LoggerFactory.getLogger(MyDefaultHttpRequestRetryHandler.class);
	
	@Override
	public boolean retryRequest(IOException ex, int executionCount,
			HttpContext context) {
		if(context.getAttribute("http.request")!=null &&  context instanceof BasicHttpContext){
			logger.info("requestLine:"+((org.apache.http.impl.client.RequestWrapper)context.getAttribute("http.request")).getRequestLine());
		}
		logger.error("I/O exception ",ex);
		return super.retryRequest(ex, executionCount, context);
	}

}
