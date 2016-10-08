/*
 * @(#)DateUtils.java 1.0 2014-3-25上午09:46:15
 *
 * 和讯信息科技有限公司 - 第三方理财事业部
 * Copyright (c) 2012-2014 HexunFSD, Inc. All rights reserved.
 */
package zhuboss.framework.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

/**
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
 * @author wujingliang
 * @version 1.0, 2014-3-25
 * @since licaike-mobile-site
 * 
 */
public class DateUtils {

	public static final String MAIL_DATE_DT_PART_FORMAT = "yyyyMMdd"; 
	public static final int SECONDS_IN_DAY = 60 * 60 * 24;
    public static final long MILLIS_IN_DAY = 1000L * SECONDS_IN_DAY;
	
    
	/**
	 * 
	 * @return new Date()
	 */
    public static Date getNow(){
        return new Date();
    }

    /**
     * 
     * @return (new Date()).getTime()
     */
    public static long getNowTimestamp(){
        return getNow().getTime();
    }

    /**
     * 
     * @return yyyyMMdd
     */
    public static String getCurrentDate(){
        return toMailDateDtPartString(getNow());
    }

    /**
     * 
     * @return HHmmss
     */
    public static String getCurrentTime(){
        return toMailTimeTmPartString(getNow());
    }

    /**
     * 
     * @return yyyyMMddHHmmss
     */
    public static String getCurrentDateTime(){
        return toMailDateString(getNow());
    }
    
    /**
     * 
     * @return yyyy年MM月dd日HH点mm分ss秒
     */
    public static String getCurrentChaWordDateTime(){
        return toChaWordDateString(getNow());
    }
    
    
    /**
     * 
     * @param aDate 
     * @return yyyy-MM-dd
     */
    public static final String toShortDateString(Date aDate){
        return toFormatDateString(aDate, "yyyy-MM-dd");
    }

    /**
     * 
     * @param aDate
     * @return yyyyMMdd
     */
    public static final String toMailDateDtPartString(Date aDate){
        return toFormatDateString(aDate, "yyyyMMdd");
    }

    /**
     * 
     * @param aDate
     * @return HHmmss
     */
    public static final String toMailTimeTmPartString(Date aDate){
        return toFormatDateString(aDate, "HHmmss");
    }

    /**
     * 
     * @param aDate
     * @return yyyyMMddHHmmss
     */
    public static final String toMailDateString(Date aDate){
        return toFormatDateString(aDate, "yyyyMMddHHmmss");
    }

    /**
     * 
     * @param aDate
     * @return yyyy.MM.dd
     */ 
    public static final String toPointDtmPart(Date aDate){
        return toFormatDateString(aDate, "yyyy.MM.dd");
    }

    /**
     * 
     * @param aDate
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static final String toLongDateString(Date aDate){
        return toFormatDateString(aDate, "yyyy-MM-dd HH:mm:ss");
    }
    
    /**
     * 
     * @param aDate
     * @return yyyy年MM月dd日HH点mm分ss秒
     */
    public static final String toChaWordDateString(Date aDate){
        return toFormatDateString(aDate, "yyyy年MM月dd日HH点mm分ss秒");
    }

    /**
     * 
     * @param aDate
     * @return HH:mm:ss
     */
    public static final String toLongDateTmPartString(Date aDate){
        return toFormatDateString(aDate, "HH:mm:ss");
    }
    
    /**
     * 
     * @param aDate
     * @return yyyy-MM-dd HH:mm:ss:SSS
     */
    public static final String toFullDateString(Date aDate){
        return toFormatDateString(aDate, "yyyy-MM-dd HH:mm:ss:SSS");
    }
    
    /**
     * 
     * @param aDate
     * @return yyyyMMddHHmmssSSS
     */
    public static final String toFullDateCompactString(Date aDate){
        return toFormatDateString(aDate, "yyyyMMddHHmmssSSS");
    }

    /**
     * 
     * @param aDate
     * @return yyyyMMddHHmm'Z'
     */
    public static final String toLDAPDateString(Date aDate){
        return toFormatDateString(aDate, "yyyyMMddHHmm'Z'");
    }
    
	//------------------------
    /**
     * 
     * @return yyyy\u5E74MM\u6708dd\u65E5HH:mm
     */
    public static String getCurrentYyyyMmDdHm(){
        return toMailDtm(getNow());
    }

    /**
     * 
     * @return MM\u6708dd\u65E5HH:mm
     */
    public static String getCurrentMmDdHm(){
        return toMailDtmPart(getNow());
    }

    /**
     * 
     * @return yyyy\u5E74MM\u6708dd\u65E5 HH\u65F6mm\u5206ss\u79D2
     */
    public static String getCurrentGBKDateTime(){
        return toLongDateGBKString(getNow());
    }
    
