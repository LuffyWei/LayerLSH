package DSH;


import util.KNNSearch;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

/**
 * Created by weixun on 2017/10/10.
 */
public class DSHQuerySearch {


    int[][] DSHKNN;

    public DSHQuerySearch(int[][] DSHKNN){
        /**
         * @Description:
         * @param DSHKNN :DSH进行KNN搜索的结果
         * @return
         */
        this.DSHKNN = DSHKNN;
    }

    public void setDSHKNN(float[][] dataset, int[] query, float[][] conHashFunction, DSHHashTable[] hashTable, int[] DSHSearchSize,
                             int n, int dimension, int k, int queryNum, int l, int m, float p1) {
        /**
         * @Description:首先计算每个query的hash签名，然后从每个hashTable中查找hash签名相同的桶，然后作为candidate集
         * 其中使用BitSet进行去重，最后对candidate集进行排序，选取前k个点作为最终的结果
         * @param dataset
         * @param query
         * @param conHashFunction
         * @param hashTable
         * @param DSHSearchSize
         * @param n
         * @param dimension
         * @param k
         * @param queryNum
         * @param l
         * @param m
         * @param p1
         * @return
         */
        for(int i=0; i<queryNum; i++){
            float[] aQuery = dataset[query[i]];
            ArrayList<Integer> searchList = new ArrayList<>();
            //用于去重
            BitSet bs = new BitSet(n);
            bs.set(query[i]);
            //桶大小的下限
            int floorT = (int) Math.ceil(k*(1-Math.pow(1-p1, 1/(double)l)));

            for(int j=0; j<l; j++){
                //----------------------------①计算query的hash签名----------------------------
                String key = "";
                for(int h=0; h<m; h++){
                    float[] aHashFunction = conHashFunction[j*m + h];
                    int hashValue = DSHHashValue.getHashValue(aQuery, aHashFunction, dimension);
                    key += hashValue;
                }

                //----------------------------②在hashTable中根据query的签名进行查找,同时去重----------
                //----------------------------③如果searchList不足k个，那么就搜索就近的buckets---------
                //----------这里就近搜索的策略是随机更改key中的某一位，然后在hashTable中重新搜索一次---------
                HashMap<String, ArrayList<Integer>> aHashTableMap = hashTable[j].DSHHashMap;
                if(aHashTableMap.containsKey(key)){
                    ArrayList<Integer> bucket = aHashTableMap.get(key);
                    //如果桶的大小大于floorT,则可以直接读取里面的点，这样所有table获取的点的总和一定大于k
                    for(int id: bucket){
                        if(!bs.get(id)){
                            searchList.add(id);
                            bs.set(id);
                        }
                    }

                    if(bucket.size() < floorT) {//如果小于floorT,则需要搜索邻近的桶
                        ArrayList<Integer> neighborBuckets = DSHSearchNeighbor.searchNeighbor(aHashTableMap, bs, key, m,
                                floorT, bucket.size());
                        searchList.addAll(neighborBuckets);
                    }

                }
            }


            //--------------------------------④从searchList中选出前k个点作为最终结果--------------
            DSHSearchSize[i] = searchList.size();
            if(searchList.size() < k){
                for(int s=0; s<searchList.size(); s++){
                    DSHKNN[i][s] = searchList.get(s);
                }
            }
            else {
                int[] aKNNList = new int[k];
                KNNSearch knnSearch = new KNNSearch(aKNNList);
                knnSearch.setKNNList(dataset, searchList, aQuery, dimension, k);
                DSHKNN[i] = knnSearch.KNNList;
            }

        }

    }
}
