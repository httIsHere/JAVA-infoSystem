import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;


@SuppressWarnings("serial")
public class SecondPanel2 extends JPanel{
	private JdbcMySQL db = new JdbcMySQL(); 
	private Vector colNames = new Vector();
	private Vector colName = new Vector();
	private Vector data = new Vector();
	private String sql = null;
	private JLabel title;
	private JPanel titlePanel;
	private JTextArea sqlArea;
	private JButton queryBtn;
	private JTable table;
    private DefaultTableModel resultTable; //表格用的数据模型
    private JScrollPane scrollPane;//存放表格的，表格必须放在里面
	private JPanel panelC;//条件文本域
	private JPanel panelList;//列表框面板
	private JPanel panelCP;//条件文本域
	private JPanel panelListP;//列表框面板
	private JList list;
	private String[] col = {"学号", "姓名", "性别", "语文", "数学", "英语"};
	private JLabel chooseOne;
	private JLabel chooseTwo;
	
	public SecondPanel2(){
		createTitle();
		createSql();
		createList();
		createTable();
	}	
	//创建标题
	public void createTitle(){
		// 创建标题面板
		titlePanel = new JPanel(new BorderLayout());
		
		// 设置标题面板的大小
		titlePanel.setPreferredSize(new Dimension(600, 80));
		
		// 设置标题面板上下左右的边距
		titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		
		// 设置标题的字体及大小
		title = new JLabel("查询信息", SwingConstants.CENTER);
		title.setFont(new Font("宋体", Font.BOLD, 28));
		
		// 把标题加入标题面板
		titlePanel.add(title);
		
		chooseOne = new JLabel("输入条件");
		chooseOne.setForeground(Color.pink);
		chooseTwo = new JLabel("选择字段");
		JPanel chooseP = new JPanel();
		chooseP.add(chooseOne);
		chooseP.add(chooseTwo);
		
		chooseOne.addMouseListener(new LabelClick());
		chooseTwo.addMouseListener(new LabelClick());
		
		titlePanel.add(chooseP, BorderLayout.SOUTH);
	    // 把标题面板加入first panel面板
		this.add(titlePanel, BorderLayout.NORTH);
	}
	//创建输入条件框
	public void createSql(){
		JPanel panelS = new JPanel();
		panelCP = new JPanel(new BorderLayout());
		sqlArea = new JTextArea(5, 70);
		queryBtn = new JButton("查询");
		panelC = new JPanel(new BorderLayout());
		panelC.setPreferredSize(new Dimension(700, 160));
		panelCP.add(sqlArea);
		panelS.add(queryBtn);
		panelCP.add(panelS, BorderLayout.SOUTH);
		panelC.add(panelCP);
		add(panelC);		
		panelCP.setVisible(true);
		queryBtn.addActionListener(new QueryBtnListener());
	}
	//创建字段查询列表框
	public void createList(){
		panelList = new JPanel(new BorderLayout());
		panelListP = new JPanel(new BorderLayout());
		JPanel btnP = new JPanel();
		panelList.setPreferredSize(new Dimension(700, 160));
		list = new JList(col);
		list.setBorder(BorderFactory.createLineBorder(Color.black));
		list.setPreferredSize(new Dimension(500, 120));
		queryBtn = new JButton("查询");
		btnP.add(queryBtn);
		panelListP.add(list);
		panelListP.add(btnP, BorderLayout.SOUTH);
		panelList.add(panelListP);
		panelListP.setVisible(false);
		queryBtn.addActionListener(new QueryBtnListener());
		list.addListSelectionListener(new ListListener());
	}
	//切换查询方式
	public class LabelClick extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getSource() == chooseOne) {
				chooseOne.setForeground(Color.pink);
				chooseTwo.setForeground(Color.black);
				if (!panelCP.isVisible()) {
					panelC.add(panelCP, BorderLayout.CENTER);
					panelCP.setVisible(true);
				}
				panelListP.setVisible(false);
			}
			if(e.getSource() == chooseTwo){
				chooseOne.setForeground(Color.black);
				chooseTwo.setForeground(Color.pink);
				if (!panelListP.isVisible()) {
					panelC.add(panelListP, BorderLayout.CENTER);
					panelListP.setVisible(true);
				}
				panelCP.setVisible(false);
			}
		}
	}
	//表格框
	public void createTable(){
		colNames.addElement("学号");
		colNames.addElement("姓名");
		colNames.addElement("性别");
		colNames.addElement("语文");
		colNames.addElement("数学");
		colNames.addElement("英语");
		colName.addElement("stuId");
		colName.addElement("name");
		colName.addElement("gender");
		colName.addElement("chinese");
		colName.addElement("math");
		colName.addElement("english");
		//初始化表格内容（初始显示六条记录行）
		for(int i = 0; i < 6; i++){
			Vector record = new Vector();
			record.addElement(" ");
			record.addElement(" ");
			record.addElement(" ");
			record.addElement(" ");
			record.addElement(" ");
			record.addElement(" ");
			data.addElement(record);
		}
		table = new JTable();
		//显示表格
		resultTable = new DefaultTableModel(data, colNames);
		table.setModel(resultTable);
		table.setPreferredScrollableViewportSize(new Dimension(800, 180));
		table.setRowHeight(30);
		scrollPane = new JScrollPane(table);
		add(scrollPane, BorderLayout.SOUTH);
		
	}
	//查询响应事件的实现
	private class QueryBtnListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			ResultSetMetaData meta = null;
			if(panelCP.isVisible()){
			sql = sqlArea.getText();
			}
			if(!sql.equals("")){
			ResultSet res = db.query(sql);
			int count = 0;
			//创建结构集的meta
			try {
				meta = res.getMetaData();
				count = meta.getColumnCount();
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
			//存放查询数据，在表格中显示
			int k = 0;
			int c = 1;
			data.clear();
			try {
				res.last();
				int rows = res.getRow();
				res.beforeFirst();
				res.next();
				while(res.next()){
					Vector record = new Vector();
					
					for(int i = 0; i < 6; i++){
						if(c <= count){//c 结果集字段号，根据对应进行存放
						//未被查询的字段为空
						if(colName.get(i).equals(meta.getColumnName(c))){
							//将数据信息到相应的位置
							record.addElement(res.getString(c));
							c++;
						}
						else{
							record.addElement(" ");
						}
						}
						else{
							record.addElement(" ");
						}
					}
					c = 1;
					data.addElement(record);
					k++;
				}
				resultTable.setDataVector(data, colNames);
				//select math, name, chinese from studentinfo
				//select name, chinese from studentinfo
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			}
			
		}
		
	}
	//列表框响应事件
	private class ListListener implements ListSelectionListener{
		public void valueChanged(ListSelectionEvent e) {
			if(list.isSelectionEmpty()){
				sql = "";
			}
			else{
				sql = "select ";
				//根据列表框内容组成sql语句
				int[] selected = list.getSelectedIndices();
				String[] cols = {"stuid", "name", "gender", "chinese", "math", "english"};
				for(int i = 0; i < selected.length; i++){
					if(i == 0){
						sql += cols[selected[i]];
					}
					else{
						sql += ", ";
						sql += cols[selected[i]];
					}
				}
				sql += " from studentinfo";
			}
		}
		
	}
}
