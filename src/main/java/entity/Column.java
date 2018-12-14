package entity;

import java.io.Serializable;

/**
 * @Description: 列名实体类
 * @Author chenjun
 * @Create 2018-12-12 14:03
 */
public class Column implements Serializable {

    private static final long serialVersionUID = 2527561282125741787L;

    /**
     * id
     */
    private int id;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 字段名
     */
    private String filedName;
    /**
     * 字段名(中文)
     */
    private String filedNameChinese;
    /**
     * 类型
     */
    private String filedType;
    /**
     * 长度
     */
    private int filedLength;
    /**
     * 精度
     */
    private int precision;
    /**
     * 默认值
     */
    private String defaultValue;
    /**
     * 是否有长度
     */
    private boolean hasLength;
    /**
     * 是否有精度
     */
    private boolean hasPrecision;
    /**
     * 是否是主键
     */
    private boolean hasPrimaryKey;
    /**
     * 是否可以为空
     */
    private boolean hasCanNull;
    /**
     * 是否是标识
     */
    private boolean hasIdentity;
    /**
     * 字段说明
     */
    private String desc;
    /**
     * 外键
     */
    private String foreignKey;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getFiledName() {
        return filedName;
    }

    public void setFiledName(String filedName) {
        this.filedName = filedName;
    }

    public String getFiledNameChinese() {
        return filedNameChinese;
    }

    public void setFiledNameChinese(String filedNameChinese) {
        this.filedNameChinese = filedNameChinese;
    }

    public String getFiledType() {
        return filedType;
    }

    public void setFiledType(String filedType) {
        this.filedType = filedType;
    }

    public int getFiledLength() {
        return filedLength;
    }

    public void setFiledLength(int filedLength) {
        this.filedLength = filedLength;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isHasLength() {
        return hasLength;
    }

    public void setHasLength(boolean hasLength) {
        this.hasLength = hasLength;
    }

    public boolean isHasPrecision() {
        return hasPrecision;
    }

    public void setHasPrecision(boolean hasPrecision) {
        this.hasPrecision = hasPrecision;
    }

    public boolean isHasPrimaryKey() {
        return hasPrimaryKey;
    }

    public void setHasPrimaryKey(boolean hasPrimaryKey) {
        this.hasPrimaryKey = hasPrimaryKey;
    }

    public boolean isHasCanNull() {
        return hasCanNull;
    }

    public void setHasCanNull(boolean hasCanNull) {
        this.hasCanNull = hasCanNull;
    }

    public boolean isHasIdentity() {
        return hasIdentity;
    }

    public void setHasIdentity(boolean hasIdentity) {
        this.hasIdentity = hasIdentity;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getForeignKey() {
        return foreignKey;
    }

    public void setForeignKey(String foreignKey) {
        this.foreignKey = foreignKey;
    }

    /**
     * 重写等于
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (!obj.getClass().toString().equals(this.getClass().toString())) {
            return false;
        }
        Column cExcel = (Column) obj;
        // 类型是否相同,为true时，则相同
        boolean boolType = true;
        // 因为从数据库读取出来的列类型全改为小写,所以这里也要改成小写
        if (!this.getFiledType().equals(cExcel.getFiledType().toLowerCase())) {
            boolType = false;
        }

        //引用修改	//TODO 此处法存在问题
        Boolean boolFK = true;
        if (this.getForeignKey() != null && cExcel.getForeignKey() != null) {
            if (this.getForeignKey().length() > 0 && cExcel.getForeignKey().length() > 0) {
                if (!this.getForeignKey().equals(cExcel.getForeignKey())) {
                    boolFK = false;
                }
            }
        }

        //中文名是否改变
        boolean name_ch = true;
        if (!this.getFiledNameChinese().equals(cExcel.getFiledNameChinese())) {
            name_ch = false;
        }

        // 长度是否相同,为true时，则长度相同
        boolean boolLength = true;
        // 有长度
        if (cExcel.isHasLength()) {
            boolLength = cExcel.getFiledLength() == this.getFiledLength();
        } else {
            // 是否都有或者没有长度
            boolLength = cExcel.isHasLength() == this.isHasLength();
        }

        // 精度是否相同,为true时，则精度相同
        // 为true时，则长度相同
        boolean boolPrecision = true;
        // 有精度
        if (cExcel.isHasPrecision()) {
            boolPrecision = cExcel.getPrecision() == this.getPrecision();
        } else {
            // 是否都有或者没有精度
            boolPrecision = cExcel.isHasPrecision() == this.isHasPrecision();
        }
        //内容一样
        if (boolType && boolLength && boolPrecision && name_ch && boolFK) {
            return true;
        }

        return false;
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Column{");
        sb.append("id=").append(id);
        sb.append(", tableName='").append(tableName).append('\'');
        sb.append(", filedName='").append(filedName).append('\'');
        sb.append(", filedNameChinese='").append(filedNameChinese).append('\'');
        sb.append(", filedType='").append(filedType).append('\'');
        sb.append(", filedLength=").append(filedLength);
        sb.append(", precision=").append(precision);
        sb.append(", defaultValue='").append(defaultValue).append('\'');
        sb.append(", hasLength=").append(hasLength);
        sb.append(", hasPrecision=").append(hasPrecision);
        sb.append(", hasPrimaryKey=").append(hasPrimaryKey);
        sb.append(", hasCanNull=").append(hasCanNull);
        sb.append(", hasIdentity=").append(hasIdentity);
        sb.append(", desc='").append(desc).append('\'');
        sb.append(", foreignKey='").append(foreignKey).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
