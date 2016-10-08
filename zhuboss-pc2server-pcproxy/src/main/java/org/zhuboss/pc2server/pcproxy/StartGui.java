package org.zhuboss.pc2server.pcproxy;

import javax.swing.JFrame;

import org.zhuboss.pc2server.pcproxy.gui.NewFrame;

public class StartGui {
	public static void main(String args[])throws Exception{
		/**
		 * GUI启动
		 */
		Constants.VERSION += ".2";
		
        NewFrame frame1 = new NewFrame();
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//一定要设置关闭
        frame1.setVisible(true);
		
    }
}
