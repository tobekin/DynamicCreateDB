import entity.Column;
import entity.ConConfig;
import entity.Table;
import org.junit.Test;
import utils.EntityBuilderHelper;
import utils.ExcelHelper;
import utils.MySqlDBHelper;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

/**
 * @Description: myql数据库表测试程序
 * @Author chenjun
 * @Create 2018-12-22 13:42
 */
public class MySqlDBTest {

    /**
     * 创建数据库表
     *
     * @throws Exception
     */
    @Test
    public void createTable() throws Exception {
        //动态创建表
        //读取当前文件路径
        String fileName = "test.xlsx";
        String filePath = MySqlDBHelper.class.getClassLoader().getResource(fileName).getPath();
        String sqlPath = filePath.replace("xlsx", "sql");

        //实例化一个文件对象
        File file = new File(filePath);
        //取出文件中所有的表
        Map<String, Table> allTables = ExcelHelper.getAllTables(file, true);

        //获取表sql数据
        MySqlDBHelper.getCreateTableSql(filePath, sqlPath, allTables);

        //构建所有表的实体类
        EntityBuilderHelper.builder(allTables);

        //初始化数据库
        MySqlDBHelper.initDB("jdbc:mysql://localhost", "root", "123456", "DYNAMIC_CREATE_DB_TEST".toLowerCase(), MySqlDBHelper.dbUTF8);
        ConConfig config = new ConConfig("jdbc:mysql://localhost", "root", "123456", "DYNAMIC_CREATE_DB_TEST".toLowerCase());

        Iterator<String> keyStr = allTables.keySet().iterator();
        //如果有值
        while (keyStr.hasNext()) {
            //取出key
            String key = keyStr.next();
            //取出table对象
            Table table = allTables.get(key);
            //创建表对象
            MySqlDBHelper.createTable(config, table, MySqlDBHelper.tableUtf8, MySqlDBHelper.tableRowFormat, true);
        }
    }

    /**
     * 取出数据库中数据库表的信息
     *
     * @throws Exception
     */
    @Test
    public void getTables() throws Exception {
        //初始化数据库
        ConConfig config = new ConConfig("jdbc:mysql://localhost", "root", "123456", "DYNAMIC_CREATE_DB_TEST".toLowerCase());

        Map<String, Table> allTablesFromDB = MySqlDBHelper.getAllTables(config);
        Iterator<String> keyStrFromDB = allTablesFromDB.keySet().iterator();
        //如果有值
        while (keyStrFromDB.hasNext()) {
            //取出key
            String key = keyStrFromDB.next();
            Table table = allTablesFromDB.get(key);
            System.out.println("数据库表" + table.getTableNameChinese() + "，内容为：" + table);

            //读取该数据库表的所有列信息
            Table tableAllColumns = MySqlDBHelper.getTable(config, table.getTableName(), true);
            System.out.println("数据库表的字段为：" + tableAllColumns.getColumns());
        }
    }

    /**
     * 更新数据库表信息
     *
     * @throws Exception
     */
    @Test
    public void updateTableInfo() throws Exception {
        //初始化数据库
        ConConfig config = new ConConfig("jdbc:mysql://localhost", "root", "123456", "DYNAMIC_CREATE_DB_TEST".toLowerCase());

        Table updateTable = new Table();
        updateTable.setId(1);
        updateTable.setPackName("com.test");
        updateTable.setTableName("SYS_TEST");
        updateTable.setTableNameChinese("测试表01");
        updateTable.setDescription("示例表01");
        MySqlDBHelper.updateTable(config, updateTable, true);
    }


    /**
     * 新增数据库表列信息
     *
     * @throws Exception
     */
    @Test
    public void addTableColumnInfo() throws Exception {
        //初始化数据库
        ConConfig config = new ConConfig("jdbc:mysql://localhost", "root", "123456", "DYNAMIC_CREATE_DB_TEST".toLowerCase());

        Column addColumn = new Column();
        addColumn.setTableName("SYS_TEST");
        addColumn.setFiledName("TEST_FILED");
        addColumn.setFiledNameChinese("测试字段");
        addColumn.setFiledType("varchar");
        addColumn.setFiledLength(64);
        addColumn.setHasPrimaryKey(false);
        addColumn.setHasPrecision(false);
        addColumn.setHasCanNull(true);
        addColumn.setHasIdentity(false);
        addColumn.setHasLength(true);
        addColumn.setDefaultValue("");
        addColumn.setForeignKey("");
        addColumn.setDesc("测试字段");
        MySqlDBHelper.createColumn(config, addColumn, true);
    }

    /**
     * 修改表字段信息
     *
     * @throws Exception
     */
    @Test
    public void updateTableColumnInfo() throws Exception {
        //初始化数据库
        ConConfig config = new ConConfig("jdbc:mysql://localhost", "root", "123456", "DYNAMIC_CREATE_DB_TEST".toLowerCase());

        Column addColumn = new Column();
        addColumn.setTableName("SYS_TEST");
        addColumn.setFiledName("TEST_FILED");
        addColumn.setFiledNameChinese("测试字段");
        addColumn.setFiledType("varchar");
        addColumn.setFiledLength(64);
        addColumn.setHasPrimaryKey(false);
        addColumn.setHasPrecision(false);
        addColumn.setHasCanNull(true);
        addColumn.setHasIdentity(false);
        addColumn.setHasLength(true);
        addColumn.setDefaultValue("");
        addColumn.setForeignKey("");
        addColumn.setDesc("测试字段");

        Column excelColumn = new Column();
        excelColumn.setTableName("SYS_TEST");
        excelColumn.setFiledName("TEST_FILED");
        excelColumn.setFiledNameChinese("测试字段");
        excelColumn.setFiledType("varchar");
        excelColumn.setFiledLength(64);
        excelColumn.setHasPrimaryKey(false);
        excelColumn.setHasPrecision(false);
        excelColumn.setHasCanNull(true);
        excelColumn.setHasIdentity(false);
        excelColumn.setHasLength(true);
        excelColumn.setDefaultValue("");
        excelColumn.setForeignKey("");
        excelColumn.setDesc("测试字段更新");
        MySqlDBHelper.updateColumn(config, addColumn, excelColumn, true);
    }

    /**
     * 删除一个字段
     *
     * @throws Exception
     */
    @Test
    public void delTableColumnInfo() throws Exception {
        //初始化数据库
        ConConfig config = new ConConfig("jdbc:mysql://localhost", "root", "123456", "DYNAMIC_CREATE_DB_TEST".toLowerCase());

        Column delColumn = new Column();
        delColumn.setTableName("SYS_TEST");
        delColumn.setFiledName("TEST_FILED");
        MySqlDBHelper.dropColumn(config, delColumn, true);
    }


}
