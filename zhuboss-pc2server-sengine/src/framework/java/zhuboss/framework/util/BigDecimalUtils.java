package zhuboss.framework.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.springframework.util.Assert;

public class BigDecimalUtils {

	static DecimalFormat BIG_DECIMAL_FORMAT2 = new java.text.DecimalFormat("###0.00");
	
	public static String format2str(BigDecimal bigDecimal){
		return BIG_DECIMAL_FORMAT2.format(bigDecimal);
	}
	/**
	 * 求两个BigDecimal中的最小值
	 * @param a
	 * @param b
	 * @return
	 */
	public static BigDecimal min(BigDecimal a, BigDecimal b){
		Assert.notNull(a);
		Assert.notNull(b);
		return a.compareTo(b)>0 ? b : a;
	}
	
	/**
	 * 除法运算
	 * @param a 除数
	 * @param b 被除数
	 * @param scale 精度
	 * @return
	 */
	public static BigDecimal divide(BigDecimal a, BigDecimal b, int scale){
		Assert.notNull(a);
		Assert.notNull(b);
		Assert.isTrue(scale >= 0);
		return a.divide(b, scale, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * 提供精确的小数位四舍五入处理。
	 * @param bigDecimal 需要四舍五入的数字 
	 * @param scale 小数点后保留几位 
	 * @return
	 */
    public static BigDecimal round(BigDecimal bigDecimal, int scale) {
    	Assert.notNull(bigDecimal);
    	Assert.isTrue(scale >= 0);
        BigDecimal one = new BigDecimal("1");  
        return bigDecimal.divide(one, scale, BigDecimal.ROUND_HALF_UP);  
    }
    
	/**
	 * 提供精确的小数位四舍五入处理(向上取整)。
	 * @param bigDecimal 需要四舍五入的数字 
	 * @param scale 小数点后保留几位 
	 * @return
	 */
    public static BigDecimal ceil(BigDecimal bigDecimal, int scale) {
    	Assert.notNull(bigDecimal);
    	Assert.isTrue(scale >= 0);
        BigDecimal one = new BigDecimal("1");  
        return bigDecimal.divide(one, scale, BigDecimal.ROUND_CEILING);  
    }
}
