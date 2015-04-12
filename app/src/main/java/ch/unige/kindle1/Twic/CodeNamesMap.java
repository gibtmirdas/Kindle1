package ch.unige.kindle1.Twic;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by thomas on 3/1/15.
 */
public class CodeNamesMap {

    private static Map<String,CodeName> codeNames = new HashMap<>();

    public static void put(String code, CodeName name){
        codeNames.put(code, name);
    }

    public static String getNameFromCode(String code){
        return codeNames.get(code).getName();
    }

    public static String getCodeFromName(String name){
        for(CodeName c:codeNames.values()){
            if(c.getName().equals(name))
                return c.getCode();
        }
        return "fr";
    }
}