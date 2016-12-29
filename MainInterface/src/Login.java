import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Login extends JFrame{
	private JdbcMySQL db = new JdbcMySQL(); 
	private int WINDOW_WIDTH = 400;
	private int WINDOW_HEIGHT = 200;
	private JLabel userNameLabel;
	private JLabel userPwdLabel;
	private JTextField userName;
	private JPasswordField userPwd;//���������
	private JButton okBtn;
	private JButton closeBtn;
	
	private JPanel inputPanel;
	private JPanel btnPanel;
	private JPanel inputName;
	private JPanel inputPwd;
	
	public Login(){
		// set title
		this.setTitle("ϵͳ��¼");
		// set size for the form
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		
		//input
		userNameLabel = new JLabel("�û���");
		userPwdLabel = new JLabel("��     ��");
		userName = new JTextField(15);
		userPwd = new JPasswordField(15);
		inputPanel = new JPanel(new BorderLayout());
		inputName = new JPanel();
		inputName.add(userNameLabel);
		inputName.add(userName);
		inputPwd = new JPanel();
		inputPwd.add(userPwdLabel);
		inputPwd.add(userPwd);
		inputPanel.add(inputName);
		inputPanel.add(inputPwd, BorderLayout.SOUTH);
		inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		
		//button
		okBtn = new JButton("ȷ��");
		closeBtn = new JButton("�ر�");
		btnPanel = new JPanel();
		btnPanel.add(okBtn);
		btnPanel.add(closeBtn);
		
		add(inputPanel, BorderLayout.NORTH);
		add(btnPanel);
		
		BtnListener btnListener = new BtnListener();
		okBtn.addActionListener(btnListener);
		closeBtn.addActionListener(btnListener);
		
		setVisible(true);
    	setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	//������ֺ�����
	private int Check(String name, String pwd){
		ResultSet rs;
		String sql = "Select * from security";
		rs = db.query(sql);
		int flag = 0;
		try {
			while(rs.next()){
			//���ͨ��
			if(name.equals(rs.getString(1)) && pwd.equals(rs.getString(2))){
				flag = 1;
			}
			//�������
			else if(name.equals(rs.getString(1)) && !pwd.equals(rs.getString(2))){
				flag = 2;
			}
			//�û�������
			else 
				flag = 3;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}
	//��ť��Ӧ�¼�
	private class BtnListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			//ȷ����ť
			if(e.getActionCommand().equals("ȷ��")){
			String name = userName.getText();
			String pwd = userPwd.getText();
			if(Check(name, pwd) == 1){
				new MainFrame();//����������
				setVisible(false);
			}
			else if(Check(name, pwd) == 2){
				JOptionPane.showMessageDialog(Login.this, "�������");
			}
			else{
				JOptionPane.showMessageDialog(Login.this, "�û�������");
			}
			}
			//�رհ�ť
			else{
				System.exit(0);
			}
		}
		
	}
	public static void main(String[] args) {
		new Login();
	}

}
