package utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.poi.ss.usermodel.CellType.STRING;

/**
 * @author chenjun
 * @description: excel写入帮助类
 * @date 2019-07-30 13:44
 */
public class ExcelWriteHelper {

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

    public static void main(String[] args) throws Exception {

        List<List<String>> head = new ArrayList();
        //sheet名称
        List<String> name = new ArrayList<>(Arrays.asList(new String[]{"大区", "分公司", "门店"}));

        //每个sheet头
        List<String> head1 = new ArrayList<>(Arrays.asList(new String[]{"大区头1", "大区头2", "大区头3", "大区头4", "大区头5"}));
        List<String> head2 = new ArrayList<>(Arrays.asList(new String[]{"分公司头1", "分公司头2", "分公司头3"}));
        List<String> head3 = new ArrayList<>(Arrays.asList(new String[]{"门店头1", "门店头2"}));

        head.add(head1);
        head.add(head2);
        head.add(head3);

        //每个sheet数据
        List<List<List<Object>>> data = new ArrayList<>();
        List<List<Object>> data1 = new ArrayList<>();
        List<List<Object>> data2 = new ArrayList<>();
        List<List<Object>> data3 = new ArrayList<>();


        for (int i = 0; i < 10; i++) {
            List tmpList = new ArrayList();
            for (int j = 0; j < 5; j++) {
                tmpList.add(j);
            }
            data1.add(tmpList);
        }

        for (int i = 0; i < 4; i++) {
            List tmpList = new ArrayList();
            for (int j = 0; j < 3; j++) {
                tmpList.add(j);
            }
            data2.add(tmpList);
        }

        for (int i = 0; i < 6; i++) {
            List tmpList = new ArrayList();
            for (int j = 0; j < 2; j++) {
                tmpList.add(j);
            }
            data3.add(tmpList);
        }

        data.add(data1);
        data.add(data2);
        data.add(data3);

        createExcelWorkBook("test.xls", "d:", name, head, data);
    }

    public static void createExcelWorkBook(String fileName, String filePath, List<String> sheetName, List<List<String>> sheetHeader, List<List<List<Object>>> sheetData) throws Exception {
        if (sheetName == null || sheetName.size() == 0) {
            return;
        }
        if (sheetHeader == null || sheetHeader.size() == 0) {
            return;
        }
        if (sheetData == null || sheetData.size() == 0) {
            return;
        }
        //如果文件名为空或者文件路径为空！
        if (StringUtils.isBlank(fileName) || StringUtils.isBlank(filePath)) {
            return;
        }
        //不包含点，说明文件名不对
        if (!StringUtils.contains(fileName, ".")) {
            return;
        }
        //文件后缀名
        String suffix = getFileFix(fileName);
        //后缀名为空
        if (StringUtils.isBlank(suffix)) {
            return;
        }

        filePath = filePath + File.separator + fileName;
        OutputStream outputStream = new FileOutputStream(filePath);

        //工作簿
        Workbook workbook = null;

        //根据文件格式判断
        if (StringUtils.equals(suffix, OFFICE_EXCEL_2003_POSTFIX)) {
            workbook = new HSSFWorkbook();
        } else if (StringUtils.equals(suffix, OFFICE_EXCEL_2007_POSTFIX)) {
            workbook = new XSSFWorkbook();
        } else {
            return;
        }

        //动态创建sheet
        workbook = createExcelSheet(workbook, sheetName, sheetHeader, sheetData);
        workbook.write(outputStream);
        outputStream.close();
    }

