package LSH;

import LayerLSH.LayerLSHTransKey;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

/**
 * Created by weixun on 2017/10/16.
 */
public class LSHSearchNeighbor {

    public static ArrayList<Integer> searchNeighbor(HashMap<String, ArrayList<Integer>> aLSHHashMap, BitSet bs,
                                                    String key, int m, int floorT, int listSize){
        ArrayList<Integer> neighborBuckets = new ArrayList<>();
        for(int i=0; i<m; i++){
            String transKey = LayerLSHTransKey.getTransKey(key, m,  i);
            if(aLSHHashMap.containsKey(transKey)){
                ArrayList<Integer> bucket = aLSHHashMap.get(transKey);
                for(Integer id : bucket){
                    if(!bs.get(id)){
                        bs.set(id);
                        listSize++;
                    }
                }
            }

            neighborBuckets.addAll(aLSHHashMap.get(transKey));
            if(listSize >= floorT){
                break;
            }
        }
        return neighborBuckets;
    }
}
