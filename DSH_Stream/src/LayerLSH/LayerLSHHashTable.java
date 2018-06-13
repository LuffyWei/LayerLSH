package LayerLSH;

import LSH.LSHHashFamily;
import LSH.LSHHashPara;
import LSH.LSHHashValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by weixun on 2017/10/15.
 */
public class LayerLSHHashTable {
    public HashMap<String, ArrayList<Integer>> LayerLSHHashMap;

    public LayerLSHHashTable(HashMap<String, ArrayList<Integer>> layerLSHHashMap) {
        this.LayerLSHHashMap = layerLSHHashMap;
    }

    public void setLayerLSHHashMap(float[][] dataset, HashMap<String, ArrayList<Integer>> aLSHHashMap,
                                   HashMap<String, LayerLSHChildBucketPara> aTableChildBucketPara,
                                   HashMap<String, LSHHashPara[]> aTableChildHashFunction, int n,
                                   int dimension, int k, int l, int m, float alpha, float beta, float w){

        //----------------------------根据阈值获取大桶hash签名和子桶参数--------------------------------
        int T = (int) Math.ceil(k/beta);
        ArrayList<String> bigBuckets = new ArrayList<>();
        getBigBuckets(aLSHHashMap, bigBuckets, aTableChildBucketPara, k, l, m, alpha, beta, T);

        //----------------------------对LSHHashMap进行分层，同时记录选取得hash函数-----------------------
        for(String bigBucketsId : bigBuckets){
            ArrayList<Integer> aBigBucket = aLSHHashMap.get(bigBucketsId);
            LayerLSHChildBucketPara aBucketChildPara = aTableChildBucketPara.get(bigBucketsId);
            int childL = aBucketChildPara.l;
            int childM = aBucketChildPara.m;
            //---------------随机生成hash函数并记录下来------------------------------------------------
            LSHHashPara[] aBucketChildHashFunction = new LSHHashPara[childL*childM];
            LSHHashFamily aBucketHashFamily = new LSHHashFamily(aBucketChildHashFunction);
//            float childW = aBucketHashFamily.setHashFamily(dataset, aBigBucket, dimension, childL, childM);
//            aBucketChildPara.setW(childW);
            aBucketHashFamily.setHashFamily(dimension, w);
            aBucketChildPara.setW(w);
            aTableChildHashFunction.put(bigBucketsId, aBucketChildHashFunction);

            //---------------对桶进行划分-------------------------------------------------------------
            for(int itemId : aBigBucket){
                float[] item = dataset[itemId];
                for(int i=0; i<childL; i++){
                    String newKey = bigBucketsId + i;
                    for(int j=0; j<childM; j++){
                        LSHHashPara aLSHHashPara = aBucketChildHashFunction[i*childM +j];
                        int hashValue = LSHHashValue.getHashValue(item, aLSHHashPara, dimension, aBucketChildPara.w);
                        newKey += hashValue;
                    }
                    if(aLSHHashMap.containsKey(newKey)){
                        aLSHHashMap.get(newKey).add(itemId);
                    }
                    else {
                        ArrayList<Integer> newList = new ArrayList<>();
                        newList.add(itemId);
                        aLSHHashMap.put(newKey, newList);
                    }
                }
            }
            aLSHHashMap.put(bigBucketsId, null);
        }
        LayerLSHHashMap = aLSHHashMap;
    }


    public void getBigBuckets(HashMap<String, ArrayList<Integer>> aLSHHashMap, ArrayList<String> bigBuckets,
                               HashMap<String, LayerLSHChildBucketPara> aTableChildPara, int k, int l, int m, float alpha, float beta, int T){

        for(Map.Entry<String, ArrayList<Integer>> entry : aLSHHashMap.entrySet()){
            int size = entry.getValue().size();
            if(size > T){
                bigBuckets.add(entry.getKey());
                LayerLSHChildBucketPara aLayerChildPara = (new LayerLSHChildBucketPara(l, m)).getChildBucketPara(k, size, alpha, beta);
                aTableChildPara.put(entry.getKey(), aLayerChildPara);
            }
        }

    }



}
