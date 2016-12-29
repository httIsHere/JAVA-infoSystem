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
    private DefaultTableModel resultTable; //����õ�����ģ��
    private JScrollPane scrollPane;//��ű��ģ��������������
	private JPanel panelC;//�����ı���
	private JPanel panelList;//�б�����
	private JPanel panelCP;//�����ı���
	private JPanel panelListP;//�б�����
	private JList list;
	private String[] col = {"ѧ��", "����", "�Ա�", "����", "��ѧ", "Ӣ��"};
	private JLabel chooseOne;
	private JLabel chooseTwo;
	
	public SecondPanel2(){
		createTitle();
		createSql();
		createList();
		createTable();
	}	
	//��������
	public void createTitle(){
		// �����������
		titlePanel = new JPanel(new BorderLayout());
		
		// ���ñ������Ĵ�С
		titlePanel.setPreferredSize(new Dimension(600, 80));
		
		// ���ñ�������������ҵı߾�
		titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		
		// ���ñ�������弰��С
		title = new JLabel("��ѯ��Ϣ", SwingConstants.CENTER);
		title.setFont(new Font("����", Font.BOLD, 28));
		
		// �ѱ������������
		titlePanel.add(title);
		
		chooseOne = new JLabel("��������");
		chooseOne.setForeground(Color.pink);
		chooseTwo = new JLabel("ѡ���ֶ�");
		JPanel chooseP = new JPanel();
		chooseP.add(chooseOne);
		chooseP.add(chooseTwo);
		
		chooseOne.addMouseListener(new LabelClick());
		chooseTwo.addMouseListener(new LabelClick());
		
		titlePanel.add(chooseP, BorderLayout.SOUTH);
	    // �ѱ���������first panel���
		this.add(titlePanel, BorderLayout.NORTH);
	}
	//��������������
	public void createSql(){
		JPanel panelS = new JPanel();
		panelCP = new JPanel(new BorderLayout());
		sqlArea = new JTextArea(5, 70);
		queryBtn = new JButton("��ѯ");
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
	//�����ֶβ�ѯ�б��
	public void createList(){
		panelList = new JPanel(new BorderLayout());
		panelListP = new JPanel(new BorderLayout());
		JPanel btnP = new JPanel();
		panelList.setPreferredSize(new Dimension(700, 160));
		list = new JList(col);
		list.setBorder(BorderFactory.createLineBorder(Color.black));
		list.setPreferredSize(new Dimension(500, 120));
		queryBtn = new JButton("��ѯ");
		btnP.add(queryBtn);
		panelListP.add(list);
		panelListP.add(btnP, BorderLayout.SOUTH);
		panelList.add(panelListP);
		panelListP.setVisible(false);
		queryBtn.addActionListener(new QueryBtnListener());
		list.addListSelectionListener(new ListListener());
	}
	//�л���ѯ��ʽ
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
	//����
	public void createTable(){
		colNames.addElement("ѧ��");
		colNames.addElement("����");
		colNames.addElement("�Ա�");
		colNames.addElement("����");
		colNames.addElement("��ѧ");
		colNames.addElement("Ӣ��");
		colName.addElement("stuId");
		colName.addElement("name");
		colName.addElement("gender");
		colName.addElement("chinese");
		colName.addElement("math");
		colName.addElement("english");
		//��ʼ��������ݣ���ʼ��ʾ������¼�У�
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
		//��ʾ���
		resultTable = new DefaultTableModel(data, colNames);
		table.setModel(resultTable);
		table.setPreferredScrollableViewportSize(new Dimension(800, 180));
		table.setRowHeight(30);
		scrollPane = new JScrollPane(table);
		add(scrollPane, BorderLayout.SOUTH);
		
	}
	//��ѯ��Ӧ�¼���ʵ��
	private class QueryBtnListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			ResultSetMetaData meta = null;
			if(panelCP.isVisible()){
			sql = sqlArea.getText();
			}
			if(!sql.equals("")){
			ResultSet res = db.query(sql);
			int count = 0;
			//�����ṹ����meta
			try {
				meta = res.getMetaData();
				count = meta.getColumnCount();
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
			//��Ų�ѯ���ݣ��ڱ������ʾ
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
						if(c <= count){//c ������ֶκţ����ݶ�Ӧ���д��
						//δ����ѯ���ֶ�Ϊ��
						if(colName.get(i).equals(meta.getColumnName(c))){
							//��������Ϣ����Ӧ��λ��
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
	//�б����Ӧ�¼�
	private class ListListener implements ListSelectionListener{
		public void valueChanged(ListSelectionEvent e) {
			if(list.isSelectionEmpty()){
				sql = "";
			}
			else{
				sql = "select ";
				//�����б���������sql���
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
