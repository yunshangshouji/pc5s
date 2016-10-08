/*
 * @(#)CodeUtils.java 1.0 2009-12-28下午02:45:58
 *
 * 和讯信息科技有限公司-第三方理财事业部-第三方理财事业部
 * Copyright (c) 1996-2012 HexunFSD, Inc. All rights reserved.
 */

 package zhuboss.framework.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import sun.misc.BASE64Encoder;

/**
 * <dl>
 *    <dt><b>Title:</b></dt>
 *    <dd>
 *    	编码工具类
 *    </dd>
 *    <dt><b>Description:</b></dt>
 *    <dd>
 *    	<p>加解密、转换编码
 *    </dd>
 * </dl>
 *
 * @author eric
 * @version 1.0, 2009-12-28
 * @since framework-1.4
 * 
 */
public final class CodeUtils {
	
	
	
	
	public static void main(String[] args) throws Exception{
		/*
		 String key = "1F08260D1AC2465ESD56GHWG30L2OPBVZXCVB16397G4H7UI";
//	       String kkk = desEncrypt("6B056E18759F5CCA",key);
//	       System.out.println(kkk);
//	       System.out.println(desDecrypt(kkk,key));
//		System.out.println(encode2Unicode("和讯 理财客"));
//			System.out.println(md5Digest("abc123"));
	    //加密
		String c = "{'usrName':'services@hexunfsd.com','usrMail':'services@hexunfsd.com', 'usrMp':'13761109199','ip':'127.0.0.1','loginTime':'20130502221200','timeoutTS':'1365945611234'}";
		String o = desEncrypt(DEFAULT_PROVIDER,generateDesKey(key),c,JceTransformation.DESEDE_CBC,"0702030105080406");
		
		System.out.println(o);
		
		//解密
		String b = desDecrypt(DEFAULT_PROVIDER, generateDesKey(key), o, JceTransformation.DESEDE_CBC, "0702030105080406");
		System.out.println(b);*/

		
		/*String[] keys = new String[2];
		keys = CodeUtils.genDotNet3DesKeyt();
		//keys[0] = "2913CD34868558261623A437200267790783B051E3E04A9D";
		//keys[1] = "KRPNNIaFWCYWI6Q3IAJneQeDsFHj4Eqd";
		String iv = "0702030105080406";
		System.out.println("和讯DES_KEY:" + keys[0]);
		System.out.println("大网DES_KEY:" + keys[1]);

	    //加密
		String src = "{'usrName':'services@hexunfsd.com','usrMail':'services@hexunfsd.com', 'usrMp':'13761109199','ip':'127.0.0.1','loginTime':'20130502221200','timeoutTS':'1365945611234'}";
		String encode = CodeUtils.desEncryptWithDotNet(src, keys[0], iv);
		System.out.println("------密文-------------------");
		System.out.println(encode);
		System.out.println("--------------------------");
		
		//解密
		String b = CodeUtils.desDecryptWithDotNet(encode, keys[0], iv);
		System.out.println(b);*/
		String key = "和讯DES_KEY";
		System.out.println(base64Encode(key));
		Map<String,String> map = new HashMap<String,String>();
		map.put("5ZKM6K6vREVTX0tFWQ==", "111212121");
		System.out.println(map.get("5ZKM6K6vREVTX0tFWQ=="));

	}

    /**
	 * byte数组=>字符串(把byte数组转换为16进制字符串)
	 * 
	 * 例如，把byte数组{0x11,0x12,0x13}的字符串为"111213"
	 * 
	 * @param src 所要转换的byte数组
	 * @return
	 */
	public static final String byte2hex(byte[] src) {
		StringBuffer ret = new StringBuffer();
		if (!ArrayUtils.isEmpty(src)){
			for (int i = 0; i < src.length; i++) {
				byte b = src[i];
				String stmp = (Integer.toHexString(b & 0XFF));
				if (stmp.length() == 1) {
					ret.append("0").append(stmp);
				} else {
					ret.append(stmp);
				}
			}
		}
		return ret.toString();
	}

