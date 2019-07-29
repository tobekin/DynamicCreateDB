package utils;

import entity.Column;
import entity.Table;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: Excel工具类 包括得到所有表及字段
 * @Author chenjun
 * @Create 2018-12-12 14:32
 */
public class ExcelHelper {

    /**
     * 从Excel中得到表
     *
     * @param file       文件
     * @param readColumn 是否读取列信息
     */
    public static Map<String, Table> getAllTables(File file, boolean readColumn) throws IOException {
        //构造Workbook（工作薄）对象
        Workbook wb = ReadExcel.getWorkBook(file);

        //获得了Workbook对象之后，就可以通过它得到Sheet对象了
        List<Sheet> sheetList = ReadExcel.getSheets(wb);

        if (sheetList == null || sheetList.size() == 0) {
            wb.close();
            return null;
        }

        Map<String, Table> tables = new LinkedHashMap<>();

        //读取第一张表的所有表信息
        Sheet firSheet = sheetList.get(0);
        List<Row> firstRowList = ReadExcel.getRows(firSheet);
        if (firstRowList == null || firstRowList.size() == 0) {
            wb.close();
            return null;
        }

        for (int i = 0; i < firstRowList.size(); i++) {
            if (i == 0) {
                continue;
            }
            //从第2行开始
            List<Cell> firstCellList = ReadExcel.getCells(firstRowList.get(i));

            //如果单元格为空
            if (firstCellList == null || firstCellList.size() == 0) {
                continue;
            }
            //如果列的长度不正确直接返回
            if (firstCellList.size() < 5) {
                continue;
            }

            //构建一张表

            Table table = new Table(i, String.valueOf(ReadExcel.getCellValue(firstCellList.get(1))), String.valueOf(ReadExcel.getCellValue(firstCellList.get(2))), String.valueOf(ReadExcel.getCellValue(firstCellList.get(3))), String.valueOf(ReadExcel.getCellValue(firstCellList.get(4))));

            //需要读限列信息
            if (readColumn) {
                //找出对应的表
                for (int j = 0; j < sheetList.size(); j++) {
                    if (j == 0) {
                        continue;
                    }
                    //从第二个sheet表开始
                    //得到表名
                    String tn = sheetList.get(i).getSheetName();
                    //找到对应的表后，读取列信息，根据数据库名中文名匹配
                    if (StringUtils.equals(table.getTableNameChinese(), tn)) {
                        table.setColumns(getColumnsByTable(sheetList.get(i)));
                        break;
                    }
                }
            }
            //键为大写
            tables.put(table.getTableName(), table);
        }

        //最后关闭资源，释放内存
        wb.close();
        return tables;
    }

    /**
     * 得到某表的所有字段
     *
     * @param sheet sheet表
     */
    private static Map<String, Column> getColumnsByTable(Sheet sheet) {
        if (sheet == null) {
            return null;
        }

        List<Row> rowList = ReadExcel.getRows(sheet);

        if (rowList == null || rowList.size() == 0) {
            return null;
        }

        Map<String, Column> columns = getColumnsByTable(rowList);

        return columns;
    }

