package zhuboss.framework.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 字符串格式化处理类，用于发送给银行时的格式化字符串
 * @author Binbin
 * @date   2010/06/29
 */
public class StringUtil {

	//private static Logger log = LoggerFactory.getLogger("system");
	/**
     * 格式化要传输的String域<br>
     * 如果要格式化的字符串长度超出指定长度length，则会截取为后length位；
     * 如果该field为变长的，并且原始串orign的长度位数超出了指定的长度位数所能表示的
     * 最大长度，则在长度位只赋予所能表示的最大长度，例如：对于一个变长域，orign的长
     * 度是120位，而指定的数据值长度表示位为两位，则数据值长度表示位只能是99。在正常
     * 情况下上述两种问题应该都不会出现。
     * @param orign 要格式化的原始串
     * @param length 串长
     * @param direction 补位方向
     * @param flexible 是否变长
     * @param digitLength 数据值长度表示位的长度
     * @param filling 补位的字符
     * @return 格式化后的字符串
     */
	public static String formatField(
        String orign,
        int length,
        String direction,
        boolean flexible,
        int digitLength,
        char filling) {
        String res = orign;
        if(res.length()>length) {
            res = res.substring(res.length()-length);
        }
        String tmp = res;
        if(direction.toLowerCase().equals("left")) {
            //for (int i=0; i<length-tmp.length(); i++) {
            for (int i=0; i<length-tmp.getBytes().length; i++) {  //modified by Binbin 2010/08/17 证件号码中存在有中文
                res = filling + res;
            }
        } else {
            //for (int i=0; i<length-tmp.length(); i++) {
        	for (int i=0; i<length-tmp.getBytes().length; i++) {  //modified by Binbin 2010/08/17 证件号码中存在有中文
                res = res + filling;
            }
        }

        if(flexible){
//	            String prefix = String.valueOf(orign.length());
        	String prefix = String.valueOf(orign.getBytes().length);//modified by Binbin 2010/08/17 证件号码中存在有中文
            tmp = prefix;
            if(prefix.length()>digitLength){
                long digit = 9;
                for(int i=0; i<digitLength; i++){
                    digit += 9*i*10;
                }
                prefix = String.valueOf(digit);
            }else{
                for(int i=0;i<digitLength-tmp.length();i++){
                    prefix = "0" + prefix;
                }
            }
            res = prefix + res;
        }

        return res;
    }
	
	/**
	 * 加工发送给银联的字符串
	 * @param orgStr   需要加工的字符串
	 * @param len      CP明码报文长度
	 * @param direct   补齐长度的方向，left 左侧补齐  right 右侧补齐
	 * 
	 * @param chr	        需补齐的数值	
	 * @param type	        字段长度类型   0 定长    1 变长
	 * @param splitFlag	如果原始字符串超出CP所要求的长度,left 表示取左侧的字符串  right表示取右侧的字符串
	 * @return  返回处理后的明码报文
	 * @author Binbin
	 */
	public static String cpFormatString(String orgStr,int len,String direct,String chr,String type,String splitFlag){
		String res = orgStr;
		byte[] orgStrs = orgStr.getBytes();
		int orgLength = orgStrs.length;
		/*
		 * 判断长度数据
		 */
		if(orgLength > len){
			//字符串的长度超出银联所需要的长度
			byte[] temp = new byte[len];
			//取从左到右的字符串，如地址，但是可能出现乱码问题，需处理
			if("left".equalsIgnoreCase(splitFlag)){
				for (int i = 0; i < len; i++) {
					temp[i] = orgStrs[i];			//截取后的字符串
				}
			}else{
				for (int i = 0,j = orgLength-len; i < len; i++,j++) {
					temp[i] = orgStrs[j];
				}
			}
			res = new String(temp);
		}
		String tempStr = res;
		
		/*
		 * 补齐长度，补CHR
		 */
		if("left".equals(direct)){  //左补
			for (int i = 0; i < len - orgLength; i++) {
				//System.out.println("======");
				res = chr + res;
			}
		}else{						//右补
			for (int i = 0; i < len - orgLength; i++) {
				//System.out.println("======");
				res = res + chr;
			}
		}
		if("1".equalsIgnoreCase(type)){  //变长参数
			int strLen = tempStr.getBytes().length; 
			String prefix = String.valueOf(strLen);
			if(strLen < 10 && strLen >= 0){
				prefix = "0" + prefix;
			}
			res = prefix + res;
		}
		
		return res;
	}
	
