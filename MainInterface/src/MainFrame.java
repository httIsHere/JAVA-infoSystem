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
	
	//菜单栏组件
	private JMenuBar menuBar;
	private JMenu file;
	private JMenuItem[] fileItem;
	private JMenu statistic;
	private JMenu score;
	private JMenuItem[] scoreItem;
	private JMenuItem studentNumber;
	private JMenu surface;
	private JRadioButtonMenuItem[] surfaceItem;
	//导航栏组件
	private JLabel firstLabel, secondLabel, thirdLabel;
	private JPanel firstP, secondP, thirdP;
	private int r, g, b;
	
	//文件操作时
	private byte[] buff = new byte[8]; // 创建字节数组，临时存放读取到的数据
	private int readNum; // 读到的字节数
	private int size = 0; // 已操作d的字节数
	private long totalSize = 0;//文件大小
	private Thread importFile;
	private boolean isRun = false;
	private JProgressBar progress;
	private String path;
	private FileInputStream inFile;
	private FileChannel inChannel;
	private ProgressFrame pf;
	
	public MainFrame() {
		// set title
		this.setTitle("班级信息管理");

		// set size for the form
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

		// set layout for the frame
		this.setLayout(new BorderLayout(10, 10));

		// 关闭方式
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//菜单设计
		createMenu();
		
		// 导航栏
		buildNavigatorPanel();
		
		
		// 把要显示的面板对象都建立起来
		buildOtherPanels();

		// add the created panels to the frame
		this.add(navigatorPanel, BorderLayout.WEST);
		
		// 把要显示的面板对象到放到相同区域，在窗体中显示的为最后放入的面板。
		// 这样做的目的是把对象都和该窗体关联起来。只有关联起来，之后应用外观风格（例如Motif）才会作用于这两个面板对象。
		this.add(thirdPanel, BorderLayout.CENTER);
		this.add(secondPanel, BorderLayout.CENTER);
		this.add(firstPanel, BorderLayout.CENTER);
		this.add(wpPanel, BorderLayout.CENTER);
		
		//菜单响应事件
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
	//创建菜单
	public void createMenu(){
		menuBar = new JMenuBar();
		//file
		file = new JMenu("文件");
		fileItem = new JMenuItem[3];
		fileItem[0] = new JMenuItem("从文件导入");
		fileItem[1] = new JMenuItem("导出到文件");
		fileItem[2] = new JMenuItem("退出系统");
		for(int i = 0; i < 3; i++){
			file.add(fileItem[i]);
			if(i == 1){
				file.addSeparator();
			}
		}
		//statistic
		statistic = new JMenu("统计");
		score = new JMenu("各科成绩");
		scoreItem = new JMenuItem[3];
		scoreItem[0] = new JMenuItem("各门课程平均分");
		scoreItem[1] = new JMenuItem("各门课程最高分");
		scoreItem[2] = new JMenuItem("各门课程最低分");		
		for(int i = 0; i < 3; i++){
			score.add(scoreItem[i]);
		}
		studentNumber = new JMenuItem("学生总人数");
		statistic.add(score);
		statistic.addSeparator();
		statistic.add(studentNumber);
		
		//surface
		surface = new JMenu("系统外观");
		surfaceItem = new JRadioButtonMenuItem[3];
		surfaceItem[0] = new JRadioButtonMenuItem("Metal风格");
		surfaceItem[1] = new JRadioButtonMenuItem("Motif风格");
		surfaceItem[2] = new JRadioButtonMenuItem("Windows风格");
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
    //导航栏设计                                                            	
	public void buildNavigatorPanel() {
		// create a panel for navigator labels
		navigatorPanel = new JPanel();
		// 设置边框来控制外观以及上下左右边距
		Border insideBorder = BorderFactory.createEmptyBorder(20, 0, 0, 0);
		Border outsideBorder = BorderFactory.createLoweredBevelBorder();
		navigatorPanel.setBorder(BorderFactory.createCompoundBorder(
				outsideBorder, insideBorder));

		// set the size for the navigator panel
		navigatorPanel.setPreferredSize(new Dimension(100, 500));

		// create label objects for navigator
		firstLabel = new JLabel("处理记录", SwingConstants.CENTER);
		firstLabel.setPreferredSize(new Dimension(100, 30));
		firstLabel.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 鼠标移上时显示手型图标
		firstP = new JPanel();
		firstP.setPreferredSize(new Dimension(95, 40));
		firstP.add(firstLabel);
		//为标题标签设置一个面板背景，并初始化背景颜色（被选中）
//		firstP.setBackground(new Color(200,200,200));

		secondLabel = new JLabel("查询记录", SwingConstants.CENTER);
		secondLabel.setPreferredSize(new Dimension(100, 30));
		secondLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		secondP = new JPanel();
		secondP.setPreferredSize(new Dimension(95, 40));
		secondP.add(secondLabel);
		//获得当前背景色（在未被选中时）
		r = navigatorPanel.getBackground().getRed();
		g = navigatorPanel.getBackground().getGreen();
		b = navigatorPanel.getBackground().getBlue();
		
		thirdLabel = new JLabel("重置密码", SwingConstants.CENTER);
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
	//面板对象
	public void buildOtherPanels() {
		// 下面创建面板对象，它们将会显示在同一个区域，但同一时刻只有一个可见
		secondPanel = new SecondPanel2();
		secondPanel.setVisible(false);
		
		firstPanel = new FirstPanel();
		firstPanel.setVisible(false);
		
		thirdPanel = new ThirdPanel2();
		thirdPanel.setVisible(false);
		
		wpPanel = new WelcomePage();
		wpPanel.setVisible(true);
		
	}
	//点击导航，切换页面
	public class LabelClick extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getSource() == firstLabel) {
				firstP.setOpaque(true);
				firstP.setBackground(new Color(200,200,200));
				//将未被选中的标题标签背景设置为原来的导航栏的背景色
				secondP.setBackground(new Color(r, g, b));
				thirdP.setBackground(new Color(r, g, b));
				secondP.setOpaque(false);
				thirdP.setOpaque(false);
				// 把要显示的面板放入指定区域
				if (!firstPanel.isVisible()) {
					MainFrame.this.add(firstPanel, BorderLayout.CENTER);
					firstPanel.setVisible(true);
				}
				// 原先显示的面板设置为不可见
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
	
	//菜单响应事件实现
	public class MenuClick implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			String look = "javax.swing.plaf.metal.MetalLookAndFeel";
			String sql;
			ResultSet rs;
			DecimalFormat df = new DecimalFormat("0.00");
			//文件菜单
			JFileChooser fileChooser = new JFileChooser();
			if(e.getSource() == fileItem[0]){
				//从文件导入(根据读取字节的占比进行进度条的变化)
				int status = fileChooser.showOpenDialog(MainFrame.this);
				if(status == JFileChooser.APPROVE_OPTION){
				path = fileChooser.getSelectedFile().getPath();
				//显示进度条窗口及开启读取进程
				pf = new ProgressFrame();
				isRun = true;
				importFile = new Thread(new ImportFile());
				importFile.start();
				}
			}
			else if(e.getSource() == fileItem[1]){
				//导出到文件（读取数据库写入文件）
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
					JOptionPane.showMessageDialog(MainFrame.this, "导出完成！");
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
			//统计
			else if(e.getSource() == scoreItem[0]){
				sql = "select avg(chinese) as 语文, avg(math) as 数学,"
						+ " avg(english) as 英语 from studentinfo";
				String[] colNames = new String[3];
				String[] averageScores = new String[3];
				rs = db.query(sql);
				//获取平均分数
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
						"语文" + ": " + df.format(Double.valueOf(averageScores[0])) + "\n"
					  + "数学" + ": " + df.format(Double.valueOf(averageScores[1])) + "\n"
					  + "英语" + ": " + df.format(Double.valueOf(averageScores[2])) + "\n"
					  , "各科平均分", JOptionPane.INFORMATION_MESSAGE);
			}
			else if(e.getSource() == scoreItem[1]){
				String[] averageScores = new String[3];
				sql = " select max(chinese) as 语文, max(math) as 数学, "
						+ "max(english) as 英语 from studentinfo";
				rs = db.query(sql);
				//获取最高数
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
						"语文" + ": " + df.format(Double.valueOf(averageScores[0])) + "\n"
					  + "数学" + ": " + df.format(Double.valueOf(averageScores[1])) + "\n"
					  + "英语" + ": " + df.format(Double.valueOf(averageScores[2])) + "\n"
					  , "各科最高分", JOptionPane.INFORMATION_MESSAGE);
			}
			else if(e.getSource() == scoreItem[2]){
				String[] averageScores = new String[3];
				sql = " select min(chinese) as 语文, min(math) as 数学, "
						+ "min(english) as 英语 from studentinfo";
				rs = db.query(sql);
				//获取最低数
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
						"语文" + ": " + df.format(Double.valueOf(averageScores[0])) + "\n"
					  + "数学" + ": " + df.format(Double.valueOf(averageScores[1])) + "\n"
					  + "英语" + ": " + df.format(Double.valueOf(averageScores[2])) + "\n"
					  , "各科最低分", JOptionPane.INFORMATION_MESSAGE);
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
				JOptionPane.showMessageDialog(MainFrame.this, "学生人数" 
				+ ": " + count + "\n", "学生人数", JOptionPane.INFORMATION_MESSAGE);
			}
			//风格变化
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
	
	//进度条线程
	class ImportFile implements Runnable{
		public void run() {
			String sql;
			try {
				//读取文件内容插入数据库
				sql = "delete from studentinfo";
				int n = db.insertInDB(sql);//清空表内数据
				sql = "Insert into studentinfo values(";
				//文件读入流
				inFile = new FileInputStream(path);
				inChannel = inFile.getChannel();
				totalSize= inChannel.size();
				String str = null;
				int nn = 0;
				while((readNum = inFile.read(buff)) != -1  && isRun){
					//将byte[]转换为string
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
					//获取已读的字节数
					size += readNum;
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						break;
					} // 使线程休眠50毫秒
					progress.setValue((size * 100 / (int)totalSize)); // 改变进度条的值
				}
				size = 0;
				pf.setVisible(false);
				JOptionPane.showMessageDialog(MainFrame.this, "导入完成！");
				inFile.close();
				isRun = false;//结束线程
//				firstPanel.setVisible(true);
//				secondPanel.setVisible(false);
//				thirdPanel.setVisible(false);
//				wpPanel.setVisible(false);
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}			
		}
	}
	
	//进度条
	private class ProgressFrame extends JFrame{
		public ProgressFrame(){
	    	setSize(550,100);
	    	setTitle("导入文件");
			//进度条
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
