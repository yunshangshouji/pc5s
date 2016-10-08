package zhuboss.pc2server.common.bufparser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

import zhuboss.pc2server.common.JavaUtil;


public class TransPackage {
	Logger logger = Logger.getLogger(this.getClass());
	
	private byte[] uuidBytes;
	private byte  type;
	private byte zipFlag; //data is ZIP format
	private byte[] data;
	private byte[] fullBytes;
	public TransPackage(byte[] uuidBytes, byte type,byte zipFlag, byte[] data) {
		if(uuidBytes == null) {
			this.uuidBytes = new byte[]{0,0,0,0
					,0,0,0,0
					,0,0,0,0
					,0,0,0,0};
		}else{
			this.uuidBytes = uuidBytes;
		}
		this.type = type;
		this.zipFlag = zipFlag;
		this.data = data;
		/**
		 * 拼装
		 */
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(this.uuidBytes);
			os.write(type);
			os.write(this.zipFlag);
			os.write(JavaUtil.int2Bytes(data.length));
			os.write(data);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		int packetsize = JavaUtil.UUID_BYTE_LENGTH + 1 +1 + JavaUtil.INT_BYTE_LENGTH + data.length;
		fullBytes = os.toByteArray();
		if(packetsize !=  fullBytes.length){
			throw new RuntimeException("data error");
		}
		logger.debug("TransPackage MD5:" + this.getMd5Sign());
	}
	
	public int getTransSize(){
		return this.uuidBytes.length + 1 + 4 + data.length;
	}
	public int getDataLen() {
		return data.length;
	}
	public String getMd5Sign(){
		try {
			return JavaUtil.bytesToHexString(JavaUtil.md5Digest(this.getFullBytes()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public byte[] getUuidBytes() {
		return uuidBytes;
	}
	public byte getType() {
		return type;
	}

	public byte[] getData() {
		return data;
	}

	public byte[] getFullBytes() {
		return fullBytes;
	}

	public byte getZipFlag() {
		return zipFlag;
	}
	
}