    /**
     * 格式化交易金额，并将其转为以分为单位
     * @param orign 交易金额（元）
     * @return 交易金额（分）
     * @throws NumberFormatException
     */
	public static String tansMoney(String orign) throws NumberFormatException {
        DecimalFormat decFormat;
        String sFormat = "####.00";
        String sNumber = "";
        try {
            decFormat =  new DecimalFormat(sFormat);
            sNumber  = decFormat.format(Double.parseDouble(orign));
            if(sNumber.equals(".00")) sNumber="0";
            sNumber = String.valueOf(
                    new Double(Double.parseDouble(sNumber)*100).longValue());
        } catch  (NumberFormatException e) {
            System.out.println("Exception: " + e.getMessage());
            throw e;
        }

        return sNumber;
    }

    /**
     * 元为单位的＝》分为单位5=>10
     * @param input
     * @return
     */
    public static String formatYuanToFen(String input){
    	String out="";
    	NumberFormat ft=NumberFormat.getInstance();
    	Number nbInput;
    	try {
			nbInput=ft.parse(input);
			double fInput=nbInput.doubleValue()*100.0;
			ft.setGroupingUsed(false);
			ft.setMaximumFractionDigits(0);
			out=ft.format(fInput);
		} catch (ParseException e) {
			//log.error("金额元转分异常",e);
		}
    	return out;
    }
    
    /**
     * 小数转换为百分数
     * 
     * @param orign
     * @return
     * @throws NumberFormatException
     */
	public static String tansPercentage (String orign) throws NumberFormatException {
        DecimalFormat decFormat;
        String sFormat = "####.0000";
        String sNumber = "";
        try {
            decFormat =  new DecimalFormat(sFormat);
            sNumber  = decFormat.format(Double.parseDouble(orign));
          /*  System.out.println(sNumber);*/
            if(sNumber.equals(".00")) 
            	{sNumber="0";}
            else if(sNumber.equals(".0000")){
            	sNumber="0.00";
            } else{
            	sNumber = String.valueOf(
            			new Double(Double.parseDouble(sNumber)*100).floatValue());
            }
        } catch  (NumberFormatException e) {
            System.out.println("Exception: " + e.getMessage());
            throw e;
        }

        return sNumber + "%";
    }

    /**
     * 字符串去空格，如为null返回空字符串
     * @param str String
     * @return String
     */
    public static String formatNullSpace(final String str){
        String res="";
        if(str!=null){
            res=str.trim();
        }
        return res;
    }
    
    /**
	 * 判断是否为空
	 * @param paramString
	 * @return
	 */
	public static boolean isEmpty(String paramString) {
		return ((paramString == null) || ("".equals(paramString)) || (paramString
				.trim().length() == 0) || ("null".equalsIgnoreCase(paramString)));
	}
	/**
	 * 判断对象是否为空
	 * @param paramString
	 * @return
	 */
	public static boolean isEmpty(Object obj) {
		if(obj==null)
			return true;
		if (obj instanceof List) {
			if(((List) obj).size()==0)
				return true;
		}
		return isEmpty(obj.toString());
	}
	
