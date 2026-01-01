package util;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * 数据库工具类
 * 负责数据库连接的创建、管理和释放
 */
public class DBUtil {
    
    private static String driver;
    private static String url;
    private static String username;
    private static String password;
    
    // 使用ThreadLocal保证线程安全
    private static ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();
    
    // 静态代码块加载配置
    static {
        try {
            Properties props = new Properties();
            InputStream is = DBUtil.class.getClassLoader()
                    .getResourceAsStream("db.properties");
            
            if (is == null) {
                // 如果配置文件不存在，使用默认配置
                driver = "com.mysql.cj.jdbc.Driver";
                url = "jdbc:mysql://localhost:3306/student_management?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8";
                username = "root";
                password = "123456";
            } else {
                props.load(is);
                driver = props.getProperty("db.driver");
                url = props.getProperty("db.url");
                username = props.getProperty("db.username");
                password = props.getProperty("db.password");
                is.close();
            }
            
            // 加载驱动
            Class.forName(driver);
            System.out.println("数据库驱动加载成功！");
            
        } catch (Exception e) {
            System.err.println("数据库配置加载失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 获取数据库连接
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
    
    /**
     * 关闭连接
     */
    public static void closeConnection() {
        Connection conn = connectionHolder.get();
        if (conn != null) {
            try {
                conn.close();
                connectionHolder.remove();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 关闭资源
     */
    public static void close(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 关闭资源（不关闭连接）
     */
    public static void close(ResultSet rs, Statement stmt) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 测试数据库连接
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            boolean valid = conn != null && !conn.isClosed();
            closeConnection();
            return valid;
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * 开启事务
     */
    public static void beginTransaction() throws SQLException {
        getConnection().setAutoCommit(false);
    }
    
    /**
     * 提交事务
     */
    public static void commit() throws SQLException {
        Connection conn = connectionHolder.get();
        if (conn != null) {
            conn.commit();
            conn.setAutoCommit(true);
        }
    }
    
    /**
     * 回滚事务
     */
    public static void rollback() {
        try {
            Connection conn = connectionHolder.get();
            if (conn != null) {
                conn.rollback();
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
