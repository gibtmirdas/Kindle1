package ch.unige.Twic.Twic;

import java.util.HashMap;
import java.util.Map;

/**
 * This Object store a map of all "language" nodes contained in the XML file "languagelist" available at the address: "http://latlapps.unige.ch/Twicff?act=ll"
 */
public class CodeNamesMap {

    /**
     * Map containing the {@link ch.unige.Twic.Twic.CodeName}.<br/>
     * Key: code<br/>
     * Value: the {@link ch.unige.Twic.Twic.CodeName} object
     */
    private static Map<String,CodeName> codeNames = new HashMap<>();

    /**
     * Add a {@link ch.unige.Twic.Twic.CodeName} to the map
     * @param code code of the language
     * @param name name of the language
     */
    public static void put(String code, CodeName name){
        codeNames.put(code, name);
    }

    /**
     * Give the name of a language identified by its code
     * @param code code to convert
     * @return name of the language
     */
    public static String getNameFromCode(String code){
        return codeNames.get(code).getName();
    }

    /**
     * Give the code of a language identified by its name
     * @param name name to convert
     * @return code of the language
     */
    public static String getCodeFromName(String name){
        for(CodeName c:codeNames.values()){
            if(c.getName().equals(name))
                return c.getCode();
        }
        return "fr";
    }

    /**
     * Identify if the list of codeNames is empty
     * @return if the list is empty
     */
    public static boolean isEmpty() {
        return codeNames.isEmpty();
    }
}