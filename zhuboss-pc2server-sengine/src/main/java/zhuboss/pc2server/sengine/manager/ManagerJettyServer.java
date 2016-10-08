package zhuboss.pc2server.sengine.manager;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.CharacterEncodingFilter;

public class ManagerJettyServer implements InitializingBean{
	@Value("#{propertyConfigurer.getContextProperty('sengine.manager.port')}")
	int port;
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	MyHttpServletDispatcher restServlet;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Server server = new Server(port);  
		HandlerList handlerList = new HandlerList();

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
		context.setContextPath("/");

        /**
         * 添加解码过滤器
         */
        CharacterEncodingFilter encodingFilter = new org.springframework.web.filter.CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);
        context.addFilter(new FilterHolder(encodingFilter), "/*", EnumSet.of(DispatcherType.REQUEST));
        
//        context.setInitParameter("resteasy.providers", "com.dooct.framework.rest.provider.mapper.BusinessExceptionMapper,com.dooct.framework.rest.provider.mapper.ResourceNotFoundExceptionMapper");
        ServletHolder servletHolder = new ServletHolder("ResteasyServlet",restServlet);
        context.addServlet(servletHolder, "/*"); 
      
        handlerList.addHandler(context);
        server.setHandler(handlerList); 
        server.start();
		
	}

}
