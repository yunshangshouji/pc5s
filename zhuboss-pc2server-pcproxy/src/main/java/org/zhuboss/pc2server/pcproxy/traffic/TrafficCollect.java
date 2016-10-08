package org.zhuboss.pc2server.pcproxy.traffic;

import java.util.concurrent.atomic.AtomicLong;

public class TrafficCollect {
	final int displayLength = 100;
	final int KB = 1024;
	final int MB = 1024*1024;
	final int GB = 1024*1024*1024;
	static TrafficCollect consoleDisplay = new TrafficCollect();
	int seq = 0;
	
	public  static TrafficCollect getInstance(){
		return consoleDisplay;
	}
	AtomicLong recB = new AtomicLong(0);
	AtomicLong sendB = new AtomicLong(0);
	
	public void rec (int byteSize){
		recB.addAndGet(byteSize);
	}
	public void send (int byteSize){
		sendB.addAndGet(byteSize);
	}
	
	public String getRecS(long x){
		int g = new Long(x>GB? x/GB:0).intValue();
		int m = new Long(x>MB? (x%GB)/MB:0).intValue();
		int k = new Long(x>KB? (x%MB)/KB:0).intValue();
		StringBuffer sb = new StringBuffer();
		if(g>0){
			sb.append(g+"G ");
		}
		if(m>0){
			sb.append(m+"M ");
		}
		sb.append(k+"K");
		return sb.toString();
	}
	public String getRecB() {
		return this.getRecS(recB.longValue());
	}
	public String getSendB() {
		return this.getRecS(sendB.longValue());
	}
	
}
