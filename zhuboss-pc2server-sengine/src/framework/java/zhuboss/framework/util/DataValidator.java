/*
 * @(#)DataValidator.java 1.0 2009-12-28下午02:49:32
 *
 * 和讯信息科技有限公司-第三方理财事业部-第三方理财事业部
 * Copyright (c) 1996-2012 HexunFSD, Inc. All rights reserved.
 */
package zhuboss.framework.util;

import java.text.ParseException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;


/**
 * <dl>
 *    <dt><b>Title:</b></dt>
 *    <dd>
 *    	数据校验器
 *    </dd>
 *    <dt><b>Description:</b></dt>
 *    <dd>
 *    	<p>facade of all kinds of relative utils,mainly be designed for using outside the framework
 *    </dd>
 * </dl>
 *
 * @author eric
 * @version 1.0, 2009-12-28
 * @since framework-1.4
 * 
 */
public class DataValidator {
	
	/**
	 * 判断字符串是否为空
	 * 
	 * 没办法,有的程序员不习惯用Apache Common或Spring的工具类
	 * 
	 * @param str
	 * @return
	 */
	public static final boolean isStrNotBlank(String str){
		return StringUtils.isNotBlank(str);
	}
	
	/**
	 * 检查字符串是否为空
	 * @param str
	 */
	public static final void checkStringNotBlank(String str){
		Assert.hasText(str);
	}

	/**
	 * 检查字符串是否为空，并且长度是否为指定值
	 * @param str 
	 * @param l 长度
	 * @return
	 */
	public static final boolean isStrLenEqual(String str,int l){
		return (StringUtils.isNotBlank(str)&&(str.trim().length()==l));
	}
	
	/**
	 * 检查字符串是否为空，并且长度是否小于指定值
	 * @param str 
	 * @param l 长度
	 * @return
	 */
	public static final boolean isStrLenLess(String str,int l){
		return (StringUtils.isNotBlank(str)&&(str.trim().length()<l));
	} 
	
	/**
	 * 检查字符串是否为空，并且长度是否小等于指定值
	 * @param str 
	 * @param l 长度
	 * @return
	 */
	public static final boolean isStrLenLessEqual(String str,int l){
		return (StringUtils.isNotBlank(str)&&(str.trim().length()<=l));
	} 
	
	/**
	 * 判断（如"0123","123L","12.3S"等带有小数点和后缀的）字串，是否代表数字类型
	 * @param str
	 * @return
	 */
	public static final boolean isNumber(String str){
		return NumberUtils.isNumber(str);
	}
	
	/**
	 * 检查（如"0123","123L","12.3S"等带有小数点和后缀的）字串，是否代表数字类型
	 * @param str
	 */
	public static final void checkNumber(String str){
		Assert.isTrue(isNumber(str),"'"+str+"' must be a Number format here.");
	}
	
	/**
	 * 判断字符串中只含有数字字符
	 * @param str
	 * @return
	 */
	public static final boolean isDigits(String str){
		return NumberUtils.isDigits(str);
	}
	
	/**
	 * 判断Long、Integer、Short、Double、Float等数字是否为空或者0
	 * @param number
	 * @return
	 */
	public static final boolean isNumberNotNullOrZero(Number number){
		return (number!=null&& number.doubleValue()!=0);
	}
	
	/**
	 * 检查Long、Integer、Short、Double、Float等数字是否为空或者0
	 * @param number
	 */
	public static final void checkNumberNotNullOrZero(Number number){
		Assert.isTrue(isNumberNotNullOrZero(number),"Number must not be null or zero.");
	}
	

	
	/**
	 * 判断字串是否符合指定的日期格式
	 * @param str
	 * @return
	 */	
	public static final boolean isDateStrMatched(String aDateStr,String formatter){
		try {
			DateUtils.parser(aDateStr, formatter);
		} catch (ParseException e) {
			return false;
		}
		return true;		
	}	
	
	/**
	 * 判断对象数组是否为空
	 * @param <T>
	 * @param object
	 * @return
	 */
	public static final boolean isArrayNotEmpty(Object[] object){
		return !ArrayUtils.isEmpty(object);
	}
	
	/**
	 * 检查对象数组是否为空
	 * @param <T>
	 * @param object
	 */
	public static final void checkArrayNotEmpty(Object[] object){
		Assert.notEmpty(object);
	}
	
	
	/**
	 * 判断ip格式是否正确
	 * @param ip
	 * @return
	 */
	public static final boolean isIPAddress(String ip){
 		return RegexHelper.isMatch(ip,IP);
	}
	