    /**
     * 格式成元.角分
     * 舍入模式为ROUND_HALF_EVEN
     * @param input 以元为单位
     * @return
     */
    public static String format2Yuanjiaofen(String input){
		NumberFormat ft=NumberFormat.getInstance();
		String out="";
			try {
				Number num=ft.parse(input);
				ft.setGroupingUsed(false);
				ft.setMinimumFractionDigits(2);
				ft.setMaximumFractionDigits(2);
				out=ft.format(num);
//				System.out.println("格式成元.角分为"+out);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
		return out;
	}
    
    /**
	 * 过滤HTML
	 * 
	 * @Author: Huangzj
	 * @CreateDate: Mar 22, 2010
	 * @Return
	 * @Edit
	 */
	public static String filterHtml(String input) {
		if (input == null) {
			return null;
		}
		if (input.length() == 0) {
			return input;
		}
		input = input.replaceAll("&", "&amp;");
		input = input.replaceAll("<", "&lt;");
		input = input.replaceAll(">", "&gt;");
		input = input.replaceAll(" ", "&nbsp;");
		input = input.replaceAll("'", "&#39");
		input = input.replaceAll("\"", "&quot");
		input = input.replaceAll("\n", "<br>");
		return input;
	}
	
	/**
	 * 格式化金额，添加千分位
	 * @param amt 金额
	 * @param decimalDigits 保留几位小数
	 * @return
	 */
	public static String format(String amt, int decimalDigits) {
		double number = 0d;
		if(isEmpty(amt)){
			return "0.00";
		}
		try{
			number = Double.parseDouble(amt);
		}catch (Exception e) {
			//log.error("金额转换出错：传入金额为["+amt+"]");
			return "0.00";
		}
		if (number == 0d) number = 0d;
		
		boolean flag=false;
		if(decimalDigits < 0) {
			return "0.00";
		}
		
		String pattern = "###,###,###,###,###,###";
		if(decimalDigits > 0) {
			flag=true;
			pattern += ".";
			for(int i=0;i<decimalDigits;i++) {
				pattern += "0";
			}
		}
		
		DecimalFormat df = new DecimalFormat(pattern);
		if (number <= -1d){
			return df.format(number);
		}else if (number > -1d && number < 0d){
			return "-0"+df.format(number).substring(1);
		}else if (number >= 0d && number < 1d){
			if(flag==true){
				return "0"+df.format(number);
			}else{
				return df.format(number);
			}
		}else{
			return df.format(number);
		}
	}
	
	public static String format2(String amt, int bf) {
		double number = 0d;
		if(isEmpty(amt)){
			return "0.00";
		}
		try{
			number = Double.parseDouble(amt);
		}catch (Exception e) {
			//log.error("金额转换出错：传入金额为["+amt+"]");
			return "0.00";
		}
		DecimalFormat df = new DecimalFormat("###0.00");
		number = number*bf;
		return df.format(number);
	}
	
	/**
	 * 格式化时间参数
	 * 
	 * @param time 6位时间
	 * @return
	 */
	public static String formatTime(String time){
		if(isEmpty(time)){
			return "";
		}
		if(time.length() != 6){
			return "时间不合法";
		}
		return time.substring(0,2) + ":" + time.substring(2,4) + ":" + time.substring(4,6);
	}
	
	/**
	 * 将金额从以分为单位的转换成以元为单位的数据
	 * @param amt
	 * @return
	 */
	public static String formatRMB(String amt){
		/*
		 *	判断金额是否为数字，略 
		 */
		return amt.substring(0, amt.length()-2) + "." + amt.substring(amt.length()-2);
	}
	
	public static String trim(String value){
		return StringUtil.isEmpty(value)?"":value.trim();
	}
	public static String delComma(String s) {

	    String formatString = "";

	    if (s != null && s.length() >= 1) {

	        formatString = s.replaceAll(",", "");

	    }
	    return formatString;

	}
	public static String utf8toString(String str){
		if(StringUtil.isEmpty(str)){
			return "";
		}else {
			try {
//				return new String(str.getBytes("ISO-8859-1"),"UTF-8");
				return java.net.URLDecoder.decode(new String(str.getBytes("ISO-8859-1"),"UTF-8") , "UTF-8");
			} catch (UnsupportedEncodingException e) {
				return "";
			}
		}
	}
/*	public static void main(String[] args) {
    	System.out.println("200411170000001".length());
    	System.out.println(StringUtil.cpFormatString("他11170000001",15,"right","0","1","right"));
    	System.out.println(StringUtil.formatRMB("123"));
    	
    	String encoding = System.getProperty("file.encoding");
        System.out.println("Default System Encoding:" + encoding);
//		System.out.println(StringUtil.delComma("1111307.06"));
		BigDecimal db = new BigDecimal("324324324324");
		String ii = db.toPlainString();
		System.out.println(ii);
	}*/
	
	/**
	 * 4位随机数
	 * @return
	 */
	public static String random4() {
		Long random = (long) (Math.random()*9000+1000);
		return String.valueOf(random);
	}
	
	public static String escape(String src) {  
        int i;  
        char j;  
        StringBuffer tmp = new StringBuffer();  
        tmp.ensureCapacity(src.length() * 6);  
        for (i = 0; i < src.length(); i++) {  
            j = src.charAt(i);  
            if (Character.isDigit(j) || Character.isLowerCase(j)  
                    || Character.isUpperCase(j))  
                tmp.append(j);  
            else if (j < 256) {  
                tmp.append("%");  
                if (j < 16)  
                    tmp.append("0");  
                tmp.append(Integer.toString(j, 16));  
            } else {  
                tmp.append("%u");  
                tmp.append(Integer.toString(j, 16));  
            }  
        }  
        return tmp.toString();  
    }  
 
    public static String unescape(String src) {  
        StringBuffer tmp = new StringBuffer();  
        tmp.ensureCapacity(src.length());  
        int lastPos = 0, pos = 0;  
        char ch;  
        while (lastPos < src.length()) {  
            pos = src.indexOf("%", lastPos);  
            if (pos == lastPos) {  
                if (src.charAt(pos + 1) == 'u') {  
                    ch = (char) Integer.parseInt(src  
                            .substring(pos + 2, pos + 6), 16);  
                    tmp.append(ch);  
                    lastPos = pos + 6;  
                } else {  
                    ch = (char) Integer.parseInt(src  
                            .substring(pos + 1, pos + 3), 16);  
                    tmp.append(ch);  
                    lastPos = pos + 3;  
                }  
            } else {  
                if (pos == -1) {  
                    tmp.append(src.substring(lastPos));  
                    lastPos = src.length();  
                } else {  
                    tmp.append(src.substring(lastPos, pos));  
                    lastPos = pos;  
                }  
            }  
        }  
        return tmp.toString();  
    }
	
    public static Double formatToDouble(String amt){
    	Double num = 0.0;
    	if(!isEmpty(amt)){
    		
    		if(amt.indexOf(",") > -1){
    			amt = amt.replaceAll(",", "").trim();
    		}
    		
    		num = Double.parseDouble(amt);
    	}
    	return num;
    }
    
    public static String format3(double amt){
    	DecimalFormat format = new DecimalFormat("#0.00");
    	String sMoney = format.format(amt);
    	return sMoney;
    }
    
    /**
     * 是否为数字(包括小数点)
     * @param amt
     * @return
     */
    public static boolean isNumeric(String amt){
    	for (int i = 0; i < amt.length(); i++){
    		if (!Character.isDigit(amt.charAt(i)) && !".".equals(String.valueOf(amt.charAt(i)))){
    		    return false;
    		}
    	}
    	return true;
    }
    
    /**
     * 获得格式化后的BigDecimal
     * @param amt
     * @return
     */
    public static BigDecimal getBigDecimal(String amt){
    	
    	BigDecimal bd = new BigDecimal(0);
    	
    	if(!isEmpty(amt)){
    		// 去掉千分位
	    	if(amt.indexOf(",") > 0){
	    		amt = amt.replaceAll(",", "");
	    	}
	    	
	    	bd = new BigDecimal(amt);
    	}
    	
    	return bd;
    }
    
    /**
     * 格式化银行卡号 (只显示后4位数字，前面用××××表示 如 ×××× 4235)
     * @param bankCardNo
     * @return
     */
    public static String formatBankCardNo(String bankCardNo){
    	
    	if(!isEmpty(bankCardNo)){
    		bankCardNo = "****" + bankCardNo.substring(bankCardNo.length() - 4, 
    				bankCardNo.length());
    	}
    	
    	return bankCardNo;
    	
    }
    
    /**
     * 格式化证件号 (只显示前几位后几位数字，如 310***********4235)
     * @param idno
     * @return
     */
    public static String formatIdno(String idno){
    	
    	if(!isEmpty(idno)){
    		if(idno.length() == 15){
    			idno = idno.substring(0, 3) + "********" + idno.substring(idno.length() - 3);
    		}else if(idno.length() == 18){
    			idno = idno.substring(0, 3) + "***********" + idno.substring(idno.length() - 3);
    		}else if(idno.length() >= 10){
    			idno = idno.substring(0, 3) + "****" + idno.substring(idno.length() - 3);
    		}else if(idno.length() >= 8){
    			idno = idno.substring(0, 2) + "****" + idno.substring(idno.length() - 2);
    		}else if(idno.length() >= 6){
    			idno = idno.substring(0, 2) + "**" + idno.substring(idno.length() - 2);
    		}
    		
    	}
    	
    	return idno;
    }
    
    /**
     * 格式化日期
     * @param date
     * @return
     */
    public static String formatDate(Date date){
    	
    	String dateStr = "";
    	
    	if(date != null){
    		dateStr = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").format(date);
    	}
    	
    	return dateStr;
    }
    
	public static void main(String[] args) {
		String sMoney = "313454843113134";
		System.out.println(formatIdno(sMoney));
	}
}
