package zhuboss.pc2server.sengine.webserver.pages;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.Headers;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import zhuboss.pc2server.common.JavaUtil;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;

public class PageDispatcher {
	Logger logger = LoggerFactory.getLogger(PageDispatcher.class);
	
	Configuration cfg = new Configuration();
	StringTemplateLoader templateLoader;
	public PageDispatcher(){
		ResourcePatternResolver resolver = (ResourcePatternResolver) new PathMatchingResourcePatternResolver();  
		Resource[] resources= null;
		try {
			resources=resolver.getResources("classpath*:zhuboss/pc2server/sengine/pages/*.ftl");
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new RuntimeException(e);
		}  
		templateLoader = new StringTemplateLoader();
		for(int i=0;i<resources.length;i++){
			String ftl = "";
			try {
				ftl = JavaUtil.InputStreamTOString(resources[i].getInputStream(), "UTF-8");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			templateLoader.putTemplate(resources[i].getFilename(), ftl);
		}
		cfg.setTemplateLoader(templateLoader);
		cfg.setOutputEncoding("UTF-8");
	}
	
	public DefaultFullHttpResponse dispatch(HttpResponseStatus status,Headers<CharSequence> headers){
		DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
		if(headers!=null){
			response.headers().set(headers);
		}
		response.headers().add("Server","pc5s");
		response.headers().add("Content-Length", "0");
		return response;
	}
	
	public DefaultFullHttpResponse buildHttpResponse(ChannelHandlerContext ctx,HttpResponseStatus status,Headers<CharSequence> headers,String ftl,Object data){
		DefaultFullHttpResponse response ;
		if(ftl!=null){
			StringWriter sw = new StringWriter();
			try {
				cfg.getTemplate(ftl).process(data, sw);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				throw new RuntimeException(e);
			} 
			String text = sw.toString();
			ByteBuf  content = ctx.alloc().buffer();
			content.writeBytes(text.getBytes(Charset.forName("UTF-8")));
			response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,content);
			response.headers().add("Content-Type", "text/html; charset=utf-8");
			response.headers().add("Content-Length", content.readableBytes()+"");
		}else{
			response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
			/**
			 * NGINX client不能有效识别HTTP已经结束
			 */
			response.headers().add("Content-Length", "0");
			response.headers().add("Connection","close");
		}
		
		if(headers!=null){
			response.headers().add(headers);
		}
		response.headers().add("Server","pc5s");
		return response;
	}
	
}
