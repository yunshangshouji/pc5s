package org.zhuboss.pc2server.pcproxy.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import org.zhuboss.pc2server.pcproxy.gui.cache.CacheData;
import org.zhuboss.pc2server.pcproxy.util.JsonUtil;
import org.zhuboss.pc2server.pcproxy.util.RestInvoker;

import zhuboss.pc2server.common.CacheResource;


public class CacheFrame extends JFrame implements ClipboardOwner{

	private static final long serialVersionUID = -1406153216091443551L;
	final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	JPanel panEdit;
	JButton jbAdd;
	JButton jbDelete;
	JButton jbRefresh;
	JButton jbSave;
	JButton jbClear ;
	JButton jbView;
	JComboBox<DictItem> cbHowMatch;
	JComboBox<DictItem> cbCaseSensitive;
	JComboBox<DictItem> cbCaseArgs;
	JTextField textExpireTime;
	JComboBox<DictItem> cbExpireUnit;
	JTextArea textLocation;
	JLabel labelCaseSensitive;
	JLabel labelCaseArgs;
	JLabel labelExpireTime;
	JLabel labelDataSize;
	DictItem[] howMatchItems = new DictItem[]{new DictItem("EQUALS","等于"),new DictItem("STARTS","起始于"),new DictItem("ENDS","结束于"),new DictItem("CONTAINS","包含")};
	DictItem[] booleanItems = new DictItem[]{new DictItem("1","是"),new DictItem("0","否")};
	DictItem[] timeUnitItems = new DictItem[]{new DictItem("MINUTES","分钟"),new DictItem("HOURS","小时"),new DictItem("DAYS","天")};
	RestInvoker restInvoker;
	public void setAppKey(String appKey){
		/**
		 * 初始化rest invoker
		 */
		restInvoker = new RestInvoker(appKey);
		restInvoker.setBaseURL("https://pc5s.cn");
        loadData();
        //默认值
        this.prepareAdd();
	}
	
