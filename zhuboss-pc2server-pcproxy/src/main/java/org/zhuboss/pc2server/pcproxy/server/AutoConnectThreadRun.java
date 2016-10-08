package org.zhuboss.pc2server.pcproxy.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zhuboss.pc2server.common.Constants;
import zhuboss.pc2server.common.bufparser.TransPackage;
import io.netty.channel.Channel;


public class AutoConnectThreadRun implements Runnable {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	Client4Server client4Server;
	
	TransPackage heartBeatTransPackage;
	
	public AutoConnectThreadRun(Client4Server client4Server) {
		this.client4Server = client4Server;
		heartBeatTransPackage = new TransPackage(null, Constants.OPERATE_TYPE_HEART_BEAT, Constants.ZIP_FLAG_NO, new byte[]{});
	}


	int i=0;
	@Override
	public void run() {
		Thread.currentThread().setName("AutoConnectServerThread[PC2Server]");
		while(true){
			try {
				Thread.sleep(3000);
				Channel channel;
				channel = Client4ServerRegister.getInstance().getChannel();
				if(channel==null || !channel.isOpen()){
					client4Server.connect();
				}
				i++;
				if(i>10){
					i=0;
					channel.writeAndFlush(heartBeatTransPackage);
				}
				
			} catch (InterruptedException e) {
				logger.warn(e.getMessage());
			}
			
		}
		
	}

}
