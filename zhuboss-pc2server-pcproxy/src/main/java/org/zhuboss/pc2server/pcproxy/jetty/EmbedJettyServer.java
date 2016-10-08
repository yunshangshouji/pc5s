package org.zhuboss.pc2server.pcproxy.jetty;

import java.io.File;
import java.net.InetAddress;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhuboss.pc2server.pcproxy.util.PropertiesUtil;

public class EmbedJettyServer {
	
	int port = 80;
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	public void init() throws Exception {
		String documentRoot = PropertiesUtil.getPropertyString("document.path");
		Integer cfgPort = PropertiesUtil.getPropertyInt("document.port");
		if(documentRoot==null || documentRoot.trim().length()==0 || cfgPort==null){
			logger.warn("参数document未完整配置,不启动本地Server");
			return;
		}
		if(!new File(documentRoot).exists()){
			System.out.println("路径不存在："+documentRoot+",不启动本地Server");
			return;
		}
		port = cfgPort;
		Server server = new Server(port);
		HandlerList handlerList = new HandlerList();
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
		context.setContextPath("/");
		context.setResourceBase(documentRoot);
		MyDefaultServlet defaultServlet = new MyDefaultServlet();
		ServletHolder servletHolder = new ServletHolder("defaultServlet",defaultServlet);
		servletHolder.setInitParameter("aliases", "false");
		servletHolder.setInitParameter("acceptRanges", "true");
		servletHolder.setInitParameter("dirAllowed", "true");
		servletHolder.setInitParameter("welcomeServlets", "false");
		servletHolder.setInitParameter("redirectWelcome", "false");
		servletHolder.setInitParameter("maxCacheSize", "256000000");
		servletHolder.setInitParameter("aliases", "200000000");
		servletHolder.setInitParameter("maxCachedFileSize", "200000000");
		servletHolder.setInitParameter("maxCachedFiles", "2048");
		servletHolder.setInitParameter("gzip", "false"); //else find /path.giz
		servletHolder.setInitParameter("useFileMappedBuffer", "false");
	    context.addServlet(servletHolder, "/*"); 
        handlerList.addHandler(context);
        server.setHandler(handlerList); 
        server.start();
        InetAddress addr = InetAddress.getLocalHost();
        String disPort = port==80?"": (":"+String.valueOf(port));
        
		logger.info("成功启动web服务，目录:"+documentRoot+",访问：http://127.0.0.1"+disPort +" 或  http://"+addr.getHostAddress()+disPort);
	}

}
