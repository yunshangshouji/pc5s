package org.zhuboss.pc2server.pcproxy.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhuboss.pc2server.pcproxy.StartPcProxy;
import org.zhuboss.pc2server.pcproxy.local.Client4LocalConManager;
import org.zhuboss.pc2server.pcproxy.traffic.ConsoleDisplay;
import org.zhuboss.pc2server.pcproxy.traffic.TrafficCollect;

import zhuboss.pc2server.common.Constants;
import zhuboss.pc2server.common.JavaUtil;
import zhuboss.pc2server.common.LogUtil;
import zhuboss.pc2server.common.LogUtil.Do;
import zhuboss.pc2server.common.NetUtil;
import zhuboss.pc2server.common.bufparser.TransPackage;

public class Client4ServerHandler extends ChannelHandlerAdapter{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	ConnectedListener connectedListener;
	
	public Client4ServerHandler(ConnectedListener connectedListener) {
		this.connectedListener = connectedListener;
	}

	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		TransPackage transPackage = (TransPackage)msg;
		byte flag = transPackage.getType();
		byte[] uuidBytes = transPackage.getUuidBytes();
		
		if(flag == Constants.OPERATE_TYPE_DISCONNECT){
			Client4LocalConManager.getInstance().unRegister(JavaUtil.bytes2UUID(uuidBytes));
			if(logger.isDebugEnabled()){
				logger.debug(LogUtil.log(Do.DISCONNECT, ctx.channel(), JavaUtil.bytes2UUID(uuidBytes)+""));
			}
		}else if(flag == Constants.OPERATE_TYPE_DATA){
			ChannelFuture channelFuture = null;
			try{
				channelFuture = Client4LocalConManager.getInstance().getSafeChannelFuture(uuidBytes,transPackage.getZipFlag());
				ByteBuf byteBuf = channelFuture.channel().alloc().buffer();
				byteBuf.writeBytes(transPackage.getData());
				ChannelFuture cf = channelFuture.channel().writeAndFlush(byteBuf);
				cf.sync();
				TrafficCollect.getInstance().rec(transPackage.getDataLen());
			}catch(Exception e){ //连接本地WebServer失败
				logger.warn(e.getMessage());
				//回写service un avaiable ,防止server端死等
				TransPackage failTransPackage = new TransPackage(uuidBytes, Constants.OPERATE_TYPE_DISCONNECT,Constants.ZIP_FLAG_NO, e.getMessage().getBytes(Charset.forName("UTF-8")));
				ctx.channel().writeAndFlush(failTransPackage);
				if(channelFuture!=null && channelFuture.channel().isOpen()) {
					channelFuture.channel().close();
				}
				return;
			}
			logger.debug(LogUtil.log(Do.READ, ctx.channel(), JavaUtil.bytes2UUID(uuidBytes)+":"+transPackage.getDataLen()));
			logger.debug(LogUtil.log(Do.WRITE, channelFuture.channel(), JavaUtil.bytes2UUID(uuidBytes)+":"+transPackage.getTransSize()));
			logger.debug(new String(transPackage.getData()));
		}else if(flag == Constants.OPERATE_TYPE_AUTH){
				final String data = new String(transPackage.getData(),Charset.forName("UTF-8"));
				if(data.substring(0, 1).equals("0")){
					logger.info(data.substring(1));
					if(connectedListener!=null){
						connectedListener.onConnected(data);
					}
				}else{
					logger.error(data.substring(1));
					//释放自身线程，否则导致后面的锁冲突
					new Thread(new Runnable() {
						@Override
						public void run() {
							connectedListener.onDisconnected(data.substring(1));
						}
					}).start();
					
					
				}
		}else{
			throw new RuntimeException("报文错误");
		}
	
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		/**
		 * 数据编码异常
		 */
		if(cause instanceof DecoderException){
			ctx.channel().close().sync();
		}
		
//		super.exceptionCaught(ctx, cause);
		logger.debug(LogUtil.log(Do.EXCEPTION_CAUGHT,ctx.channel(),cause.getMessage()));
	}

	@Override
	public void channelRegistered(final ChannelHandlerContext ctx) throws Exception {
		super.channelRegistered(ctx);
		
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.debug(LogUtil.log(Do.CONNECT,ctx.channel(),""));
		// TODO Auto-generated method stub
		super.channelActive(ctx);
		Client4ServerRegister.getInstance().register(ctx.channel());
	}
	
	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		logger.debug(LogUtil.log(Do.DISCONNECT,ctx.channel(),""));
		super.channelUnregistered(ctx);
		Client4ServerRegister.getInstance().unRegister(ctx.channel());
	}
	
	
}
