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
	//��������
	public void createTitle(){
		// �����������
		titlePanel = new JPanel();
		
		// ���ñ������Ĵ�С
		titlePanel.setPreferredSize(new Dimension(600, 80));
		
		// ���ñ�������������ҵı߾�
		titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		
		// ���ñ�������弰��С
		title = new JLabel("ѧ����Ϣ", SwingConstants.CENTER);
		title.setFont(new Font("����", Font.BOLD, 28));
		
		// �ѱ������������
		titlePanel.add(title);
		
	    // �ѱ���������first panel���
		this.add(titlePanel, BorderLayout.NORTH);
		
	}
	//�ɼ���ʾ���
	public void createTable(){
		int count = 0;
		colName = new String[6];
		String[][] data = null;
		String sql = "select * from studentinfo";
		rs = db.query(sql);
		//�����ṹ����meta
		try {
			ResultSetMetaData meta = rs.getMetaData();
			count = meta.getColumnCount();
			colName = new String[count];
			//��ȡ��ѯ����ֶ���
			for(int i = 0; i < count; i++){
				colName[i] = meta.getColumnName(i + 1);
			}
		} catch (SQLException e2) {
			e2.printStackTrace();
		}
		//��Ų�ѯ���ݣ��ڱ������ʾ
		int k = 0;
		try {
			rs.last();
			int rows = rs.getRow();
			rs.beforeFirst();
			data = new String[rows][count];
			while(rs.next()){
				for(int i = 0; i < count; i++){
					//��������Ϣ���ڶ�ά������
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
	//�޸������
	public void createUpdateInput(){
		JPanel panelC = new JPanel();
		JPanel panelS = new JPanel();
		colNames = new JLabel[6];
		updateInput = new JTextField[6];
		colNames[0] = new JLabel("ѧ��");
		colNames[1] = new JLabel("����");
		colNames[2] = new JLabel("�Ա�");
		colNames[3] = new JLabel("����");
		colNames[4] = new JLabel("��ѧ");
		colNames[5] = new JLabel("Ӣ��");
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
		updateBtn[0] = new JButton("���");
		updateBtn[1] = new JButton("�޸�");
		updateBtn[2] = new JButton("ɾ��");
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
	//�޸�ѧ����Ϣ��Ӧ�¼�ʵ��
	private class UpdateStudentInfoListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			int flag = 0;
			String[] info = new String[6];//������������
			//��������
			for(int i  = 0; i < 6; i++){
				info[i] = updateInput[i].getText();
			}
			if(e.getSource() == updateBtn[0]){
				add = 1;
				//��Ӽ�¼(ʹ��sql�����в���)
				sql = "Insert into studentinfo values(";
				flag = isAvailable(info);
				sql += ")";
				if(flag == 1){
					JOptionPane.showMessageDialog(FirstPanel.this, "ѧ����Ϣ��д����������������Ϣ");
				}
				else if(flag == 2){
					JOptionPane.showMessageDialog(FirstPanel.this, "ѧ��ѧ���Ѵ��ڣ�����ѧ���Ƿ���д����");
				}
				else if(flag == 3){
					JOptionPane.showMessageDialog(FirstPanel.this, "�Ա��������������female��male");					
				}
				else if(flag == 4){
					JOptionPane.showMessageDialog(FirstPanel.this, "�ɼ���ʽ��������ɼ��Ƿ�Ϊ���ָ�ʽ");					
				}
				else{
				int num = db.insertInDB(sql);
				if(num >= 1){
					JOptionPane.showMessageDialog(FirstPanel.this, "��¼����ɹ�");
					//�ڱ�������һ����¼
					Object[] rowData = {updateInput[0].getText(), updateInput[1].getText(), 
										updateInput[2].getText(), updateInput[3].getText(), 
										updateInput[4].getText(), updateInput[5].getText()};
					tableModel.addRow(rowData);
				}
				else{
					JOptionPane.showMessageDialog(FirstPanel.this, "��¼����ʧ��");
				}
				}
				flag = 0;
			}
			else if(e.getSource() == updateBtn[1]){
				//�޸ļ�¼(ʹ�ý�����ķ������в���)
				int selectedRow = table.getSelectedRow();
				try {
					//ѧ���Ƿ��޸��Լ��������
					String id = "";
					id = (String) table.getValueAt(selectedRow, 0);
					if(!id.equals(info[0])){
						isU = 1;//ѧ�����޸�
					}
					System.out.println(flag);
					
					flag = isAvailable(info);
					System.out.println(flag);
					if(flag == 1){
						JOptionPane.showMessageDialog(FirstPanel.this, "ѧ����Ϣ��д����������������Ϣ");
					}
					else if(flag == 2 && isU == 1){//��ѧ��δ�޸������
						JOptionPane.showMessageDialog(FirstPanel.this, "ѧ��ѧ���Ѵ��ڣ�����ѧ���Ƿ���д����");
					}
					else if(flag == 3){
						JOptionPane.showMessageDialog(FirstPanel.this, "�Ա��������������female��male");					
					}
					else if(flag == 4){
						JOptionPane.showMessageDialog(FirstPanel.this, "�ɼ���ʽ��������ɼ��Ƿ�Ϊ���ָ�ʽ");					
					}
					else{
					//��λ
					rs = db.query("select * from studentinfo");
					rs.absolute(selectedRow + 1);
					for(int i = 0; i < 6; i++)
					rs.updateString(i + 1, info[i]);
					rs.updateRow();
					//�޸ı���е�����
					for(int i = 0; i < 6; i++)
					tableModel.setValueAt(updateInput[i].getText(), selectedRow, i);
					}
					JOptionPane.showMessageDialog(FirstPanel.this, "��¼�޸ĳɹ�");
				} catch (SQLException e1) {
					e1.printStackTrace();
				}				
			}
			else if(e.getSource() == updateBtn[2]){
				int option = JOptionPane.showConfirmDialog(FirstPanel.this, "ȷ��ɾ����¼��");
				if(option == JOptionPane.YES_OPTION){
				//ɾ����¼(ʹ�ý�����ķ������в���)
				int selectedRow = table.getSelectedRow();
				try {
					//��λ
					rs = db.query("select * from studentinfo");
					rs.absolute(selectedRow + 1);
					rs.deleteRow();
					//ɾ������еļ�¼
					tableModel.removeRow(selectedRow);
					JOptionPane.showMessageDialog(FirstPanel.this, "��¼ɾ���ɹ�");
				} catch (SQLException e1) {
					e1.printStackTrace();
				}	
				}
			}
			
		}
		//�ɼ��Ƿ���ת��Ϊ���ּ������Ƿ���ȷ
		public boolean isNum(String s){
			try{
				int num = Integer.valueOf(s);
				return true;
			}
		    catch(Exception e){
				return false;				
			}
					
		}
		//���������Ϣ�����Եļ��
		public int isAvailable(String[] info){
			//0:�������,1:��Ϣ������,2:ѧ���Ѵ���
			//3:�Ա��ʽ����,4:�ɼ���ʽ����
			int flag = 0;
			for(int i  = 0; i < 6; i++){
				String repeat;
				//����ѧ���Ƿ��Ѵ���
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
				
				if(info[i].equals("")){//��Ϣ�Ƿ���д����
					flag = 1;
					break;
				}						
				if(i >= 3){
					if(!isNum(info[i])){//�ɼ���ʽ�Ƿ���ȷ
						flag = 4;
						break;
					}
					if(i == 5){
						sql += info[i];
					}
					else
					sql += info[i] + ",";//����
				}
				else{
					//�Ա��ʽ���
					if(i == 2){
						if(!info[i].equals("male") && !info[i].equals("female")){
							flag = 3;
						}
					}
					sql += "'" + info[i] + "',";//�ַ���
				}
			}
			return flag;
		}
	}
	//��ʾѡ����Ϣ
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
