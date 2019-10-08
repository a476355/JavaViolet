package violet.document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式工具
 */
public class RegExpTools {


    /**
     * 正则表达式匹配功能
     * @param pattern
     * @param find
     * @return
     */
    public static String[] getRegValues(Pattern pattern,String find){

        Matcher matcher = pattern.matcher(find);

        String[] finds = null;
        if(matcher.find()){
            int count = matcher.groupCount();
            if(count > 1){
                finds = new String[count];
                for (int i = 0; i < count ; i++) {
                    finds[i] = matcher.group(i+1);
                }
            }
        }
        return finds;
    };
}
