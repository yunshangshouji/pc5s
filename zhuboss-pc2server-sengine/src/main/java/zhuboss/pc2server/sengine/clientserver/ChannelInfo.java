package zhuboss.pc2server.sengine.clientserver;

import zhuboss.pc2server.common.ProxyAddrResolveUtil;
import io.netty.channel.Channel;

public class ChannelInfo {
	private Channel channel;
	private String host;
	
	public ChannelInfo(Channel channel, String local) {
		this.channel = channel;
		host = ProxyAddrResolveUtil.resolve(local).getHost();
	}
	
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
}
