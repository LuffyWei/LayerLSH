package LayerLSH;

import LSH.LSHHashPara;
import LSH.LSHHashValue;
import LayerDSH.LayerDSHSearchNeighbor;
import util.KNNSearch;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

/**
 * Created by weixun on 2017/10/16.
 */
public class LayerLSHQuerySearch {
    public int[][] KNNList;

    public LayerLSHQuerySearch(int[][] KNNList){
        this.KNNList = KNNList;
    }

    public void setKNNList(float[][] dataset, int[] query, LayerLSHHashTable[] layerLSHHashTable, LSHHashPara[] conHashFunction,
                           ArrayList<HashMap<String, LayerLSHChildBucketPara>> tableChildBucketPara,
                           ArrayList<HashMap<String, LSHHashPara[]>> tableChildHashFunction,
                           int[] LayerLSHSearchSize,
                           int n, int dimension, int queryNum, int k, int l, int m, float w, float alpha){

        for(int q=0; q<queryNum; q++){
            int queryId = query[q];
            float[] aQuery = dataset[queryId];
            ArrayList<Integer> searchList = new ArrayList<>();

            BitSet bs = new BitSet(n);
            bs.set(queryId);

            //第一层桶大小的下限
            int floorT = (int) Math.ceil(k*(1-Math.pow(1-alpha, 1/(double)l)));

            for(int i=0; i<l; i++){
                String key = "";
                for(int j=0; j<m; j++){
                    LSHHashPara aLSHHashPara = conHashFunction[i*m + j];
                    int hashValue = LSHHashValue.getHashValue(aQuery, aLSHHashPara, dimension, w);
                    key += hashValue;
                }

                HashMap<String, ArrayList<Integer>> aLayerLSHHashMap = layerLSHHashTable[i].LayerLSHHashMap;
                HashMap<String, LayerLSHChildBucketPara> aTableChildBucketPara = tableChildBucketPara.get(i);
                HashMap<String, LSHHashPara[]> aTableChildHashFunction = tableChildHashFunction.get(i);
                if(aLayerLSHHashMap.containsKey(key)){
                    if(aLayerLSHHashMap.get(key) != null){
                        ArrayList<Integer> list = aLayerLSHHashMap.get(key);
                        for(int id:list){
                            if(!bs.get(id)){
                                searchList.add(id);
                                bs.set(id);
                            }
                        }
                        if(list.size() < floorT){
                            ArrayList<Integer> neighborBuckets = LayerLSHSearchNeighbor.searchNeighbor(aLayerLSHHashMap, bs, key, m, floorT, list.size());
                            searchList.addAll(neighborBuckets);
                        }
                    }
                    else {//如果桶存在且为空，说明原桶是大桶
                        LayerLSHChildBucketPara aBucketChildPara = aTableChildBucketPara.get(key);
                        LSHHashPara[] aBucketHashFunction = aTableChildHashFunction.get(key);
                        int childL = aBucketChildPara.l;
                        int childM = aBucketChildPara.m;
                        float childW = aBucketChildPara.w;

                        //子桶大小的下限
                        int childFloorT = (int) Math.ceil(k*(1-Math.pow(1-alpha, 1/(double)childL)));

                        for(int s1=0; s1<childL; s1++){
                            String newKey = key + s1;
                            for(int s2=0; s2<childM; s2++){
                                LSHHashPara aLSHHashPara = aBucketHashFunction[s1*childM + s2];
                                int hashValue = LSHHashValue.getHashValue(aQuery, aLSHHashPara, dimension, childW);
                                newKey += hashValue;
                            }

                            if(aLayerLSHHashMap.containsKey(newKey)){
                                ArrayList<Integer> childList = aLayerLSHHashMap.get(newKey);
                                for(int id : childList){
                                    if(! bs.get(id)){
                                        searchList.add(id);
                                        bs.set(id);
                                    }
                                }

                                if(childList.size() < childFloorT){
                                   ArrayList<Integer> childNeighborBuckets = LayerLSHSearchNeighbor.searchNeighbor(aLayerLSHHashMap, bs, newKey, m,
                                           childM, childFloorT, childList.size());
                                   searchList.addAll(childNeighborBuckets);
                                }
                            }
                        }
                    }
                }
            }


            LayerLSHSearchSize[q] = searchList.size();
            if(searchList.size() < k){
//                int[] aLSHKNN = new int[k];
//                KNNList[q] = aLSHKNN;
                for(int s=0; s<searchList.size(); s++){
                    KNNList[q][s] = searchList.get(s);
                }
//                continue;
            }
            else {
                int[] aLSHKNN = new int[k];
                KNNSearch KNN = new KNNSearch(aLSHKNN);
                KNN.setKNNList(dataset, searchList, aQuery, dimension, k);
                KNNList[q] = aLSHKNN;
            }
        }
    }
}