    /**
     * 创建多个excel的sheet页(带链接的，第一页最后一列)
     *
     * @param wb          工作簿
     * @param sheetName   sheet页名
     * @param sheetHeader sheet页表头信息
     * @param sheetData   sheet页表内数据
     * @return
     * @throws Exception
     */
    private static Workbook createExcelSheet(Workbook wb, List<String> sheetName, List<List<String>> sheetHeader, List<List<List<Object>>> sheetData) throws Exception {
        /* 链接跳转*/
        CreationHelper createHelper = wb.getCreationHelper();

        //sheet页号
        int sheetNum = sheetName.size();
        for (int m = 0; m < sheetNum; m++) {
            Sheet sheet = wb.createSheet();
            String msg = sheetName.get(m);
            String str = new String(msg.getBytes("UTF-8"), "UTF-8");

            wb.setSheetName(m, str);
            Header header = sheet.getHeader();
            header.setCenter("sheet");
            Row headerRow = sheet.createRow(0);

            //头样式
            CellStyle headStyle = wb.createCellStyle();
            Font headFont = wb.createFont();
            headFont.setColor(IndexedColors.BLACK.getIndex());
            headFont.setBold(true);
            headStyle.setFont(headFont);
            // 下边框
            headStyle.setBorderBottom(BorderStyle.THIN);
            // 左边框
            headStyle.setBorderLeft(BorderStyle.THIN);
            // 上边框
            headStyle.setBorderTop(BorderStyle.THIN);
            // 右边框
            headStyle.setBorderRight(BorderStyle.THIN);

            //内容样式
            CellStyle dataStyle = wb.createCellStyle();
            // 下边框
            dataStyle.setBorderBottom(BorderStyle.THIN);
            // 左边框
            dataStyle.setBorderLeft(BorderStyle.THIN);
            // 上边框
            dataStyle.setBorderTop(BorderStyle.THIN);
            // 右边框
            dataStyle.setBorderRight(BorderStyle.THIN);

            //设置为超链接的样式
            CellStyle linkStyle = wb.createCellStyle();
            Font cellFont = wb.createFont();
            cellFont.setUnderline(Font.U_SINGLE);
            cellFont.setColor(IndexedColors.BLUE.getIndex());
            linkStyle.setFont(cellFont);
            // 下边框
            linkStyle.setBorderBottom(BorderStyle.THIN);
            // 左边框
            linkStyle.setBorderLeft(BorderStyle.THIN);
            // 上边框
            linkStyle.setBorderTop(BorderStyle.THIN);
            // 右边框
            linkStyle.setBorderRight(BorderStyle.THIN);

            //设置为超链接的样式（其他）
            CellStyle linkStyle2 = wb.createCellStyle();
            linkStyle2.setFont(cellFont);

            //设置表头信息
            int sheetHeaderNum = sheetHeader.get(m).size();
            for (int i = 0; i < sheetHeaderNum; i++) {
                Cell headerCell = headerRow.createCell(i);
                headerCell.setCellStyle(headStyle);
                // 设置cell的值
                headerCell.setCellValue(sheetHeader.get(m).get(i));
                headerCell.setCellStyle(headStyle);
            }

            //设置表内数据，从第二行开始
            int rowIndex = 1;
            int sheetDataNum = sheetData.get(m).size();
            for (int i = 0; i < sheetDataNum; i++) {
                List<Object> dataList = sheetData.get(m).get(i);
                //动态创建行
                Row row = sheet.createRow(rowIndex);
                //列大小
                int columnSize = dataList.size();
                for (int q = 0; q < columnSize; q++) {
                    // 创建第i个单元格
                    Cell cell = row.createCell(q);
                    cell.setCellValue(String.valueOf(dataList.get(q)).replace("null", ""));
                    cell.setCellStyle(dataStyle);
                    //sheet.setColumnWidth(q, (80 * 50));
                    sheet.autoSizeColumn(q);

                    //第一个sheet页，最后一列为链接地址;只链接已有的sheet
                    if (m == 0 && q == columnSize - 1 && rowIndex < sheetNum) {
                        Hyperlink hyperlink = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
                        // "#"表示本文档    "页面"表示sheet页名称  "A1"表示第几列第几行
                        hyperlink.setAddress("#" + sheetName.get(rowIndex) + "!A1");
                        cell.setHyperlink(hyperlink);
                        cell.setCellStyle(linkStyle);
                    }
                    //第二行最后一列外添加一个名称链接到第一个sheet页
                    if (m != 0 && q == columnSize - 1 && rowIndex == 1) {
                        // 创建第i个单元格
                        Cell cellOut = row.createCell(columnSize);
                        cellOut.setCellValue(sheetName.get(0));
                        sheet.setColumnWidth(columnSize, (80 * 50));
                        Hyperlink hyperlink = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
                        // "#"表示本文档    "页面"表示sheet页名称  "A1"表示第几列第几行
                        hyperlink.setAddress("#" + sheetName.get(0) + "!A1");
                        cellOut.setHyperlink(hyperlink);
                        cellOut.setCellStyle(linkStyle2);
                    }
                }
                rowIndex++;
            }
            // 处理中文不能自动调整列宽的问题
            setSizeColumn(sheet, sheetDataNum);
        }
        return wb;
    }


    /**
     * 自适应宽度(中文支持)
     *
     * @param sheet 表
     * @param size  表数据大小
     */
    private static void setSizeColumn(Sheet sheet, int size) {
        for (int columnNum = 0; columnNum < size; columnNum++) {
            int columnWidth = sheet.getColumnWidth(columnNum) / 256;
            for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
                Row currentRow;
                //当前行未被使用过
                if (sheet.getRow(rowNum) == null) {
                    currentRow = sheet.createRow(rowNum);
                } else {
                    currentRow = sheet.getRow(rowNum);
                }

                if (currentRow.getCell(columnNum) != null) {
                    Cell currentCell = currentRow.getCell(columnNum);
                    if (currentCell.getCellTypeEnum() == STRING) {
                        int length = currentCell.getStringCellValue().getBytes().length;
                        if (columnWidth < length) {
                            columnWidth = length;
                        }
                    }
                }
            }
            sheet.setColumnWidth(columnNum, columnWidth * 256);
        }
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
