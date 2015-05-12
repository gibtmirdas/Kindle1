package ch.unige.Twic.Twic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 3/1/15.
 */
public class PairsList{

    private static List<LanguagePair> listPairs = new ArrayList<>();

    public static void add(LanguagePair lp){
        listPairs.add(lp);
    }

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

    public static List<String> getSrcList(boolean named){
        List<String> tmp = new ArrayList<>();
        for(LanguagePair lp: listPairs)
            if(!tmp.contains(lp.getSrc()))
                tmp.add(lp.getSrc());

        if(named)
            return convertListCodeToName(tmp);
        return tmp;
    }

    public static List<String> getTgtBySrcId(int id){
        String src = getSrcList(false).get(id);
        return getTgtFromSrc(src,true);
    }

    private static List<String> convertListCodeToName(List<String> tmp){
        for(int i=0; i<tmp.size(); i++){
            tmp.set(i, CodeNamesMap.getNameFromCode(tmp.get(i)));
        }
        return tmp;
    }

    public static boolean containsKey(String key){
        for (LanguagePair lp: listPairs)
            if(lp.getSrc().equals(key))
                return true;
        return false;
    }

    public static int[] getIndexesForPair(String srcLg, String dstLg){
        int[] indexes = new int[2];
        List<String> srcList = getSrcList(true);
        String nameFromCode = CodeNamesMap.getNameFromCode(srcLg);
        indexes[0] = srcList.indexOf(nameFromCode);
        indexes[1] = getTgtFromSrc(srcLg, true).indexOf(CodeNamesMap.getNameFromCode(dstLg));
        return indexes;
    }

    public static boolean containsCouple(LanguagePair couple){
        for(LanguagePair lp: listPairs)
            if(lp.equals(couple))
                return true;
        return false;
    }
}
