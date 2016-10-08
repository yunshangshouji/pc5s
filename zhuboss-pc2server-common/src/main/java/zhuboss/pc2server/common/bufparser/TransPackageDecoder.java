package zhuboss.pc2server.common.bufparser;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zhuboss.pc2server.common.Constants;
import zhuboss.pc2server.common.JavaUtil;

public class TransPackageDecoder extends ByteToMessageDecoder{

Logger logger = LoggerFactory.getLogger(getClass());
	
	private boolean senseHttp = false; //数据包将要被 HttpResponseDecoder使用
	
	public TransPackageDecoder(boolean senseHttp) {
		this.senseHttp = senseHttp;
	}
	
	enum State{
		uuid,
		flag,
		zipFlag,
		len,
		data
	}
	
	State state = State.uuid; //系统初始值
	byte flag;
	ByteArrayOutputStream uuidByteArray = new ByteArrayOutputStream();
	byte zipFlag;
	ByteArrayOutputStream lenByteArray = new ByteArrayOutputStream();
	ByteArrayOutputStream dataByteArray = new ByteArrayOutputStream();
	
	public void parse(ByteBuf buf) throws Exception{
		if(buf.readableBytes()<1)	return;
		if(state == State.uuid){
			int needSize = JavaUtil.UUID_BYTE_LENGTH - uuidByteArray.size();
			int actRead = buf.readableBytes()>needSize?needSize:buf.readableBytes();
			byte[] uuidBytes = new byte[actRead];
			buf.readBytes(uuidBytes,0,actRead);
			uuidByteArray.write(uuidBytes);
			if(uuidByteArray.size()==JavaUtil.UUID_BYTE_LENGTH){
				state = State.flag;
			}
		}else if(state == State.flag){
			flag = buf.readByte();
			if(flag!=Constants.OPERATE_TYPE_DATA && flag!=Constants.OPERATE_TYPE_DISCONNECT  && flag!=Constants.OPERATE_TYPE_AUTH && flag!=Constants.OPERATE_TYPE_HEART_BEAT){
				throw new RuntimeException("data error of flag");
			}
			state = State.zipFlag;
		}else if(state == State.zipFlag){
			zipFlag = buf.readByte();
			if(zipFlag!=Constants.ZIP_FLAG_YES && zipFlag!=Constants.ZIP_FLAG_NO){
				throw new RuntimeException("data error of zipFlag");
			}
			state = State.len;
		}else if(state == State.len){
			int needSize = JavaUtil.INT_BYTE_LENGTH - lenByteArray.size();
			int actRead = buf.readableBytes()>needSize?needSize:buf.readableBytes();
			byte[] lenBytes = new byte[actRead];
			buf.readBytes(lenBytes,0,actRead);
			lenByteArray.write(lenBytes);
			if(lenByteArray.size()==JavaUtil.INT_BYTE_LENGTH){
				int len = JavaUtil.bytes2int(lenByteArray.toByteArray());
				if(len> (65535+100) || len<0){ //好像最大65535，由于我加了头信息
					throw new RuntimeException("data error");
				}
				state = State.data;
			}
		}else if(state == State.data){
			int len = JavaUtil.bytes2int(lenByteArray.toByteArray());
			if(len == 0){
				changeToUUID();
			}else{
				int needSize = len - dataByteArray.size();
				int actRead = buf.readableBytes()>needSize?needSize:buf.readableBytes();
				byte[] dataBytes = new byte[actRead];
				buf.readBytes(dataBytes,0,actRead);
				dataByteArray.write(dataBytes);
				if(dataByteArray.size() == len){
					changeToUUID();
				}
			}
		}
		parse(buf);
	}
	
	public void reset(){
		state = State.uuid;
		uuidByteArray.reset();
		lenByteArray.reset();
		dataByteArray.reset();
	}
	
	private void changeToUUID(){
		/**
		 * package finish
		 */
		//debug
		logger.debug("gen transPackage:");
		if(senseHttp == true && flag == Constants.OPERATE_TYPE_DATA){
			ByteBuf byteBuf = ctx.alloc().buffer();
			byteBuf.writeBytes(this.zipFlag==Constants.ZIP_FLAG_YES?JavaUtil.gunzip(dataByteArray.toByteArray()):dataByteArray.toByteArray());
			out.add(new MyByteBuf(this.uuidByteArray.toByteArray(),Constants.ZIP_FLAG_NO,byteBuf));
			if(logger.isDebugEnabled()){
				logger.debug("http response:"+JavaUtil.bytes2UUID(this.uuidByteArray.toByteArray())+"\n"+new String(dataByteArray.toByteArray()));
			}
		}else{
			TransPackage transPackage = new TransPackage(uuidByteArray.toByteArray(), flag, zipFlag,dataByteArray.toByteArray());
			out.add(transPackage);
		}
		//reset
		this.reset();
	}
	ChannelHandlerContext ctx;
	List<Object> out;
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		this.ctx = ctx;
		this.out = out;
		try{
		this.parse(in);
		} catch(Exception e){
			logger.warn(ctx.channel()+e.getMessage(),e);
			throw new TransPackageDecoderException(e);
			
		}
	}

}
