package utils;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: JAVA类生成
 * @Author chenjun
 * @Create 2018-12-12 18:32
 */
public class JavaCodeHelper {

    private String superClassName;

    private boolean isInterface;

    private List<String> interfaces = new ArrayList<String>();

    private List<String> annotations = new ArrayList<String>();

    private String clzName;

    private String packageName;

    private List<String> packages = new ArrayList<String>();

    private List<String> fields = new ArrayList<String>();

    private Map<String, String> methods = new LinkedHashMap<String, String>();

    private Map<Integer, String> stepDescriptions = new LinkedHashMap<>();

    private String modify = "public";

    int fieldStep = 0;

    int methodStep = 10000000;

    private String classDescription;

    boolean isLastMethod = true;

    public static final String NEWLINE = "\r\n";

    public static final String METHOD_NEWLINE = "\r\n\t";

    public static final String METHOD_BODY_NEWLINE = "\r\n\t\t";

    public JavaCodeHelper(String className, boolean isInterface) {
        this.isInterface = isInterface;
        String[] strArr = classNameSubPackage(className);
        if (strArr.length == 2) {
            this.packageName = strArr[0];
            clzName = strArr[1];
        } else {
            this.clzName = className;
        }
    }

    public JavaCodeHelper(String className, boolean isInterface, String description) {
        this.isInterface = isInterface;
        String[] strArr = classNameSubPackage(className);
        if (strArr.length == 2) {
            this.packageName = strArr[0];
            clzName = strArr[1];
        } else {
            this.clzName = className;
        }
        classDescription = description;
    }

    /**
     * 添加继承类
     */
    public void setSuperClassName(String className) {
        this.superClassName = className;
    }

    /**
     * 添加注解
     */
    public void addAnnotation(String annoco) {
        annotations.add(annoco);
    }

    /**
     * 添加继承接口
     */
    public void implementInterface(String interfaceName) {
        importPage(interfaceName);
        interfaces.add(classNameSubName(interfaceName));
    }

    /**
     * 添加注释
     *
     * @param description 注释内容
     * @param isMethod    是否是方法
     */
    public void insertDescription(String description, boolean isMethod) {
        isLastMethod = isMethod;
        String str = "\t/**\r\n\t * " + description + "\r\n\t */";
        if (isLastMethod) {
            stepDescriptions.put(methodStep, str);
        } else {
            stepDescriptions.put(fieldStep, str);
        }
    }

    /**
     * 添加构造函数
     */
    public void addConstructor(Map<String, String> params) {
        String str = "";
        String str2 = "";
        if (params != null) {
            for (String key : params.keySet()) {
                str += params.get(key) + " " + key + ",";
                str2 += "this." + key + " = " + key + ";\r\n";
            }
            if (str.length() > 0) {
                str = str.substring(0, str.length() - 1);
            }
        }
        str2 = str2.trim();
        this.addMethod("public " + clzName + "(" + str + ")", str2);
    }

    /**
     * 导入包
     *
     * @param className 包名
     */
    public void importPage(String className) {
        if (isNotEmpty(this.packageName)) {
            //非java.lang和当前类的包，并还没有导入过
            if (className.indexOf("java.lang") == -1 && className.indexOf(this.packageName) == -1 && packages.indexOf(className) == -1) {
                packages.add(className);
            }
        } else {
            if (className.indexOf("java.lang") == -1 && className.indexOf(".") != -1 && packages.indexOf(className) == -1) {
                packages.add(className);
            }
        }
    }

    /**
     * 添加方法
     *
     * @param methodDefine 方法声明,包括批注
     * @param body         方法体,需换行时,请用JavaCodeFile.METHOD_BODY_NEWLINE
     */
    public void addMethod(String methodDefine, String body) {
        methods.put(methodDefine, body);
        methodStep++;
        isLastMethod = true;
    }

    /**
     * 添加字段
     */
    public void addField(String modify, String type, String fieldName, String init) {
        fieldName = camelCaseFieldName(fieldName);
        fields.add(modify + " " + type + " " + fieldName + init);
        fieldStep++;
        isLastMethod = false;
    }

    /**
     * 添加get方法
     */
    public void addGetter(String fieldName, String type) {
        fieldName = camelCaseFieldName(fieldName);
        this.addMethod("public " + type + " get" + firstToUpperCase(fieldName) + "()", "return this." + fieldName + ";");
    }

