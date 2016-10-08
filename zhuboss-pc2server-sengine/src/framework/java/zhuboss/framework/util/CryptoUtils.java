/*
 * @(#)CryptoUtils.java 1.0 2012-10-8上午11:54:08
 *
 * 和讯信息科技有限公司 - 第三方理财事业部
 * Copyright (c) 1996-2012 HexunFSD, Inc. All rights reserved.
 */
package zhuboss.framework.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

public class CryptoUtils {
	
	private static Log logger = LogFactory.getLog(CryptoUtils.class);
	
	public static final JceAlgorithm DEFAULT_DES_ALGORITHM = JceAlgorithm.DES;
	public static final String DEFAULT_IV = "0102030405060708";
	public static final String GBK_CHARSET = "GBK";
	public static final String GB2312_CHARSET = "gb2312";
	public static final String UTF8_CHARSET = "utf-8";
	public static final String ISO_CHARSET = "iso-8859-1";
	public static final String DEFAULT_CHARSET = "utf-8";
	public static final String DEFAULT_ENCODING = "UTF-8";
	  
	/**
	 * SHA1加密
	 * @param text
	 * @return
	 */
	public static String SHA1Digest(String text) {
    	String pwd = "";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			md.update(text.getBytes());
			pwd = byte2hex(md.digest()); 
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
    	return pwd;
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
	 * urlDecode解码
	 * @param src
	 * @return
	 * @throws Exception
	 */
	public static final String urlDecode(String src) throws Exception {
		if (StringUtils.isBlank(src)) return "";
		return URLDecoder.decode(src, "gb2312");
	}

	/**
	 * urlDecode解码
	 * @param src
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public static final String urlDecode(String src, String charset) throws Exception {
		if (StringUtils.isBlank(src)) return "";
		return URLDecoder.decode(src, charset);
	}
	
	/**
	 *  md5加密
	 * @param src
	 * @return
	 * @throws Exception
	 */
	public static final byte[] md5Digest(byte[] src) throws Exception{
		if (ArrayUtils.isEmpty(src)) return ArrayUtils.EMPTY_BYTE_ARRAY;
		MessageDigest alg = MessageDigest.getInstance(JceAlgorithm.MD5.toString());
		return alg.digest(src);
	}

	/**
	 * md5加密
	 * @param src
	 * @return
	 * @throws Exception
	 */
	public static final String md5Digest(String src) {
		if (StringUtils.isBlank(src)) return "";
		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		byte[] tmp = null;
		try {
			tmp = md5Digest(src.getBytes("UTF-8"));
		}  catch (Exception e) {
			throw new RuntimeException(e);
		}
		char[] str = new char[32];
		int k = 0;
		for (int i = 0; i < 16; i++) {
			byte byte0 = tmp[i];
			str[(k++)] = hexDigits[(byte0 >>> 4 & 0xF)];
			str[(k++)] = hexDigits[(byte0 & 0xF)];
		}
		return new String(str);
	}
	  
	/**
	 * PhoneApi-verifyCode加密通用方法
	 * @param queryString
	 * @return
	 */
	public static String getVerifyCode(String queryString){
		String verifyCode = null;
		try{
			Assert.notNull(queryString);
			Map<String,String> map = new HashMap<String, String>(); 
			String[] params = queryString.split(";");
			for (String str : params) {
				String[] strs = str.split(":");
				try{
					map.put(strs[0], CryptoUtils.urlDecode(strs[1], CryptoUtils.UTF8_CHARSET));
				}catch(Exception e){
					map.put(strs[0], "");
				}
			}
			String nowDate=DateUtils.getCurrentDate();
			StringBuffer raw= new StringBuffer().append("HEXUNFSD"+nowDate+CryptoUtils.urlDecode(queryString, CryptoUtils.UTF8_CHARSET));
			verifyCode=CryptoUtils.md5Digest(raw.toString());
		}catch(Throwable t){
			logger.error(t.getMessage(),t);
		}
		return verifyCode;
	}
	
	/**
	 * 内部枚举类
	 * <dl>
	 *    <dt><b>Title:</b></dt>
	 *    <dd>
	 *    	none
	 *    </dd>
	 *    <dt><b>Description:</b></dt>
	 *    <dd>
	 *    	<p>none
	 *    </dd>
	 * </dl>
	 *
	 * @author tl
	 * @version 1.0, 2014-3-25
	 * @since licaike-framework
	 *
	 */
	public static enum JceAlgorithm{
	    AES, DES, DESede, RSA, SHA1("SHA-1"), MD5;
	    
	    private String algorithm;

	    private JceAlgorithm() { this.algorithm = name(); }

	    private JceAlgorithm(String algorithm) {
	    	this.algorithm = algorithm;
	    }

	    public String toString() {
	      return this.algorithm;
	    }
	}
	
	/**
	 * 为了和cprdev兼容，提供一个方法，专门与使用cprdev的项目交互之用
	 * @param src
	 * @param isConvertToUpcase
	 * @return
	 * @throws Exception
	 */
	public static final String md5Digest4CPRDEV(String src) {		
		try {
			return CodeUtils.md5Digest(src);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) throws Exception {
//		String verifyCode=md5Digest("HEXUNFSD20140325name:hxck_fsd_lcksso;rnd:1112232365444");
//		System.out.println("verifyCode:"+verifyCode);
	}
	
	/**
	 * PhoneApi-verifyCode加密通用方法
	 * @param queryString
	 * @return
	 */
	public static String getVerifyCode(String queryString, String transVerifyKey){
		String verifyCode = null;
		try{
			Assert.notNull(queryString);
			Assert.notNull(transVerifyKey);
			Map<String,String> map = new HashMap<String, String>(); 
			String[] params = queryString.split(";");
			for (String str : params) {
				String[] strs = str.split(":");
				try{
					map.put(strs[0], CryptoUtils.urlDecode(strs[1], CryptoUtils.UTF8_CHARSET));
				}catch(Exception e){
					map.put(strs[0], "");
				}
			}
			String nowDate=DateUtils.getCurrentDate();
			StringBuffer raw= new StringBuffer().append(transVerifyKey+nowDate+CryptoUtils.urlDecode(queryString, CryptoUtils.UTF8_CHARSET));
			verifyCode=CryptoUtils.md5Digest(raw.toString());
		}catch(Throwable t){
			logger.error(t.getMessage(),t);
		}
		return verifyCode;
	}
	
}
