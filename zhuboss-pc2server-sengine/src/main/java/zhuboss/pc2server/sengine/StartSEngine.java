package zhuboss.pc2server.sengine;

import java.io.File;
import java.io.FileInputStream;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StartSEngine {
	private static ApplicationContext applicationContext;
	static Object lock = new Object();
	static Object inital = new Object();
	
	/**
	 * 避免在启动过程中有请求进入，处理task，可能此时applicationContext还是空的
	 * @return
	 */
	public static ApplicationContext getApplicationContext(){
		synchronized (inital) {
			return applicationContext;
		}
	}
	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configure(new FileInputStream(new File("./conf/log4j.properties")));
		synchronized (inital) {
			applicationContext = new ClassPathXmlApplicationContext(
					new String[] { "spring.xml"});
		}
		synchronized(lock){
			lock.wait();
		}
	}
}
