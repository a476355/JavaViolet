package violet.document;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateAdapter{

    private static Date date = new Date();

    private static SimpleDateFormat format = new SimpleDateFormat();

    public static String getDateFormat(Field pattern){
        format.applyPattern(pattern.value);
        return format.format(date);
    }

    public static String getDateFormat(Date date){
        format.applyPattern(Field.yyyMMdd.value);
        return format.format(date);
    }

    public static String stringToDate(String value){
        format.applyPattern(Field.yyyMMdd.value);
        try {
            String s = value.replaceAll("/", "-");
            return getDateFormat(format.parse(s));
        } catch (ParseException e) {
            return  value;
        }
    }

    public enum  Field{

        //最常用的完整日期格式
        ALL("yyyy-MM-dd HH:mm:ss"),

        yyyMMdd("yyyy-MM-dd"),

        //一天中的24小时 1-24
        HOUR24("k");

        Field(String s) {
            this.value = s;
        }

        private final String value;

    }
}
