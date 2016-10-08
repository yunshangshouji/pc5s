/*
 * @(#)RegexHelper.java 1.0 2009-12-28下午02:53:50
 *
 * 和讯信息科技有限公司-第三方理财事业部-第三方理财事业部
 * Copyright (c) 1996-2012 HexunFSD, Inc. All rights reserved.
 */
package zhuboss.framework.util;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * <dl>
 *    <dt><b>Title:</b></dt>
 *    <dd>
 *    	Regular Expressions Helper
 *    </dd>
 *    <dt><b>Description:</b></dt>
 *    <dd>
 *    	<p>正则表达式帮助类 
 *    </dd>
 * </dl>
 *
 * @author eric
 * @version 1.0, 2009-12-28
 * @since framework-1.4
 * 
 */
@SuppressWarnings("all")
public class RegexHelper {
	
	/**
	 * 利用正则表达式从文本text中找到夹在两个关键字中间的部分(keyword1)(.+)(keyword2)
	 * @param text
	 * @param keyword1
	 * @param keyword2
	 * @return
	 */
	public static final String getStrBetweenKeywords(String text,String keyword1,String keyword2){		
		Pattern p = Pattern.compile( new StringBuffer()
		  									.append("(").append(keyword1).append(")")
		  									.append("(.+)")
		  									.append("(").append(keyword2).append(")")
		  									.toString(),
		  								Pattern.CASE_INSENSITIVE);	    
		Matcher m = p.matcher(text);
		while(m.find()){
		  return m.group(2);
		}	    
		return null;
	}
	
	/**
	 * 利用正则表达式从文本text中找到夹在两个关键字中间的部分(keyword1)(.+?)(keyword2)，懒惰模式匹配
	 * @param text 
	 * @param keyword1
	 * @param keyword2
	 * @return
	 */
	public static final String[] getLazyStrBetweenKeywords(String text,String keyword1,String keyword2){	
		String [] ret = new String[0];
		Set<String> tmp = new HashSet<String>();
		Pattern p = Pattern.compile( new StringBuffer()
		  									.append("(").append(keyword1).append(")")
		  									.append("(.+?)")
		  									.append("(").append(keyword2).append(")")
		  									.toString(),
		  								Pattern.CASE_INSENSITIVE);	    
		Matcher m = p.matcher(text);
		while(m.find()){
			tmp.add(m.group(2));			
		}	    
		return tmp.toArray(ret);
	}
	
	
	/**
	 * 利用正则表达式检查是否完整匹配
	 * @param text
	 * @param reg
	 * @return
	 */
	public static final boolean isMatch(String text,String reg){
		if (StringUtils.isNotEmpty(text)&&StringUtils.isNotBlank(reg)){
			Pattern p = Pattern.compile(reg); 
			Matcher m = p.matcher(text);
			return m.matches();
		}
 		return false;
	}
	
	/**
	 * 利用正则表达式检查是否包含
	 * @param text
	 * @param reg
	 * @return
	 */
	public static final boolean isContain(String text,String reg){
		if (StringUtils.isNotEmpty(text)&&StringUtils.isNotBlank(reg)){
			Pattern p = Pattern.compile(reg); 
			Matcher m = p.matcher(text);
			return m.find();
		}
 		return false;
	}
	
	
	private RegexHelper(){}
	
	public static void main(String[] args) {
	}
	
}
