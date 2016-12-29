import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;

import com.mysql.jdbc.PreparedStatement;



public class MainFrame extends JFrame {
	private JdbcMySQL db = new JdbcMySQL(); 
	private static final long serialVersionUID = 1L;
	private int WINDOW_WIDTH = 1000;
	private int WINDOW_HEIGHT = 600;
	private JPanel navigatorPanel;
	private SecondPanel2 secondPanel;
	private FirstPanel firstPanel;
	private ThirdPanel2 thirdPanel;
	private WelcomePage wpPanel;
	
	//�˵������
	private JMenuBar menuBar;
	private JMenu file;
	private JMenuItem[] fileItem;
	private JMenu statistic;
	private JMenu score;
	private JMenuItem[] scoreItem;
	private JMenuItem studentNumber;
	private JMenu surface;
	private JRadioButtonMenuItem[] surfaceItem;
	//���������
	private JLabel firstLabel, secondLabel, thirdLabel;
	private JPanel firstP, secondP, thirdP;
	private int r, g, b;
	
	//�ļ�����ʱ
	private byte[] buff = new byte[8]; // �����ֽ����飬��ʱ��Ŷ�ȡ��������
	private int readNum; // �������ֽ���
	private int size = 0; // �Ѳ���d���ֽ���
	private long totalSize = 0;//�ļ���С
	private Thread importFile;
	private boolean isRun = false;
	private JProgressBar progress;
	private String path;
	private FileInputStream inFile;
	private FileChannel inChannel;
	private ProgressFrame pf;
	
