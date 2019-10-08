package violet.manager;

import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ExcelWriteManager {

    //构造一个默认工作簿对象
    private SXSSFWorkbook workbook = new SXSSFWorkbook();

    //当前 Sheet 索引值
    private int atIndex;

    //当前选中 SheetPojo 对象,里面包含了 Sheet 的基本参数
    private SheetPojo atPojo;

    //创建的 Sheet 容器，方便快速选择
    private LinkedList<SheetPojo> infos = new LinkedList<>();

    /**
     * 默认构造，并初始化 Sheet 对象
     */
    public ExcelWriteManager() {
        this.createSheetPojo();
        workbook.setActiveSheet(0);
        atIndex = 0;
    }

    /**
     * 设置当前需要操作的 Sheet 对象
     * @param index Sheet 在数据对象中的索引
     * @return 是否成功改变 Sheet 对象
     */
    public boolean setAtSheetPojo(int index){
        if(index != atIndex && infos.size() < index){
            this.atIndex = index;
            this.atPojo = infos.get(index);
            return true;
        }
        return false;
    }

    /**
     * 创建新的 Sheet 对象
     */
    public void createSheetPojo(){
        SheetPojo sheetInfo = new SheetPojo(workbook.createSheet());
        infos.add(sheetInfo);
        atPojo = sheetInfo;
        ++this.atIndex;
    };

    /**
     * 设置当前 Sheet 名称
     * @param name Sheet 名称
     * @return 链式结构
     */
    public ExcelWriteManager setSheetName(String name){
        workbook.setSheetName(atIndex,name);
        return this;
    }

    public void setSheetColumnNames(String[] names){
        if(names == null || names.length == 0){
            return;
        }
        atPojo.setColumnNames(names);
    }

    public void addRowData(List<?> list){
        for (Object item : list){
            if(item.getClass().isArray()){
                String [] arr = (String[]) item;
                atPojo.addData(arr);
            }
            else if(item instanceof List){
                List<String> itemClass = (List<String>) item;
                String[] arr = (String[]) itemClass.toArray();
                atPojo.addData(arr);
            }
        }
    }

    public void Write(File file) throws IOException {
        for (SheetPojo item : infos){
            item.close();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        workbook.write(fileOutputStream);
        fileOutputStream.close();
    }

    private class SheetPojo{

        private SXSSFSheet Sheet;

        private int rowIndex = 0;

        private String[] fields = null;

        private LinkedList<String[]> rowData = new LinkedList<>();

        public SheetPojo(SXSSFSheet sheet) {
            this.Sheet = sheet;
        }

        public void setColumnNames(String[] names){
            if(this.fields == null){
                this.rowIndex++;
            }
            this.fields = names;
        }

        public void addData(String[] item){
            rowData.add(item);
            this.rowIndex++;
        }

        public void close(){
            int startIndex = 0;
            if(fields != null ){
                startIndex++;
                SXSSFRow row = Sheet.createRow(0);
                for (int i = 0; i < fields.length; i++) {
                    row.createCell(i).setCellValue(fields[i]);
                }
            }
            for (int i = 0; i < rowData.size(); i++) {
                SXSSFRow row = Sheet.createRow(i+startIndex);
                String[] strings = rowData.get(i);
                for (int j = 0; j < strings.length; j++) {
                    row.createCell(j).setCellValue(strings[j]);
                }
            }
        }

    }

}