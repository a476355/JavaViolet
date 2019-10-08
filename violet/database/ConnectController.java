package violet.database;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import violet.PATH;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Set;

/**
 * 数据库连接参数分析器,主要功能是负责解析 XML 配置文件
 */
public class ConnectController {

    private HashMap<String, HashMap<String, String>> jdbcLink = new HashMap<>();

    private HashMap<String, Connection> connectionPond = new HashMap<>();

    public ConnectController() throws Exception{
        String classPath = PATH.resolveName(ConnectController.class);
        String xmlPath = classPath + "xml/jdbc-link.xml";
        this.setClassXmlPath(xmlPath);
    }

    /**
     * 传出指定路径 解析数据库连接XML文件
     * @param classPath
     */
    public  ConnectController(String classPath) throws Exception{
         this.setClassXmlPath(classPath);
    }

    /**
     * 加载驱动,有SPI机制的可以忽略这个步骤
     *
     * @param DrivePackage 接口实现类的全路径名
     */
    public static void loadDrive(String DrivePackage) throws ClassNotFoundException {
        Class.forName(DrivePackage);
    }

    /**
     * 获取连接参数 的所有ID名称
     * @return
     */
    public Set<String> getLinkIdNames(){
         return this.jdbcLink.keySet();
    }

    /**
     * 数据库连接对象 - 单列模式 每个ID对应一个独立连接对象
     * @param linkID   xml 文件中的配置ID
     * @return
     * @throws SQLException
     */
    public Connection getConnection(String linkID) throws SQLException {

        Connection conn = connectionPond.get(linkID);

        if(conn == null){

            //通过ID 获取数据库连接参数
            HashMap<String, String> map = jdbcLink.get(linkID);

            //创建数据库连接对象
            conn = DriverManager.getConnection(map.get("url"), map.get("user"), map.get("password"));

            //连接对象存入缓存库
            connectionPond.put(linkID,conn);
        }

        return conn;
    }

    /**
     * 数据库连接对象 每次都新建连接对象 - 为多线程预留的
     * @param linkID
     * @return
     * @throws SQLException
     */
    public Connection createConnection(String linkID) throws SQLException {

        //通过ID 获取数据库连接参数
        HashMap<String, String> map = jdbcLink.get(linkID);

        //创建数据库连接对象
        return DriverManager.getConnection(map.get("url"), map.get("user"), map.get("password"));
    }

    /**
     * 返回 mysql 的链接参数
     * @param linkID
     * @return
     */
    public HashMap<String,String> getLinkMap(String linkID){
        HashMap<String, String> HashMap = this.jdbcLink.get(linkID);
        return HashMap;
    }

    /**
     * 关闭数据库连接对象
     * @param linkID
     * @return
     * @throws SQLException
     */
    public boolean closeConnection(String linkID) throws SQLException {
        Connection remove = connectionPond.remove(linkID);
        if(remove == null){
            return  true;
        }
        else{
            remove.close();
            return remove.isClosed();
        }
    };

    /**
     * 获取 Statement 对象
     * @param linkID    xml文件中的配置ID
     * @return
     * @throws SQLException
     */
    public Statement getStatement(String linkID) throws SQLException {
        return getConnection(linkID).createStatement();
    }

    /**
     * 获取 Statement 对象 , ResultSet 结果集可滚动
     * @param linkID   xml文件中的配置ID
     * @return
     * @throws SQLException
     */
    public Statement getRollStatement(String linkID) throws SQLException {
        return getConnection(linkID).createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
    }

    /**
     * 获取预编译的 Statement 对象
     * @param linkID   xml文件中的配置ID
     * @param sql       预编译的SQL语句
     * @return
     * @throws SQLException
     */
    public PreparedStatement getPrepareStatement(String linkID, String sql) throws SQLException {
        return getConnection(linkID).prepareStatement(sql);
    }

    /**
     * 解析xml配置文件,获取数据库连接参数
     *
     * @param xmlPath xml配置文件路径
     */
    private void setClassXmlPath(String xmlPath) throws ParserConfigurationException, IOException, SAXException {

        //获取XML文件输入流
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(xmlPath);

        //DOM 对象分析器
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        //DOM 节点对象
        NodeList list = documentBuilder.parse(in).getFirstChild().getChildNodes();

        //遍历 DOM 节点
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node.getNodeType() == 1) {
                //根据节点名称分配解析方法
                String name = node.getNodeName();
                switch (name) {
                    case "mysql":
                        addLinkJDBC(node);
                        break;
                    case "sqlserver":
                        addLinkJDBC(node);
                        break;
                    default:
                        break;
                }
            }
        }

    }

    /**
     * 分析 mysql 元素节点
     *
     * @param node
     */
    private void addLinkJDBC(Node node) {

        HashMap map = new HashMap<String, String>();

        //解析XML数据 获取连接参数
        NodeList dbList = node.getChildNodes();
        for (int i = 0; i < dbList.getLength(); i++) {
            Node n = dbList.item(i);
            if (n.getNodeType() == 1) {
                String name = n.getNodeName();
                String value = elementCDATA(n);
                map.put(name, value);
            }
        }

        //获取当前元素的id
        String dbName = node.getAttributes().getNamedItem("id").getNodeValue();

        //添加连接类型声明
        map.put("type", node.getNodeName());

        //将连接参数添加进集合中
        jdbcLink.put(dbName, map);
    }

    /**
     * 分析 CDATA 元素节点
     *
     * @param node
     * @return 节点文本值 如果没有值则返回 ""
     */
    private String elementCDATA(Node node){
        NodeList list = node.getChildNodes();
        if (list == null) {
            return "";
        }
        for (int i = 0; i < list.getLength(); i++) {
            Node n = list.item(i);
            if (n.getNodeType() == 4) {
                return n.getNodeValue();
            }
        }
        return "";
    }

}
