package LayerDSH;

import DSH.DSHHashValue;
import util.KNNSearch;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

/**
 * Created by weixun on 2017/10/11.
 */
public class LayerDSHQuerySearch {
    int[][] LayerDSHKNN;

    public LayerDSHQuerySearch(int[][] KNN){
        this.LayerDSHKNN = KNN;
    }

    public void setLayerDSHKNN(float[][] dataset, int[] query, LayerDSHHashTable[] layerDSHHashTable, float[][] conHashFunction,
                                  ArrayList<HashMap<String, float[][]>> childHashFuntion, ArrayList<HashMap<String, LayerDSHPara>> tableChildPara,
                                  int[] LayerDSHSearchSize,
                                  int n, int dimension, int queryNum, int k, int l, int m, float p1){

        for(int i=0; i<queryNum; i++){

            ArrayList<Integer> searchList = new ArrayList<>();
             float[] aQuery = dataset[query[i]];
            BitSet bs = new BitSet(n);
            bs.set(query[i]);

            //第一层桶大小的下限
            int floorT = (int) Math.ceil(k*(1-Math.pow(1-p1, 1/(double)l)));

            for(int j=0; j<l; j++){
                String key = "";
                for(int s=0; s<m; s++){
                     float[] aHashFunction = conHashFunction[j*m + s];
                     int hashValue = DSHHashValue.getHashValue(aQuery, aHashFunction, dimension);
                     key += hashValue;
                }

                HashMap<String, ArrayList<Integer>> aLayerDSHHashMap = layerDSHHashTable[j].LayerDSHHashMap;
                if(aLayerDSHHashMap.containsKey(key)){
                    //如果通过key查找到的list不是null,说明是小桶，可以直接添加入searchList
                    //如果桶的大小大于floorT,则可以直接读取里面的点，这样所有table获取的点的总和一定大于k
                    if(aLayerDSHHashMap.get(key) != null){
                        ArrayList<Integer> bucket = aLayerDSHHashMap.get(key);
                        for(int id : bucket){
                            if(!bs.get(id)){
                                searchList.add(id);
                                bs.set(id);
                            }
                        }

                        if(bucket.size() < floorT) {//如果小于floorT,则需要搜索邻近的桶
                            ArrayList<Integer> neighborBuckets = LayerDSHSearchNeighbor.searchNeighbor(aLayerDSHHashMap, bs, key, m, floorT, bucket.size());
                            searchList.addAll(neighborBuckets);
                        }
                    }
                    //如果查找得到的list是null,说明是大桶，需要分层查找
                    else{
                        float[][] aBucketChildHashFunction = childHashFuntion.get(j).get(key);
                        LayerDSHPara aBucketChildPara = tableChildPara.get(j).get(key);
                        int childL = aBucketChildPara.l;
                        int childM = aBucketChildPara.m;

                        //子桶大小的下限
                        int childFloorT = (int) Math.ceil(k*(1-Math.pow(1-p1, 1/(double)childL)));

                        for(int s1=0; s1<childL; s1++){
                            String newKey = key + s1;
                            for(int s2=0; s2<childM; s2++){
                                float[] aHashFunction = aBucketChildHashFunction[s1*childM + s2];
                                int hashValue = DSHHashValue.getHashValue(aQuery, aHashFunction, dimension);
                                newKey += hashValue;
                            }
                            if(aLayerDSHHashMap.containsKey(newKey)){
                                //如果子桶的大小大于childFloorT,则可以直接读取里面的点
                                ArrayList<Integer> childBucket = aLayerDSHHashMap.get(newKey);
                                for(int id : childBucket){
                                    if(!bs.get(id)){
                                        searchList.add(id);
                                        bs.set(id);
                                    }
                                }

                                if(childBucket.size() < childFloorT){
                                    ArrayList<Integer> childNeighborBuckets = LayerDSHSearchNeighbor.searchNeighbor(aLayerDSHHashMap,
                                            bs, newKey, childM, childFloorT, childBucket.size());
                                    searchList.addAll(childNeighborBuckets);
                                }
                            }

                        }
                    }

                }
            }

            LayerDSHSearchSize[i] = searchList.size();
            if(searchList.size() < k){
                for(int s=0; s<searchList.size(); s++){
                    LayerDSHKNN[i][s] = searchList.get(s);
                }
            }
            else {
                int[] aLayerDSHKNN = new int[k];
                KNNSearch KNN = new KNNSearch(aLayerDSHKNN);
                KNN.setKNNList(dataset, searchList, aQuery, dimension, k);
                LayerDSHKNN[i] = aLayerDSHKNN;
            }

        }

    }
}
