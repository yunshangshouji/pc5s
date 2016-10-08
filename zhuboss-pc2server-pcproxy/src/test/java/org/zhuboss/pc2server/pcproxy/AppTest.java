package org.zhuboss.pc2server.pcproxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import zhuboss.pc2server.common.JavaUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     * @throws Exception 
     */
    public void testXX( ) throws Exception
    {
    	ByteArrayOutputStream os = new ByteArrayOutputStream();
    	GZIPOutputStream gos = new GZIPOutputStream(os);
    	String s = "HTTP/1.1 200 OK\nServer: nginx\nDate: Fri, 10 Jun 2016 11:46:31 GMT\nContent-Type: text/html; charset=utf-8\nTransfer-Encoding: chunked\nConnection: keep-alive\nVary: Accept-Encoding\nCache-Control: no-transform\nX-Cache: HIT0\nContent-Encoding: gzip";
    	System.out.println(s.getBytes().length);
    	gos.write(s.getBytes());
    	gos.finish();
    	gos.flush();
    	gos.close();
    	System.out.println(os.toByteArray().length);
    	//还原
    	ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
    	 GZIPInputStream gis = new GZIPInputStream(is); 
    	String back =  JavaUtil.InputStreamTOString(gis, "UTF-8");
    	System.out.println(back);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
    
	 public static void main(String[] argv) throws Exception{
	   String anim= "|/-\\";
	   for (int x =0 ; x < 100 ; x++){
	     String data = "\b" + anim.charAt(x % anim.length()) + " " + x ;
	     System.out.write(data.getBytes());
	     Thread.sleep(100);
	   }
	 }
}
