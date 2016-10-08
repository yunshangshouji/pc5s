package zhuboss.framework.util.nginx;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import zhuboss.framework.util.JavaUtil;

/** 
 * @author 作者 zhuzhengquan: 
 * @version 创建时间：2015年9月29日 上午9:46:21 
 * 类说明 
 */
public class LogRead {
	final static Pattern pattern = Pattern.compile("(\\\\x[0-9|A-E]{2})");
	public static String convertText(String text) throws UnsupportedEncodingException, IOException{
		if(text==null) return text;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Matcher matcher = pattern.matcher(text);
		while(matcher.find()){
			String s = matcher.group(0);
			int c = Integer.parseInt(s.substring(2),16);
			StringBuffer sb = new StringBuffer();
			matcher.appendReplacement(sb,"");
			bos.write(sb.toString().getBytes("UTF-8"));
			bos.write(c);
		}
		StringBuffer sb = new StringBuffer();
		matcher.appendTail(sb);
		bos.write(sb.toString().getBytes("UTF-8"));
		return bos.toString("UTF-8");
	}
	
	public static void main(String[] args) throws Exception{
		FileInputStream fis = new FileInputStream(new File("c:\\nginx.txt"));
		String text = JavaUtil.InputStreamTOString(fis, "UTF-8");
		System.out.println(convertText(text));
	}
}
