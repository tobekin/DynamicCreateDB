package utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ReadExcel {

    /**
     * 读取Excel，获取工作簿
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static Workbook getWorkBook(File file) throws IOException {
        String xls = "xls";
        String xlsx = "xlsx";
        String fileName = file.getName();
        String extension = fileName.lastIndexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf(".") + 1);
        if (xls.equals(extension)) {
            HSSFWorkbook hwb = new HSSFWorkbook(new FileInputStream(file));
            return hwb;
        } else if (xlsx.equals(extension)) {
            XSSFWorkbook xwb = new XSSFWorkbook(new FileInputStream(file));
            return xwb;
        } else {
            throw new IOException("不支持的文件类型");
        }
    }

    /**
     * 从工作簿中获取sheet表
     *
     * @param workbook 工作簿
     * @return
     */
    public static List<Sheet> getSheets(Workbook workbook) {

        //如果工作簿为空，直接返回空
        if (workbook == null) {
            return null;
        }

        //实例化对象
        List<Sheet> sheetList = new ArrayList<>();

        Sheet sheet = null;

        //获取每个Sheet表
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            sheet = workbook.getSheetAt(i);
            sheetList.add(sheet);
        }

        return sheetList;
    }


    /**
     * 从sheet表中获取行
     *
     * @param sheet sheet表
     * @return
     */
    public static List<Row> getRows(Sheet sheet) {

        //如果sheet表为空，直接返回空
        if (sheet == null) {
            return null;
        }

        //实例化对象
        List<Row> rowList = new ArrayList<>();

        Row row = null;

        //获取每个Sheet表中的行
        for (int i = sheet.getFirstRowNum(); i <= sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            rowList.add(row);
        }

        return rowList;
    }

    /**
     * 从每行中获取列
     *
     * @param row 每行
     * @return
     */
    public static List<Cell> getCells(Row row) {

        //如果每行为空，直接返回空
        if (row == null) {
            return null;
        }

        //实例化对象
        List<Cell> cellList = new ArrayList<>();

        Cell cell = null;

        //获取每个Sheet表中的行
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            cell = row.getCell(i);
            cellList.add(cell);
        }

        return cellList;
    }

    /**
     * 判断Excel导入的数据类型，转换成数据库可识别的数据类型
     *
     * @param cell
     * @return
     */
    public static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        Object cellValue = null;

        switch (cell.getCellTypeEnum()) {
            case _NONE:
                cellValue = "";
                break;
            case STRING:
                // 字符串
                cellValue = cell.getStringCellValue();
                break;
            case BOOLEAN:
                // Boolean
                cellValue = cell.getBooleanCellValue();
                break;
            case FORMULA:
                // 公式
                cellValue = cell.getCellFormula();
                break;
            case BLANK:
                // 空值
                cellValue = "";
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // 如果是date类型则 ，获取该cell的date值
                    cellValue = JdDateUtil.formatDate(DateUtil.getJavaDate(cell.getNumericCellValue()), JdDateUtil.yyyy_MM_dd_HH_mm_ss);
                } else {
                    // 纯数字
                    cellValue = AmountUtil.round(new BigDecimal(cell.getNumericCellValue()));
                }
                break;
            case ERROR:
                // 故障
                cellValue = cell.getErrorCellValue();
                break;
            default:
                cellValue = cell.getStringCellValue();
                break;
        }

        return cellValue;
    }
}