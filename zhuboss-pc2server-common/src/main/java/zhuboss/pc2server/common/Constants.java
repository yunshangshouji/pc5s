package zhuboss.pc2server.common;

public class Constants {
	/**
	 * 传输数据<br>
	 * server<-->client 双向
	 */
	public static byte OPERATE_TYPE_DATA = 49 ; //'1'
	
	/**
	 * 断开连接<br>
	 * 1. server-->client WWW用户<br>
	 * 2. client-->server WEB容器不提供服务
	 */
	public static byte OPERATE_TYPE_DISCONNECT = 50 ; // '2'
	
	/**
	 * 客户端与服务端的密码校验
	 */
	public static byte OPERATE_TYPE_AUTH = 51 ; // '3'
	
	/**
	 * 客户端向服务端发心跳，防防火墙断掉
	 */
	public static byte OPERATE_TYPE_HEART_BEAT = 52 ; // '4'
	
	/**
	 * 未压缩
	 */
	public static byte ZIP_FLAG_NO = 0;

	/**
	 * 已压缩
	 */
	public static byte ZIP_FLAG_YES = 1;
}