	public CacheFrame(){
		
		
		
		this.setSize(536,610);
        this.setLocationRelativeTo(null); //使窗口剧中
        this.getContentPane().setLayout(null);//设置布局控制器
        this.setTitle("缓存管理");
        /**
         * 策略
         */
        JPanel panCfg = new JPanel();
        panCfg.setBounds(10, 10, 500, 340);
        panCfg.setBorder(new TitledBorder("缓存策略"));
        panCfg.setLayout(null);
        //
        jbAdd = new JButton("新增");
        jbAdd.setBounds(10, 20, 60, 30);
        jbAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CacheFrame.this.prepareAdd();
			}
        	
        });
        panCfg.add(jbAdd);
        //
        jbDelete = new JButton("删除");
        jbDelete.setBounds(100, 20, 60, 30);
        jbDelete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				if(row == -1){
					return;
				}
				Integer id = (Integer)list.get(row).get("id");
				String resultText = CacheFrame.this.restInvoker.get("/client/cache/delete/"+id, String.class);
				Map<String,Object> map = JsonUtil.unserializeFromJson(resultText, Map.class);
 				if(((String)map.get("result")).equals("success")){
 					CacheFrame.this.loadData();
 					prepareAdd();
 				}else{
 					JOptionPane.showMessageDialog(null, map.get("message"), "删除失败",JOptionPane.ERROR_MESSAGE);
 				}
			}
        	
        });
        panCfg.add(jbDelete);
        
        jbRefresh =  new JButton("刷新");
        jbRefresh.setBounds(190, 20, 60, 30);
        jbRefresh.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CacheFrame.this.loadData();
				
			}
        	
        });
        panCfg.add(jbRefresh);
        
        panEdit = new JPanel();
        panEdit.setBounds(10, 180, 470, 150);
        panEdit.setBorder(new TitledBorder("新增"));
        panEdit.setLayout(null);
        cbHowMatch = new JComboBox<>(howMatchItems);
        cbHowMatch.setBounds(10, 40, 100, 30);
        panEdit.add(cbHowMatch);
        //
        textLocation = new JTextArea();
        panEdit.add(textLocation);
        JScrollPane scroll = new JScrollPane(textLocation); 
        scroll.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
       scroll.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
       scroll.setBounds(120, 20, 130, 85);
       panEdit.add(scroll);
       //
       labelCaseSensitive = new JLabel("区分大小写");
       labelCaseSensitive.setBounds(260, 20,100, 25);
       panEdit.add(labelCaseSensitive);
       cbCaseSensitive = new JComboBox<>(booleanItems);
       cbCaseSensitive.setBounds(340, 20,60, 25);
       panEdit.add(cbCaseSensitive);
       //
       labelCaseArgs = new JLabel("包含参数");
       labelCaseArgs.setBounds(260, 50,100, 25);
       panEdit.add(labelCaseArgs);
       cbCaseArgs = new JComboBox<>(booleanItems);
       cbCaseArgs.setBounds(340,50,60, 25);
       panEdit.add(cbCaseArgs);
       //
       labelExpireTime = new JLabel("过期时间");
       labelExpireTime.setBounds(260, 80,100, 25);
       panEdit.add(labelExpireTime);
       textExpireTime = new JTextField();
       textExpireTime.setBounds(340,80,60, 25);
       cbExpireUnit = new JComboBox<>(timeUnitItems);
       cbExpireUnit.setBounds(405,80,60, 25);
       panEdit.add(textExpireTime);
       panEdit.add(cbExpireUnit);
       //
       jbSave = new JButton("保存");
       jbSave.setBounds(170,115,60, 25);
       jbSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Map<String,Object> data = new HashMap<String,Object>();
				data.put("id", id);
				data.put("userDomainId", userDomainId);
				data.put("howMatch", ((DictItem)cbHowMatch.getSelectedItem()).getValue());
				data.put("location", textLocation.getText());
				data.put("caseSensitive", ((DictItem)cbCaseSensitive.getSelectedItem()).getValue());
				data.put("caseArgs", ((DictItem)cbCaseArgs.getSelectedItem()).getValue());
				data.put("expireTime", textExpireTime.getText());
				data.put("expireUnit", ((DictItem)cbExpireUnit.getSelectedItem()).getValue());
 				String result = restInvoker.post("/client/cache/save", null, data);
 				Map<String,Object> map = JsonUtil.unserializeFromJson(result, Map.class);
 				if(((String)map.get("result")).equals("success")){
 					CacheFrame.this.loadData();
 					prepareAdd();
 				}else{
 					JOptionPane.showMessageDialog(null, map.get("message"), "保存失败",JOptionPane.ERROR_MESSAGE);
 				}
			}
		});
       panEdit.add(jbSave);
       
        //
        panCfg.add(panEdit);
        //
        JPanel gridPannel = createStrategyGridPanel();
        gridPannel.setBounds(15, 45, 470, 140);
        panCfg.add(gridPannel);
       
        /**
         * 缓存数据
         */
        JPanel panData = new JPanel();
        panData.setBounds(10, 360, 500, 200);
        panData.setBorder(new TitledBorder("缓存数据查看"));
        panData.setLayout(null);
        //
        jbView = new JButton("加载");
        jbView.setBounds(10, 20, 60, 25);
        jbView.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CacheData cacheData = restInvoker.get("/client/cache/view", CacheData.class);
				((DefaultTableModel) dataTable.getModel()).getDataVector().clear();
				((DefaultTableModel) dataTable.getModel()).fireTableDataChanged();
				dataTable.updateUI();
				for(CacheResource row : cacheData.getList()){
					((DefaultTableModel)dataTable.getModel()).addRow(new Object[]{
							row.getUri(),
							row.getBytes()+"",
							sdf.format(row.getExpireDate())
							});
				}
				//统计
				labelDataSize.setText("文件数:"+cacheData.getList().size()+",\t大小:"+cacheData.getTotalSize());
			}
		});
        panData.add(jbView);
        //
        jbClear = new JButton("清空");
        jbClear.setBounds(100, 20, 60, 25);
        jbClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String resultText = restInvoker.get("/client/cache/removeall", String.class);
				Map<String,Object> map = JsonUtil.unserializeFromJson(resultText, Map.class);
 				if(!((String)map.get("result")).equals("success")){
 					JOptionPane.showMessageDialog(null, map.get("message"), "清空失败",JOptionPane.ERROR_MESSAGE);
 				}
 				jbView.getActionListeners()[0].actionPerformed(null);
			}
        	
        });
        panData.add(jbClear);
        labelDataSize = new JLabel("");
        labelDataSize.setBounds(200, 20, 150, 25);
        panData.add(labelDataSize);
        //
        JPanel panDataGrid = this.createDataGridPanel();
        panDataGrid.setBounds(10, 40, 470, 160);
        panData.add(panDataGrid);
        
        Container cont = getContentPane();
        cont.add(panCfg, BorderLayout.CENTER);
        cont.add(panData, BorderLayout.CENTER);
        
	}
	
	private void prepareAdd(){
		panEdit.setBorder(new TitledBorder("新增"));
		CacheFrame.this.id=null;
		CacheFrame.this.userDomainId=null;
		cbHowMatch.setSelectedIndex(CacheFrame.this.findIndex("EQUALS", howMatchItems));
		CacheFrame.this.textLocation.setText("/");
		cbCaseSensitive.setSelectedIndex(CacheFrame.this.findIndex("1", booleanItems));
		cbCaseArgs.setSelectedIndex(CacheFrame.this.findIndex("0", booleanItems));
		textExpireTime.setText("1");
		cbExpireUnit.setSelectedIndex(CacheFrame.this.findIndex("HOURS", timeUnitItems));
	}
	
	List<Map> list;
	JTable table;
	private void loadData(){
		list = restInvoker.getList("/client/cache/userDomainCacheList", Map.class);
		((DefaultTableModel) table.getModel()).getDataVector().clear();
		((DefaultTableModel) table.getModel()).fireTableDataChanged();
		table.updateUI();
		for(Map<String,Object> map : list){
			((DefaultTableModel)table.getModel()).addRow(new Object[]{
					(String)map.get("howMatchName"),
					(String)map.get("location"),
					(String)map.get("caseSensitiveNa"),
					(String)map.get("caseArgsName"),
					map.get("expireTime")+"\t"+map.get("expireUnitName")
					});
		}
	}
	
	private Integer id;
	private Integer userDomainId;
	private JPanel createStrategyGridPanel() { 
		JPanel topPanel = new JPanel(); 
		 String[] columnName = { "匹配方式", "路径", "区分大小写", "包含参数", "过期时间" }; 
		 String[][] rowData = {}; 
		            // 创建表格
		 table = new JTable(new DefaultTableModel(rowData, columnName));
		 table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				int row = table.getSelectedRow();
				panEdit.setBorder(new TitledBorder("修改"));
				id = (Integer)list.get(row).get("id");
				userDomainId = (Integer)list.get(row).get("userDomainId");
				//
				String howMatch = (String)list.get(row).get("howMatch");
				cbHowMatch.setSelectedIndex(CacheFrame.this.findIndex(howMatch, howMatchItems));
				//
				String location = (String)list.get(row).get("location");
				CacheFrame.this.textLocation.setText(location);
				//
				String caseSensitive = String.valueOf(list.get(row).get("caseSensitive"));
				cbCaseSensitive.setSelectedIndex(CacheFrame.this.findIndex(caseSensitive, booleanItems));
				//
				String caseArgs = String.valueOf(list.get(row).get("caseArgs"));
				cbCaseArgs.setSelectedIndex(CacheFrame.this.findIndex(caseArgs, booleanItems));
				//
				textExpireTime.setText(list.get(row).get("expireTime")+"");
				//
				cbExpireUnit.setSelectedIndex(CacheFrame.this.findIndex((String)list.get(row).get("expireUnit"), timeUnitItems));
				super.mouseClicked(e);
			}
			 
		});
		            // 创建包含表格的滚动窗格
		 JScrollPane scrollPane = new JScrollPane(table);
		 scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
		            // 定义 topPanel 的布局为 BoxLayout，BoxLayout 为垂直排列
		 topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS)); 
		            // 先加入一个不可见的 Strut，从而使 topPanel 对顶部留出一定的空间
		 topPanel.add(Box.createVerticalStrut(10)); 
		            // 加入包含表格的滚动窗格 
		 topPanel.add(scrollPane); 
		            // 再加入一个不可见的 Strut，从而使 topPanel 和 middlePanel 之间留出一定的空间
		  topPanel.add(Box.createVerticalStrut(10)); 
		  return topPanel;
	}
	
	JTable dataTable ;
	private JPanel createDataGridPanel() { 
		JPanel topPanel = new JPanel(); 
		 String[] columnName = { "URI",  "大小", "过期时间" }; 
		 String[][] rowData = {}; 
		            // 创建表格
		 dataTable = new JTable(new DefaultTableModel(rowData, columnName));
		 dataTable.getColumnModel().getColumn(0).setPreferredWidth(255);
		 dataTable.getColumnModel().getColumn(1).setPreferredWidth(70);
		 dataTable.getColumnModel().getColumn(2).setPreferredWidth(125);
		 dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		            // 创建包含表格的滚动窗格
		 JScrollPane scrollPane = new JScrollPane(dataTable);
		 scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
		            // 定义 topPanel 的布局为 BoxLayout，BoxLayout 为垂直排列
		 topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS)); 
		            // 先加入一个不可见的 Strut，从而使 topPanel 对顶部留出一定的空间
		 topPanel.add(Box.createVerticalStrut(10)); 
		            // 加入包含表格的滚动窗格 
		 topPanel.add(scrollPane); 
		            // 再加入一个不可见的 Strut，从而使 topPanel 和 middlePanel 之间留出一定的空间
		  topPanel.add(Box.createVerticalStrut(10)); 
		  return topPanel;
	}
	
	
	private int findIndex(String value,DictItem[] items){
		for(int i=0;i<items.length;i++){
			if(items[i].getValue().equals(value)){
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// TODO Auto-generated method stub
		
	}

}
