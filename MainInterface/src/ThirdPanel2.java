import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class ThirdPanel2 extends JPanel{
	private JdbcMySQL db = new JdbcMySQL(); 
	private JLabel title;
	private JPanel titlePanel;
	private JLabel[] updateLabel;
	private JPasswordField[] updateInput;
	private JPanel updatePanel;
	private JButton setBtn;
	private JPanel btnPanel;
	private JPanel allPanel;
	private JPanel panelN;
	private JPanel panelC;
	
	public ThirdPanel2(){
		panelN = new JPanel();
		panelC = new JPanel();
		allPanel = new JPanel(new BorderLayout());
		createTitle();
		createUpdateInput();
		setBtn = new JButton("确定");
		btnPanel = new JPanel();
		btnPanel.add(setBtn);
		allPanel.add(btnPanel, BorderLayout.SOUTH);
		add(allPanel);
		
		setBtn.addActionListener(new SetPwdBtn());
	}
	public void createTitle(){
		// 创建标题面板
		titlePanel = new JPanel();
		
		// 设置标题面板的大小
		titlePanel.setPreferredSize(new Dimension(600, 80));
		
		// 设置标题面板上下左右的边距
		titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		
		// 设置标题的字体及大小
		title = new JLabel("重置密码", SwingConstants.CENTER);
		title.setFont(new Font("宋体", Font.BOLD, 28));
		
		// 把标题加入标题面板
		titlePanel.add(title);
		
	    // 把标题面板加入first panel面板
		allPanel.add(titlePanel, BorderLayout.NORTH);
	}
	//创建修改输入框
	public void createUpdateInput(){
		updatePanel = new JPanel(new GridLayout(3, 2, 5, 5));
		updateLabel = new JLabel[3];
		updateInput = new JPasswordField[3];
		updateLabel[0] = new JLabel("请输入旧密码：");
		updateLabel[1] = new JLabel("请输入新密码：");
		updateLabel[2] = new JLabel("请再次输入新密码：");
		for(int i = 0; i < 3; i++){
			updatePanel.add(updateLabel[i]);
			updateInput[i] = new JPasswordField(15);
			updatePanel.add(updateInput[i]);
		}
		panelC.add(updatePanel);
		panelC.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));
		allPanel.add(panelC);
	}
	
	private class SetPwdBtn implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			String sql = "select password from security";
			ResultSet rs = db.query(sql);
			String pwd;
			String[] newpwd = new String[2];
			pwd = updateInput[0].getText();
			newpwd[0] = updateInput[1].getText();
			newpwd[1] = updateInput[2].getText();
			
			try {
				rs.next();
				String realPwd = rs.getString(1);
				if(realPwd.equals(pwd)){
					if(newpwd[0].equals(newpwd[1])){
						sql = "update security password = '" + newpwd[0] + "'";
						JOptionPane.showMessageDialog(ThirdPanel2.this, "修改成功！");
					}
					else{
						JOptionPane.showMessageDialog(ThirdPanel2.this, "新密码两次输入不相同！");
					}
				}
				else{
					JOptionPane.showMessageDialog(ThirdPanel2.this, "原密码输入错误！");
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
		}
		
	}
}
