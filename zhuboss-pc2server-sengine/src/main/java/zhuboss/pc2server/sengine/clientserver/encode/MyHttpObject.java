package zhuboss.pc2server.sengine.clientserver.encode;


public class MyHttpObject {
	private byte[] uuidBytes;
	private byte zipFlag;
	private Object message; //HttpObject,ByteBuf4WebSocket
	
	public MyHttpObject(byte[] uuidBytes,byte zipFlag, Object message) {
		this.uuidBytes = uuidBytes;
		this.zipFlag = zipFlag;
		this.message = message;
	}
	public byte[] getUuidBytes() {
		return uuidBytes;
	}
	public void setUuidBytes(byte[] uuidBytes) {
		this.uuidBytes = uuidBytes;
	}
	public Object getMessage() {
		return message;
	}
	public void setMessage(Object message) {
		this.message = message;
	}
	public byte getZipFlag() {
		return zipFlag;
	}
	public void setZipFlag(byte zipFlag) {
		this.zipFlag = zipFlag;
	}
}
