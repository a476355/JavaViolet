package violet.document;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;

/**
 * 自定义 *.ini 文件读取工具
 *  支持一级命名空间,格式为 [*]
 *  数据内容为 * = * ,没有结束符号默认结束符号为换行符
 *  "#" 这个符号为注释符， # 打头的数据都不会被解析
 */
public class PropertiesTools {

    public final static String namespace = "INI_TPM";

    public static LinkedHashMap<String,LinkedHashMap<String,String>> readProperties(String classPath) throws IOException {
        return readProperties(FileTools.readFileBytes(classPath));
    }

    public static LinkedHashMap<String,LinkedHashMap<String,String>> readProperties(File file) throws IOException {
        return readProperties(FileTools.readFileBytes(file));
    }

    public static LinkedHashMap<String, LinkedHashMap<String, String>> readProperties(InputStream openStream) throws IOException {
        return readProperties(openStream.readAllBytes());
    }

    private static LinkedHashMap<String,LinkedHashMap<String,String>> readProperties(byte[] bytes){
        String str = new String(bytes);

        //ini 数据集合
        LinkedHashMap<String,LinkedHashMap<String,String>> hashMap = new LinkedHashMap<>();

        //[] 命名空间对象
        LinkedHashMap<String, String> addMap = new LinkedHashMap<>();

        //默认 命名空间
        hashMap.put(namespace,addMap);

        //单行数据处理
        String[] split = str.split("\\r\\n|\\n");
        for (String tr : split){
            tr = tr.replaceAll("^\\s*(.*)\\s*$", "$1");
            if(tr.equals("")){
                continue;
            }

            String firstString = tr.substring(0, 1);
            switch (firstString){
                case "//":
                case "#":
                    continue;
                case "[":
                    if(tr.lastIndexOf("]") >= 0){
                        String key = tr.replaceAll("[\\s||\\[||\\]]", "");
                        addMap = new LinkedHashMap<>();
                        hashMap.put(key,addMap);
                        break;
                    }
                default:
                    String[] enu = tr.split("\\s*=\\s*");
                    addMap.put(enu[0],enu[1]);
            }
        }

        return hashMap;
    }


}