    /**
     * 添加set方法
     */
    public void addSetter(String fieldName, String type) {
        fieldName = camelCaseFieldName(fieldName);
        this.addMethod("public void set" + firstToUpperCase(fieldName) + "(" + type + " " + fieldName + ")", "this." + fieldName + " = " + fieldName + ";");
    }

    /**
     * 添加get,set方法
     */
    public void addGetterAndSetter(String fieldName, String type) {
        this.addGetter(fieldName, type);
        this.addSetter(fieldName, type);
        if (fieldName.indexOf("is") == 0) {
            addIster(fieldName);
        }
    }

    /**
     * 添加boolean型get和set方法
     */
    public void addIster(String fieldName) {
        fieldName = camelCaseFieldName(fieldName);
        this.addMethod("public Boolean " + firstToUpperCase(fieldName) + "()", "return this." + fieldName + ".intValue()==1;");
        this.addMethod("public void set" + firstToUpperCase(fieldName) + "(Boolean " + fieldName + ")", "this." + fieldName + "=Integer.valueOf(" + fieldName
                + " ? 1 : 0);");
    }

    /**
     * 将字段转化为驼峰命名法
     *
     * @param fieldName 字段名
     * @return 驼峰命名法的字段
     */
    public String camelCaseFieldName(String fieldName) {
        StringBuilder tempFieldName = new StringBuilder();
        if (StringUtils.isBlank(fieldName)) {
            return tempFieldName.toString();
        }
        //分割字符
        String[] fieldNameStr = fieldName.split("_");
        //如果分割字符串不为空
        if (fieldNameStr != null && fieldNameStr.length > 0) {
            //驼峰命名法
            for (int i = 0; i < fieldNameStr.length; i++) {
                //如果第一个分词字小写
                if (i == 0) {
                    tempFieldName.append(fieldNameStr[i].toLowerCase());
                } else {
                    tempFieldName.append(firstToUpperCase(fieldNameStr[i].toLowerCase()));
                }
            }
        }
        return tempFieldName.toString();
    }


    /**
     * 将类全名拆成包含：包名+类名 的数组<br>如果只有类名，则直接返回类名
     */
    public static String[] classNameSubPackage(String className) {
        int index = className.lastIndexOf(".");
        if (index != -1) {
            String[] strArr = new String[2];
            strArr[0] = className.substring(0, index);
            strArr[1] = className.substring(index + 1);
            return strArr;
        }
        return new String[]{"", className};
    }

    /**
     * 将类的包名过滤，只返回类名 的数组<br>如果只有类名，则直接返回类名
     */
    public static String classNameSubName(String className) {
        int index = className.lastIndexOf(".");
        if (index != -1) {
            return className.substring(index + 1);
        }
        return className;
    }

    /**
     * 类名标准化
     * 如:SYS_TEST改为com.test.SysTest
     */
    public static String getClassAllName(String className) {
        int index = className.lastIndexOf(".");
        if (index != -1) {
            String str1 = className.substring(0, index);
            String str2 = className.substring(index + 1);
            str2 = getClassName(str2);
            return str1 + "." + str2;
        } else {
            return getClassName(className);
        }
    }


    /**
     * 类名标准化
     * 如:SYS_TEST改为SysTest
     */
    public static String getClassName(String className) {
        StringBuilder tempClassName = new StringBuilder();
        if (StringUtils.isBlank(className)) {
            return tempClassName.toString();
        }
        //分割字符
        String[] fieldNameStr = className.split("_");
        //如果分割字符串不为空
        if (fieldNameStr != null && fieldNameStr.length > 0) {
            //驼峰命名法
            for (int i = 0; i < fieldNameStr.length; i++) {
                tempClassName.append(firstToUpperCase(fieldNameStr[i].toLowerCase()));
            }
        }
        return tempClassName.toString();
    }

    /**
     * 得到此类的全路径(实际保存的物理路径)
     */
    public String getFullPath() {
        String path = getProjectPath();
        path += "\\src\\main\\java\\" + packageName.replace(".", "\\");
        path += "\\" + clzName + ".java";
        return path;
    }

    /**
     * 生成类,保存到工程+类对应的包目录下
     */
    public void builder() throws IOException {
        builder(getFullPath());
    }