	public MainFrame() {
		// set title
		this.setTitle("�༶��Ϣ����");

		// set size for the form
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

		// set layout for the frame
		this.setLayout(new BorderLayout(10, 10));

		// �رշ�ʽ
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//�˵����
		createMenu();
		
		// ������
		buildNavigatorPanel();
		
		
		// ��Ҫ��ʾ�������󶼽�������
		buildOtherPanels();

		// add the created panels to the frame
		this.add(navigatorPanel, BorderLayout.WEST);
		
		// ��Ҫ��ʾ�������󵽷ŵ���ͬ�����ڴ�������ʾ��Ϊ���������塣
		// ��������Ŀ���ǰѶ��󶼺͸ô������������ֻ�й���������֮��Ӧ����۷������Motif���Ż�������������������
		this.add(thirdPanel, BorderLayout.CENTER);
		this.add(secondPanel, BorderLayout.CENTER);
		this.add(firstPanel, BorderLayout.CENTER);
		this.add(wpPanel, BorderLayout.CENTER);
		
		//�˵���Ӧ�¼�
		MenuClick menuClick = new MenuClick();
		for(int i = 0; i < 3; i++){
			fileItem[i].addActionListener(menuClick);
			scoreItem[i].addActionListener(menuClick);
			surfaceItem[i].addActionListener(menuClick);
		}
		studentNumber.addActionListener(menuClick);
		// show the window
    	this.setLocationRelativeTo(null);
		this.setVisible(true);
		

	}
	//�����˵�
	public void createMenu(){
		menuBar = new JMenuBar();
		//file
		file = new JMenu("�ļ�");
		fileItem = new JMenuItem[3];
		fileItem[0] = new JMenuItem("���ļ�����");
		fileItem[1] = new JMenuItem("�������ļ�");
		fileItem[2] = new JMenuItem("�˳�ϵͳ");
		for(int i = 0; i < 3; i++){
			file.add(fileItem[i]);
			if(i == 1){
				file.addSeparator();
			}
		}
		//statistic
		statistic = new JMenu("ͳ��");
		score = new JMenu("���Ƴɼ�");
		scoreItem = new JMenuItem[3];
		scoreItem[0] = new JMenuItem("���ſγ�ƽ����");
		scoreItem[1] = new JMenuItem("���ſγ���߷�");
		scoreItem[2] = new JMenuItem("���ſγ���ͷ�");		
		for(int i = 0; i < 3; i++){
			score.add(scoreItem[i]);
		}
		studentNumber = new JMenuItem("ѧ��������");
		statistic.add(score);
		statistic.addSeparator();
		statistic.add(studentNumber);
		
		//surface
		surface = new JMenu("ϵͳ���");
		surfaceItem = new JRadioButtonMenuItem[3];
		surfaceItem[0] = new JRadioButtonMenuItem("Metal���");
		surfaceItem[1] = new JRadioButtonMenuItem("Motif���");
		surfaceItem[2] = new JRadioButtonMenuItem("Windows���");
		ButtonGroup group = new ButtonGroup();
		for(int i = 0; i < 3; i++){
			group.add(surfaceItem[i]);
			surface.add(surfaceItem[i]);
		}
		
		menuBar.add(file);
		menuBar.add(statistic);
		menuBar.add(surface);
		setJMenuBar(menuBar);		
	}
    //���������                                                            	
	public void buildNavigatorPanel() {
		// create a panel for navigator labels
		navigatorPanel = new JPanel();
		// ���ñ߿�����������Լ��������ұ߾�
		Border insideBorder = BorderFactory.createEmptyBorder(20, 0, 0, 0);
		Border outsideBorder = BorderFactory.createLoweredBevelBorder();
		navigatorPanel.setBorder(BorderFactory.createCompoundBorder(
				outsideBorder, insideBorder));

		// set the size for the navigator panel
		navigatorPanel.setPreferredSize(new Dimension(100, 500));

		// create label objects for navigator
		firstLabel = new JLabel("�����¼", SwingConstants.CENTER);
		firstLabel.setPreferredSize(new Dimension(100, 30));
		firstLabel.setCursor(new Cursor(Cursor.HAND_CURSOR)); // �������ʱ��ʾ����ͼ��
		firstP = new JPanel();
		firstP.setPreferredSize(new Dimension(95, 40));
		firstP.add(firstLabel);
		//Ϊ�����ǩ����һ����屳��������ʼ��������ɫ����ѡ�У�
//		firstP.setBackground(new Color(200,200,200));

		secondLabel = new JLabel("��ѯ��¼", SwingConstants.CENTER);
		secondLabel.setPreferredSize(new Dimension(100, 30));
		secondLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		secondP = new JPanel();
		secondP.setPreferredSize(new Dimension(95, 40));
		secondP.add(secondLabel);
		//��õ�ǰ����ɫ����δ��ѡ��ʱ��
		r = navigatorPanel.getBackground().getRed();
		g = navigatorPanel.getBackground().getGreen();
		b = navigatorPanel.getBackground().getBlue();
		
		thirdLabel = new JLabel("��������", SwingConstants.CENTER);
		thirdLabel.setPreferredSize(new Dimension(100, 30));
		thirdLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		thirdP = new JPanel();
		thirdP.setPreferredSize(new Dimension(95, 40));
		thirdP.add(thirdLabel);
		
		// add the labels to the panel
		navigatorPanel.add(firstP);
		navigatorPanel.add(secondP);
		navigatorPanel.add(thirdP);

		// register action listener for the navigator labels
		firstLabel.addMouseListener(new LabelClick());
		secondLabel.addMouseListener(new LabelClick());
		thirdLabel.addMouseListener(new LabelClick());
	}
	//������
	public void buildOtherPanels() {
		// ���洴�����������ǽ�����ʾ��ͬһ�����򣬵�ͬһʱ��ֻ��һ���ɼ�
		secondPanel = new SecondPanel2();
		secondPanel.setVisible(false);
		
		firstPanel = new FirstPanel();
		firstPanel.setVisible(false);
		
		thirdPanel = new ThirdPanel2();
		thirdPanel.setVisible(false);
		
		wpPanel = new WelcomePage();
		wpPanel.setVisible(true);
		
	}
	//����������л�ҳ��
	public class LabelClick extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getSource() == firstLabel) {
				firstP.setOpaque(true);
				firstP.setBackground(new Color(200,200,200));
				//��δ��ѡ�еı����ǩ��������Ϊԭ���ĵ������ı���ɫ
				secondP.setBackground(new Color(r, g, b));
				thirdP.setBackground(new Color(r, g, b));
				secondP.setOpaque(false);
				thirdP.setOpaque(false);
				// ��Ҫ��ʾ��������ָ������
				if (!firstPanel.isVisible()) {
					MainFrame.this.add(firstPanel, BorderLayout.CENTER);
					firstPanel.setVisible(true);
				}
				// ԭ����ʾ���������Ϊ���ɼ�
				secondPanel.setVisible(false);
				thirdPanel.setVisible(false);
				wpPanel.setVisible(false);
			}
			if(e.getSource() == thirdLabel){
				thirdP.setOpaque(true);
				thirdP.setBackground(new Color(200,200,200));
				firstP.setBackground(new Color(r, g, b));
				secondP.setBackground(new Color(r, g, b));
				secondP.setOpaque(false);
				firstP.setOpaque(false);
				if (!thirdPanel.isVisible()) {
					MainFrame.this.add(thirdPanel, BorderLayout.CENTER);
					thirdPanel.setVisible(true);
				}
				firstPanel.setVisible(false);
				secondPanel.setVisible(false);
				wpPanel.setVisible(false);
			}
			if (e.getSource() == secondLabel) {
				secondP.setOpaque(true);
				secondP.setBackground(new Color(200,200,200));
				firstP.setBackground(new Color(r, g, b));
				thirdP.setBackground(new Color(r, g, b));
				firstP.setOpaque(false);
				thirdP.setOpaque(false);
				if (!secondPanel.isVisible()) {
					MainFrame.this.add(secondPanel, BorderLayout.CENTER);
					secondPanel.setVisible(true);
				}
				firstPanel.setVisible(false);
				thirdPanel.setVisible(false);
				wpPanel.setVisible(false);
			}
		}
	}
	
	//�˵���Ӧ�¼�ʵ��
	public class MenuClick implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			String look = "javax.swing.plaf.metal.MetalLookAndFeel";
			String sql;
			ResultSet rs;
			DecimalFormat df = new DecimalFormat("0.00");
			//�ļ��˵�
			JFileChooser fileChooser = new JFileChooser();
			if(e.getSource() == fileItem[0]){
				//���ļ�����(���ݶ�ȡ�ֽڵ�ռ�Ƚ��н������ı仯)
				int status = fileChooser.showOpenDialog(MainFrame.this);
				if(status == JFileChooser.APPROVE_OPTION){
				path = fileChooser.getSelectedFile().getPath();
				//��ʾ���������ڼ�������ȡ����
				pf = new ProgressFrame();
				isRun = true;
				importFile = new Thread(new ImportFile());
				importFile.start();
				}
			}
			else if(e.getSource() == fileItem[1]){
				//�������ļ�����ȡ���ݿ�д���ļ���
				int status = fileChooser.showSaveDialog(MainFrame.this);
				if(status == JFileChooser.APPROVE_OPTION){
				File file = fileChooser.getSelectedFile();
				path = fileChooser.getSelectedFile().getPath();
		        try {
					PrintWriter outfile = new PrintWriter(file);
					sql = "select * from studentinfo";
					rs = db.query(sql);
					while(rs.next()){
						for(int i = 1; i < 7; i++){
							outfile.print(rs.getString(i) + "\t");
						}
						outfile.println();
					}
					JOptionPane.showMessageDialog(MainFrame.this, "������ɣ�");
					outfile.close();
				} catch (FileNotFoundException | SQLException e2) {
					e2.printStackTrace();
				}
				}
			}
			else if(e.getSource() == fileItem[2]){
				setVisible(false);
				new Login();
			}
			//ͳ��
			else if(e.getSource() == scoreItem[0]){
				sql = "select avg(chinese) as ����, avg(math) as ��ѧ,"
						+ " avg(english) as Ӣ�� from studentinfo";
				String[] colNames = new String[3];
				String[] averageScores = new String[3];
				rs = db.query(sql);
				//��ȡƽ������
				try {
					while(rs.next()){
						averageScores[0] = rs.getString(1);
						averageScores[1] = rs.getString(2);
						averageScores[2] = rs.getString(3);
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				JOptionPane.showMessageDialog(MainFrame.this,
						"����" + ": " + df.format(Double.valueOf(averageScores[0])) + "\n"
					  + "��ѧ" + ": " + df.format(Double.valueOf(averageScores[1])) + "\n"
					  + "Ӣ��" + ": " + df.format(Double.valueOf(averageScores[2])) + "\n"
					  , "����ƽ����", JOptionPane.INFORMATION_MESSAGE);
			}
			else if(e.getSource() == scoreItem[1]){
				String[] averageScores = new String[3];
				sql = " select max(chinese) as ����, max(math) as ��ѧ, "
						+ "max(english) as Ӣ�� from studentinfo";
				rs = db.query(sql);
				//��ȡ�����
				try {
					while(rs.next()){
						averageScores[0] = rs.getString(1);
						averageScores[1] = rs.getString(2);
						averageScores[2] = rs.getString(3);
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				JOptionPane.showMessageDialog(MainFrame.this,
						"����" + ": " + df.format(Double.valueOf(averageScores[0])) + "\n"
					  + "��ѧ" + ": " + df.format(Double.valueOf(averageScores[1])) + "\n"
					  + "Ӣ��" + ": " + df.format(Double.valueOf(averageScores[2])) + "\n"
					  , "������߷�", JOptionPane.INFORMATION_MESSAGE);
			}
			else if(e.getSource() == scoreItem[2]){
				String[] averageScores = new String[3];
				sql = " select min(chinese) as ����, min(math) as ��ѧ, "
						+ "min(english) as Ӣ�� from studentinfo";
				rs = db.query(sql);
				//��ȡ�����
				try {
					while(rs.next()){
						averageScores[0] = rs.getString(1);
						averageScores[1] = rs.getString(2);
						averageScores[2] = rs.getString(3);
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				JOptionPane.showMessageDialog(MainFrame.this,
						"����" + ": " + df.format(Double.valueOf(averageScores[0])) + "\n"
					  + "��ѧ" + ": " + df.format(Double.valueOf(averageScores[1])) + "\n"
					  + "Ӣ��" + ": " + df.format(Double.valueOf(averageScores[2])) + "\n"
					  , "������ͷ�", JOptionPane.INFORMATION_MESSAGE);
			}
			else if(e.getSource() == studentNumber){
				String count = null;
				sql = " select count(*) from studentinfo";
				rs = db.query(sql);
				try {
					while(rs.next()){
						count = rs.getString(1);
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				JOptionPane.showMessageDialog(MainFrame.this, "ѧ������" 
				+ ": " + count + "\n", "ѧ������", JOptionPane.INFORMATION_MESSAGE);
			}
			//���仯
			else if(e.getSource() == surfaceItem[0]){
				look = "javax.swing.plaf.metal.MetalLookAndFeel";
			}
			else if(e.getSource() == surfaceItem[1]){
				look = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
			}
			else if(e.getSource() == surfaceItem[2]){
				look = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
			}
			try {
				UIManager.setLookAndFeel(look);
				SwingUtilities.updateComponentTreeUI(MainFrame.this);
			} catch (ClassNotFoundException 
					| InstantiationException 
					| IllegalAccessException 
					| UnsupportedLookAndFeelException e1) {
				e1.printStackTrace();
			}
			}
			
		}
	
	//�������߳�
	class ImportFile implements Runnable{
		public void run() {
			String sql;
			try {
				//��ȡ�ļ����ݲ������ݿ�
				sql = "delete from studentinfo";
				int n = db.insertInDB(sql);//��ձ�������
				sql = "Insert into studentinfo values(";
				//�ļ�������
				inFile = new FileInputStream(path);
				inChannel = inFile.getChannel();
				totalSize= inChannel.size();
				String str = null;
				int nn = 0;
				while((readNum = inFile.read(buff)) != -1  && isRun){
					//��byte[]ת��Ϊstring
					str = new String(buff, "GB2312");
					if(nn < 3){
						sql += ("'" + str + "',");
						nn++;
					}
					else{
						if(nn != 5){
							sql += (str + ",");
							nn++;
						}
						else{
							sql += (str + ")");
							nn = 0;
							sql.replaceAll(" ", "");
							int num = db.insertInDB(sql);
							sql = "Insert into studentinfo values(";
						}
					}
					//��ȡ�Ѷ����ֽ���
					size += readNum;
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						break;
					} // ʹ�߳�����50����
					progress.setValue((size * 100 / (int)totalSize)); // �ı��������ֵ
				}
				size = 0;
				pf.setVisible(false);
				JOptionPane.showMessageDialog(MainFrame.this, "������ɣ�");
				inFile.close();
				isRun = false;//�����߳�
//				firstPanel.setVisible(true);
//				secondPanel.setVisible(false);
//				thirdPanel.setVisible(false);
//				wpPanel.setVisible(false);
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}			
		}
	}
	
	//������
	private class ProgressFrame extends JFrame{
		public ProgressFrame(){
	    	setSize(550,100);
	    	setTitle("�����ļ�");
			//������
			JPanel progressP = new JPanel();
	    	progress = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
	    	progress.setStringPainted(true);
	    	progress.setPreferredSize(new Dimension(500,20));
	    	progressP.add(progress);
	    	progressP.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
	    	this.add(progressP);
	    	this.setLocationRelativeTo(null);
			this.setVisible(true);
		}
	}
	public static void main(String[] args) {
//		new MainFrame();
	}

}
