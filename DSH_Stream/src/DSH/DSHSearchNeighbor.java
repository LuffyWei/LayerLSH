package DSH;

import DSH.DSHTransKey;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

/**
 * Created by weixun on 2017/10/15.
 */
public class DSHSearchNeighbor {
    /**
     * 通过对原hash签名进行变换（变换某一位），搜索其临近的桶，再根据有关阈值，这样可以防止搜索结果小于k
     * @param aHashTableMap
     * @param key
     * @param m
     * @param floorT
     * @param listSize
     * @return
     */
    public static ArrayList<Integer> searchNeighbor(HashMap<String, ArrayList<Integer>> aHashTableMap, BitSet bs, String key,
                                                    int m, int floorT , int listSize){
        ArrayList<Integer> neighborBuckets = new ArrayList<>();
        for(int i=0; i<m; i++){
            String newKey = DSHTransKey.getTransKey(key,i,m);
            if(aHashTableMap.containsKey(newKey)){
                ArrayList<Integer> bucket = aHashTableMap.get(newKey);
                for(int id: bucket){
                    if(!bs.get(id)){
                        bs.set(id);
                        listSize++;
                    }
                }
            }
            neighborBuckets.addAll(aHashTableMap.get(newKey));
            if(listSize >= floorT){
                break;
            }
        }
        return neighborBuckets;
    }
}
