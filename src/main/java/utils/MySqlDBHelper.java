package utils;

import entity.Column;
import entity.ConConfig;
import entity.Table;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description: 对应sqlserver底层操作类 包括得到所有表及字段、得到与数据库连接
 * @Author chenjun
 * @Create 2018-12-12 18:50
 */
public class MySqlDBHelper {

    public final static String EN = " ";
    public final static String DS = ",";
    public final static String LS = "(";
    public final static String RS = ")";
    public final static String LM = "`";
    public final static String RM = "`";
    public final static String NL = "\r\n";

    /**
     * 数据库 字符编码（GBK）
     */
    public final static String dbGBK = " DEFAULT CHARACTER SET gbk COLLATE gbk_chinese_ci;";

    /**
     * 数据库 字符编码(UTF8)
     */
    public final static String dbUTF8 = " DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;";

    /**
     * 数据库表 字符编码(UTF8)
     */
    public final static String tableUtf8 = " ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ";

    /**
     * 数据库表 行格式
     */
    public final static String tableRowFormat = " ROW_FORMAT = Dynamic";

    /**
     * 列集全的表名
     */
    public final static String ALL_COLUMNS = "COLUMN_MATE";

    /**
     * 表集全的表名
     */
    public final static String ALL_TABLES = "TABLE_MATE";

    /** -----------------------以下是生成数据库基本配置---------------------  **/

    /**
     * 初始化数据库
     *
     * @param url       数据库路径
     * @param user      用户名
     * @param pwd       密码
     * @param dbName    数据库名
     * @param character 字符集
     */
    public static void initDB(String url, String user, String pwd, String dbName, String character) {
        String sql = null;
        Connection conn = null;
        try {
            //创建数据库
            ConConfig conConfig = new ConConfig(url, user, pwd, dbName);
            //创建数据库
            createDataBase(conConfig, character);

            //设置数据库(转化为小写)
            conConfig.setDbName(dbName.toLowerCase());
            conn = ConnectionHelper.getCon(conConfig);

            //如果没有则执行创建数据库表（ALL_COLUMNS）
            sql = "DROP TABLE IF EXISTS " + ALL_COLUMNS + ";";
            ConnectionHelper.execSql(sql, conn);

            sql = "CREATE TABLE " + ALL_COLUMNS + "(ID int AUTO_INCREMENT not null primary key, TABLE_NAME_INFO varchar(200), FILED_NAME varchar(200),";
            sql += " FILED_NAME_CHINESE varchar(200), DATA_TYPE varchar(30), FILED_LENGTH varchar(30), DEFAULT_VALUE varchar(200),";
            sql += " FOREIGN_KEY varchar(200), DESCRIPTION varchar(255))";
            sql += tableUtf8 + "COMMENT = '动态创建的所有数据库表字段'" + tableRowFormat;
            ConnectionHelper.execSql(sql, conn);

            //如果没有则执行创建数据库表（ALL_TABLES）
            sql = "DROP TABLE IF EXISTS " + ALL_TABLES + ";";
            ConnectionHelper.execSql(sql, conn);

            sql = "CREATE TABLE " + ALL_TABLES + "(ID int AUTO_INCREMENT not null primary key, PACK_NAME varchar(400),";
            sql += " TABLE_NAME_INFO varchar(200), TABLE_NAME_CHINESE varchar(200), DESCRIPTION varchar(255))";
            sql += tableUtf8 + "COMMENT = '动态创建的所有数据库表'" + tableRowFormat;
            ConnectionHelper.execSql(sql, conn);

        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            ConnectionHelper.closeCon(conn);
        }
    }

