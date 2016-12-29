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
		setBtn = new JButton("ȷ��");
		btnPanel = new JPanel();
		btnPanel.add(setBtn);
		allPanel.add(btnPanel, BorderLayout.SOUTH);
		add(allPanel);
		
		setBtn.addActionListener(new SetPwdBtn());
	}
	public void createTitle(){
		// �����������
		titlePanel = new JPanel();
		
		// ���ñ������Ĵ�С
		titlePanel.setPreferredSize(new Dimension(600, 80));
		
		// ���ñ�������������ҵı߾�
		titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		
		// ���ñ�������弰��С
		title = new JLabel("��������", SwingConstants.CENTER);
		title.setFont(new Font("����", Font.BOLD, 28));
		
		// �ѱ������������
		titlePanel.add(title);
		
	    // �ѱ���������first panel���
		allPanel.add(titlePanel, BorderLayout.NORTH);
	}
	//�����޸������
	public void createUpdateInput(){
		updatePanel = new JPanel(new GridLayout(3, 2, 5, 5));
		updateLabel = new JLabel[3];
		updateInput = new JPasswordField[3];
		updateLabel[0] = new JLabel("����������룺");
		updateLabel[1] = new JLabel("�����������룺");
		updateLabel[2] = new JLabel("���ٴ����������룺");
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
						JOptionPane.showMessageDialog(ThirdPanel2.this, "�޸ĳɹ���");
					}
					else{
						JOptionPane.showMessageDialog(ThirdPanel2.this, "�������������벻��ͬ��");
					}
				}
				else{
					JOptionPane.showMessageDialog(ThirdPanel2.this, "ԭ�����������");
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
		}
		
	}
}
