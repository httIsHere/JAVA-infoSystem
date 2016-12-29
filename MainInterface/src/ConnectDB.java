import java.sql.*;

public class ConnectDB {
	private String connector = "com.mysql.jdbc.Driver";
	private String url = "jdbc:mysql://127.0.0.1:3306/student";
	private String user  = "root";
	private String pwd = "";
	
	private Connection con;
	private Statement sta;
	private ResultSet res;
	
	public ConnectDB(){
		try {
			// Add mysql driver
			Class.forName(connector);
			
			// Connect to mysql
			con = DriverManager.getConnection(url, user, pwd);
			
			// Create statement
			sta = con.createStatement();
			
			
		} catch (ClassNotFoundException e) {
			System.out.println("��������ʧ�ܣ�");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("�������ݿ�ʧ�ܣ�");
			e.printStackTrace();
		}
	}
	
	public ResultSet query(String sql){
		try {
			res = sta.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}
	
}
