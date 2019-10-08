package violet.database;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 这个一个通用工具类
 */
public class MappingTools {

    private static RowSetFactory rowSetFactory;

    /**
     * 将普通 ResultSet 转换为离线 ResultSet
     * 与数据库连接关闭后 缓存在本地内存的 RowSet 对象。
     * @param res
     * @return
     */
    public static CachedRowSet asCacheRowSet(ResultSet res) throws SQLException {
        if(rowSetFactory == null){
            rowSetFactory = RowSetProvider.newFactory();
        }
        CachedRowSet cachedRowSet = rowSetFactory.createCachedRowSet();
        cachedRowSet.populate(res);
        res.close();
        return cachedRowSet;
    }

    /**
     * 将 ResultSet 对象转换为  ArrayList<String[]>
     * @param res
     * @return
     * @throws SQLException
     */
    public static ArrayList<String[]> getAsListArray(ResultSet res) throws SQLException {

        ArrayList<String[]> group = new ArrayList<>();

        ResultSetMetaData metaData = res.getMetaData();

        int count = metaData.getColumnCount();

        while ( res.next() ){
            String[] row = new String[count];
            for (int i = 0; i < count; i++) {
                row[i] = res.getString(i+1);
            }
            group.add(row);
        };

        return  group;
    }

    /**
     * ResultSet 对象转换为 ArrayList<HashMap<String, String>>
     * @param res
     * @return
     * @throws SQLException
     */
    public static ArrayList<HashMap<String, String>> getAsListMap(ResultSet res) throws SQLException {
        ArrayList<HashMap<String,String>> list = new ArrayList<>();
        ResultSetMetaData metaData = res.getMetaData();
        int count = metaData.getColumnCount();
        while ( res.next() ){
            HashMap<String,String> rowData = new HashMap<>();
            for (int i = 1; i <= count; i++) {
                rowData.put(metaData.getColumnName(i), res.getString(i));
            }
            list.add(rowData);
        };
        return list;
    }

    /**
     * 清理表中所有数据 重置自增长值
     * @param stat
     * @param tableName     数据库表名称
     * @throws SQLException
     */
    public static void resetTable(Statement stat , String tableName) throws SQLException {
        stat.executeUpdate("TRUNCATE TABLE " + tableName);
    }

    /**
     * 删除指定的数据表(连带表结构一起删除)
     * @param stat
     * @param tableName     数据库表名称
     * @throws SQLException
     */
    public static void deleteTable(Statement stat ,String tableName) throws SQLException {
        stat.execute("DROP TABLE "+tableName);
    }

    /**
     * 获取数据表字段集合
     * @param metaData
     * @return
     * @throws SQLException
     */
    public static String[] getFields(ResultSetMetaData metaData) throws SQLException {
        //字段数量
        int count = metaData.getColumnCount();

        //获取所有的字段名称
        String[] fields = new String[count];
        for (int i = 0; i < count; i++) {
            fields[i] = metaData.getColumnLabel(i+1);
        }
        return fields;
    }

    /**
     * 将字符数组转换成 csv 格式的字符串
     * @param strings
     * @return
     */
    public static String asCSV(String[] strings){
        String files = "";
        for (String string:strings){
            files += ','+string;
        }
        String substring = files.substring(1);

        return substring;
    }

}
