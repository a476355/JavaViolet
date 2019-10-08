package violet.manager;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import violet.document.DateAdapter;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Double.valueOf;

public class ExcelReadManager {

    private Workbook workbook;

    public ExcelReadManager(){}

    public ExcelReadManager(File excel) throws IOException, InvalidFormatException {
        workbook = new XSSFWorkbook(excel);
    }

    public ExcelReadManager(String absFilePath) throws IOException, InvalidFormatException {
        workbook = new XSSFWorkbook(new File(absFilePath));
    }

    public void setWorkbook(File excel) throws IOException, InvalidFormatException {
        workbook = new XSSFWorkbook(excel);
    }

    /**
     * 根据索引索取 Sheet 名称
     * @param index 索引值
     * @return
     */
    public String getSheetName(int index){
        return workbook.getSheetName(index);
    }

    /**
     * 获取工作簿(Sheet) 索引值和名称
     *
     * @return
     */
    public String[] getSheetNames() {
        int numberOfSheets = workbook.getNumberOfSheets();
        String[] names = new String[numberOfSheets];
        for (int i = 0; i < numberOfSheets; i++) {
            names[i] = workbook.getSheetName(i);
        }
        return names;
    }

    public Sheet getSheet(int index){
        return workbook.getSheetAt(index);
    }

    /**
     * 快速获取 Excel 第一行数据
     * @param index
     * @return
     */
    public List<String> getColumnInfo(int index) {

        //获取第一行数据
        Sheet sheet = workbook.getSheetAt(index);
        Row row = sheet.getRow(sheet.getFirstRowNum());

        //获取当前行索引长度
        int rowIndex = row.getLastCellNum();

        //保存行数据
        ArrayList<String> rows = new ArrayList<>();

        //遍历当前行数据
        for (int j = 0; j < rowIndex; j++) {
            String value = getCellFormatValue(row.getCell(j));
            rows.add(value);
        }

        return rows;
    }

    /**
     * 获取 指定单元格中的数据
     * @param sheet
     * @param rowIndex 行号
     * @param colIndex 列索引，字母类型（ 参考excel中的列值如 'A' 'B'）
     * @return
     */
    public String getCellValue(Sheet sheet,int rowIndex, String colIndex){
        return  getCellValue(sheet,rowIndex,letterToNumber(colIndex));
    }

    /**
     * 获取 指定单元格中的数据
     * @param sheet
     * @param rowIndex      行索引
     * @param colIndex      列索引,整数类型
     * @return
     */
    public String getCellValue(Sheet sheet,int rowIndex, int colIndex){
        Cell cell = sheet.getRow(rowIndex).getCell(colIndex);
        return getCellFormatValue(cell);
    }

    /**
     * 获取 Sheet 字段的最大值
     * @param index     Sheet 的索引值
     * @param row       需要获取哪一行的最大值
     * @return
     */
    public int getSheetLastCellNum(int index,int row){
        return workbook.getSheetAt(index).getRow(row).getLastCellNum();
    }

    /**
     * 获取Excel工作簿数据,默认获取索引为 0 的工作簿全部数据
     * @return
     */
    public List<List<String>> getSheetList() {
        return getSheetList(0, 0);
    }

    /**
     * 根据指定索引获取 Excel Sheet 里的所有数据
     * @param index     工作簿索引值
     * @return
     */
    public List<List<String>> getSheetList(int index) {
        return getSheetList(index, 0);
    }

    /**
     * 根据指定索引获取 Excel Sheet 里的数据
     * @param index     工作簿索引值
     * @param start     row 默认值(默认开始行)
     * @return
     */
    public List<List<String>> getSheetList(int index, int start) {
        Sheet sheet = workbook.getSheetAt(index);
        return SheetAsList(sheet, start, sheet.getLastRowNum());
    }

    /**
     * 根据指定索引获取 Excel Sheet 里的数据
     * @param index     工作簿索引值
     * @param start     row 默认值(默认开始行)
     * @param count     行数限制(数据总量)
     * @return
     */
    public List<List<String>> getSheetList(int index, int start, int count) {
        Sheet sheet = workbook.getSheetAt(index);
        int maxRow = sheet.getLastRowNum();
        int length = start + count - 1;
        if (length < maxRow) {
            return SheetAsList(sheet, start, length);
        } else {
            return SheetAsList(sheet, start, maxRow);
        }
    }

    private List<List<String>> SheetAsList(Sheet sheet, int start, int maxRow) {
        ArrayList<List<String>> trs = new ArrayList<>();
        for (int i = start; i <= maxRow; i++) {
            ArrayList<String> td = new ArrayList<>();

            //获取当前行数据
            Row row = sheet.getRow(i);

            //获取当前行索引长度
            int rowIndex = row.getLastCellNum();

            //便利当前行数据
            for (int j = 0; j < rowIndex; j++) {
                String value = getCellFormatValue(row.getCell(j));
                td.add(value);
            }
            trs.add(td);
        }
        return trs;
    }

    /**
     * 以垂直方向获取数据,（String[]）大小根据数据表中第一行的字段来决定的
     * @param index
     * @return
     */
    public List<String[]> getSheetListVertical(int index){
        Sheet sheet = workbook.getSheetAt(index);
        short lastCellNum1 = sheet.getRow(0).getLastCellNum();
        return getSheetListVertical(sheet,lastCellNum1,0);
    }

    /**
     * 以垂直方向获取数据,（String[]）大小根据数据 maxColumn
     * @param index         工作簿索引值
     * @param maxColumn     字段最大值
     * @return
     */
    public List<String[]> getSheetListVertical(int index,int maxColumn){
        Sheet sheet = workbook.getSheetAt(index);
        return getSheetListVertical(sheet,maxColumn,0);
    }