    /**
     * 得到数据库中的表,不包括列
     *
     * @param config 配置
     * @return
     * @throws SQLException
     */
    public static Map<String, Table> getAllTables(ConConfig config) throws SQLException {
        Map<String, Table> tables = new LinkedHashMap<>();
        String sql = "SELECT ID, PACK_NAME, TABLE_NAME_INFO ,TABLE_NAME_CHINESE ,DESCRIPTION FROM  " + ALL_TABLES;
        Connection conn = null;
        try {
            conn = ConnectionHelper.getCon(config);
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("ID");
                String packName = rs.getString("PACK_NAME");
                String tableName = rs.getString("TABLE_NAME_INFO");
                String tableName_ch = rs.getString("TABLE_NAME_CHINESE");
                String description = rs.getString("DESCRIPTION");
                // 从数据库读取出来的表名
                tables.put(tableName, new Table(id, packName, tableName, tableName_ch, description));
            }
        } finally {
            ConnectionHelper.closeCon(conn);
        }
        return tables;
    }

    /**
     * 得到某一张表
     *
     * @param config     配置
     * @param tableName  表名
     * @param readColumn 是否需要读取列信息
     */
    public static Table getTable(ConConfig config, String tableName, boolean readColumn) {
        Table table = null;
        String sql = "SELECT ID, PACK_NAME, TABLE_NAME_INFO ,TABLE_NAME_CHINESE ,DESCRIPTION FROM  " + ALL_TABLES + " where TABLE_NAME_INFO='" + tableName + "'";
        Connection conn = null;
        try {
            conn = ConnectionHelper.getCon(config);
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("ID");
                String packName = rs.getString("PACK_NAME");
                String tableName_ch = rs.getString("TABLE_NAME_CHINESE");
                String description = rs.getString("DESCRIPTION");
                table = new Table(id, packName, tableName, tableName_ch, description);
            }
            // 如果需要查列，则查出列集合
            if (table != null && readColumn) {
                table.setColumns(getColumnsByTable(config, tableName));
            }
        } catch (Exception err) {
            System.out.println("查询某一张表失败," + err.getMessage());
            err.printStackTrace();
        } finally {
            ConnectionHelper.closeCon(conn);
        }

        return table;
    }

    /**
     * 得到某表的所有字段
     *
     * @param config    配置
     * @param tableName 表名
     * @return
     */
    public static Map<String, Column> getColumnsByTable(ConConfig config, String tableName) {
        Map<String, Column> columns = new LinkedHashMap<>();
        String sql = "SELECT ID,TABLE_NAME_INFO,FILED_NAME,FILED_NAME_CHINESE,DATA_TYPE,FILED_LENGTH,DEFAULT_VALUE,FOREIGN_KEY,DESCRIPTION FROM " + ALL_COLUMNS + " WHERE TABLE_NAME_INFO='" + tableName + "'";
        Connection conn = null;
        try {
            conn = ConnectionHelper.getCon(config);
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Column c = new Column();
                c.setId(rs.getInt("ID"));
                // 表名
                c.setTableName(tableName);
                // 字段名
                c.setFiledName(rs.getString("FILED_NAME"));
                // 字段名（中文）
                c.setFiledNameChinese(rs.getString("FILED_NAME_CHINESE"));
                // 类型
                c.setFiledType(rs.getString("DATA_TYPE"));
                // 长度
                c.setFiledLength(rs.getInt("FILED_LENGTH"));
                // 默认值
                c.setDefaultValue(rs.getString("DEFAULT_VALUE"));
                // 外键
                c.setForeignKey(rs.getString("FOREIGN_KEY"));
                // 说明
                c.setDesc(rs.getString("DESCRIPTION"));

                columns.put(c.getFiledName(), c);
            }
        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            ConnectionHelper.closeCon(conn);
        }
        return columns;
    }

    /** -----------------------以下是生成脚本方法---------------------  **/

    /**
     * 创建数据库
     * 1:连接到master
     * 2:创建数据库
     *
     * @param config    配置
     * @param character 字符集
     */
    public static void createDataBase(ConConfig config, String character) {
        Connection conn = null;
        //记录数据库名
        String dbName = config.getDbName();
        try {
            config.setDbName(null);
            //创建数据库 dbName数据库中
            conn = ConnectionHelper.getCon(config);
            //判断是否有该数据库
            config.setDbName(dbName);
            boolean flagDB = getIsHasDataBase(config);
            //如果没有则执行创建数据库
            if (flagDB == false) {
                String sql = "CREATE DATABASE " + dbName;
                //设置字符编码
                if (StringUtils.isNotBlank(character)) {
                    sql += character;
                }
                ConnectionHelper.execSql(sql, conn);
            }
        } catch (Exception err) {
            System.out.println("创建数据库出错：" + err.getMessage());
            err.printStackTrace();
        } finally {
            ConnectionHelper.closeCon(conn);
        }
    }

    /**
     * 在数据库中创建一张表
     *
     * @param config         配置
     * @param table          表名
     * @param tableCharacter 字符集
     * @param execStatus     是否需要执行
     */
    public static String createTable(ConConfig config, Table table, String tableCharacter, String tableRowFormat, boolean execStatus) {
        //如果表为空，直接返回
        if (table == null) {
            return null;
        }

        Connection conn = null;
        try {
            //是否需要执行
            if (execStatus) {
                conn = ConnectionHelper.getCon(config);
            }
            StringBuffer sql = new StringBuffer();
            //判断是否存在该表
            sql.append("DROP TABLE IF EXISTS ");
            sql.append(LM).append(table.getTableName()).append(RM).append(";").append(NL);
            //是否需要执行
            if (execStatus) {
                //执行
                ConnectionHelper.execSql(sql.toString(), conn);
            }
            //重置sql语句
            sql.setLength(0);
            sql.append("CREATE TABLE ").append(table.getTableName()).append(LS);
            int index = 0;
            //主键
            StringBuffer primaryKey = new StringBuffer();
            //唯一索引
            StringBuffer uniqueIndex = new StringBuffer();
            for (String key : table.getColumns().keySet()) {
                Column c = table.getColumns().get(key);
                sql.append(getCommonColumnSql(c));
                //如果有主键
                if (c.isHasPrimaryKey()) {
                    primaryKey.append(c.getFiledName());
                    primaryKey.append(DS);
                }
                //如果唯一标识
                if (c.isHasIdentity()) {
                    uniqueIndex.append("UNIQUE INDEX").append(EN);
                    uniqueIndex.append("UNIQUE_KEY_").append(c.getFiledName()).append(EN);
                    uniqueIndex.append(LS).append(c.getFiledName()).append(RS);
                    uniqueIndex.append(EN).append("USING BTREE");
                    uniqueIndex.append(DS);
                }
                if (index < table.getColumns().size() - 1) {
                    // 加上,
                    sql.append(DS).append(NL);
                } else {
                    //如果有主键
                    if (primaryKey.length() > 0) {
                        sql.append(DS).append(NL).append("PRIMARY KEY").append(EN);
                        sql.append(LS).append(primaryKey.substring(0, primaryKey.length() - 1)).append(RS).append(" USING BTREE");
                    }
                    //如果有唯一索引
                    if (uniqueIndex.length() > 0) {
                        sql.append(DS).append(NL).append(uniqueIndex.substring(0, uniqueIndex.length() - 1));
                    }
                }
                index++;
                //是否需要执行
                if (execStatus) {
                    // 添加到ALL_COLUMNS表中
                    String inSql = "INSERT INTO " + ALL_COLUMNS + "(TABLE_NAME_INFO,FILED_NAME,FILED_NAME_CHINESE,DATA_TYPE,FILED_LENGTH,DEFAULT_VALUE,FOREIGN_KEY,DESCRIPTION)"
                            + " VALUES('" + c.getTableName() + "','" + c.getFiledName() + "','" + c.getFiledNameChinese() + "','"
                            + c.getFiledType() + "','" + c.getFiledLength() + "','" + c.getDefaultValue() + "','" + c.getForeignKey() + "','" + c.getDesc() + "')";
                    ConnectionHelper.execSql(inSql, conn);
                }
            }

            sql.append(RS).append(EN);

            //如果字符集不为空
            if (StringUtils.isNotBlank(tableCharacter)) {
                sql.append(tableCharacter);
            }
            sql.append("COMMENT = '" + table.getTableNameChinese() + "'").append(EN);
            //如果行格式不为空
            if (StringUtils.isNotBlank(tableRowFormat)) {
                sql.append(tableRowFormat);
            }
            //分号结尾
            sql.append(";");

            //是否需要执行
            if (execStatus) {
                // 执行sql
                ConnectionHelper.execSql(sql.toString(), conn);
            } else {
                return sql.toString();
            }

            // 添加到ALL_TABLES表中
            sql.setLength(0);
            sql.append("INSERT INTO " + ALL_TABLES + "(PACK_NAME, TABLE_NAME_INFO ,TABLE_NAME_CHINESE ,DESCRIPTION)" + " VALUES(");
            sql.append("'").append(table.getPackName());
            sql.append("','").append(table.getTableName());
            sql.append("','").append(table.getTableNameChinese());
            sql.append("','").append(table.getDescription()).append("')");
            ConnectionHelper.execSql(sql.toString(), conn);
        } catch (Exception err) {
            System.out.println("创建数据库表" + table.getTableName() + "出错：" + err.getMessage());
            err.printStackTrace();
        } finally {
            ConnectionHelper.closeCon(conn);
        }
        return null;
    }

    /**
     * 修改表信息
     *
     * @param config     配置
     * @param table      表信息
     * @param execStatus 是否需要执行
     * @return
     */
    public static String updateTable(ConConfig config, Table table, boolean execStatus) {
        if (table == null) {
            return null;
        }
        Connection conn = null;
        try {
            //是否需要执行
            if (execStatus) {
                conn = ConnectionHelper.getCon(config);
            }
            StringBuffer sql = new StringBuffer();
            sql.append(" UPDATE ").append(ALL_TABLES).append(" SET ");
            sql.append(" PACK_NAME='").append(table.getPackName());
            sql.append("' ,TABLE_NAME_INFO='").append(table.getTableName());
            sql.append("' ,TABLE_NAME_CHINESE='").append(table.getTableNameChinese());
            sql.append("' ,DESCRIPTION='").append(table.getDescription());
            sql.append("' WHERE ID=").append(table.getId());

            //是否需要执行
            if (execStatus) {
                // 执行sql
                ConnectionHelper.execSql(sql.toString(), conn);
            }

            return sql.toString();
        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            ConnectionHelper.closeCon(conn);
        }
        return null;
    }

    /**
     * 添加一个新字段
     *
     * @param config     配置
     * @param c          列
     * @param execStatus 是否需要执行
     */
    public static String createColumn(ConConfig config, Column c, boolean execStatus) {
        if (c == null) {
            return null;
        }
        Connection conn = null;
        try {
            //是否需要执行
            if (execStatus) {
                conn = ConnectionHelper.getCon(config);
            }
            StringBuffer sql = new StringBuffer();
            sql.append("ALTER TABLE ").append(c.getTableName()).append(" ADD ");
            sql.append(getCommonColumnSql(c));
            //是否需要执行
            if (execStatus) {
                ConnectionHelper.execSql(sql.toString(), conn);
            } else {
                return sql.toString();
            }

            String inSql = "INSERT INTO " + ALL_COLUMNS + "(TABLE_NAME_INFO,FILED_NAME,FILED_NAME_CHINESE,DATA_TYPE,FILED_LENGTH,DEFAULT_VALUE,FOREIGN_KEY,DESCRIPTION)"
                    + " VALUES('" + c.getTableName() + "','" + c.getFiledName() + "','" + c.getFiledNameChinese() + "','"
                    + c.getFiledType() + "','" + c.getFiledLength() + "','" + c.getDefaultValue() + "','" + c.getForeignKey() + "','" + c.getDesc() + "')";
            ConnectionHelper.execSql(inSql, conn);
        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            ConnectionHelper.closeCon(conn);
        }
        return null;
    }


    /**
     * 修改表信息
     *
     * @param config     配置信息
     * @param cDB        原列信息
     * @param cExcel     新列信息
     * @param execStatus 是否需要执行
     * @return
     * @throws Exception
     */
    public static String updateColumn(ConConfig config, Column cDB, Column cExcel, boolean execStatus) throws Exception {
        // 如果需要修改
        if (!cDB.equals(cExcel)) {
            Connection conn = null;
            try {
                //是否需要执行
                if (execStatus) {
                    conn = ConnectionHelper.getCon(config);
                }
                StringBuffer sql = new StringBuffer();
                sql.append("ALTER TABLE ").append(cExcel.getTableName()).append(" CHANGE COLUMN ");
                sql.append(LM).append(cExcel.getFiledName()).append(RM).append(EN);
                sql.append(getCommonColumnSql(cExcel));
                //是否需要执行
                if (execStatus) {
                    // 更新数据库
                    ConnectionHelper.execSql(sql.toString(), conn);
                } else {
                    return sql.toString();
                }

                String upSql = "UPDATE " + ALL_COLUMNS + " set FILED_NAME_CHINESE='" + cExcel.getFiledNameChinese() + "',DATA_TYPE='" + cExcel.getFiledType() + "',FOREIGN_KEY='"
                        + cExcel.getForeignKey() + "',DESCRIPTION='" + cExcel.getDesc() + "',FILED_LENGTH='" + cExcel.getFiledLength() + "',TABLE_NAME_INFO='"
                        + cExcel.getTableName() + "',FILED_NAME='" + cExcel.getFiledName()
                        + "',DEFAULT_VALUE='" + cExcel.getDefaultValue() + "' WHERE FILED_NAME='" + cExcel.getFiledName() + "'";
                // 修改ALL_COLUMNS
                ConnectionHelper.execSql(upSql, conn);
            } catch (Exception err) {
                err.printStackTrace();
            } finally {
                ConnectionHelper.closeCon(conn);
            }
        }
        return null;
    }

    /**
     * 删除一个字段
     *
     * @param config     配置信息
     * @param c          列
     * @param execStatus 状态
     * @throws Exception
     */
    public static String dropColumn(ConConfig config, Column c, boolean execStatus) throws Exception {
        Connection conn = null;
        try {
            if (c == null) {
                return null;
            }
            //是否需要执行
            if (execStatus) {
                conn = ConnectionHelper.getCon(config);
            }
            StringBuffer sql = new StringBuffer();
            sql.append("ALTER TABLE ").append(c.getTableName()).append(" DROP COLUMN ").append(c.getFiledName());
            //是否需要执行
            if (execStatus) {
                // 更新数据库
                ConnectionHelper.execSql(sql.toString(), conn);
            } else {
                return sql.toString();
            }

            String inSql = "DELETE FROM " + ALL_COLUMNS + " WHERE FILED_NAME='" + c.getFiledName() + "'";
            ConnectionHelper.execSql(inSql, conn);
        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            ConnectionHelper.closeCon(conn);
        }
        return null;
    }

    /**
     * 处理列信息
     *
     * @param c 列信息
     * @return
     */
    public static String getCommonColumnSql(Column c) {
        if (c == null) {
            return "";
        }
        StringBuffer sql = new StringBuffer();
        // 字段名+类型
        sql.append(LM + c.getFiledName() + RM).append(EN).append(c.getFiledType());
        // 有长度
        if (c.isHasLength()) {
            // 有精度
            if (c.isHasPrecision()) {
                sql.append(LS).append(c.getFiledLength()).append(DS).append(c.getPrecision()).append(RS);
            } else {
                // 无精度
                sql.append(LS).append(c.getFiledLength()).append(RS);
            }
        }
        // 是否为空
        if (c.isHasCanNull()) {
            sql.append(EN).append("NULL");
        } else {
            sql.append(EN).append("NOT NULL");
        }
        //默认值
        if (c.isHasPrimaryKey() == false && StringUtils.isNotBlank(c.getDefaultValue())) {
            if (StringUtils.equals(c.getDefaultValue(), "null")) {
                sql.append(EN).append("DEFAULT ").append(c.getDefaultValue().toUpperCase());
            } else {
                sql.append(EN).append("DEFAULT ").append("'").append(c.getDefaultValue()).append("'");
            }
        }
        //备注
        sql.append(EN).append("COMMENT '" + c.getDesc() + "'");
        return sql.toString();
    }


    /**
     * 判断是否有该数据库
     *
     * @param config 配置
     * @return
     */
    public static boolean getIsHasDataBase(ConConfig config) {
        //如果数据库名为空
        if (StringUtils.isBlank(config.getDbName())) {
            return false;
        }
        Connection conn = null;
        String dbName = config.getDbName();
        try {
            config.setDbName(null);
            conn = ConnectionHelper.getCon(config);
            //sql语句
            String sql = "SHOW DATABASES";
            //预加载
            PreparedStatement ps = conn.prepareStatement(sql);
            //结果集
            ResultSet rs = ps.executeQuery();
            //如果结果集有值
            while (rs.next()) {
                //取出数据库名
                String dataBaseName = rs.getString("Database");
                //如果相等
                if (StringUtils.equals(dataBaseName.toLowerCase(), dbName.toLowerCase())) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("查询数据库出错：" + e.getMessage());
            e.printStackTrace();
        } finally {
            ConnectionHelper.closeCon(conn);
        }

        return false;
    }

    /** -----------------------以下是生成脚本文件方法---------------------  **/

    /**
     * 获取创建表的sql语句
     *
     * @param excelPath excel文件路径
     * @param sqlPath   sql文件路径
     * @param allTables 所有表信息
     * @return
     */
    public static void getCreateTableSql(String excelPath, String sqlPath, Map<String, Table> allTables) {
        if (StringUtils.isBlank(excelPath)) {
            return;
        }
        if (StringUtils.isBlank(sqlPath)) {
            return;
        }
        //如果所有表为空
        if (allTables == null) {
            return;
        }
        //文件输出
        FileWriter writer = null;
        try {
            writer = new FileWriter(new File(sqlPath));
            //循环读取
            Iterator<String> keyStr = allTables.keySet().iterator();
            ConConfig config = new ConConfig();
            //如果有值
            while (keyStr.hasNext()) {
                //取出key
                String key = keyStr.next();
                //取出table对象
                Table table = allTables.get(key);
                if (table == null) {
                    continue;
                }
                StringBuffer createTableSql = new StringBuffer();
                createTableSql.append("/* " + " 添加表【" + table.getTableName() + "】 陈俊  */").append(NL);
                createTableSql.append("DROP TABLE IF EXISTS ");
                createTableSql.append(LM).append(table.getTableName()).append(RM).append(";").append(NL);
                //创建表对象
                String createSql = createTable(config, table, tableUtf8, tableRowFormat, false);
                createTableSql.append(createSql).append(NL);
                createTableSql.append(NL);
                writer.write(createTableSql.toString());
            }
        } catch (Exception e) {
            System.out.println("获取创建表sql异常！");
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    System.out.println("关闭失败！");
                    e.printStackTrace();
                }
            }
        }
    }

}
