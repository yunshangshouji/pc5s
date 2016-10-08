package zhuboss.pc2server.sengine;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class LogChannelHandlerAdapter extends ChannelHandlerAdapter {
	private String use;
	public LogChannelHandlerAdapter(String use){
		this.use = use;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		Thread.currentThread().setName(use+"-channelRead");
		super.channelRead(ctx, msg);
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg,
			ChannelPromise promise) throws Exception {
		Thread.currentThread().setName(use+"-write");
		super.write(ctx, msg, promise);
	}
	
}
