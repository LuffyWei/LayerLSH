package LSH;

import util.KNNSearch;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

/**
 * Created by weixun on 2017/10/14.
 */
public class LSHQuerySearch {
    int[][] KNNList;

    public LSHQuerySearch(int[][] KNNList){
        this.KNNList = KNNList;
    }

    public void setKNNList(float[][] dataset, int[] query, LSHHashPara[] conHashFunction, LSHHashTable[] lshHashTable,
                           int[] DSHSearchSize,
                           int n, int dimension, int queryNum, int k, int l, int m, float w, float alpha){

        for(int i=0; i<queryNum; i++){
            float[] aQuery = dataset[query[i]];
            BitSet bs = new BitSet(n);
            bs.set(query[i]);

            //第一层桶大小的下限
            int floorT = (int) Math.ceil(k*(1-Math.pow(1-alpha, 1/(double)l)));

            ArrayList<Integer> searchList = new ArrayList<>();
            for(int j=0; j<l; j++){
                HashMap<String, ArrayList<Integer>> aLSHHashMap = lshHashTable[j].LSHHashMap;
                String key = "";
                for(int s=0; s<m; s++){
                    LSHHashPara aHashPara = conHashFunction[j*m + s];
                    int hashValue = LSHHashValue.getHashValue(aQuery, aHashPara, dimension, w);
                    key += hashValue;
                }
                if(aLSHHashMap.containsKey(key)){
                    ArrayList<Integer> list = aLSHHashMap.get(key);
                    for(Integer id : list){
                        if(!bs.get(id)){
                            searchList.add(id);
                            bs.set(id);
                        }
                    }

                    if(list.size() < floorT){
                        ArrayList<Integer> neighborBuckets = LSHSearchNeighbor.searchNeighbor(aLSHHashMap, bs, key, m, floorT, list.size());
                        for(Integer id : neighborBuckets){
                            if(! bs.get(id)){
                                searchList.add(id);
                                bs.set(id);
                            }
                        }
                    }
                }
            }

            DSHSearchSize[i] = searchList.size();
            if(searchList.size() < k){
//                int[] aLSHKNN = new int[k];
                for(int s=0; s<searchList.size(); s++){
                    KNNList[i][s] = searchList.get(s);
                }
//                KNNList[i] = aLSHKNN;
//                continue;
            }
            else {
                int[] aLSHKNN = new int[k];
                KNNSearch KNN = new KNNSearch(aLSHKNN);
                KNN.setKNNList(dataset, searchList, aQuery, dimension, k);
                KNNList[i] = aLSHKNN;

            }

        }

    }
}
