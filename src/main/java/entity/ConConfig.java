package entity;

import java.io.Serializable;

/**
 * @Description: 数据库连接配置
 * @Author chenjun
 * @Create 2018-12-12 14:13
 */
public class ConConfig implements Serializable {

    private static final long serialVersionUID = 9105317210112073394L;

    /**
     * 数据库url
     */
    private String url;
    /**
     * 用户名
     */
    private String user;
    /**
     * 密码
     */
    private String pwd;
    /**
     * 数据库名
     */
    private String dbName;

    public ConConfig() {
    }

    /**
     * 构造方法（不带数据库名）
     *
     * @param url
     * @param user
     * @param pwd
     */
    public ConConfig(String url, String user, String pwd) {
        super();
        this.url = url;
        this.user = user;
        this.pwd = pwd;
    }

    /**
     * 构造方法（带数据库名）
     *
     * @param url
     * @param user
     * @param pwd
     */
    public ConConfig(String url, String user, String pwd, String dbName) {
        super();
        this.url = url;
        this.user = user;
        this.pwd = pwd;
        this.dbName = dbName;
    }

    public String getUrl() {
        if (dbName != null) {
            return url + "/" + dbName;
        }
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ConConfig{");
        sb.append("url='").append(url).append('\'');
        sb.append(", user='").append(user).append('\'');
        sb.append(", pwd='").append(pwd).append('\'');
        sb.append(", dbName='").append(dbName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
