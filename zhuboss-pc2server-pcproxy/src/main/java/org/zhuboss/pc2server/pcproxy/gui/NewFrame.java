package org.zhuboss.pc2server.pcproxy.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhuboss.pc2server.pcproxy.Constants;
import org.zhuboss.pc2server.pcproxy.StartPcProxy;
import org.zhuboss.pc2server.pcproxy.server.Client4Server;
import org.zhuboss.pc2server.pcproxy.server.ConnectedListener;
import org.zhuboss.pc2server.pcproxy.util.PropertiesUtil;

import zhuboss.pc2server.common.JavaUtil;

public class NewFrame extends JFrame implements ClipboardOwner{
    private JMenuBar menuBar;
    JLabel labelDomain;
    private JTextField textDomain;
    private JTextField textAuthKey;
    private JTextField textServer ;
    JTextArea jTextFieldConsole;
    Clipboard clipboard ;
    JButton jb1;
    JButton jb2;
    JButton jb3;
    JLabel labelTraffic;
    Client4Server client = new Client4Server();
    CacheFrame cacheFrame;
    GuiDisplay guiDisplay;
    Thread sizeStatis;
    Logger logger ;
    public NewFrame(){
        super();
        PropertyConfigurator.configure(StartPcProxy.class.getClassLoader().getResourceAsStream("log4j.properties"));
        logger = LoggerFactory.getLogger(StartPcProxy.class);
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        
        this.setSize(536,610);
        this.setLocationRelativeTo(null); //使窗口剧中
        this.getContentPane().setLayout(null);//设置布局控制器

        /**
         * 配置
         */
        JPanel panCfg = new JPanel();
        panCfg.setBounds(10, 10, 500, 200);
        panCfg.setBorder(new TitledBorder("连接"));
        panCfg.setLayout(null);
        //
        JLabel labelAuthKey = new JLabel();
        labelAuthKey.setText("Auth Key: ");
        labelAuthKey.setBounds(10, 20, 100, 30);
        labelAuthKey.setHorizontalAlignment(SwingConstants.RIGHT);
        panCfg.add(labelAuthKey);
        //
        textAuthKey = new JTextField();
        textAuthKey.setBounds(115, 20, 300, 30);
        textAuthKey.setColumns(25);
        panCfg.add(textAuthKey);
      
        //
        JLabel labelServer = new JLabel();
        labelServer.setText("服务器(Server): ");
        labelServer.setBounds(10, 60, 100, 30);
        labelServer.setHorizontalAlignment(SwingConstants.RIGHT);
        panCfg.add(labelServer);
        //
        textServer = new JTextField();
        textServer.setBounds(115, 60, 200, 30);
        textServer.setColumns(20);
        textServer.setText("http://127.0.0.1:80");
//        textServer.setHorizontalAlignment(SwingConstants.RIGHT);
        panCfg.add(textServer);
        
        
        jb1 = new JButton("Start");
        jb1.setBounds(110, 140, 100, 30);
        jb1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					final String proxyAddr = textServer.getText();
					if(textAuthKey.getText()!=null && textAuthKey.getText().length()==0){
						Toolkit.getDefaultToolkit().beep();
						JOptionPane.showMessageDialog(null, "AuthKey不能为空", "配置错误",JOptionPane.ERROR_MESSAGE);
						return;
					}
					if(textServer.getText()!=null && textServer.getText().length()==0){
						Toolkit.getDefaultToolkit().beep();
						JOptionPane.showMessageDialog(null, "Server不能为空", "配置错误",JOptionPane.ERROR_MESSAGE);
						return;
					}
					System.setProperty(Constants.AUTH_KEY, textAuthKey.getText());
					System.setProperty(Constants.PROXY_ADDR,proxyAddr );
					client.start(
							new ConnectedListener(){
								@Override
								public void onConnected(String data) {
									labelDomain.setText("已连接");
									textDomain.setText(data.substring(data.indexOf("http"), data.indexOf("->")));
									guiDisplay.setTerminate(false);
									//保存配置文件
									try{
										File configFile = new File("config.ini");
										String text = JavaUtil.InputStreamTOString(new FileInputStream(configFile), "UTF-8");
										if(text.contains(Constants.PROXY_ADDR+"=")){
											text = text.replaceFirst(Constants.PROXY_ADDR+"=[^\\s]+", Constants.PROXY_ADDR+"="+proxyAddr);
										}else{
											text += "\n" +  Constants.PROXY_ADDR+"="+proxyAddr;
										}
										if(text.contains(Constants.AUTH_KEY +"=")){
											text = text.replaceFirst(Constants.AUTH_KEY+"=[^\\s]+", Constants.AUTH_KEY+"="+textAuthKey.getText());
										}else{
											text += "\n" +  Constants.AUTH_KEY+"="+textAuthKey.getText();
										}
										FileOutputStream os = new FileOutputStream(configFile);
										BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8")); 
										writer.write(text);
										writer.close();
										os.close();
									}catch(Exception e){
										e.printStackTrace();
									}
								}

								@Override
								public void onDisconnected(String data) {
									jb2.getActionListeners()[0].actionPerformed(null);
									
								}
					});
					jb1.setEnabled(false);
					jb2.setEnabled(true);
					jb3.setEnabled(true);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
        panCfg.add(jb1);
        
        jb2 = new JButton("Stop");
        jb2.setBounds(220, 140, 100, 30);
        jb2.setEnabled(false);
        jb2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					client.stop();
					jb1.setEnabled(true);
					jb2.setEnabled(false);
					jb3.setEnabled(false);
					cacheFrame.setVisible(false);
					labelDomain.setText("未连接");
					textDomain.setText("");
					guiDisplay.setTerminate(true);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
        panCfg.add(jb2);
        
        jb3 = new JButton("缓存管理");
        jb3.setBounds(330, 140, 100, 30);
        jb3.setEnabled(false);
        jb3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					cacheFrame.setVisible(true);
					cacheFrame.setAppKey(textAuthKey.getText());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
        panCfg.add(jb3);
        
        
        /**
         * 连接状态
         */
        JPanel panStatus = new JPanel();
        panStatus.setBounds(10, 215, 300, 60);
        panStatus.setLayout(new FlowLayout());//注意此处的null
        panStatus.setBorder(new TitledBorder("运行状态"));
        //
      labelDomain = new JLabel();
      labelDomain.setBounds(34,49,20,18);
      labelDomain.setText("未连接");
      panStatus.add(labelDomain);
        //
        textDomain = new JTextField();
        textDomain.setBounds(0,0,160,20);
        textDomain.setColumns(15);
        textDomain.setEditable(false);
        textDomain.setBackground(Color.CYAN);
        panStatus.add(textDomain);
        JButton btCopy = new JButton("复制");
        btCopy.setBounds(34,49, 50, 30);
        btCopy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StringSelection contents = new StringSelection(textDomain.getText());
				clipboard.setContents(contents, NewFrame.this);
				
			}
		});
        panStatus.add(btCopy);
        

        /**
         * 流量 统计
         */
        JPanel panTraffic = new JPanel(); 
        panTraffic.setBounds(315, 215, 196, 60);
        panTraffic.setBorder(new TitledBorder("流量统计"));
        panTraffic.setLayout(null);
        labelTraffic = new JLabel();
        labelTraffic.setBounds(10,23,180,18);
        labelTraffic.setText("");
        panTraffic.add(labelTraffic);
        
        /**
         * 日志输出
         */
        jTextFieldConsole = new JTextArea();
