package violet.document;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;


public class FileTools {

    private static URL relativePath = FileTools.class.getResource("");

    /**
     * 创建 File 文件实列,并生成对应的目录与文件
     */
    public static File createFile(String filePath) throws IOException {
        filePath = filePath.replace("/","\\");
        File file = new File(filePath);
        return createFile(file);
    }

    /**
     * 创建 File 文件实列,并生成对应的目录与文件
     */
    public static File createFile(File file) throws IOException {
        if(!file.exists()){
            String filePath = file.getAbsolutePath();
            String substring = filePath.substring(0, filePath.lastIndexOf("\\"));

            File dirs = new File(substring);
            if(dirs.exists()){
                return file;
            }else {
                if(dirs.mkdirs() && file.createNewFile()){
                    return file;
                }else{
                    throw new IOException("directory creation failed:"+substring);
                }
            }
        }
        return file;
    }

    /**
     * 获取当问目录中指定后缀的文件
     * @param dir       文件目录
     * @param suffix    文件类型,也指文件后缀名 如 *.xml
     */
    public static LinkedList<File> getFiles(String dir, String suffix){
        LinkedList<File> fileList = new LinkedList<>();
        File[] files = new File(dir).listFiles();
        for (File item : files){
            if(item.isFile()){
                String name =  item.getName();
                if(name.endsWith(suffix)){
                    fileList.add(item);
                }
            }
        }
        return fileList;
    }

    /**
     * 递归获取 指定后缀的文件
     * @param dir
     * @param suffix
     * @return
     */
    public static ArrayList<File> getDirsFiles(String dir, String suffix){

        File rootFile = new File(dir);

        LinkedList<File> dirs = new LinkedList<>();

        ArrayList<File> document = new ArrayList<>();

        //遍历根目录
        if(rootFile.isDirectory()){
            File[] files = rootFile.listFiles();
            if(files == null){
                return document;
            }
            for (File file : files){
                if(file.isDirectory()){
                    dirs.add(file);
                }
                else if(file.isFile()){
                    document.add(file);
                }
            }
        }

        //递归 子目录
        while ( dirs.size() > 0 ){
            File dirFile = dirs.pollLast();
            for (File file : Objects.requireNonNull(Objects.requireNonNull(dirFile).listFiles())){
                if(file.isDirectory()){
                    dirs.add(file);
                }
                else if(file.isFile()){
                    document.add(file);
                }
            }
        }

        //剔除 不需要的文件
        for (int index = document.size()-1; index >= 0; index--) {
            if(!document.get(index).getName().endsWith(suffix)){
                document.remove(index);
            }
        }

        return document;
    }

    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static String readFileByLines(File file) throws IOException {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while (true){
            String s = reader.readLine();
            if(s == null){
                break;
            }else{
                buffer.append(s).append("\n");
            }
        }
        reader.close();
        return buffer.toString();
    }

    /**
     * 以流数据形式 读取文件数据
     */
    public static byte[] readFileBytes(File file) throws IOException {
        if(file.isFile()){
            return new FileInputStream(file).readAllBytes();
        }else {
            throw new IOException("File type error or file non-existent\n\t File path equal to : "+file.getAbsolutePath());
        }
    }

    public static byte[] readFileBytes(String classPath) throws IOException {
        URL url = new URL(relativePath, "../../" + classPath);

        InputStream inputStream = url.openStream();

        byte[] bytes = inputStream.readAllBytes();

        inputStream.close();

        return bytes;
    }

    /**
     * 通过覆盖的方式 向文件中写入字符流
     */
    public static int coverOutFile(File file, String text) throws IOException {
        if(!file.exists()){
            createFile(file);
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file),text.length());
        writer.write(text);
        writer.flush();
        writer.close();
        return text.length();
    }

    /**
     * 通过覆盖的方式 向文件中写入字节流
     */
    public static int coreOutFile(File file, byte[] bytes) throws IOException {
        if(!file.exists()){
            createFile(file);
        }
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file), bytes.length);
        out.write(bytes);
        out.flush();
        out.close();
        return bytes.length;
    }

    public static long coverOutFile(String filePath, String text) throws IOException {
        File file = new File(filePath);
        return coverOutFile(file,text);
    }

    public static double toFormatFileSize(long fileS, SizeField value) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (value) {
            case SIZE_TYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case SIZE_TYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                break;
            case SIZE_TYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case SIZE_TYPE_GB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

    public enum  SizeField{

        //最常用的完整日期格式
        SIZE_TYPE_B,
        SIZE_TYPE_KB,
        SIZE_TYPE_MB,
        SIZE_TYPE_GB
    }

}
