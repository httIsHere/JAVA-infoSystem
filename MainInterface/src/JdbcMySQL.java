import java.sql.*;

public class JdbcMySQL {
	private String connector = "com.mysql.jdbc.Driver";
	private String url = "jdbc:mysql://127.0.0.1:3306/student";
	private String user  = "root";
	private String pwd = "";
	
	private Connection con;
	private Statement sta;
	private ResultSet res;
	
	public JdbcMySQL(){
		try {
			// Add mysql driver
			Class.forName(connector);
			
			// Connect to mysql
			con = DriverManager.getConnection(url, user, pwd);
			
			// Create statement
			// 滚动结果集以及结果集更新数据库
			sta = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, 
									  ResultSet.CONCUR_UPDATABLE);
			
			
		} catch (ClassNotFoundException e) {
			System.out.println("加载驱动失败！");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("连接数据库失败！");
			e.printStackTrace();
		}
	}
	//查询学生信息
	public ResultSet query(String sql){
		try {
			res = sta.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	//插入信息
	public int insertInDB(String sql){
		int num = 0;
		try {
			num = sta.executeUpdate(sql);
			System.out.println("成功更新" + num + "条记录");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return num;
	}
	
	//关闭数据库
	public void closeConnection(){
		if(con != null){
			try {
				con.close();
				if(sta != null){
					sta.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
	}
	public static void main(String[] args) {
		JdbcMySQL db = new JdbcMySQL();
		String sql = "select * from student";
		db.query(sql);
		sql = "Insert into studentinfo values('11223313', 'mmm', 'female', 78, 95, 86)";
		//db.insertInDB(sql);
	}

}