//        jTextFieldConsole.setBounds(20, 20, 460, 220);
//        jTextFieldConsole.setBorder(BorderFactory.createLoweredBevelBorder());
        JPanel panConsole = new JPanel(); 
        panConsole.setBounds(10, 280, 500, 260);
        panConsole.setBorder(new TitledBorder("日志"));
        panConsole.setLayout(null);
      //分别设置水平和垂直滚动条自动出现 
        JScrollPane scroll = new JScrollPane(jTextFieldConsole); 
        scroll.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
       scroll.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
        panConsole.add(scroll);
        scroll.setBounds(10, 20, 480, 230);
        
        Container cont = getContentPane();
        cont.add(panStatus, BorderLayout.CENTER);
        cont.add(panCfg, BorderLayout.CENTER);
        cont.add(panConsole);
        cont.add(panTraffic);
        
        this.setJMenuBar(this.getMenu());//添加菜单

        this.setTitle("www.pc5s.cn(v1.1)");//设置窗口标题

        MyPrintStream mps = new MyPrintStream(System.out, jTextFieldConsole);
        System.setOut(mps);  
        System.setErr(mps);
        
        loadFromConfigFile();
        
        guiDisplay = new GuiDisplay(labelTraffic);
        sizeStatis = new Thread(guiDisplay);
        sizeStatis.start();
        
        cacheFrame = new CacheFrame();
        cacheFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);//关闭时隐藏窗口
        cacheFrame.setVisible(false);
    }
    /**
     * 菜单的级别JMenuBar->JMenu->JMenuItem
     * 三级都是1：n的关系
     * 最后添加菜单用的SetJMenuBar方法
     * @return 建立好的菜单
     */
    private JMenuBar getMenu(){
        if(menuBar==null){
            menuBar = new JMenuBar();
            JMenu m1 = new JMenu();
            m1.setText("文件");
            JMenu m3 = new JMenu();
            m3.setText("帮助");
            
            JMenuItem item11 = new JMenuItem();
            item11.setText("退出");
            item11.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try{
						client.stop();
						System.exit(0);
					}catch(Exception ex){
						System.exit(0);
					}
				}
			});
            
            JMenuItem item31 = new JMenuItem();
            item31.setText("在线帮助");
            item31.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					 try {
						Runtime.getRuntime().exec("cmd.exe /c start " + "https://pc5s.cn/pub/doc/get-started");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
            m1.add(item11);
            
            
            m3.add(item31);
            
            menuBar.add(m1);
            menuBar.add(m3);
        }
        return menuBar;
    }
    
    private void loadFromConfigFile(){
    	String fileName = "config.ini";
		File file = new File(fileName);
		if(!file.exists()){
			logger.error("配置文件"+fileName+"不存在");
		}
		try {
			PropertiesUtil.load(new FileInputStream(file));
			String userKey = PropertiesUtil.getPropertyString(Constants.AUTH_KEY);
			String local = PropertiesUtil.getPropertyString(Constants.PROXY_ADDR);
			this.textServer.setText(local);
			this.textAuthKey.setText(userKey);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// TODO Auto-generated method stub
		
	}
    
}
