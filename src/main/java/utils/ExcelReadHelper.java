package utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenjun
 * @description: excel读取帮助类
 * @date 2019-02-23 15:11
 */
public class ExcelReadHelper {

    /**
     * 后缀（excel 2003）
     */
    public static final String OFFICE_EXCEL_2003_POSTFIX = "xls";

    /**
     * 后缀(excel 2007)
     */
    public static final String OFFICE_EXCEL_2007_POSTFIX = "xlsx";

    /**
     * 空串
     */
    public static final String EMPTY = "";

    /**
     * 点
     */
    public static final String POINT = ".";

    /**
     * 读取文件
     *
     * @param file      文件
     * @param sheetNum  sheet表号 从0开始
     * @param startRow  开始行号 从0开始
     * @param endRow    结束行号 可以传null
     * @param startCell 开始列号 从0开始
     * @param endCell   结束列号 可以传null
     * @return 返回集合数据
     * @throws IOException 输入输出异常
     */
    public static List<ArrayList<Object>> readExcel(File file, int sheetNum, Integer startRow, Integer endRow, Integer startCell, Integer endCell) throws IOException {
        //如果文件为空，或者文件名为空
        boolean flag = file == null || EMPTY.equals(file.getName().trim());
        if (flag == true) {
            return null;
        }
        //原文件名称
        String prefix = getFileFix(file.getName());
        //后缀名为空
        if (StringUtils.isBlank(prefix)) {
            return null;
        }
        //工作簿
        Workbook workbook = null;
        InputStream inputStream = null;

        //根据文件格式判断
        if (StringUtils.equals(prefix, OFFICE_EXCEL_2003_POSTFIX)) {
            inputStream = new FileInputStream(file);
            workbook = new HSSFWorkbook(inputStream);
        } else if (StringUtils.equals(prefix, OFFICE_EXCEL_2007_POSTFIX)) {
            inputStream = new FileInputStream(file);
            workbook = new XSSFWorkbook(inputStream);
        } else {
            return null;
        }
        //如果工作簿为空
        if (workbook == null) {
            return null;
        }
        //根据sheet表号获取工作表
        Sheet sheet = workbook.getSheetAt(sheetNum);
        if (sheet == null) {
            return null;
        }
        //数据
        List<ArrayList<Object>> dataList = new ArrayList<ArrayList<Object>>();
        //sheet中总行数(不包括空行)
        int totalRows = sheet.getPhysicalNumberOfRows();
        //判断如果开始行数为空或大于总行数
        if (startRow == null || startRow > totalRows) {
            //默认从第一行开始读取
            startRow = sheet.getFirstRowNum();
        }
        //判断如果结束行数为空或大于总行数
        if (endRow == null || endRow > totalRows) {
            //默认读取总行数(包括空行)
            endRow = totalRows;
        }
        ArrayList<Object> rowList = null;
        //循环读取行
        for (int i = startRow; i <= endRow; i++) {
            //第几行
            Row row = sheet.getRow(i);
            //如果该行为空
            if (row == null) {
                continue;
            }
            //开始列（临时）
            Integer startCellTemp = startCell;
            //结束列（临时）
            Integer endCellTemp = endCell;
            //每行的总列数(包括空列)
            int totalCells = row.getLastCellNum() - 1;
            //第一列的序号
            int firstCellNum = row.getFirstCellNum();
            //判断如果开始列数为空或大于总列数
            if (startCellTemp == null || startCellTemp > totalCells) {
                //默认从第一行开始读取
                startCellTemp = firstCellNum;
            }
            //判断如果结束列数为空或大于总列数
            if (endCellTemp == null || endCellTemp > totalCells) {
                //默认读取总列数(包括空列)
                endCellTemp = totalCells;
            }
            //实例化对象
            rowList = new ArrayList<>();
            //循环读取列
            for (int j = startCellTemp; j <= endCellTemp; j++) {
                //第几列
                Cell cell = row.getCell(j);
                Object value = ReadExcel.getCellValue(cell);
                //如果该列值为空
                if (value == null) {
                    value = "";
                }
                //加入集合中
                rowList.add(value);
            }

            //将行数据加入到数据集合中
            dataList.add(rowList);
        } 

        return dataList;
    }

    /**
     * 获得文件的后缀名
     *
     * @param fileName
     * @return
     */
    public static String getFileFix(String fileName) {
        //如果文件名为空
        if (StringUtils.isBlank(fileName)) {
            return EMPTY;
        }
        //如果文件名中包含点
        if (fileName.contains(POINT)) {
            return fileName.substring(fileName.lastIndexOf(POINT) + 1);
        }
        return EMPTY;
    }
}
