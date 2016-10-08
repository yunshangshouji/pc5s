package org.zhuboss.pc2server.pcproxy.local;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhuboss.pc2server.pcproxy.server.Client4ServerRegister;
import org.zhuboss.pc2server.pcproxy.traffic.TrafficCollect;

import zhuboss.pc2server.common.Constants;
import zhuboss.pc2server.common.JavaUtil;
import zhuboss.pc2server.common.LogUtil;
import zhuboss.pc2server.common.LogUtil.Do;
import zhuboss.pc2server.common.bufparser.TransPackage;

public class Client4LocalHandler extends ChannelHandlerAdapter{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	private byte[] uuidBytes;
	private byte zipFlag;
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf = (ByteBuf)msg;
		try{
			byte[] bytes = new byte[buf.readableBytes()];
			buf.readBytes(bytes);
			
			Channel returnChannel = Client4ServerRegister.getInstance().getChannel();
			if(returnChannel == null || !returnChannel.isActive()){
				throw new RuntimeException("server stoped,for:");
			}
			//报文格式
			TransPackage transPackage = new TransPackage(uuidBytes, Constants.OPERATE_TYPE_DATA,zipFlag,
					(zipFlag==Constants.ZIP_FLAG_YES?JavaUtil.gzip(bytes):bytes));
			if(logger.isDebugEnabled()){
				logger.debug(LogUtil.log(Do.READ,ctx.channel(),bytes.length+""));
				logger.debug(LogUtil.log(Do.WRITE,returnChannel,JavaUtil.bytes2UUID(uuidBytes) +":"+transPackage.getTransSize()));
				logger.debug(new String(transPackage.getData()));
			}
			returnChannel.writeAndFlush(transPackage);
			TrafficCollect.getInstance().send(transPackage.getDataLen());
		}finally{
			/**
	         * ByteBuf是一个引用计数对象，这个对象必须显示地调用release()方法来释放。
	         * 请记住处理器的职责是释放所有传递到处理器的引用计数对象。
	         */
			ReferenceCountUtil.release(msg); //ByteBuf的最后使用者要负责释放，必须！
		}
	        
	}

	public byte[] getUuidBytes() {
		return uuidBytes;
	}

	public void setUuidBytes(byte[] uuidBytes) {
		this.uuidBytes = uuidBytes;
	}


	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelRegistered(ctx);
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelUnregistered(ctx);
	}

	public byte getZipFlag() {
		return zipFlag;
	}

	public void setZipFlag(byte zipFlag) {
		this.zipFlag = zipFlag;
	}
	
	
}
