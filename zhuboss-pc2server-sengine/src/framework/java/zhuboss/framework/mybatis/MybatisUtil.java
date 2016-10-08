package zhuboss.framework.mybatis;

import java.util.Collection;
import java.util.Iterator;

/** 
 * @author 作者 zhuzhengquan: 
 * @version 创建时间：2015年9月16日 下午4:33:27 
 * 类说明 
 */
public class MybatisUtil {
	/**
	 * 拼接mybatis 中的in 表达式
	 * @param c
	 * @return
	 */
	public static String convertInExp(Collection<String> c){
		if(c==null || c.size()==0){
			throw new RuntimeException("collection 不能为空");
		}
		String s = null;
		Iterator<String> iterator = c.iterator();
		while(iterator.hasNext()){
			String item = iterator.next();
			if(s==null){
				s ="('"+item+"'";
			}else{
				s += ",'"+item+"'";
			}
		}
		s += ")";
		return s;
	}
}
