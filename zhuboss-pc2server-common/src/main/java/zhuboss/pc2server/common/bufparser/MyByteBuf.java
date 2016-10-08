package zhuboss.pc2server.common.bufparser;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.WrappedByteBuf;

public class MyByteBuf extends  WrappedByteBuf{
	private byte[] uuidBytes;
	private byte zipFlag;
	private String userDomain;
	public MyByteBuf(byte[] uuidBytes,byte zipFlag, ByteBuf byteBuf) {
		super(byteBuf);
		this.zipFlag = zipFlag;
		this.uuidBytes = uuidBytes;
	}
	public byte[] getUuidBytes() {
		return uuidBytes;
	}
	public void setUuidBytes(byte[] uuidBytes) {
		this.uuidBytes = uuidBytes;
	}
	public String getUserDomain() {
		return userDomain;
	}
	public void setUserDomain(String userDomain) {
		this.userDomain = userDomain;
	}
	public byte getZipFlag() {
		return zipFlag;
	}
	public void setZipFlag(byte zipFlag) {
		this.zipFlag = zipFlag;
	}

	
}
