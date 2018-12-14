package entity;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description: 表实体类
 * @Author chenjun
 * @Create 2018-12-12 14:21
 */
public class Table implements Serializable {

    private static final long serialVersionUID = -794063236789134654L;

    /**
     * id
     */
    private int id;
    /**
     * 包名
     */
    private String packName;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 中文表名
     */
    private String tableNameChinese;
    /**
     * 备注
     */
    private String description;

    /**
     * 列信息
     */
    private Map<String, Column> columns = new LinkedHashMap<String, Column>();

    /**
     * 无参构造方法
     */
    public Table() {
    }

    /**
     * 构造方法
     *
     * @param name
     */
    public Table(String name) {
        this.tableName = name;
    }

    /**
     * 构造方法
     *
     * @param packName
     * @param tableName
     * @param tableNameChinese
     * @param description
     */
    public Table(String packName, String tableName, String tableNameChinese, String description) {
        super();
        this.packName = packName;
        this.tableName = tableName;
        this.tableNameChinese = tableNameChinese;
        this.description = description;
    }

    /**
     * 构造方法
     *
     * @param id
     * @param packName
     * @param tableName
     * @param tableNameChinese
     * @param description
     */
    public Table(int id, String packName, String tableName, String tableNameChinese, String description) {
        super();
        this.id = id;
        this.packName = packName;
        this.tableName = tableName;
        this.tableNameChinese = tableNameChinese;
        this.description = description;
    }

    /**
     * 增加列
     *
     * @param name
     * @param column
     */
    public void addColumn(String name, Column column) {
        this.columns.put(name, column);
    }

    /**
     * 取出列信息
     *
     * @return
     */
    public Map<String, Column> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, Column> columns) {
        this.columns = columns;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableNameChinese() {
        return tableNameChinese;
    }

    public void setTableNameChinese(String tableNameChinese) {
        this.tableNameChinese = tableNameChinese;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        if (!obj.getClass().toString().equals(this.getClass().toString())) {
            return false;
        }
        Table t = (Table) obj;
        if (t.getPackName().equals(this.getPackName()) && t.getTableName().equals(this.tableName) && t.getTableNameChinese().equals(this.getTableNameChinese())
                && t.getDescription().equals(this.description)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Table{");
        sb.append("id=").append(id);
        sb.append(", packName='").append(packName).append('\'');
        sb.append(", tableName='").append(tableName).append('\'');
        sb.append(", tableNameChinese='").append(tableNameChinese).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", columns=").append(columns);
        sb.append('}');
        return sb.toString();
    }
}
