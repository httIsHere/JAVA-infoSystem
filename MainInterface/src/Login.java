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
	private JPasswordField userPwd;//密码输入框
	private JButton okBtn;
	private JButton closeBtn;
	
	private JPanel inputPanel;
	private JPanel btnPanel;
	private JPanel inputName;
	private JPanel inputPwd;
	
	public Login(){
		// set title
		this.setTitle("系统登录");
		// set size for the form
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		
		//input
		userNameLabel = new JLabel("用户名");
		userPwdLabel = new JLabel("密     码");
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
		okBtn = new JButton("确定");
		closeBtn = new JButton("关闭");
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
	//检测名字和密码
	private int Check(String name, String pwd){
		ResultSet rs;
		String sql = "Select * from security";
		rs = db.query(sql);
		int flag = 0;
		try {
			while(rs.next()){
			//检测通过
			if(name.equals(rs.getString(1)) && pwd.equals(rs.getString(2))){
				flag = 1;
			}
			//密码错误
			else if(name.equals(rs.getString(1)) && !pwd.equals(rs.getString(2))){
				flag = 2;
			}
			//用户不存在
			else 
				flag = 3;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}
	//按钮响应事件
	private class BtnListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			//确定按钮
			if(e.getActionCommand().equals("确定")){
			String name = userName.getText();
			String pwd = userPwd.getText();
			if(Check(name, pwd) == 1){
				new MainFrame();//进入主界面
				setVisible(false);
			}
			else if(Check(name, pwd) == 2){
				JOptionPane.showMessageDialog(Login.this, "密码错误");
			}
			else{
				JOptionPane.showMessageDialog(Login.this, "用户不存在");
			}
			}
			//关闭按钮
			else{
				System.exit(0);
			}
		}
		
	}
	public static void main(String[] args) {
		new Login();
	}

}
