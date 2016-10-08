package org.zhuboss.pc2server.pcproxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhuboss.pc2server.pcproxy.jetty.EmbedJettyServer;
import org.zhuboss.pc2server.pcproxy.server.AutoConnectThreadRun;
import org.zhuboss.pc2server.pcproxy.server.Client4Server;
import org.zhuboss.pc2server.pcproxy.server.ConnectedListener;
import org.zhuboss.pc2server.pcproxy.traffic.ConsoleDisplay;
import org.zhuboss.pc2server.pcproxy.util.PropertiesUtil;


public class StartPcProxy {
	static Object lock = new Object();
	static Thread sizeStatis;
	public static void main(String[] args) throws Exception {
		
		/**
		 * 控制台启动
		 */
		Constants.VERSION += ".1";
		
		PropertyConfigurator.configure(StartPcProxy.class.getClassLoader().getResourceAsStream("log4j.properties"));
		Logger logger = LoggerFactory.getLogger(StartPcProxy.class);
		
		/**
		 * 加载配置文件
		 */
		String fileName;
		if(args.length==0){
			fileName = "config.ini";
		}else{
			fileName = args[0];
		}
		File file = new File(fileName);
		if(!file.exists()){
			logger.error("配置文件"+fileName+"不存在");
		}
		PropertiesUtil.load(new FileInputStream(file));
		
		/**
		 * 流量统计
		 */
		if(PropertiesUtil.getPropertyString("sizeStatis")==null){
			Thread sizeStatis = new Thread(ConsoleDisplay.getInstance());
//			sizeStatis.setDaemon(true);
			sizeStatis.start();
		}
		
		/**
		 * 
		 */
		logger.info("启动中...");
		String authKey = PropertiesUtil.getPropertyString(org.zhuboss.pc2server.pcproxy.Constants.AUTH_KEY) ;
		String proxyAddr = PropertiesUtil.getPropertyString(org.zhuboss.pc2server.pcproxy.Constants.PROXY_ADDR) ;
		System.setProperty(org.zhuboss.pc2server.pcproxy.Constants.AUTH_KEY, authKey == null ? "": authKey);
		System.setProperty(org.zhuboss.pc2server.pcproxy.Constants.PROXY_ADDR, proxyAddr == null ? "": proxyAddr);
		Client4Server client = new Client4Server();
		client.start(new ConnectedListener() {
			Logger logger = LoggerFactory.getLogger(this.getClass());
			@Override
			public void onConnected(String data) {
				synchronized(ConsoleDisplay.getInstance().getLock()){
					ConsoleDisplay.getInstance().getLock().notify();
				}
			}

			@Override
			public void onDisconnected(String data) {
				ConsoleDisplay.getInstance().setTerminate(true);
				logger.error(""+ data.substring(1));
				logger.error("Press any key to exit");
				try {
					System.in.read();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.exit(-1);;
				
			}
		});
		/**
		 * 启动自动连接装置
		 */
//		Thread thread = new Thread(new AutoConnectThreadRun(client));
//		thread.setDaemon(true);
//		thread.start();
		
		/**
		 * 嵌入 WebServer
		 */
		EmbedJettyServer jettyServer = new EmbedJettyServer();
		try{
			jettyServer.init();
		}catch(Exception e){
			logger.error("启动失败："+e.getMessage());
		}
		synchronized(lock){
			lock.wait();
		}
	}

}