    /**
     * 
     * @param aDate
     * @return MM\u6708dd\u65E5HH:mm
     */
    public static final String toMailDtmPart(Date aDate){
        return toFormatDateString(aDate, "MM\u6708dd\u65E5HH:mm");
    }

    /**
     * 
     * @param aDate
     * @return yyyy\u5E74MM\u6708dd\u65E5HH:mm
     */
    public static final String toMailDtm(Date aDate){
        return toFormatDateString(aDate, "yyyy\u5E74MM\u6708dd\u65E5HH:mm");
    }
    
    /**
     * 
     * @param aDate
     * @return yyyy\u5E74MM\u6708dd\u65E5
     */
    public static final String toShortDateGBKString(Date aDate){
        return toFormatDateString(aDate, "yyyy\u5E74MM\u6708dd\u65E5");
    }

    /**
     * 
     * @param aDate
     * @return yyyy\u5E74MM\u6708dd\u65E5 HH\u65F6mm\u5206
     */
    public static final String toDateGBKString(Date aDate){
        return toFormatDateString(aDate, "yyyy\u5E74MM\u6708dd\u65E5 HH\u65F6mm\u5206");
    }

    /**
     * 
     * @param aDate
     * @return yyyy\u5E74MM\u6708dd\u65E5 HH\u65F6mm\u5206ss\u79D2
     */
    public static final String toLongDateGBKString(Date aDate){
        return toFormatDateString(aDate, "yyyy\u5E74MM\u6708dd\u65E5 HH\u65F6mm\u5206ss\u79D2");
    }

    /**
     * 
     * @param aDate
     * @return HH\u65F6mm\u5206ss\u79D2
     */
    public static final String toLongDateTmPartGBKString(Date aDate){
        return toFormatDateString(aDate, "HH\u65F6mm\u5206ss\u79D2");
    }
    
    /**
     * 
     * @param aDate
     * @return yyyy\u5E74MM\u6708dd\u65E5 HH\u65F6mm\u5206ss\u79D2SSS\u6BEB\u79D2
     */
    public static final String toFullDateGBKString(Date aDate){
        return toFormatDateString(aDate, "yyyy\u5E74MM\u6708dd\u65E5 HH\u65F6mm\u5206ss\u79D2SSS\u6BEB\u79D2");
    }
	//------------------------
    public static String formatDate(Date date, String pattern){
    	return toFormatDateString(date, pattern);
    }
    
    public static final String getPrevDay(){
 	   Calendar calendar = Calendar.getInstance();
 		calendar.add(Calendar.DATE, -1);    //得到前一天
 		Date date = calendar.getTime();
 		return toFormatDateString(date,MAIL_DATE_DT_PART_FORMAT);
    }
    
    public static boolean isDate(String str){
    	if(StringUtils.isEmpty(str)){
    		return false;
    	}
    	if(str.length()!=8 && str.length()!=10){
    		return false;
    	}
    	String year="", month="", day="";
    	if(str.length()==8){
    		year=str.substring(0, 4);
    		month=str.substring(4, 6);
    		day=str.substring(6, 8);
    	}
    	if(str.length()==10){
    		year=str.substring(0, 4);
    		month=str.substring(5, 7);
    		day=str.substring(8, 10);
    	}
    	if(StringUtils.isNotEmpty(year) && StringUtils.isNotEmpty(month) && StringUtils.isNotEmpty(day)){
    		int maxDays = 31;
    		if( StringUtils.isNumeric(year)== false || 
    				StringUtils.isNumeric(month)== false || 
    				StringUtils.isNumeric(day)== false ){
    			return false;
    		}else if(year.length() < 4){
    			return false;
    		}
    		int y = Integer.parseInt(year);
    		int m = Integer.parseInt(month);
    		int d = Integer.parseInt(day);
    		if(m > 12 || m < 1){
    			return false;
    		}else if(m == 4 || m == 6 || m == 9 || m == 11){
    			maxDays = 30;
    		}else if (m == 2) {
    			//四年一闰，百年不闰，四百年又闰
    			if (y % 4 > 0)
    				maxDays = 28;
    			else if (y % 100 == 0 && y % 400 > 0)
    				maxDays = 28;
    			else
    	            maxDays = 29;
    		}
    		if(d <1 || d > maxDays){
    			return false;
    		}
    	}
    	return true;
	}

    //=============private function===================
    public static final String toFormatDateString(Date aDate, String formatStr){
        if(aDate == null){
            return "";
        } else {
            Assert.hasText(formatStr);
            return (new SimpleDateFormat(formatStr)).format(aDate);
        }
    }
    
    //=====test===
    public static void main(String[] args) throws ParseException {
    	//System.out.println(getCurrentChaWordDateTime());
    	String abc = "20151203";
    	System.out.println(formatDateAndTime(abc));
	}
    
