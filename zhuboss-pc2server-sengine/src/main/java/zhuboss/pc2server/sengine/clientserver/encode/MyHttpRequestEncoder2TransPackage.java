package zhuboss.pc2server.sengine.clientserver.encode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zhuboss.pc2server.common.Constants;
import zhuboss.pc2server.common.bufparser.MyByteBuf;
import zhuboss.pc2server.common.bufparser.TransPackage;

/**
 * 接手HttpRequestEncoder的ByteBuf
 * 基础上使用TransPackage传输协议
 * @author Administrator
 *
 */
public class MyHttpRequestEncoder2TransPackage extends MessageToMessageEncoder<MyByteBuf>{
Logger logger = LoggerFactory.getLogger(this.getClass());




	@Override
	public boolean acceptOutboundMessage(Object msg) throws Exception {
		// TODO Auto-generated method stub
		return super.acceptOutboundMessage(msg);
	}




	@Override
	protected void encode(ChannelHandlerContext ctx, MyByteBuf msg,
			List<Object> out) throws Exception {
		if(msg.refCnt()>1) {
			System.out.println();
		}
		byte[] bytes = new byte[msg.readableBytes()];
		msg.readBytes(bytes);
//父类中做了释放，所以不能再释放		ReferenceCountUtil.release(msg.getByteBuf());

		TransPackage transPackage = new TransPackage(msg.getUuidBytes(), Constants.OPERATE_TYPE_DATA, msg.getZipFlag(),bytes);
		
		out.add(transPackage); //复用，不要拷贝代码
	}




}
