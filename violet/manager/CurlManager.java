package violet.manager;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;

public class CurlManager {

    /**
     * 获取网页数据
     * @param url   目标网站地址
     * @return
     * @throws IOException
     */
    public static String getHttpInterface(String url) throws IOException {

        URLConnection curl = getURLConnection(new URL(url));

        //html 数据输入流
        InputStream in = curl.getInputStream();

        //通过输入流,获取文本数据
        String value = getHttpInputStreamValue(in);

        //关闭输入流
        in.close();

        return value;
    }

    /**
     * 发送 POST 到指定地址
     * @param url   需要访问的地址
     * @param postData  Post 数据
     */
    public static String sendHttpPost(String url , Map<String,Object> postData) throws IOException {
        return sendHttpPost(url, CurlManager.asMapToParam(postData));
    }

    public static String sendHttpPost(String url , String postData) throws IOException{
        URLConnection curl = getURLConnection(new URL(url));

        PrintWriter out = new PrintWriter(curl.getOutputStream());

        //发送请求参数
        out.print( postData );

        // flush输出流的缓冲
        out.flush();

        //html 数据输入流
        InputStream in = curl.getInputStream();

        //通过输入流,获取文本数据
        String value = getHttpInputStreamValue(in);

        //关闭输入输出
        out.close();
        in.close();

        return value;
    }

    /**
     * 获取通用连接器
     * @param url 连接地址
     */
    private static URLConnection getURLConnection(URL url) throws IOException {

        // 打开和 URL 之间的连接
        URLConnection conn = url.openConnection();

        // 设置通用的请求属性
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Charset", "utf-8");

        // 发送POST请求必须设置如下两行
        conn.setDoOutput(true);
        conn.setDoInput(true);

        return conn;
    }

    /**
     * 将 Mmp 对象转换成 POST 需要的格式（name1=value1&name2=value2）
     * @param postData map 对象
     */
    private static String asMapToParam(Map<String,Object> postData){
        Set<? extends Map.Entry<String, Object> > entries = postData.entrySet();
        StringBuffer buffer = new StringBuffer();
        for (Map.Entry<String, ?> item : entries){
            String key = item.getKey();
            String value = item.getValue().toString();
            buffer.append(key).append("=").append(value).append('&');
        }
        String substring = buffer.substring(0, buffer.length() - 1);
        return substring;
    }

    /**
     * 通过 URL 输入流，获取地址的文本数据
     * @param input 输入流
     */
    private static String getHttpInputStreamValue(InputStream input) throws IOException {
        StringBuffer result = new StringBuffer();

        //读取URL的响应
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        String line;
        while ((line = in.readLine()) != null) {
            result.append(line);
        }

        return result.toString();
    }

}