    /**
	 * 计算两个日期之间相差的的毫秒数
	 * @param startDateStr yyyyMMddHHmmss
	 * @param endDateStr yyyyMMddHHmmss
	 * @return
	 * @throws ParseException
	 */
    public static final long getDifferenceMillis2(String startDateStr, String endDateStr){
		return getDifferenceMillis(startDateStr,endDateStr,"yyyyMMddHHmmss");
    }
    /**
     * 计算两个日期之间相差的的毫秒数
     * @param startDateStr
     * @param endDateStr
     * @param dateFormat
     * @return
     */
    public static final long getDifferenceMillis(String startDateStr, String endDateStr, String dateFormat){
		try {
			return getDifferenceMillis(parser(startDateStr,dateFormat),parser(endDateStr,dateFormat));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
    }
    
    /**
     * 将一个符合指定格式的字串解析成日期型
     * @param aDateStr
     * @param formatter
     * @return
     * @throws ParseException 
     */
    public static final Date parser(String aDateStr, String formatter) throws ParseException{
    	if (StringUtils.isBlank(aDateStr)) return null;
    	Assert.hasText(formatter);
        SimpleDateFormat sdf = new SimpleDateFormat(formatter);
        return sdf.parse(aDateStr);

    }
    /**
     * 计算两个日期之间相差的的毫秒数
     * @param startDate
     * @param endDate
     * @return
     */
    public static final long getDifferenceMillis(Date startDate, Date endDate){
    	Assert.notNull(startDate);
    	Assert.notNull(endDate);
		return Math.abs(endDate.getTime()-startDate.getTime());
    }

    /**
     * 判断是否同一天
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameDay(Date date1,Date date2){
    	long differenceMillis = getDifferenceMillis(date1,date2);
    	
    	return differenceMillis < MILLIS_IN_DAY
                && differenceMillis > -1L * MILLIS_IN_DAY
                && toDay(date1.getTime()) == toDay(date2.getTime());
    	
    }
    
    private static long toDay(long millis) {
        return (millis + TimeZone.getDefault().getOffset(millis)) / MILLIS_IN_DAY;
    }
    
    public static final Date addDays(Date date, int days){
    	return new Date(date.getTime() + days * (1000l * 60l * 60l * 24l));
    }
    
    final static SimpleDateFormat removeTimeDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    /**
     * 移除日期的时间值
     * @param date
     * @return
     */
    public static Date removeTime(Date date){
    	try {
			return removeTimeDateFormat.parse(removeTimeDateFormat.format(date));
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
    }
    
	public static final Date parseMailDateDtPartString(String aDateStr) throws ParseException {
		return parser(aDateStr, "yyyyMMdd");
	}
	
	public static final Date parseMailDateTmPartString(String aDateStr) throws ParseException {
		return parser(aDateStr, "HHmmss");
	}
    
	public static boolean isMailDateStr(String mailDateStr) {
		boolean ret = true;
		try {
			parseMailDateDtPartString(mailDateStr);
		} catch (ParseException e) {
			ret = false;
		}
		return ret;
	}

	public static boolean isMailTimeStr(String mailTimeStr) {
		boolean ret = true;
		try {
			parseMailDateTmPartString(mailTimeStr);
		} catch (ParseException e) {
			ret = false;
		}
		return ret;
	}

	public static Date parseMailDateString(String dateString)
			throws ParseException {
		return parser(dateString, "yyyyMMddHHmmss");
	}
	
	public static String formatDateString(String dateString)
			throws ParseException {
		if(StringUtil.isEmpty(dateString)){
			return "";
		}
		dateString = dateString.replace("/", "").replace("-", "");
		if(dateString.length() > 8){
			dateString = dateString.substring(0, 8);
		}
		if(!isDate(dateString)){
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date = sdf.parse(dateString);
		return toFormatDateString(date,"yyyy/MM/dd");
	}
	
	public static String formatDateStr(String dateString)
			throws ParseException {
		
		if(StringUtil.isEmpty(dateString)){
			return "";
		}
		dateString = dateString.replace("/", "").replace("-", "").replace(" ", "").replace(":", "");
		
		SimpleDateFormat sdf = null;
		
		if(dateString.length() == 8){
			if(!isDate(dateString)){
				return "";
			}
		}
		
		String format = "";
		
		if(dateString.length() > 8){
			sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			format = "yyyy/MM/dd HH:mm:ss";
		}else{
			dateString = dateString.substring(0,8);
			sdf = new SimpleDateFormat("yyyyMMdd");
			format = "yyyy/MM/dd";
		}
		
		Date date = sdf.parse(dateString);
		
		return toFormatDateString(date,format);
	}
	
	public static String formatDateAndTimeString(String dateTimeString)
			throws ParseException {
		if(StringUtil.isEmpty(dateTimeString)){
			return "";
		}
		dateTimeString = dateTimeString.replace("/", "").replace("-", "").replace(" ", "").replace(":", "");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = sdf.parse(dateTimeString);
		return toFormatDateString(date,"yyyy/MM/dd HH:mm:ss");
	}
	
	/**
	 * 当前季度的开始时间
	 * @param date
	 * @return
	 */
	public static Date getCurrentQuarterStartDate(Date date) { 
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int currentMonth = c.get(Calendar.MONTH) + 1; 
		if (currentMonth >= 1 && currentMonth <= 3){ 
			c.set(Calendar.MONTH, 1); 
		}else if (currentMonth >= 4 && currentMonth <= 6){ 
			c.set(Calendar.MONTH, 3); 
		}else if (currentMonth >= 7 && currentMonth <= 9){
			c.set(Calendar.MONTH, 4); 
		}else if (currentMonth >= 10 && currentMonth <= 12){
			c.set(Calendar.MONTH, 9); 
		}
		c.set(Calendar.DATE, 1); 
		
		Date now = c.getTime(); 
		
		return now; 
	}
	
	/**
	 * 当前季度的结束时间
	 * @param date
	 * @return
	 */
	public static Date getCurrentQuarterEndDate(Date date) { 
		Calendar c = Calendar.getInstance(); 
		c.setTime(date);
		int currentMonth = c.get(Calendar.MONTH) + 1; 
		if (currentMonth >= 1 && currentMonth <= 3) { 
			c.set(Calendar.MONTH, 2); 
			c.set(Calendar.DATE, 31); 
		} else if (currentMonth >= 4 && currentMonth <= 6) { 
			c.set(Calendar.MONTH, 5); 
			c.set(Calendar.DATE, 30); 
		} else if (currentMonth >= 7 && currentMonth <= 9) { 
			c.set(Calendar.MONTH,8); 
			c.set(Calendar.DATE, 30); 
		} else if (currentMonth >= 10 && currentMonth <= 12) { 
			c.set(Calendar.MONTH, 11); 
			c.set(Calendar.DATE, 31); 
		} 
		Date now = c.getTime(); 
		return now; 
	}
	
	/**
	 * 上一季度的开始时间
	 * @param date
	 * @return
	 */
	public static Date getPreQuarterStartDate(Date date) { 
		Date startDate = getCurrentQuarterStartDate(date);
		Calendar c = Calendar.getInstance();  
        c.setTime(startDate);
        // 上一季度
        c.add(Calendar.MONTH, -3);
		
		return c.getTime(); 
	}
	
	/**
	 * 上一季度的结束时间
	 * @param date
	 * @return
	 */
	public static Date getPreQuarterEndDate(Date date) { 
		Date endDate = getCurrentQuarterEndDate(date);
		Calendar c = Calendar.getInstance();  
        c.setTime(endDate);
        // 上一季度
        c.add(Calendar.MONTH, -3);
		
		return c.getTime(); 
	}
	
	/**
	 * 下一季度的开始时间
	 * @param date
	 * @return
	 */
	public static Date getNextQuarterStartDate(Date date) { 
		Date startDate = getCurrentQuarterStartDate(date);
		Calendar c = Calendar.getInstance();  
        c.setTime(startDate);
        // 下一季度
        c.add(Calendar.MONTH, 3);
		
		return c.getTime(); 
	}
	
	/**
	 * 下一季度的结束时间
	 * @param date
	 * @return
	 */
	public static Date getNextQuarterEndDate(Date date) { 
		Date endDate = getCurrentQuarterEndDate(date);
		Calendar c = Calendar.getInstance();  
        c.setTime(endDate);
        // 下一季度
        c.add(Calendar.MONTH, 33);
		
		return c.getTime(); 
	}
	
	public static Date formatDateAndTime(String dateTimeString)
			throws ParseException {
		if(StringUtil.isEmpty(dateTimeString)){
			return new Date();
		}
		dateTimeString = dateTimeString.replace("/", "").replace("-", "").replace(" ", "").replace(":", "");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = sdf.parse(dateTimeString);
		return date;
	}
	
	/**
	 * 将日期改为M/dd的格式
	 * @param date(yyyyMMdd)
	 * @return
	 * @throws ParseException 
	 */
	public static String navDateFormat(String date) throws ParseException{
		
		String navDate = "";
		
		if(!StringUtil.isEmpty(date)){
		
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat format1 = new SimpleDateFormat("M/dd");
			
			if(date.indexOf("/") > -1){
				format = new SimpleDateFormat("yyyy/MM/dd");
			}
			
			Date time = format.parse(date);
				
			navDate = format1.format(time);
		}
		
		return navDate;
	}
}