    public List<String[]> getSheetListVertical(int index,int maxColumn ,int startRow){
        Sheet sheet = workbook.getSheetAt(index);
        return getSheetListVertical(sheet,maxColumn,startRow);
    }

    /**
     * 以垂直方向获取数据, 通过数组来指定获取特定字段中的数据
     * @param index         工作簿索引值
     * @param columns       字段对应的索引
     * @return
     */
    public List<String[]> getSheetListVertical(int index,int[] columns){
        return getSheetListVertical(index,columns,0);
    }

    public List<String[]> getSheetListVertical(int index,int[] columns ,int startRow){
        Sheet sheet = workbook.getSheetAt(index);
        return getSheetListVertical(sheet,columns,startRow);
    }

    private List<String[]> getSheetListVertical(Sheet sheet,int maxColumn,int startRow){
        LinkedList<String[]> tables = new LinkedList<>();

        //获取 Sheet 表中一共有多少行
        int lastRowNum = sheet.getLastRowNum()+1;
        int maxRow = lastRowNum - startRow;
        if(maxRow < 0){
            maxRow = 0;
        }

        for(int c = 0 ; c < maxColumn;c++){
            tables.add(new String[maxRow]);
        }

        //获取 Sheet 表中一共有多少列
        int last = maxColumn;

        for(int i = startRow ; i < lastRowNum;i++){
            Row row = sheet.getRow(i);
            for(int c = 0 ; c < last;c++){
                String[] strings = tables.get(c);
                strings[i-startRow] = getCellFormatValue(row.getCell(c));
            }
        };

        return tables;
    }

    private List<String[]> getSheetListVertical(Sheet sheet,int[] columns,int startRow){
        //获取 Sheet 表中一共有多少行
        int lastRowNum = sheet.getLastRowNum();

        //实际需要读取的行数
        int maxRow = lastRowNum - startRow;
        if(maxRow < 0){
            maxRow = 0;
        }

        String[][] list = new String[columns.length][maxRow];

        for(int i = startRow ; i < lastRowNum;i++){
            Row row = sheet.getRow(i);
            for(int c = 0 ; c < columns.length;c++){
                list[c][i-startRow] = getCellFormatValue(row.getCell(columns[c]));
            }
        };

        List<String[]> ListVertical = new LinkedList<>();
        Collections.addAll(ListVertical, list);

        return ListVertical;
    }

    public void close(){
        try {
            workbook.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * 获取Excel 时间值
     * @param value 需要转换的日期格式
     * @return
     */
    public static String getAsExcelDate(String value){
        try {
            return getAsExcelDate(valueOf(value).intValue());
        }catch (NumberFormatException Exc){
            value = value.replaceAll("/", "-");
            boolean matches = Pattern.matches("[0-9]{4}-[0-9]{1,2}-?[0-9]{0,2}", value);
            if(matches){
                return value;
            }else{
                throw new NumberFormatException("Excel cell dateValue Error.");
            }
        }

    }

    public static String getAsExcelDate(Float value){
        return getAsExcelDate(valueOf(value).intValue());
    }

    /**
     * 格式化 Excel 里的数据
     * @param value
     * @return
     */
    public static String getAsExcelDate(long value){
        long dataTime = ( (value-25569) * 86400) * 1000;
        return DateAdapter.getDateFormat(new Date(dataTime));
    }

    /**
     * 批量时间转换
     * @param       values    Number 的字符数组
     * @return      yyyMMdd 格式的日期字符数组
     */
    public static String[] getAsExcelDates(String[] values){
        String[] datesYmd  = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            datesYmd[i] = getAsExcelDate(values[i]);
        }
        return  datesYmd;
    }

    /**
     * 将以字母表示的Excel列数转换成数字表示
     * @param letter    字母值
     * @return
     */
    public static int letterToNumber(String letter) {
        // 检查字符串是否为空
        if (letter == null || letter.isEmpty()) {
            return -1;
        }
        String upperLetter = letter.toUpperCase(); // 转为大写字符串
        int num = 0; // 存放结果数值
        int base = 1;
        // 从字符串尾部开始向头部转换
        for (int i = upperLetter.length() - 1; i >= 0; i--) {
            char ch = upperLetter.charAt(i);
            num += (ch - 'A' + 1) * base;
            base *= 26;
            if (num > Integer.MAX_VALUE) { // 防止内存溢出
                return -1;
            }
        }
        return num-1;
    }

    /**
     * 将数字转换成以字母表示的Excel列数
     * @param num   需要转换的整数
     * @return
     */
    public static String numberToLetter(int num) {
        if (num < 0) { // 检测列数是否正确
            return null;
        }else{
            num++;
        }

        StringBuffer letter = new StringBuffer();
        do {
            --num;
            int mod = num % 26; // 取余
            letter.append( (char) (mod+'A')); // 组装字符串
            num = (num - mod) / 26; // 计算剩下值
        } while (num > 0);
        return letter.reverse().toString(); // 返回反转后的字符串
    }

    /**
     * 获取 Excel 单元里的数据
     * @param cell
     * @return
     */
    private String getCellFormatValue(Cell cell) {

        String cellValue = "";

        if (cell == null) {
            return cellValue;
        }
        CellType cellType = cell.getCellType();

        switch (cellType) {
            case STRING:
                cellValue = cell.getRichStringCellValue().getString();
                break;
            case NUMERIC: //数字
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            case FORMULA: //公式
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            case BLANK: //空值
                break;
            case BOOLEAN: //Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
            case ERROR: //故障
                cellValue = "error value";
                break;
            default:
                cellValue = "undefined";
                break;
        }
        return cellValue;
    }

}
