package zhuboss.framework.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JavaUtil {
	
	private static Log logger = LogFactory.getLog(JavaUtil.class);
	
	final static int BUFFER_SIZE = 4096;  
	
	public static String propLoad(String key) throws FileNotFoundException, IOException{
		Properties properties = new Properties();
		properties.load(new FileInputStream("./conf/worker.properties"));
		return properties.getProperty(key);
	}
	
	public static String InputStreamTOString(InputStream in, String encoding) throws Exception{
	        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
	        byte[] data = new byte[BUFFER_SIZE];  
	        int count = -1;  
	        while((count = in.read(data,0,BUFFER_SIZE)) != -1)  
	            outStream.write(data, 0, count);  
	          
	        data = null;  
	        return new String(outStream.toByteArray(),encoding);  
	    }  

	 public static byte[] InputStreamToBytes(InputStream in) throws Exception{  
         
	        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
	        byte[] data = new byte[BUFFER_SIZE];  
	        int count = -1;  
	        while((count = in.read(data,0,BUFFER_SIZE)) != -1)  
	            outStream.write(data, 0, count);  
	          
	        data = null;  
	        return outStream.toByteArray();  
	    }  
	 
	 public static void byteArrayTOFile(byte[] b, String fileName) throws Exception{
			File file = new File(fileName);
			OutputStream os = new FileOutputStream(file);
			os.write(b);
			os.flush();
			os.close();
		 }
	 
	 public static void InputStreamTOFile(InputStream in, String fileName) throws Exception{
		File file = new File(fileName);
		OutputStream os = new FileOutputStream(file);
		byte[] data = new byte[BUFFER_SIZE];
		int count = -1;
		while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
			os.write(data, 0, count);
		in.close();
		os.flush();
		os.close();

	 }

	public static InputStream getURLFileInputStream(String urlPath) {
		try {
			URL url = new URL(urlPath);
			URLConnection conn = url.openConnection();
			InputStream inStream = conn.getInputStream();
			return inStream;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void mkdirs(String path) {
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	 public static byte[] objectToBytes(Object object) {
			ObjectOutputStream stream = null;
			ByteArrayOutputStream bit = null;
			try {
				bit = new ByteArrayOutputStream();
				stream = new ObjectOutputStream(bit);
				stream.writeObject(object);
				return bit.toByteArray();
			} catch (Exception exp) {
				logger.error(exp.getMessage(), exp);
				throw new RuntimeException(exp);
			} finally {
				try {
					bit.close();
				} catch (Exception e) {
				}
				try {
					stream.close();
				} catch (Exception e) {
				}
				stream = null;
				bit = null;
			}
		}

	public static Object bytesToObject(byte[] bs) {
			ObjectInputStream stream = null;
			ByteArrayInputStream bit = null;
			try {
				bit = new ByteArrayInputStream(bs);
				stream = new ObjectInputStream(bit);
				return stream.readObject();
			} catch (Exception exp) {
				logger.error(exp.getMessage(), exp);
				throw new RuntimeException(exp);
			} finally {
				try {
					bit.close();
				} catch (Exception e) {
				}
				try {
					stream.close();
				} catch (Exception e) {
				}
				stream = null;
				bit = null;
			}
		}
	
    /**
     * 验证输入的邮箱格式是否符合
     * @param email
     * @return 是否合法
     */
	public static boolean isEmail(String email){
		boolean tag = true;
		final String pattern1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		final Pattern pattern = Pattern.compile(pattern1);
		final Matcher mat = pattern.matcher(email);
		if (!mat.find()) {
		    tag = false;
		}
		return tag;
	}
	
	/**  
     * 验证手机号码  
     * @param mobiles  
     * @return  [0-9]{5,9}  
     */  
	public static boolean isMobileNO(String mobiles){  
		boolean flag = false;  
		try{  
			Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");  
			Matcher m = p.matcher(mobiles);  
			flag = m.matches();  
		}catch(Exception e){  
			logger.error(e.getMessage(),e);
			flag = false;  
		}  
		return flag;  
	}
	
}
