package zhuboss.pc2server.sengine.manager;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class MyHttpServletDispatcher extends HttpServletDispatcher implements ApplicationContextAware{
	private ApplicationContext applicationContext;
	/**
	 * 
	 */
	private static final long serialVersionUID = -3360412828965778798L;
	
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		
		Map<String,Object> restMap = applicationContext.getBeansWithAnnotation(javax.ws.rs.Path.class);
		Iterator<String> iterator = restMap.keySet().iterator();
		while(iterator.hasNext()){
			String key = iterator.next();
			this.servletContainerDispatcher.getDispatcher().getRegistry().addSingletonResource(restMap.get(key));
		}
		
	}

	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
	
}
