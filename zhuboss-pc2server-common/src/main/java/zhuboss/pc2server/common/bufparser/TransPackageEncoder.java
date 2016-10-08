package zhuboss.pc2server.common.bufparser;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class TransPackageEncoder extends MessageToByteEncoder<TransPackage>{
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	static List<ByteBuf> tests = new ArrayList<ByteBuf>();
	static synchronized void add(ByteBuf buf){
		if(tests.size()==1000){
			for(int i=0;i<100;i++){
				System.out.println(tests.get(i).refCnt());
			}
		}
		tests.add(buf);
		
	}
	
	@Override
	public boolean acceptOutboundMessage(Object msg) throws Exception {
		return super.acceptOutboundMessage(msg) && msg instanceof TransPackage;
	}


	@Override
	protected void encode(ChannelHandlerContext ctx, TransPackage transPackage,
			ByteBuf out) throws Exception {
		add(out);
		out.writeBytes(transPackage.getFullBytes());
		//不使用分批写入，性能显著提升
		/**
		 * 以前发现的问题？？？
		 * if(transPackage.getFullBytes().length>65500){//65535
			//过大数据包，分两次传，否则报错！传输图片的时候经常出现大块
			//注意：不要自己构建ByteBuf用于分包写入，ByteBuf涉及reference count引用计数的问题！
			logger.debug("分包传送"+transPackage.getFullBytes().length);
			out.writeBytes(transPackage.getFullBytes(),0,transPackage.getTransSize()/2);
			ctx.flush();
			out.writeBytes(transPackage.getFullBytes(),transPackage.getTransSize()/2,transPackage.getTransSize() - transPackage.getTransSize()/2);
			ctx.flush();
		}else{
			if(logger.isDebugEnabled()){
				logger.debug(new String(transPackage.getData()));
			}
			out.writeBytes(transPackage.getFullBytes());
			ctx.flush();
		}
		 */
	}

}
