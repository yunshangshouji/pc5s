package org.zhuboss.pc2server.pcproxy.gui;

import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.text.JTextComponent;
import javax.swing.SwingUtilities; 

public class MyPrintStream extends PrintStream {  
 
private JTextComponent text;
private StringBuffer sb = new StringBuffer();
   
   public MyPrintStream(OutputStream out, JTextComponent text) {  
       super(out);  
        this.text = text;  
   }
  
  /** 
     * 在这里重截,所有的打印方法都要调用的方法 
     */  
    public void write(byte[] buf, int off, int len) {  
         final String message = new String(buf, off, len);   
         SwingUtilities.invokeLater(new Runnable(){
         public void run(){
        	 if(sb.length()>10000){
        		sb = new StringBuffer();
        	 }
          sb.append(message);
          text.setText(sb.toString());
         }
      });
   }
}
