package DSH;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by weixun on 2017/10/10.
 */
public class DSHHashTable {

    public HashMap<String, ArrayList<Integer>> DSHHashMap;

    public DSHHashTable(HashMap<String, ArrayList<Integer>> hashTable){
        /**
         * @Description:
         * @param hashTable : 将HashMap封装成hashTable
         * @return
         */
        this.DSHHashMap = hashTable;
    }

    public void setHashTable(float[][] dataset, float[][] conHashFunction, int n,
                                                            int dimension, int l, int m){
        /**
         * @Description:计算数据集中每个点的hash签名，然后将hash签名相同的点放在相同的桶中
         * @param dataset ： 原始数据集
         * @param conHashFunction ：串联的函数函数
         * @param n
         * @param dimension
         * @param l ：hashtable的编号
         * @param m ：hash签名的长度
         * @return java.util.HashMap<java.lang.String,java.util.ArrayList<java.lang.Integer>>
         */
        for(int i=0; i<n; i++){
            float[] item = dataset[i];
            //----------------------------①计算每个点的hash签名的长度--------------------
            String key = "";
            for(int j=0; j<m; j++){
                float[] aHashFunction = conHashFunction[l*m + j];
                int hashValue = DSHHashValue.getHashValue(item, aHashFunction, dimension);
                key += hashValue;
            }

            //----------------------------②根据hash签名将点放入hash桶中-------------------
            if(DSHHashMap.containsKey(key)){
                DSHHashMap.get(key).add(i);
            }
            else {
                ArrayList<Integer> newList = new ArrayList<>();
                newList.add(i);
                DSHHashMap.put(key, newList);
            }
        }

    }
}
