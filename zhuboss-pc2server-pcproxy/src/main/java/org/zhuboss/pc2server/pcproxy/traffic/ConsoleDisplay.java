package org.zhuboss.pc2server.pcproxy.traffic;

import io.netty.channel.Channel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.zhuboss.pc2server.pcproxy.server.Client4ServerRegister;

public class ConsoleDisplay implements Runnable {
	private boolean terminate = false;
	static ConsoleDisplay consoleDisplay = new ConsoleDisplay();
	final char[] processChar = new char[]{'-', '\\','|','/'};
	int seq = 0;
	
	private Object lock = new Object();
	public  static ConsoleDisplay getInstance(){
		return consoleDisplay;
	}
	
	
	private int backspaceSize = 0;
	@Override
	public void run() {
		try {
			synchronized (lock) {//wait之前必须先锁定对象
				lock.wait();
			}
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(terminate == false){
			try {
				Thread.sleep(1*1000);
				Channel channel = Client4ServerRegister.getInstance().getChannel();
				if(channel!=null && channel.isOpen()){
					for(int i=0;i<backspaceSize;i++){
						System.out.write('\b');
					}
					//注意字符串中使用\t字符，实际控制台输出长度并不是1，可能是8，导致\b不能完成清空
					String text = "Receive:"+ TrafficCollect.getInstance().getRecB() + "     Send:" + TrafficCollect.getInstance().getSendB()+ ' '
							+processChar[seq];
					byte[] bytes = text.getBytes();
					System.out.write(bytes);
					backspaceSize = bytes.length;
					seq++;
					if(seq>3)seq=0;
				}
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	public Object getLock() {
		return lock;
	}
	public boolean isTerminate() {
		return terminate;
	}
	public void setTerminate(boolean terminate) {
		this.terminate = terminate;
	}
	
}
