package zhuboss.pc2server.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.lang.ArrayUtils;


public class JavaUtil {
	
	public static final int UUID_BYTE_LENGTH = 16;
	public static final int INT_BYTE_LENGTH = 4;
	
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
	
	public static byte[] uuid2Bytes(UUID uuid){
		long l1 = uuid.getMostSignificantBits();
		long l2 = uuid.getLeastSignificantBits();
		byte[] byteNum = new byte[16];  
		System.arraycopy(long2Bytes(l1), 0, byteNum, 0, 8);
		System.arraycopy(long2Bytes(l2), 0, byteNum, 8, 8);
		return byteNum;
	}
	
	public static UUID bytes2UUID(byte[] bytes){
		return new UUID(bytes2Long(Arrays.copyOfRange(bytes, 0, 8)),
				bytes2Long(Arrays.copyOfRange(bytes, 8,16)));
		
	}
	
	public static byte[] long2Bytes(long num) {  
	    byte[] byteNum = new byte[8];  
	    for (int ix = 0; ix < 8; ++ix) {  
	        int offset = 64 - (ix + 1) * 8;  
	        byteNum[ix] = (byte) ((num >> offset) & 0xff);  
	    }  
	    return byteNum;  
	}  
	
	public static long bytes2Long(byte[] byteNum) {  
	    long num = 0;  
	    for (int ix = 0; ix < 8; ++ix) {  
	        num <<= 8;  
	        num |= (byteNum[ix] & 0xff);  
	    }  
	    return num;  
	} 
	
	public static byte[] int2Bytes(int num) {  
	    byte[] byteNum = new byte[4];  
	    for (int ix = 0; ix < 4; ++ix) {  
	        int offset = 32 - (ix + 1) * 8;  
	        byteNum[ix] = (byte) ((num >> offset) & 0xff);  
	    }  
	    return byteNum;  
	}  
	
	public static int bytes2int(byte[] byteNum) {  
	    int num = 0;  
	    for (int ix = 0; ix < 4; ++ix) {  
	        num <<= 8;  
	        num |= (byteNum[ix] & 0xff);  
	    }  
	    return num;  
	} 
	
	public static String bytesToHexString(byte[] src){  
	    StringBuilder stringBuilder = new StringBuilder("");  
	    if (src == null || src.length <= 0) {  
	        return null;  
	    }  
	    for (int i = 0; i < src.length; i++) {  
	        int v = src[i] & 0xFF;  
	        String hv = Integer.toHexString(v);  
	        if (hv.length() < 2) {  
	            stringBuilder.append(0);  
	        }  
	        stringBuilder.append(hv);  
	    }  
	    return stringBuilder.toString();  
	}  
	private static byte charToByte(char c) {  
	    return (byte) "0123456789ABCDEF".indexOf(c);  
	}  
	/** 
	 * Convert hex string to byte[] 
	 * @param hexString the hex string 
	 * @return byte[] 
	 */  
	public static byte[] hexStringToBytes(String hexString) {  
	    if (hexString == null || hexString.equals("")) {  
	        return null;  
	    }  
	    hexString = hexString.toUpperCase();  
	    int length = hexString.length() / 2;  
	    char[] hexChars = hexString.toCharArray();  
	    byte[] d = new byte[length];  
	    for (int i = 0; i < length; i++) {  
	        int pos = i * 2;  
	        d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));  
	    }  
	    return d;  
	}  
	
	/**
	 *  md5加密
	 * @param src
	 * @return
	 * @throws Exception
	 */
	public static final byte[] md5Digest(byte[] src) throws Exception{
		if (ArrayUtils.isEmpty(src)) return ArrayUtils.EMPTY_BYTE_ARRAY;
		MessageDigest alg = MessageDigest.getInstance("MD5");
		return alg.digest(src);
	}
	
	public static byte[] gzip(byte[] src) throws IOException{

    	ByteArrayOutputStream os = new ByteArrayOutputStream();
    	GZIPOutputStream gos = new GZIPOutputStream(os);
    	gos.write(src);
    	gos.finish();
    	gos.flush();
    	gos.close();
    	return os.toByteArray();
    
	}
	
	public static byte[] gunzip(byte[] src) {
    	//还原
		try{
    	ByteArrayInputStream is = new ByteArrayInputStream(src);
    	 GZIPInputStream gis = new GZIPInputStream(is); 
    	 return JavaUtil.InputStreamToBytes(gis);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args){
//		byte[] b= new byte[3];
//		b[0] = 1;
//		b[1] = 2;
//		b[2] =3 ;
//		byte[] n = Arrays.copyOfRange(b, 0, 2);
		
//		UUID uuid = UUID.randomUUID();
//		System.out.println(uuid);
//		System.out.println(uuid2Bytes(uuid));
//		System.out.println(bytes2UUID(uuid2Bytes(uuid)));
		
//		System.out.println(bytes2int(int2Bytes(0)));
		
//		String s = "abc1231313131300000";
//		System.out.println(new String(hexStringToBytes(bytesToHexString(s.getBytes()))));
		
		byte[] b = JavaUtil.uuid2Bytes(UUID.randomUUID());
		System.out.println(UUID.nameUUIDFromBytes(b));
		System.out.println(JavaUtil.bytes2UUID(b));
	}
}
