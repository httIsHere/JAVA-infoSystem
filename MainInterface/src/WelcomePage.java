import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class WelcomePage extends JPanel{
	
	private ImageIcon welcomeImage;
	private JLabel iconLabel;
	private JLabel title;
	
	public WelcomePage(){
		this.setLayout(new BorderLayout());
		welcomeImage = new ImageIcon("bg.png");
		iconLabel = new JLabel();
		iconLabel.setIcon(welcomeImage);
		iconLabel.setFont(new Font("΢���ź�", Font.BOLD,40));
		iconLabel.setForeground(Color.white);
		iconLabel.setText("��ӭ����ѧ����Ϣ����ϵͳ");
		//�����ı�λ��
		iconLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
		iconLabel.setVerticalAlignment(SwingConstants.CENTER);
		iconLabel.setVerticalTextPosition(SwingConstants.CENTER);
		this.add(iconLabel);
	}

}
