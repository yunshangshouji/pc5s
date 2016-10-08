package zhuboss.framework.util;

import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/** 
 * @author 作者 zhuzhengquan: 
 * @version 创建时间：2015年12月14日 下午3:31:05 
 * 类说明 
 */
public class DesInstance {
	private String cipherInstance;
	private String secretKeyInstance;
	private String dataEncryptCharset;
	private KeySpec keySpec;
	
	
	public DesInstance(String cipherInstance,String secretKeyInstance,KeySpec keySpec,String dataEncryptCharset) throws Exception{
		this.cipherInstance = cipherInstance;
		this.secretKeyInstance = secretKeyInstance;
		this.keySpec = keySpec;
		this.dataEncryptCharset = dataEncryptCharset;
	}
	
	public DesInstance(String desKey) throws Exception{
		this("DESede/ECB/PKCS5Padding",
				"DESede",
				(KeySpec)(new DESedeKeySpec(desKey.getBytes("UTF-8"))),
				"UTF-8");
	}
	
	

	public byte[] encrypt(byte[] message) throws Exception {
		SecretKeyFactory keyFactory = SecretKeyFactory
				.getInstance(secretKeyInstance);
		SecretKey securekey = keyFactory.generateSecret(keySpec);

		Cipher cipher = Cipher.getInstance(cipherInstance);
		cipher.init(Cipher.ENCRYPT_MODE, securekey);

		return cipher.doFinal(message);
	}
	
	public byte[] decrypt(byte[] bytesrc) throws Exception {
					// 解密的key
					SecretKeyFactory keyFactory = SecretKeyFactory
							.getInstance(secretKeyInstance);
					SecretKey securekey = keyFactory.generateSecret(keySpec);
					// Chipher对象解密
					Cipher cipher = Cipher.getInstance(cipherInstance);
					cipher.init(Cipher.DECRYPT_MODE, securekey);
					byte[] retByte = cipher.doFinal(bytesrc);
					return retByte;
	}
	
	public String decrypt(String str){
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] byteStr = decoder.decodeBuffer(str);
			byte[] oriStrBytes = this.decrypt(byteStr);
			return new String(oriStrBytes,this.dataEncryptCharset);
		}  catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String encrypt(String str){
		try {
			byte[] data = str.getBytes(this.dataEncryptCharset);
			return  new BASE64Encoder().encode(this.encrypt(data));
		}  catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) throws Exception{
		final String DES_KEY = "^h$_0j9kwf$y7,@w%;o+f[]-";
		String text = "hello1111111112222";
		
		DesInstance desInstance = new DesInstance(DES_KEY);
		System.out.println(desInstance.encrypt(text));
		System.out.println(desInstance.decrypt(desInstance.encrypt(text)));
		
		/**
		 * 
		 */
		desInstance = new DesInstance("DESede/ECB/PKCS5Padding","DESede",new DESedeKeySpec(DES_KEY.getBytes("UTF-8")),"UTF-8");
		System.out.println(desInstance.encrypt(text));
		System.out.println(desInstance.decrypt(desInstance.encrypt(text)));
		/**
		 * 解密
		 */
		String value = "Wa4iODjuXVQLlbeOBBjodz7891yk6Yu1ET94j1EquwH48GNFFCmJSmiW3Es6toQIIB2uhUnEM3z1nwgjMs8BTXGeoQ6dr+OYzL5Ai6UYqGMnMOg7IM41UcY6NRe+JqHIf0eo7Wmj4ig90YupwKMN3A==";
		String result = desInstance.decrypt(value);
		System.out.println(result);
	}
}
