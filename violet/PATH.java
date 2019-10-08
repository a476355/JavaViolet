package violet;


import java.net.MalformedURLException;
import java.net.URL;

public class PATH {

    /**
     * 相对资源路径,相对位置是当前类目录
     */
    private static URL relativePath = PATH.class.getResource("");

    /**
     * 项目 CLASS 入口文件路径
     */
    public static String PATH_CLASS_ROOT = PATH.class.getProtectionDomain().getCodeSource().getLocation().getPath();

    /**
     * 工程路径 通常指 class main 所在的类
     */
    public static String PATH_SYSTEM_ROOT = System.getProperty("user.dir")+'\\';

    /**
     * 将类路径转换成文件路径
     * @param object
     * @return
     */
    public static String resolveName(Class<?> object) {
        Class<?> c = object;
        while (c.isArray()) {
            c = c.getComponentType();
        }
        String baseName = c.getPackageName();
        if (baseName != null && !baseName.isEmpty()) {
            return baseName.replace('.', '/') + "/";
        }else{
            return "";
        }
    }

    /**
     * 获取资源定位对象,支持获取 jar 包中的资源
     * @param classPath
     * @return
     * @throws MalformedURLException
     */
    public static URL getProjectURL(String classPath) throws MalformedURLException {
        return new URL( PATH.relativePath, "../"+classPath);
    }


}
