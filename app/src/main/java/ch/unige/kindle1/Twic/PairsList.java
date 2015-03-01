package ch.unige.kindle1.Twic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