	/**
	 * 字符串=>byte数组(把16进制字符串转换为数组)
	 * 
	 * 例如，把字符串为"111213"转换为byte数组{0x11,0x12,0x13}
	 * 
	 * @param src 所要转换的字符串
	 * @return
	 */
	public static final byte[] hex2byte(String src) {
		byte [] ret = null;
		if (StringUtils.isNotBlank(src)){		
			char[] arr = src.toCharArray();
			ret = new byte[src.length() / 2];
			for (int i = 0, j = 0, l = src.length(); i < l; i++, j++) {
				StringBuffer swap = new StringBuffer().append(arr[i++]).append(arr[i]);
				int byteint = Integer.parseInt(swap.toString(), 16) & 0xFF;
				ret[j] = new Integer(byteint).byteValue();
			}
		}
		return ret;
	}
	
	/**
	 * 生产与大网交互3DES秘钥
	 * @return
	 */
	public static String[] genDotNet3DesKeyt(){
		String[] keys = new String[2];
		try{
			KeyGenerator keyGenerator = KeyGenerator.getInstance("DESede");
			keyGenerator.init(new SecureRandom());
			byte[] key = keyGenerator.generateKey().getEncoded();
			keys[0] = CodeUtils.byte2hex(key);
			keys[1] = new BASE64Encoder().encode(key);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return keys;
	}

	/**
	 * 获得deskey
	 * @param key
	 * @param algorithm
	 * @return
	 */
	public static final SecretKey generateDesKey(String key){
		JceAlgorithm algorithm = getAlgorithmByKey(key);
		SecretKey secretKey = null;
		try {
			KeySpec des = null;
			if (JceAlgorithm.DESede.equals(algorithm)) {
				des = new DESedeKeySpec(key.getBytes());
			} else {
				des = new DESKeySpec(key.getBytes());
			}
			SecretKeyFactory keyFactory = SecretKeyFactory
					.getInstance(algorithm.name());
			secretKey = keyFactory.generateSecret(des);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return secretKey;
	}
	
	/**
	 *  加载秘钥，生产秘钥对象,用于与大网进行3DES加解密用
	 * @param key
	 * @return
	 */
	public static final SecretKey generateDesKeyFor3DESDotNet(String key){
		return CodeUtils.generateDesKeyFor3DESDotNet(CodeUtils.hex2byte(key));
	}
	
	/**
	 * 加载秘钥，生产秘钥对象,用于与大网进行3DES加解密用
	 * @param key
	 * @return
	 */
	public static final SecretKey generateDesKeyFor3DESDotNet(byte[] key){
		SecretKey secretKey = null;
		try {
			KeySpec des = new DESedeKeySpec(key);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("desede");
			secretKey = keyFactory.generateSecret(des);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return secretKey;
	}
	
	/**
	 * 根据key的长度获得对应des算法
	 * @param key
	 * @return
	 */
	private static JceAlgorithm getAlgorithmByKey(String key){
		Assert.hasText(key);
		JceAlgorithm algorithm = null;
		if (key.length() == 16) {
			algorithm = JceAlgorithm.DES;
		} else if (key.length() == 48) {
			algorithm = JceAlgorithm.DESede;
		}
		return algorithm;
	}
		
	/**
	 * des加解密
	 * @param provider			- 加解密类库提供商 缺省为 SunJCE
	 * @param key				- 加解密key
	 * @param byteSrc			- 明文或密文数组
	 * @param transformation	- 传输模式 缺省为 DES/ECB/NoPadding
	 * @param iv				- 初始化向量，缺省为空
	 * @return
	 * @throws Exception
	 */
	public static byte[] desCipher(JceProvider provider,SecretKey key,byte[] byteSrc,JceTransformation transformation,byte[] iv,int mode) throws Exception {
		if (transformation == null){
			transformation = JceTransformation.DES_ECB_NOPADDING;
		}
		Assert.isTrue(!JceTransformation.DESEDE_CBC_PKCS7.equals(transformation)||
				JceTransformation.DESEDE_CBC_PKCS7.equals(transformation)
				&&JceProvider.BOUNCY_CASTLE_PROVIDER.equals(provider));
		Cipher des = Cipher.getInstance(transformation.toString(), provider.toString());
		if (iv==null||iv.length==0) {
			des.init(mode, key);
		} else {
			IvParameterSpec ivSpec = new IvParameterSpec(iv);
			des.init(mode, key, ivSpec);
		}
		return des.doFinal(byteSrc);
	}

	/**
	 * des解密
	 * @param provider			- 加解密类库提供商 缺省为 SunJCE
	 * @param key				- 解密key
	 * @param src				- 密文
	 * @param transformation	- 传输模式,为空缺省为 DES/ECB/NoPadding
	 * @param iv				- 初始化向量，缺省为空
	 * @return
	 * @throws Exception
	 */
	public static String desDecrypt(JceProvider provider,SecretKey key, String src,JceTransformation transformation,String iv) throws Exception {
		return new String(desCipher(provider,key,hex2byte(src),transformation,hex2byte(iv),Cipher.DECRYPT_MODE));
	}
	
	/**
	 * des加密
	 * @param provider			- 加解密类库提供商 缺省为 SunJCE
	 * @param key				- 加密key
	 * @param src				- plain text
	 * @param transformation	- 传输模式，为空缺省为 DES/ECB/NoPadding
	 * @param iv				- 初始化向量，缺省为空
	 * @return
	 * @throws Exception
	 */
	public static String desEncrypt(JceProvider provider,SecretKey key, String src,JceTransformation transformation,String iv) throws Exception {
		return byte2hex(desCipher(provider,key,src.getBytes("UTF-8"),transformation,hex2byte(iv),Cipher.ENCRYPT_MODE));
	}
	
	/**
	 * des加密，类库提供商为 SunJCE，传输模式为 DES/ECB/PKCS5Padding， 初始化向量为空
	 * @param src			- plain text
	 * @param key			- 加密key
	 * @return
	 * @throws Exception
	 */
	public static String desEncrypt(String src,String key) throws Exception {
		return desEncrypt(DEFAULT_PROVIDER,generateDesKey(key),src,JceTransformation.DES_ECB,null);
	}

	/**
	 * des解密，类库提供商为 SunJCE，传输模式为 DES/ECB/NoPadding， 初始化向量为空
	 * @param src
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String desDecrypt(String src,String key) throws Exception {
		return desDecrypt(DEFAULT_PROVIDER,generateDesKey(key),src,null,null);
	}
	
	/**
	 * 使用3des加密算法，对明文进行加密，与大网进行交互
	 * @param src
	 * @param key 16进制字符串
	 * @param iv  16进制字符串
	 * @return
	 * @throws Exception
	 */
	public static String desEncryptWithDotNet(String src,String key,String iv) throws Exception {
		return desEncrypt(DEFAULT_PROVIDER,CodeUtils.generateDesKeyFor3DESDotNet(key),src,JceTransformation.DESEDE_CBC,iv);
	}

	public static String desDecryptWithDotNet(String src,String key,String iv) throws Exception {
		return desDecrypt(DEFAULT_PROVIDER,CodeUtils.generateDesKeyFor3DESDotNet(key),src,JceTransformation.DESEDE_CBC,iv);
	}
	
	/**
	 * 3des加密，类库提供商为 SunJCE，传输模式为 DES/ECB/PKCS5Padding， 
	 * @param src - plain text
	 * @param key - 加密key
	 * @param vi -初始化向量为空
	 * @return
	 * @throws Exception
	 */
	public static String tripleDesEncrypt(String src,String key,String vi) throws Exception{
		return desEncrypt(DEFAULT_PROVIDER,generateDesKey(key),src,JceTransformation.DESEDE_CBC,vi);
	}
	/**
	 * 3des解密，类库提供商为 SunJCE，传输模式为 DES/ECB/NoPadding，
	 * @param src
	 * @param key
	 * @param vi -- 初始化向量为空
	 * @return
	 * @throws Exception
	 */
	public static String tripleDesDecrypt(String src,String key,String vi) throws Exception{
		return desDecrypt(DEFAULT_PROVIDER, generateDesKey(key), src, JceTransformation.DESEDE_CBC, vi);
	}

	
	
	/**
	 * MD5 摘要计算(byte[]).
	 *
	 * @param src byte[]
	 * @throws Exception
	 * @return byte[] 16 bit digest
	 */
	public static final byte[] md5Digest(byte[] src) throws Exception {
		if (ArrayUtils.isEmpty(src)) return ArrayUtils.EMPTY_BYTE_ARRAY;
		MessageDigest alg = MessageDigest.getInstance(JceAlgorithm.MD5.toString()); // MD5 is 16 bit coremessage digest
		return alg.digest(src);
	}

	/**
	 * 32位MD5 摘要计算(String).
	 *
	 * @param src String
	 * @throws Exception
	 * @return String
	 */
	public static final String md5Digest(String src) throws Exception {
		if (StringUtils.isBlank(src)) return StringUtils.EMPTY;
		char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',  'e', 'f'};  
		byte tmp[] = md5Digest(src.getBytes("UTF-8"));
		char str[] = new char[16 * 2]; 
		int k = 0;                                
		for (int i = 0; i < 16; i++) {        
			byte byte0 = tmp[i];                
			str[k++] = hexDigits[byte0 >>> 4 & 0xf];  
			str[k++] = hexDigits[byte0 & 0xf];           
		} 
		return new String(str);
	}
	

	/**
	 * SHA-1 摘要计算(byte[]).
	 * 
	 * @param src byte[]
	 * @return Exception
	 * @throws Exception
	 */
	public static final byte[] sha1Digest(byte[] src) throws Exception {
		if (ArrayUtils.isEmpty(src)) return ArrayUtils.EMPTY_BYTE_ARRAY;
		MessageDigest alg = MessageDigest.getInstance(JceAlgorithm.SHA1.toString());
		alg.update(src);
		return alg.digest();	
	}

	/**
	 * SHA-1 摘要计算(String).
	 * 
	 * @param src String
	 * @return Exception
	 * @throws Exception
	 */
	public static final String sha1Digest(String src) throws Exception {
		if (StringUtils.isBlank(src)) return StringUtils.EMPTY;
		return new String(CodeUtils.sha1Digest(src.getBytes()));
	}

	/**
	 * BASE64 编码(String).
	 *
	 * @param src String inputed string
	 * @return String returned string
	 */
	public static final String base64Encode(String src)  {
		if (StringUtils.isBlank(src)) return StringUtils.EMPTY;
		return new String(new Base64().encode(src.getBytes()));
	}

	/**
	 * BASE64 编码(byte[]).
	 *
	 * @param src byte[] inputed string
	 * @return String returned string
	 */
	public static final String base64Encode(byte[] src) {
		if (ArrayUtils.isEmpty(src)) return StringUtils.EMPTY;
		return new String(new Base64().encode(src));
	}

	/**
	 * BASE64 解码(String).
	 *
	 * @param src String inputed string
	 * @return String returned string
	 */
	public static final String base64Decode(String src) throws Exception{
		if (StringUtils.isBlank(src)) return StringUtils.EMPTY;
		return new String(base64DecodeToBytes(src));
	}

	/**
	 * BASE64 解码(byte[]).
	 *
	 * @param src String inputed string
	 * @return String returned string
	 */
	public static final byte[] base64DecodeToBytes(String src) throws Exception{
		if (StringUtils.isBlank(src)) return ArrayUtils.EMPTY_BYTE_ARRAY;
		return new Base64().decode(src.getBytes());
	}
	
	/**
	 * 对给定字符进行 URL 编码(GB2312).
	 *
	 * @param src String
	 * @return String
	 */
	public static final String urlEncode(String src) throws Exception{
		if (StringUtils.isBlank(src)) return StringUtils.EMPTY;
		return URLEncoder.encode(src, GB2312_CHARSET);
	}

	/**
	 * 对给定字符进行 URL 编码(charset).
	 *
	 * @param src String
	 * @param charset String
	 * @return String
	 */
	public static final String urlEncode(String src,String charset) throws UnsupportedEncodingException{
		if (StringUtils.isBlank(src)) return StringUtils.EMPTY;
		return URLEncoder.encode(src, charset);
	}
	
	/**
	 * 对给定字符进行 URL 解码(GB2312).
	 * 
	 * @param src String
	 * @return String
	 */
	public static final String urlDecode(String src) throws Exception{
		if (StringUtils.isBlank(src)) return StringUtils.EMPTY;
		return URLDecoder.decode(src, GB2312_CHARSET);
	}

	/**
	 * 对给定字符进行 URL 解码(charset).
	 * @param src
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public static final String urlDecode(String src,String charset) throws Exception{
		if (StringUtils.isBlank(src)) return StringUtils.EMPTY;
		return URLDecoder.decode(src, charset);
	}


	/**
	 * 将一个字串编码成unicode
	 * @param src
	 * @return
	 */
    public static final String encode2Unicode(String src){
		if (StringUtils.isEmpty(src)) return StringUtils.EMPTY;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < src.length(); i++) {
			sb.append(encode2Unicode(src.charAt(i)));
		}
		return sb.toString();
    }
    
    /**
     * 将一个字符编码成unicode
     * @param c
     * @return
     */
    public static final String encode2Unicode(char c){
    	StringBuffer sb = new StringBuffer();
    	String tmp;
    	if (c > 255) {
			sb.append("\\u");
			//高8位
			tmp = Integer.toHexString(c >>> 8);
			if (tmp.length() == 1) sb.append("0");
			sb.append(tmp);
			//低8位
			tmp = Integer.toHexString(c & 0xff);
			if (tmp.length() == 1) sb.append("0");
			sb.append(tmp);			
		} else {
			sb.append(c);
		}
    	return sb.toString();
    }
    
    /**
     * 将一个字串编码成html所用的某种特殊的unicode
     * @param src
     * @return
     */
    public static final String encode2HtmlUnicode(String src){
		if (StringUtils.isEmpty(src)) return StringUtils.EMPTY;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < src.length(); i++) {	
			sb.append(encode2HtmlUnicode(src.charAt(i)));
		}
		return sb.toString();
    }

    /**
     * 将一个字符编码成html所用的某种特殊的unicode
     * @param c
     * @return
     */
    public static final String encode2HtmlUnicode(char c){
		if (c > 255) {
			return new StringBuffer().append("&#").append(c & 0xffff).append(";").toString();
		} else {
			return String.valueOf(c);
		}
    }
    
    
    public enum JceTransformation {
    	DES_ECB("DES/ECB/PKCS5Padding"),
    	DES_CBC("DES/CBC/PKCS5Padding"),
    	DES_ECB_NOPADDING("DES/ECB/NoPadding"),
    	DESEDE_CBC("DESede/CBC/PKCS5Padding"),
    	DESEDE_CBC_PKCS7("DESede/CBC/PKCS7Padding"),
    	DESEDE_ECB("DESede/ECB/PKCS5Padding");
		private JceTransformation(String transformation) {
			this.transformation = transformation;
		}
		private String transformation;
    	public String toString() {
    		return transformation;
    	};
    }
    
    //**3DES加密秘钥类型
    //LCK_HS_DES3 与恒生交互秘钥
    //LCK_HEXUN_DES3 与和讯大网交互秘钥类型
    public enum DES3_KEY_TYEP{
    	LCK_HS_DES3,LCK_HEXUN_DES3
    }
    
    public enum JceAlgorithm{
    	AES,DES,DESede,RSA,SHA1("SHA-1"),MD5;
		private JceAlgorithm() {
			this.algorithm = name();
		}
		private JceAlgorithm(String algorithm) {
			this.algorithm = algorithm;
		}
    	private String algorithm;
		public String toString() {
    		return algorithm;
    	}
    }
    
    public enum JceProvider{
    	SUN_PROVIDER("SunJCE"),BOUNCY_CASTLE_PROVIDER("BC");
		private JceProvider(String provider) {
			this.provider = provider;
		}
		private String provider;
    	public String toString(){
    		return provider;
    	}
    }
	
	public static final JceProvider DEFAULT_PROVIDER = JceProvider.SUN_PROVIDER;    
	public static final JceAlgorithm DEFAULT_DES_ALGORITHM = JceAlgorithm.DES;	
	public static final String DEFAULT_IV = "0102030405060708"; 
 

	public static final String GBK_CHARSET = "GBK";
	public static final String GB2312_CHARSET = "gb2312";
	public static final String UTF8_CHARSET = "utf-8";
	public static final String ISO_CHARSET = "iso-8859-1";
	
	public static final String DEFAULT_CHARSET = UTF8_CHARSET;	
		
	private CodeUtils(){}
	
	
    
	public static final String DEFAULT_ENCODING = "UTF-8";
	
	public static String fuzzify(String input) {
		try {
			byte[] bytes = Base64
					.encodeBase64(input.getBytes(DEFAULT_ENCODING));
			swap(bytes);
			return new String(bytes);
		} catch (UnsupportedEncodingException e) {
			return input;
		}
	}

	public static String defuzzify(String input) {
		try {
			byte[] bytes = input.getBytes();
			swap(bytes);
			return new String(Base64.decodeBase64(bytes), DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e) {
			return input;
		}
	}

	private static void swap(byte[] bytes) {
		int half = bytes.length / 2;
		for (int i = 0; i < half; i++) {
			byte temp = bytes[i];
			bytes[i] = bytes[half + i];
			bytes[half + i] = temp;
		}
	}

	public static String swap(String str) {
		char[] chars = str.toCharArray();
		int half = chars.length / 2;
		for (int i = 0; i < half; i++) {
			char temp = chars[i];
			chars[i] = chars[half + i];
			chars[half + i] = temp;
		}
		return new String(chars);
	}

	
	

}

