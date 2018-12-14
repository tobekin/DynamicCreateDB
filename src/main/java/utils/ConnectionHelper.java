package utils;

import entity.ConConfig;

import java.sql.*;

/**
 * @Description: 连接帮助
 * @Author chenjun
 * @Create 2018-12-12 18:26
 */
public class ConnectionHelper {

    static {
        try {
            //mysql
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 得到某个数据库的连接
     *
     * @param url  不包含databaseName的url
     * @param user 数据库用户
     * @param pwd  密码
     */
    public static Connection getCon(String url, String user, String pwd) throws SQLException {
        Connection con = DriverManager.getConnection(url, user, pwd);
        return con;
    }

    /**
     * 得到某个数据库的连接
     *
     * @param url    不包含databaseName的url
     * @param user   数据库用户
     * @param pwd    密码
     * @param dbName 数据库名
     */
    public static Connection getCon(String url, String user, String pwd, String dbName) throws SQLException {
        Connection con = DriverManager.getConnection(url + "/" + dbName, user, pwd);
        return con;
    }

    /**
     * 得到数据库的连接,不一定要指定数据库
     *
     * @param config 配置
     */
    public static Connection getCon(ConConfig config) throws SQLException {
        Connection con = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPwd());
        return con;
    }

    /**
     * 测试是否能连上数据库服务器
     */
    public static boolean testConnection(ConConfig config) {
        Connection connection = null;
        try {
            connection = getCon(config);
            return true;
        } catch (Exception err) {
            System.out.println("连上数据库服务器错误：" + err.getMessage());
            closeCon(connection);
        }
        return false;
    }

    /**
     * 关闭连接
     */
    public static void closeCon(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 执行sql
     */
    public static boolean execSql(String sql, Connection conn) throws Exception {
        PreparedStatement ps = conn.prepareStatement(sql);
        return ps.execute();
    }

    /**
     * 批量执行执行sql
     */
    public static boolean batchExecSql(String sql, Connection conn) throws Exception {
        Statement statement = conn.createStatement();
        statement.addBatch(sql);
        return statement.execute(sql);
    }
}
