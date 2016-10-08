package org.zhuboss.pc2server.pcproxy.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import zhuboss.pc2server.common.JavaUtil;

public class PropertiesUtil {
	static final Properties properties = new Properties();
	
	public static void load(InputStream is) throws IOException{
		byte[] bytes = null;
		try {
			bytes = JavaUtil.InputStreamToBytes(is);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte [] utf8Bom = new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF };
		InputStream actIS ;
		if(bytes.length>3 && bytes[0] == utf8Bom[0] && bytes[1] == utf8Bom[1] && bytes[2] == utf8Bom[2]){
			actIS = new ByteArrayInputStream(bytes, 3, bytes.length-3);
		}else{
			actIS = new ByteArrayInputStream(bytes);
		}
		properties.load(actIS);
	}
	public static String getPropertyString(String property){
		return properties.getProperty(property);
	}
	
	public static Integer getPropertyInt(String property){
		String value = properties.getProperty(property);
		if(value ==null || value.equals("")){
			return null;
		}else{
			return Integer.parseInt(value);
		}
	}

	public static Properties getProperties() {
		return properties;
	}
}