	/**
	 * 判断密码是否6到12位
	 * false 为非6-12位或汉字
	 * */
	public static final boolean isPasswordFormat(String password){
 		return RegexHelper.isMatch(password,"^(.{6,12})$")&&RegexHelper.isMatch(password,"^([\\x00-\\xff]+)");
	}
	
	/**
	 * 判断email格式是否正确
	 * @param email
	 * @return
	 */
	public static final boolean isEmail(String email){
		return RegexHelper.isMatch(email,EMAIL);
	}
	
	/**
	 * 判断电话号码格式是否正确
	 * @param phoneNum
	 * @return
	 */
	public static final boolean isPhoneNum(String phoneNum){
		return RegexHelper.isMatch(phoneNum,PHONE);
	}
	
	/**
	 * 判断手机号码格式是否正确
	 * @param cellPhoneNum
	 * @return
	 */
	public static final boolean isCellPhoneNum(String cellPhoneNum){
		/*
		 * 2011-05-09 lele.feng
		 * 手机号格式判断1开头11位数字
		 * start
		 */
		//return RegexHelper.isMatch(cellPhoneNum,"^((\\(\\d{2,3}\\))|(\\d{3}\\-))?1(3|5|8)\\d{9}$");
		return RegexHelper.isMatch(cellPhoneNum,CELLPHONE);
		/*
		 * 2011-05-09 lele.feng
		 * end
		 */
	}
	
	/**
	 * 判断邮编格式是否正确
	 * @param postalcode
	 * @return
	 */
	public static final boolean isPostalcode(String postalcode){
		return RegexHelper.isMatch(postalcode,POSTCODE);
	}
	
	/**
	 * 判断是否是客户号
	 * @param custId
	 * @return
	 */
	public static final boolean isCustId(String custId){
		return RegexHelper.isMatch(custId,CUSTID);
	}
	
	/**
	 * 判断是否是商户号
	 * @param merId
	 * @return
	 */
	public static final boolean isMerId(String merId){
		return RegexHelper.isMatch(merId,MERID);
	}
	
	
	/**
	 * 判断是否正确银行卡号
	 */
	public static final boolean isCardNo(String cardNo){
		return RegexHelper.isMatch(cardNo, CARDNO);
	}
	
	/**
	 * 判断身份证号是否正确
	 * @param idNo
	 * @return
	 */
	public static final boolean isIdNo(String idNo){
		boolean ret = false;
		if (StringUtils.isNotBlank(idNo)){

			idNo = idNo.toUpperCase();
			
			if (idNo.length()==18&&RegexHelper.isMatch(idNo, IDNO18)){
				//18位检查校验位
				int[] ai = new int[17];// AI
				int sum = 0;
				for (int i = 0; i < 17; i++) {
					ai[i] = Integer.parseInt(idNo.substring(i, i + 1));
				}
				for (int i = 0; i < WI.length; i++) {
					sum = sum + WI[i] * ai[i];
				}
				int r = sum % 11;
				String tmp = (r == 2 ? "X" : Integer.toString(VI[r]));
				ret = tmp.equals(idNo.substring(17, 18));
				
			}else if(idNo.length()==15&&RegexHelper.isMatch(idNo, IDNO15)){
				ret = true;
			}		
		}
		_logger.info("checkIdNoFormat[ret]:"+ret);
		return ret;
	}
	
	private final static Logger _logger = Logger.getLogger(DataValidator.class);
	
	private static int[] WI = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };// WI
	private static int[] VI = { 1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2 };// 校验码
	private static String IDNO15 = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
	private static String IDNO18 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$";
	private static String EMAIL = "^([a-zA-Z0-9_\\.\\-]+)(@{1})([a-zA-Z0-9_\\.\\-]+)(\\.[a-zA-Z0-9]{1,3})$";
	private static String PHONE = "^((\\(\\d{2,3}\\))|(\\d{3}\\-))?(\\(0\\d{2,3}\\)|0\\d{2,3}-)?[1-9]\\d{6,7}(\\-\\d{1,4})?$";
	private static String IP = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
	private static String CELLPHONE = "^((\\(\\d{2,3}\\))|(\\d{3}\\-))?1\\d{10}$";
	private static String POSTCODE = "^[1-9]\\d{5}$";
	private static String CUSTID = "[6][0]{4}[0-9]{11}";
	private static String MERID = "[6|8][0-9]{5}";
	private static String CARDNO = "(^[0-9]{12,25}$)";
	
	private DataValidator(){}
	
	
	
	
}
