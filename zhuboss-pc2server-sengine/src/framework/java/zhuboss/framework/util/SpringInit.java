package zhuboss.framework.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/** 
 * @author 作者 zhuzhengquan: 
 * @version 创建时间：2015年10月16日 上午8:02:14 
 * 类说明 
 */
public class SpringInit implements ApplicationContextAware {
	public static ApplicationContext applicationContext;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		SpringInit.applicationContext = applicationContext;
		
	}

}
