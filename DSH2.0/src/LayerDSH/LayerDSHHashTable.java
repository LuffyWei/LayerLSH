package LayerDSH;

import DSH.DSHHashValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by weixun on 2017/10/11.
 */
public class LayerDSHHashTable {


    public HashMap<String, ArrayList<Integer>> LayerDSHHashMap;

    public LayerDSHHashTable(HashMap<String, ArrayList<Integer>> layerDSHHashMap) {
        this.LayerDSHHashMap = layerDSHHashMap;
    }

    public void setLayerDSHHashMap(float[][] dataset, ArrayList<float[]> hashFamily,
                                                                  HashMap<String, ArrayList<Integer>> DSHHashMap,
                                                                  HashMap<String, float[][]> aChildHashFunction,
                                                                  HashMap<String, LayerDSHPara> aTableChildPara,
                                                                  int dimension, int k, int l, int m, float p1, float beta) {

        //----------------------------根据阈值获取大桶hash签名和子桶参数--------------------------------
        //必须要先记录下大桶，然后再根据大桶进行分层，不能边遍历LSHHashMap边进行分层，因为将大桶置空时会报错，HashMap遍历是可以添加元素，但是不能更改元素
        int T = (int) Math.ceil(k/beta);
        ArrayList<String> bigBuckets = new ArrayList<>();
        getBigBuckets(DSHHashMap, bigBuckets, aTableChildPara, k, l, m, p1, beta, T);

        //----------------------------根据子桶参数划分子桶，同时确定子桶Hash函数--------------------------
        int num = bigBuckets.size();
        Random rand = new Random();
        for(int i=0; i<num; i++){
            String aBigBucketId = bigBuckets.get(i);
            LayerDSHPara aChildPara = aTableChildPara.get(aBigBucketId);
            int childL = aChildPara.l;
            int childM = aChildPara.m;
            //------------------------记录子桶的hash函数----------------------------------------------
            float[][] aBucketHashFunction = new float[childL*childM][dimension];
            int realHashFunctionNum = hashFamily.size();
            for(int j=0; j<childL*childM; j++){
                aBucketHashFunction[j] = hashFamily.get(rand.nextInt(realHashFunctionNum));
            }
            aChildHashFunction.put(aBigBucketId, aBucketHashFunction);
            //------------------------将大桶进行划分--------------------------------------------------
            ArrayList<Integer> aBigBucket = DSHHashMap.get(aBigBucketId);
            for(int itemId : aBigBucket){
                float[] item = dataset[itemId];
                for(int j=0; j<childL; j++){
                    /**
                     * 新的hash签名：newKey的组成方式是：原来的key(长度m) + 子桶编号 + 新的key(长度childM)
                     */
                    String key = aBigBucketId + j;
                    for(int s=0; s<childM; s++){
                        float[] aHashFunction = aBucketHashFunction[j*childM + s];
                        int hashValue = DSHHashValue.getHashValue(item, aHashFunction, dimension);
                        key += hashValue;
                    }
                    if(DSHHashMap.containsKey(key)){
                        DSHHashMap.get(key).add(itemId);
                    }
                    else{
                        ArrayList<Integer> newList = new ArrayList<>();
                        newList.add(itemId);
                        DSHHashMap.put(key, newList);
                    }
                }
            }
            DSHHashMap.put(aBigBucketId, null);
        }
        LayerDSHHashMap = DSHHashMap;

    }

    private void getBigBuckets(HashMap<String, ArrayList<Integer>> DSHHashMap, ArrayList<String> bigBuckets,
                               HashMap<String, LayerDSHPara> childParas, int k, int l, int m, float p1, float beta, int T){

        for(Map.Entry<String, ArrayList<Integer>> entry : DSHHashMap.entrySet()){
            int size = entry.getValue().size();
            if(size > T){
                bigBuckets.add(entry.getKey());
                LayerDSHPara aChildPara = (new LayerDSHPara(l, m)).getChildPara(k, size, p1, beta);
                childParas.put(entry.getKey(), aChildPara);
            }
        }

    }
}
