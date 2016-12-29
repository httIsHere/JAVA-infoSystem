import java.awt.BorderLayout;
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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;


@SuppressWarnings("serial")
public class FirstPanel extends JPanel{
	private JdbcMySQL db = new JdbcMySQL(); 
	private ResultSet rs;
	private String[] colName;
	private JLabel title;
	private JPanel titlePanel;
	private static final long serialVersionUID = 1L;
	private JPanel centerPanel;
	private JScrollPane scrollPane;
	private JLabel[] colNames;
	private JTextField[] updateInput;
	private JPanel updatePanel;
	private JButton[] updateBtn;
	private JTable table;
	private DefaultTableModel tableModel;
	private String sql = "";
	private int isU = 0;
	private int add = 0;
	
	public FirstPanel(){
		createTitle();
		createTable();
		createUpdateInput();
	}
	//创建标题
	public void createTitle(){
		// 创建标题面板
		titlePanel = new JPanel();
		
		// 设置标题面板的大小
		titlePanel.setPreferredSize(new Dimension(600, 80));
		
		// 设置标题面板上下左右的边距
		titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		
		// 设置标题的字体及大小
		title = new JLabel("学生信息", SwingConstants.CENTER);
		title.setFont(new Font("宋体", Font.BOLD, 28));
		
		// 把标题加入标题面板
		titlePanel.add(title);
		
	    // 把标题面板加入first panel面板
		this.add(titlePanel, BorderLayout.NORTH);
		
	}
	//成绩显示表格
	public void createTable(){
		int count = 0;
		colName = new String[6];
		String[][] data = null;
		String sql = "select * from studentinfo";
		rs = db.query(sql);
		//创建结构集的meta
		try {
			ResultSetMetaData meta = rs.getMetaData();
			count = meta.getColumnCount();
			colName = new String[count];
			//获取查询结果字段名
			for(int i = 0; i < count; i++){
				colName[i] = meta.getColumnName(i + 1);
			}
		} catch (SQLException e2) {
			e2.printStackTrace();
		}
		//存放查询数据，在表格中显示
		int k = 0;
		try {
			rs.last();
			int rows = rs.getRow();
			rs.beforeFirst();
			data = new String[rows][count];
			while(rs.next()){
				for(int i = 0; i < count; i++){
					//将数据信息放在二维数组内
					data[k][i] = rs.getString(i + 1);
				}
				k++;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		// Create a TableModel with the results.
		tableModel = new DefaultTableModel(data, colName);
		table = new JTable(tableModel);
		// TableSelectionModel
		table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		table.addMouseListener(new ClickRow());
		// Put the table in a scroll pane.
		scrollPane = new JScrollPane(table);
		table.setPreferredScrollableViewportSize(new Dimension(700, 300));
		
		table.setRowHeight(30);
		centerPanel = new JPanel();
		centerPanel.add(scrollPane);

		this.add(centerPanel, BorderLayout.CENTER);
	}
	public void updateTable(){
	
	}
	//修改输入框
	public void createUpdateInput(){
		JPanel panelC = new JPanel();
		JPanel panelS = new JPanel();
		colNames = new JLabel[6];
		updateInput = new JTextField[6];
		colNames[0] = new JLabel("学号");
		colNames[1] = new JLabel("姓名");
		colNames[2] = new JLabel("性别");
		colNames[3] = new JLabel("语文");
		colNames[4] = new JLabel("数学");
		colNames[5] = new JLabel("英语");
		updateInput[0] = new JTextField(10);
		updateInput[1] = new JTextField(10);
		updateInput[2] = new JTextField(5);
		updateInput[3] = new JTextField(5);
		updateInput[4] = new JTextField(5);
		updateInput[5] = new JTextField(5);
		
		updatePanel = new JPanel(new BorderLayout());
		for(int i = 0; i < 6; i++){
			panelC.add(colNames[i]);
			panelC.add(updateInput[i]);
		}
		updateBtn = new JButton[3];
		updateBtn[0] = new JButton("添加");
		updateBtn[1] = new JButton("修改");
		updateBtn[2] = new JButton("删除");
		for(int i = 0; i < 3; i++){
			panelS.add(updateBtn[i]);
		}
		updatePanel.add(panelC);
		updatePanel.add(panelS, BorderLayout.SOUTH);
		add(updatePanel, BorderLayout.SOUTH);
		
		for(int i = 0; i < 3; i++){
			updateBtn[i].addActionListener(new UpdateStudentInfoListener());
		}
	}
	//修改学生信息响应事件实现
	private class UpdateStudentInfoListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			int flag = 0;
			String[] info = new String[6];//存放输入的数据
			//存入数组
			for(int i  = 0; i < 6; i++){
				info[i] = updateInput[i].getText();
			}
			if(e.getSource() == updateBtn[0]){
				add = 1;
				//添加纪录(使用sql语句进行操作)
				sql = "Insert into studentinfo values(";
				flag = isAvailable(info);
				sql += ")";
				if(flag == 1){
					JOptionPane.showMessageDialog(FirstPanel.this, "学生信息填写不完整，请完整信息");
				}
				else if(flag == 2){
					JOptionPane.showMessageDialog(FirstPanel.this, "学生学号已存在，请检查学号是否填写错误");
				}
				else if(flag == 3){
					JOptionPane.showMessageDialog(FirstPanel.this, "性别输入错误，请输入female或male");					
				}
				else if(flag == 4){
					JOptionPane.showMessageDialog(FirstPanel.this, "成绩格式错误，请检查成绩是否为数字格式");					
				}
				else{
				int num = db.insertInDB(sql);
				if(num >= 1){
					JOptionPane.showMessageDialog(FirstPanel.this, "记录插入成功");
					//在表格中添加一条记录
					Object[] rowData = {updateInput[0].getText(), updateInput[1].getText(), 
										updateInput[2].getText(), updateInput[3].getText(), 
										updateInput[4].getText(), updateInput[5].getText()};
					tableModel.addRow(rowData);
				}
				else{
					JOptionPane.showMessageDialog(FirstPanel.this, "记录插入失败");
				}
				}
				flag = 0;
			}
			else if(e.getSource() == updateBtn[1]){
				//修改记录(使用结果集的方法进行操作)
				int selectedRow = table.getSelectedRow();
				try {
					//学号是否修改以及其合理性
					String id = "";
					id = (String) table.getValueAt(selectedRow, 0);
					if(!id.equals(info[0])){
						isU = 1;//学号已修改
					}
					System.out.println(flag);
					
					flag = isAvailable(info);
					System.out.println(flag);
					if(flag == 1){
						JOptionPane.showMessageDialog(FirstPanel.this, "学生信息填写不完整，请完整信息");
					}
					else if(flag == 2 && isU == 1){//若学号未修改则忽视
						JOptionPane.showMessageDialog(FirstPanel.this, "学生学号已存在，请检查学号是否填写错误");
					}
					else if(flag == 3){
						JOptionPane.showMessageDialog(FirstPanel.this, "性别输入错误，请输入female或male");					
					}
					else if(flag == 4){
						JOptionPane.showMessageDialog(FirstPanel.this, "成绩格式错误，请检查成绩是否为数字格式");					
					}
					else{
					//定位
					rs = db.query("select * from studentinfo");
					rs.absolute(selectedRow + 1);
					for(int i = 0; i < 6; i++)
					rs.updateString(i + 1, info[i]);
					rs.updateRow();
					//修改表格中的数据
					for(int i = 0; i < 6; i++)
					tableModel.setValueAt(updateInput[i].getText(), selectedRow, i);
					}
					JOptionPane.showMessageDialog(FirstPanel.this, "记录修改成功");
				} catch (SQLException e1) {
					e1.printStackTrace();
				}				
			}
			else if(e.getSource() == updateBtn[2]){
				int option = JOptionPane.showConfirmDialog(FirstPanel.this, "确定删除记录？");
				if(option == JOptionPane.YES_OPTION){
				//删除记录(使用结果集的方法进行操作)
				int selectedRow = table.getSelectedRow();
				try {
					//定位
					rs = db.query("select * from studentinfo");
					rs.absolute(selectedRow + 1);
					rs.deleteRow();
					//删除表格中的记录
					tableModel.removeRow(selectedRow);
					JOptionPane.showMessageDialog(FirstPanel.this, "记录删除成功");
				} catch (SQLException e1) {
					e1.printStackTrace();
				}	
				}
			}
			
		}
		//成绩是否能转换为数字即输入是否正确
		public boolean isNum(String s){
			try{
				int num = Integer.valueOf(s);
				return true;
			}
		    catch(Exception e){
				return false;				
			}
					
		}
		//输入框内信息合理性的检测
		public int isAvailable(String[] info){
			//0:输入合理,1:信息不完整,2:学号已存在
			//3:性别格式错误,4:成绩格式错误
			int flag = 0;
			for(int i  = 0; i < 6; i++){
				String repeat;
				//检测该学号是否已存在
				repeat = "select stuid from studentinfo where stuid=" + info[0];
				ResultSet res = db.query(repeat);
				try {
					res.last();
					int num = res.getRow();
					if(num >= 1){
						flag = 2;
						if(add == 1 || isU == 1)
						break;
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
				if(info[i].equals("")){//信息是否填写完整
					flag = 1;
					break;
				}						
				if(i >= 3){
					if(!isNum(info[i])){//成绩格式是否正确
						flag = 4;
						break;
					}
					if(i == 5){
						sql += info[i];
					}
					else
					sql += info[i] + ",";//数字
				}
				else{
					//性别格式检测
					if(i == 2){
						if(!info[i].equals("male") && !info[i].equals("female")){
							flag = 3;
						}
					}
					sql += "'" + info[i] + "',";//字符串
				}
			}
			return flag;
		}
	}
	//显示选中信息
	private class ClickRow extends MouseAdapter{
		public void mouseClicked(MouseEvent e){
			int selectedRow = table.getSelectedRow();
			if(selectedRow != -1){
				for(int i = 0; i < 6; i++){
				updateInput[i].setText((String) table.getValueAt(selectedRow, i));
				}
			}
		}
	}
}
