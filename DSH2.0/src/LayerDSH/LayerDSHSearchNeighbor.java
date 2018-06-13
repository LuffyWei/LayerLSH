package LayerDSH;

import DSH.DSHTransKey;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

/**
 * Created by weixun on 2017/10/15.
 */
public class LayerDSHSearchNeighbor {

    public static ArrayList<Integer> searchNeighbor(HashMap<String, ArrayList<Integer>> aLayerDSHHashMap, BitSet bs, String key,
                                                    int m, int floorT, int listSize) {

        ArrayList<Integer> neighborBuckets = new ArrayList<>();
        for(int i=0; i<m; i++){
            String newKey = DSHTransKey.getTransKey(key,i,m);
            if(aLayerDSHHashMap.containsKey(newKey) && aLayerDSHHashMap.get(newKey) != null){
                ArrayList<Integer> bucket = aLayerDSHHashMap.get(newKey);
                for(int id:bucket){
                    if(!bs.get(id)){
                        bs.set(id);
                        listSize++;
                    }
                }
            }
            neighborBuckets.addAll(aLayerDSHHashMap.get(newKey));
            if(listSize >= floorT){
                break;
            }
        }
        return neighborBuckets;

    }

    public static ArrayList<Integer> searchNeighbor(HashMap<String, ArrayList<Integer>> aLayerDSHHashMap, BitSet bs, String key, int m, int childM,
                                                    int floorT, int listSize) {

        ArrayList<Integer> neighborBuckets = new ArrayList<>();
        //新的hash签名：newKey的组成方式是：原来的key(长度m) + 子桶编号 + 新的key(长度childM)
        for(int i=m+1; i<m+1+childM; i++){
            String newKey = LayerDSHTransKey.getTransKey(key, i, m, childM);
            if(aLayerDSHHashMap.containsKey(newKey) && aLayerDSHHashMap.get(newKey) != null){
                ArrayList<Integer> bucket = aLayerDSHHashMap.get(newKey);
                for(int id:bucket){
                    if(!bs.get(id)){
                        bs.set(id);
                        listSize++;
                    }
                }
            }

            neighborBuckets.addAll(aLayerDSHHashMap.get(newKey));
            if(listSize >= floorT){
                break;
            }
        }
        return neighborBuckets;

    }
}
