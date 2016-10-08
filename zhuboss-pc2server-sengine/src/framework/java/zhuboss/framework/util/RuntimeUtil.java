package zhuboss.framework.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * 最终执行的命令分块，会进入
 * java.lang.ProcessImpl.ProcessImpl(String[] cmd, String envblock, String path, long[] stdHandles, boolean redirectErrorStream) 
 * Im4Java也是这样进入
 * @author Administrator
 *
 */
public class RuntimeUtil {
	private static Logger logger = LoggerFactory.getLogger(RuntimeUtil.class);
//	public static void main(String[] args){
//		Map<String,String> argMap = new HashMap<String,String>();
//		argMap.put("oriFile", "c:\\test.jpg");
//		argMap.put("destFile", "c:\\test2.jpg");
//		execProcess("C:\\Program Files (x86)\\ImageMagick-6.8.9-Q16\\","convert {oriFile} -resize 50x50! {destFile}",argMap);
//	}
	
	/**
	 * 
	 * @param binPath 可执行文件所在目录 ，linux已在环境中，则不需要设置
	 * @param cmdLine  
	 * @param argMap
	 */
	public static void execProcess(String binPath,String cmdLine,Map<String,String> argMap){
		String newCmd = cmdLine;
		if(argMap!=null){
			Iterator<String> iterator = argMap.keySet().iterator();
			while(iterator.hasNext()){
				String key = iterator.next();
				newCmd = newCmd.replace("{"+key+"}", argMap.get(key));
			}
		}
		Runtime run = Runtime.getRuntime();
		try{
				Process p = run.exec(binPath+newCmd,null,null);
	        BufferedInputStream in = new BufferedInputStream(p.getInputStream());  
	        BufferedReader inBr = new BufferedReader(new InputStreamReader(in));  
	        String lineStr;  
	        while ((lineStr = inBr.readLine()) != null)  
	            //获得命令执行后在控制台的输出信息  
	            System.out.println(lineStr);// 打印输出信息  
	        //检查命令是否执行失败。  
	        if (p.waitFor() != 0) {  
	            if (p.exitValue() != 0){
	            	//p.exitValue()==0表示正常结束，1：非正常结束  
	            	String e = System.getProperty("sun.jnu.encoding"); //操作系统字符集
	            	logger.error(JavaUtil.InputStreamTOString( p.getErrorStream(),e));
	            	logger.error("命令执行失败!");  
	            }
	        }  
	        inBr.close();  
	        in.close();  
	    } catch (Exception e) {  
	        logger.error(e.getMessage(),e);
	    }  
	}
}
