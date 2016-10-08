package zhuboss.pc2server.sengine.clientserver;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.ReferenceCountUtil;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zhuboss.framework.rest.OperateResult;
import zhuboss.framework.spring.CustomizedPropertyPlaceholderConfigurer;
import zhuboss.pc2server.common.Constants;
import zhuboss.pc2server.common.JavaUtil;
import zhuboss.pc2server.common.LogUtil;
import zhuboss.pc2server.common.LogUtil.Do;
import zhuboss.pc2server.common.bufparser.MyByteBuf;
import zhuboss.pc2server.common.bufparser.TransPackage;
import zhuboss.pc2server.sengine.StartSEngine;
import zhuboss.pc2server.sengine.mis.mapper.HisLoginPOMapper;
import zhuboss.pc2server.sengine.mis.po.HisLoginPO;
import zhuboss.pc2server.sengine.mis.po.UserDomainPO;
import zhuboss.pc2server.sengine.mis.service.UserService;
import zhuboss.pc2server.sengine.webserver.Server4WwwConManager;
import zhuboss.pc2server.sengine.webserver.pages.PageDispatcher;

public class Server4ClientHandler extends ChannelHandlerAdapter {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	String userDomain;
	Integer userId;
	/**
	 *  每当从客户端收到新的数据时，
     * 这个方法会在收到消息时被调用，
     * 这个例子中，收到的消息的类型是ByteBuf
	 * @throws Exception 

	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try{
		if(msg instanceof TransPackage && ((TransPackage)msg).getType() == Constants.OPERATE_TYPE_HEART_BEAT){
			logger.debug("heart beat:"+ctx.channel().remoteAddress());
			/**
			 * 容错检测
			 */
//			if(userDomain==null || Server4ClientRegister.getInstance().getChannelInfo(userDomain) == null){
//				ctx.channel().disconnect();
//			}
			return;
		}
		/**
		 * 客户端发来disconnect请求，一般是客户端本地server服务有问题
		 */
		if(msg instanceof TransPackage && ((TransPackage)msg).getType() == Constants.OPERATE_TYPE_DISCONNECT){
			final byte[] uuidBytes = ((TransPackage)msg).getUuidBytes();
			final ChannelHandlerContext serverCtx = Server4WwwConManager.getInstance().getChannelHandlerContext(uuidBytes);
			if(serverCtx !=null){
				PageDispatcher pageDispatcher = StartSEngine.getApplicationContext().getBean(PageDispatcher.class);
				Map<String,Object> data = new HashMap<String,Object>();
				data.put("msg", new String(((TransPackage)msg).getData(),"UTF-8"));
				DefaultFullHttpResponse response = pageDispatcher.buildHttpResponse(serverCtx,HttpResponseStatus.OK,null,"service_unavailable.ftl",data);
				serverCtx.writeAndFlush(response);
			}
			return;
		}
		
		/**
		 * 密码校验
		 */
		if(userDomain == null ){
			TransPackage transPackage = (TransPackage)msg;
			if(transPackage.getType() == Constants.OPERATE_TYPE_AUTH){
				String data = new String(transPackage.getData());
				logger.info("auth data:"+data);
				String responseText;
				OperateResult<UserDomainPO> loginResult = null;
				try{
					String version = data.split(",")[0];
					String appkey = data.split(",")[1];
					String local = data.split(",")[2];
					
					UserService userService = StartSEngine.getApplicationContext().getBean(UserService.class);
					loginResult = userService.login(appkey);
					
					if(loginResult.getErrcode().equals(OperateResult.SUCCESS_CODE)){
						responseText = "0登录成功,外网地址: http://"+loginResult.getData().getUserDomain()
								+ StartSEngine.getApplicationContext().getBean(CustomizedPropertyPlaceholderConfigurer.class).getContextProperty("sengine.domain")
								+ " -> " + local;
						userDomain = loginResult.getData().getUserDomain();
						userId = loginResult.getData().getUserId();
						Server4ClientRegister.getInstance().register(loginResult.getData().getUserDomain(),new ChannelInfo(ctx.channel(), local));
						logger.info("log success:"+data);
					}else{
						responseText = "1"+loginResult.getErrmsg();
						logger.info("log fail:"+data);
					}
				}catch(Exception e){
					logger.error(e.getMessage(),e);
					responseText = "1提交数据不完整,appkey,proxyAddr不能为空";
				}
				TransPackage ret = new TransPackage(null,Constants.OPERATE_TYPE_AUTH,Constants.ZIP_FLAG_NO,responseText.getBytes(Charset.forName("UTF-8")));
				ctx.channel().writeAndFlush(ret);
				/**
				 * 记录登录日志，考虑异步执行
				 */
				try{
					if(loginResult!=null){
						HisLoginPOMapper logHisPOMapper = StartSEngine.getApplicationContext().getBean(HisLoginPOMapper.class);
						HisLoginPO logHisPO = new HisLoginPO();
						if(loginResult.getErrcode().equals(OperateResult.SUCCESS_CODE)){
							logHisPO.setUserId(loginResult.getData().getUserId());
						}
						logHisPO.setIp(((InetSocketAddress)ctx.channel().remoteAddress()).getHostString());
						logHisPO.setLogDate(new Date());
						logHisPO.setParams(data+","+loginResult.getErrcode());
						logHisPOMapper.insert(logHisPO);
					}
					
				}catch(Throwable t){
					logger.error(t.getMessage(),t);
				}
			}else{
				//写失败
				String retResult = "1Not Login";
				TransPackage ret = new TransPackage(null,Constants.OPERATE_TYPE_AUTH,Constants.ZIP_FLAG_NO,retResult.getBytes(Charset.forName("UTF-8")));
				ctx.channel().writeAndFlush(ret);
			}
			return;
		}else if (msg instanceof MyByteBuf){
			MyByteBuf myByteBuf = (MyByteBuf)msg;
			final byte[] uuidBytes = myByteBuf.getUuidBytes();
			final ChannelHandlerContext serverCtx = Server4WwwConManager.getInstance().getChannelHandlerContext(uuidBytes);
			if(serverCtx == null){
				//浏览器端请求已经断开连接，做discard处理
				logger.warn("discard:"+JavaUtil.bytes2UUID(uuidBytes));
				ReferenceCountUtil.release(myByteBuf);
				return;
			}
			
			/**
			 * 流量控制<防止碰到大文件下载>
			 */
			long flowLimit = this.getFlowLimit();
			long tranficSize = TranficStatis.getInstance().get(userId);
			if(tranficSize>flowLimit){
				ReferenceCountUtil.release(myByteBuf); //做discard处理
				//中断连接
				serverCtx.disconnect();
				ctx.channel().disconnect();//防止客户端大文件死发数据
				return;
			}
			
			
			TranficStatis.getInstance().add(userId,myByteBuf.readableBytes());//流量统计
			myByteBuf.setUserDomain(userDomain);
			ChannelFuture cf = serverCtx.channel().writeAndFlush(myByteBuf,serverCtx.channel().newPromise()); //提交pipeline队列处理
			/**
			 * 2016-7-6 有bug，以下程序可能出现死等.可能的原因，客户端断断，manager等worker释放，worker无法释放，连接也处于close_wait状态，也导致连接池不能释放
			 */
//			cf.addListener(new ChannelFutureListener() {
//				@Override
//				public void operationComplete(ChannelFuture future)
//						throws Exception {
//					if(!future.isSuccess() ){
//						logger.warn("lost:"+JavaUtil.bytes2UUID(uuidBytes) , ((ChannelPromise)future).cause());
//						serverCtx.close();
//					}
//					
//				}
//			}).sync();
		}else{
			logger.warn("unexpect data package");
			ctx.channel().close();
		}
		
		}catch(Throwable t){
			logger.error(t.getMessage(),t);
			throw t;
		}

	}
	
	public long getFlowLimit(){
		UserService userService = StartSEngine.getApplicationContext().getBean(UserService.class);
		return userService.getFlowLimit(userDomain);
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if(cause instanceof DecoderException){
			logger.warn("decode error,close:"+ctx.channel());
			ctx.channel().close(); //复位，等待客户端重连
		}
		logger.debug(LogUtil.log(Do.EXCEPTION_CAUGHT, ctx.channel(), cause.getMessage()));
//		super.exceptionCaught(ctx, cause); //仅仅打印异常，没干什么事
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.debug(LogUtil.log(Do.DISCONNECT, ctx.channel(), null));
		super.channelInactive(ctx);
		Server4ClientRegister.getInstance().unRegister(ctx.channel());
	}
	
	
}