    /**
     * 返回此类是否在项目中存在
     */
    public boolean isExists() throws IOException {
        String path = getProjectPath();
        path += "\\src\\" + packageName.replace(".", "\\");
        path += "\\" + clzName + ".java";
        return new File(path).exists();
    }

    /**
     * 生成类,并保存到filePath中
     * 如果没有文件，则生成
     */
    public void builder(String filePath) throws IOException {
        File file = new File(filePath);
        //如果文件不存在
        if (!file.exists()) {
            try {
                //先尝试生成目录
                new File(file.getParent()).mkdirs();
                //现生成文件
                file.createNewFile();
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
        builder(file);
    }

    public void builder(File file) throws IOException {
        String encoding = "UTF-8";
        BufferedWriter bw;
        if (encoding != null && encoding.length() > 0) {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));
        } else {
            bw = new BufferedWriter(new FileWriter(file));
        }

        // 打包
        if (isNotEmpty(packageName)) {
            bw.write("package ");
            bw.write(packageName);
            bw.write(";");
            bw.write(NEWLINE);
            bw.write(NEWLINE);
        }
        // 导入包
        for (String packageName : packages) {
            bw.write("import ");
            bw.write(packageName);
            bw.write(";");
            bw.write(NEWLINE);
        }
        bw.write(NEWLINE);
        //添加类注释
        String classDesc = "/**\r\n *" + " @Description: ";
        if (classDescription != null) {
            classDesc += classDescription;
        }
        classDesc += NEWLINE + " *" + " @Author " + System.getProperty("user.name");
        classDesc += NEWLINE + " *" + " @Create " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        classDesc += NEWLINE + " */";
        bw.write(classDesc);
        bw.write(NEWLINE);
        // 注解
        for (String annoco : this.annotations) {
            bw.write("@" + annoco + NEWLINE);
        }
        //接口
        if (isInterface) {
            bw.write(modify + " interface ");
        } else {
            bw.write(modify + " class ");
        }
        bw.write(clzName);
        if (this.superClassName != null) {
            bw.write(" extends " + this.superClassName);
        }
        if (interfaces.size() > 0) {
            bw.write(" implements ");
            String str = "";
            for (String interfaceName : interfaces) {
                str += interfaceName + ",";
            }
            str = str.substring(0, str.length() - 1);
            bw.write(str);
        }
        bw.write(" {");
        bw.write(NEWLINE);
        bw.write(NEWLINE);
        int cnt = 1;
        // 字段
        for (String field : fields) {
            String description = stepDescriptions.get(cnt);
            if (description != null) {
                bw.write(description);
                bw.write(NEWLINE);
            }
            bw.write("\t");
            bw.write(field);
            bw.write(NEWLINE);
            bw.write(NEWLINE);
            cnt++;
        }
        cnt = 10000000;
        // 方法
        for (String method : methods.keySet()) {
            String description = stepDescriptions.get(cnt);
            if (description != null) {
                bw.write(description);
                bw.write(NEWLINE);
            }
            String m = methods.get(method);
            bw.write("\t");
            bw.write(method);
            if (m != null) {
                bw.write("{");
                bw.write("\r\n");
                bw.write("\t\t");
                bw.write(m);
                bw.write("\r\n");
                bw.write("\t}");
            } else {
                bw.write(";");
            }
            bw.write(NEWLINE);
            bw.write(NEWLINE);
            cnt++;
        }
        bw.write("}");
        bw.flush();
        bw.close();
    }

    public void setModify(String modify) {
        this.modify = modify;
    }

    /**
     * 验证字符串是否非空
     */
    public boolean isNotEmpty(String str) {
        return str != null && str.trim().length() > 0;
    }

    /**
     * 字符串首个字母大写
     */
    public static String firstToUpperCase(String str) {
        char[] trimChars = str.toCharArray();
        trimChars[0] = Character.toUpperCase(trimChars[0]);
        return new String(trimChars);
    }

    /**
     * 字符串首个字母小写
     */
    public static String firstToLowerCase(String str) {
        char[] trimChars = str.toCharArray();
        trimChars[0] = Character.toLowerCase(trimChars[0]);
        return new String(trimChars);
    }

    /**
     * 得到项目路径
     */
    public static String getProjectPath() {
        //获取当前工程路径
        System.out.println(System.getProperty("user.dir"));
        return System.getProperty("user.dir");
    }
}
