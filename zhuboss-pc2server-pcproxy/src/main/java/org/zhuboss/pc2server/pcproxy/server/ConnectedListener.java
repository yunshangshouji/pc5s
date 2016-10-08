package org.zhuboss.pc2server.pcproxy.server;

public interface ConnectedListener {
	
	void onConnected(String data);
	
	void onDisconnected(String data);
}
