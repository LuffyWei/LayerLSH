package LayerLSH;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

/**
 * Created by weixun on 2017/10/16.
 */
public class LayerLSHSearchNeighbor {

    public static ArrayList<Integer> searchNeighbor(HashMap<String, ArrayList<Integer>> aLayerLSHHashMap, BitSet bs, String newkey,
                                                    int m, int childM ,int childFloorT, int listSize){
        ArrayList<Integer> neighborBuckets = new ArrayList<>();
        for(int i=0; i<childM; i++){
            String transKey = LayerLSHTransKey.getTransKey(newkey, m, childM, i);
            if(aLayerLSHHashMap.containsKey(transKey) && aLayerLSHHashMap.get(transKey) !=null){
                ArrayList<Integer> bucket = aLayerLSHHashMap.get(transKey);
                for(Integer id : bucket){
                    if(!bs.get(id)){
                        bs.set(id);
                        listSize++;
                    }
                }
            }

            neighborBuckets.addAll(aLayerLSHHashMap.get(transKey));
            if(listSize >= childFloorT){
                break;
            }
        }
        return neighborBuckets;
    }

    public static ArrayList<Integer> searchNeighbor(HashMap<String, ArrayList<Integer>> aLayerLSHHashMap, BitSet bs, String key,
                                                    int m, int childFloorT, int listSize){

        ArrayList<Integer> neighborBuckets = new ArrayList<>();
        for(int i=0; i<m; i++){
            String transKey = LayerLSHTransKey.getTransKey(key, m,  i);
            if(aLayerLSHHashMap.containsKey(transKey) && aLayerLSHHashMap.get(transKey) !=null){
                ArrayList<Integer> bucket = aLayerLSHHashMap.get(transKey);
                for(Integer id : bucket){
                    if(!bs.get(id)){
                        bs.set(id);
                        listSize++;
                    }
                }

            }
            neighborBuckets.addAll(aLayerLSHHashMap.get(transKey));
            if(listSize >= childFloorT){
                break;
            }
        }
        return neighborBuckets;
    }
}
