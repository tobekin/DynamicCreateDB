package utils;

import entity.Column;
import entity.Table;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description: 实体类生成器
 * @Author chenjun
 * @Create 2018-12-14 20:06
 */
public class EntityBuilderHelper {

    public static void builder(Map<String, Table> excelTables) {
        for (String tableKey : excelTables.keySet()) {
            Table table = excelTables.get(tableKey);

            //通过表名取得类[如:com.test.SYS_TEST改为com.test.SysTest]
            String className = JavaCodeHelper.getClassAllName(table.getPackName() + "." + table.getTableName());

            //初始化
            JavaCodeHelper codeFile = new JavaCodeHelper(className, false, table.getTableNameChinese());

            //默认构造方法
            codeFile.addConstructor(null);
            /*Map<String, String> map = new LinkedHashMap<>();
            map.put("id", "String");
            //id构造方法
            codeFile.addConstructor(map);*/

            //其他属性
            for (String key : table.getColumns().keySet()) {
                Column c = table.getColumns().get(key);
                String fk = c.getForeignKey();
                //关联字段
                if (StringUtils.isNotBlank(fk) && fk.length() > 0 && !StringUtils.equals("null", fk)) {
                    fk = fk.replace("[", "");
                    String fks[] = fk.split("]");
                    //类型
                    String fullType = JavaCodeHelper.getClassAllName(fks[0]);
                    //包入类型包
                    codeFile.importPage(fullType);
                    String type = JavaCodeHelper.classNameSubPackage(fullType)[1];
                    codeFile.addField("private", type, c.getFiledName(), ";");
                    //注释(false：生成注释在字段上，true：生成注释在get方法上)
                    codeFile.insertDescription(c.getFiledNameChinese(), false);
                    //get and set
                    codeFile.addGetterAndSetter(c.getFiledName(), type);
                } else {
                    //普通字段
                    String fullType = TypeMapping.sqlToJava(c.getFiledType());
                    String type = JavaCodeHelper.classNameSubPackage(fullType)[1];
                    //包入类型包
                    codeFile.importPage(fullType);
                    codeFile.addField("private", type, c.getFiledName(), ";");
                    //注释
                    codeFile.insertDescription(c.getFiledNameChinese(), false);
                    //get and set
                    codeFile.addGetterAndSetter(c.getFiledName(), type);
                }
            }

            try {
                codeFile.builder();//生成文件
                System.out.println("生成:" + className);
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }
}
