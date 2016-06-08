package com.hqu.indoor_pos;

import java.io.IOException;  
import java.io.InputStream;  
import java.sql.Connection;  
import java.sql.DriverManager;  
import java.sql.ResultSet;  
import java.sql.SQLException;  
import java.sql.Statement;  
import java.util.Properties;  
  
public class DBUtil { 
	
	 /*数据库连接地址*/
    private static String url; 
    
    /*用户名*/
    private static String username;
    
    /*密码*/
    private static String password; 
    
    /*数据库驱动*/
    private static String driver;
    
    /*加载类时，先将jdbc连接数据库信息获取并赋值*/
    static {  
    	
        Properties prop = new Properties();  
        InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("jdbc.properties");  
        
        try {  
            prop.load(in);  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        
        url = prop.getProperty("jdbc.url");  
        username = prop.getProperty("jdbc.username");  
        password = prop.getProperty("jdbc.password");  
        driver = prop.getProperty("jdbc.driverClassName");  
    }  
    
    static{  
        try {  
            Class.forName(driver);  
        } catch (ClassNotFoundException e) {  
            e.printStackTrace();  
        }  
    }  
    
    /**
     * <p>
     * 获取连接
     * </p>
     * 
     * @return Connection 返回Connection连接对象
     */
    public static Connection getConnection(){  
        Connection conn=null;  
        try {  
            conn = DriverManager.getConnection(url,username,password);  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
        return conn;  
    }  
    
    /**
     * <p>
     * 关闭结果集
     * </p>
     */
    public static  void close(ResultSet rs){  
        if(rs!=null){  
            try {  
                rs.close();  
            } catch (SQLException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
    
    /**
     * <p>
     * 关闭封装sql命令的对象
     * </p>
     */
    public static  void close(Statement state){  
        if(state!=null){  
            try {  
                state.close();  
            } catch (SQLException e) {  
                e.printStackTrace();  
            }  
        }  
    }  

    /**
     * <p>
     * 关闭连接
     * </p>
     */
    public static  void close(Connection conn){  
        if(conn!=null){  
            try {  
                conn.close();  
            } catch (SQLException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
}  