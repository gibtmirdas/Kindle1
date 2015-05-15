package ch.unige.Twic.language;

import java.util.ArrayList;
import java.util.List;

/**
 * This Object store a list of all "pair" nodes contained in the XML file "languagelist" available at the address: "http://latlapps.unige.ch/Twicff?act=ll"
 */
public class PairsList{

    /**
     * The list of {@link LanguagePair}
     */
    private static List<LanguagePair> listPairs = new ArrayList<>();

    /**
     * Add a {@link LanguagePair} to the list
     * @param lp {@link LanguagePair} to add
     */
    public static void add(LanguagePair lp){
        listPairs.add(lp);
    }

    /**
     * Build the target list of language corresponding to the source language. Because of pairs, the target list is different for each source language. The parameter {@code named} define the format if the result (code list or name list)
     * @param src Source language
     * @param named get list of name or code
     * @return list of language name/code for the given source language
     */
    public static List<String> getTgtFromSrc(String src,boolean named){
        List<String> tmp = new ArrayList<>();
        for(LanguagePair lp: listPairs){
            if(lp.getSrc().equals(src))
                tmp.add(lp.getTgt());
        }

        if(named)
            return convertListCodeToName(tmp);
        return tmp;
    }

    /**
     * Build a list of all source languages. The parameter {@code named} define the format if the result (code list or name list)
     * @param named Get list of name or code
     * @return List of all source language name/code
     */
    public static List<String> getSrcList(boolean named){
        List<String> tmp = new ArrayList<>();
        for(LanguagePair lp: listPairs)
            if(!tmp.contains(lp.getSrc()))
                tmp.add(lp.getSrc());

        if(named)
            return convertListCodeToName(tmp);
        return tmp;
    }

    /**
     * Build the target list of language corresponding to the source language id (position in the PairsList) . Because of pairs, the target list is different for each source language. The parameter {@code named} define the format if the result (code list or name list)
     * @param id Position of the source language
     * @return List of target named languages
     */
    public static List<String> getTgtBySrcId(int id){
        String src = getSrcList(false).get(id);
        return getTgtFromSrc(src,true);
    }

    /**
     * Convert a code list to a name list
     * @param tmp Code list
     * @return Name list
     */
    private static List<String> convertListCodeToName(List<String> tmp){
        for(int i=0; i<tmp.size(); i++){
            tmp.set(i, CodeNamesMap.getNameFromCode(tmp.get(i)));
        }
        return tmp;
    }

    /**
     * Identify if the {@link LanguagePair} list contains a specific language as source already
     * @param key language to find
     * @return if the language is already there
     */
    public static boolean containsKey(String key){
        for (LanguagePair lp: listPairs)
            if(lp.getSrc().equals(key))
                return true;
        return false;
    }

    /**
     * Build an array containing the position in the {@link PairsList} of the source language and the target associated to the source
     * @param srcLg source language code
     * @param dstLg target language code
     * @return array with the position of the source and target languager
     */
    public static int[] getIndexesForPair(String srcLg, String dstLg){
        int[] indexes = new int[2];
        List<String> srcList = getSrcList(true);
        String nameFromCode = CodeNamesMap.getNameFromCode(srcLg);
        indexes[0] = srcList.indexOf(nameFromCode);
        indexes[1] = getTgtFromSrc(srcLg, true).indexOf(CodeNamesMap.getNameFromCode(dstLg));
        return indexes;
    }

    /**
     * Identify if the {@link PairsList} already contain a {@link LanguagePair}
     * @param couple {@link LanguagePair} to test with
     * @return If the list already contains the {@link LanguagePair}
     */
    public static boolean containsCouple(LanguagePair couple){
        for(LanguagePair lp: listPairs)
            if(lp.equals(couple))
                return true;
        return false;
    }
}
