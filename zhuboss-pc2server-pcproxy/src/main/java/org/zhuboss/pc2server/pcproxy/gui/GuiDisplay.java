package org.zhuboss.pc2server.pcproxy.gui;

import javax.swing.JLabel;

import io.netty.channel.Channel;

import org.zhuboss.pc2server.pcproxy.server.Client4ServerRegister;
import org.zhuboss.pc2server.pcproxy.traffic.TrafficCollect;

public class GuiDisplay implements Runnable {
	JLabel label;
	boolean terminate = true;
	
	
	public GuiDisplay(JLabel label){
		this.label = label;
	}
	
	@Override
	public void run() {
		while(true){
			try {
				Thread.sleep(1*1000);
				if(terminate == true){
					continue;
				}
				Channel channel = Client4ServerRegister.getInstance().getChannel();
				if(channel!=null && channel.isOpen()){
					this.label.setText("已接收:"+TrafficCollect.getInstance().getRecB()+", 已发送:"+TrafficCollect.getInstance().getSendB());
				}
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		}
		
	}
	public boolean isTerminate() {
		return terminate;
	}
	public void setTerminate(boolean terminate) {
		this.terminate = terminate;
	}
	
}