    /**
     * 将excel中sheet的每一行封装为Column
     */
    private static Map<String, Column> getColumnsByTable(List<Row> rowList) {
        if (rowList == null || rowList.size() == 0) {
            return null;
        }

        Map<String, Column> columns = new LinkedHashMap<>();

        for (int i = 0; i < rowList.size(); i++) {
            if (i == 0) {
                continue;
            }
            //从第二行开始
            List<Cell> cellList = ReadExcel.getCells(rowList.get(i));

            if (cellList == null || cellList.size() == 0) {
                continue;
            }

            //如果列的长度不正确直接返回
            if (cellList.size() < 10) {
                continue;
            }

            Column column = new Column();

            // 表名
            column.setTableName(String.valueOf(ReadExcel.getCellValue(cellList.get(0))));

            // 字段名
            column.setFiledName(String.valueOf(ReadExcel.getCellValue(cellList.get(1))));

            //中文字段名
            column.setFiledNameChinese(String.valueOf(ReadExcel.getCellValue(cellList.get(2))));

            //处理数据类型
            // 类型和长度 varchar(10,3)
            String type = String.valueOf(ReadExcel.getCellValue(cellList.get(3)));
            // 没有长度的类型
            if (type.indexOf("(") == -1) {
                column.setHasLength(false);
                // 类型
                column.setFiledType(type);
            } else {
                // 有长度的类型
                column.setHasLength(true);
                int beginIndex = type.indexOf("(");
                int endIndex = type.indexOf(")");
                // 类型
                String typeTemp = type.substring(0, beginIndex);
                // 长度
                String typeLengthTemp = type.substring(beginIndex + 1, endIndex);
                int dianIndex = typeLengthTemp.indexOf(",");
                // 一位长度
                if (dianIndex == -1) {
                    // 无精度
                    column.setHasPrecision(false);
                    column.setFiledLength(Integer.parseInt(typeLengthTemp.trim()));
                } else {
                    // 多位长度
                    // 有精度
                    column.setHasPrecision(true);
                    column.setFiledLength(Integer.parseInt(typeLengthTemp.substring(0, dianIndex).trim()));
                    column.setPrecision(Integer.parseInt(typeLengthTemp.substring(dianIndex + 1).trim()));
                }
                column.setFiledType(typeTemp);
            }

            //是否可为空
            if (StringUtils.equals("Y", String.valueOf(ReadExcel.getCellValue(cellList.get(4)))) || StringUtils.equals("YES", String.valueOf(ReadExcel.getCellValue(cellList.get(4))))) {
                column.setHasCanNull(true);
            } else if (StringUtils.equals("N", String.valueOf(ReadExcel.getCellValue(cellList.get(4)))) || StringUtils.equals("NO", String.valueOf(ReadExcel.getCellValue(cellList.get(4))))) {
                column.setHasCanNull(false);
            } else {
                column.setHasCanNull(true);
            }

            //默认值
            column.setDefaultValue(String.valueOf(ReadExcel.getCellValue(cellList.get(5))));

            // 是否为主键
            if (StringUtils.equals("Y", String.valueOf(ReadExcel.getCellValue(cellList.get(6)))) || StringUtils.equals("YES", String.valueOf(ReadExcel.getCellValue(cellList.get(6))))) {
                column.setHasPrimaryKey(true);
            } else if (StringUtils.equals("N", String.valueOf(ReadExcel.getCellValue(cellList.get(6)))) || StringUtils.equals("NO", String.valueOf(ReadExcel.getCellValue(cellList.get(6))))) {
                column.setHasPrimaryKey(false);
            } else {
                column.setHasPrimaryKey(false);
            }

            // 是否唯一标识
            if (StringUtils.equals("Y", String.valueOf(ReadExcel.getCellValue(cellList.get(7)))) || StringUtils.equals("YES", String.valueOf(ReadExcel.getCellValue(cellList.get(7))))) {
                column.setHasIdentity(true);
            } else if (StringUtils.equals("N", String.valueOf(ReadExcel.getCellValue(cellList.get(7)))) || StringUtils.equals("NO", String.valueOf(ReadExcel.getCellValue(cellList.get(7))))) {
                column.setHasIdentity(false);
            } else {
                column.setHasIdentity(false);
            }

            // 外键
            column.setForeignKey(String.valueOf(ReadExcel.getCellValue(cellList.get(8))));

            //备注
            column.setDesc(String.valueOf(ReadExcel.getCellValue(cellList.get(9))));

            //键为大写
            columns.put(column.getFiledName(), column);
        }
        return columns;
    }

    /**
     * 根据表名得到某一张表
     *
     * @param tableName  表名
     * @param readColumn 是否需要读取列信息
     */
    public static Table getTable(File file, String tableName, boolean readColumn) throws IOException {
        //如果表名为空
        if (StringUtils.isBlank(tableName)) {
            return null;
        }

        //从map对象中取出某一张表
        Map<String, Table> allTables = getAllTables(file, readColumn);

        return allTables.get(tableName);
    }

}
