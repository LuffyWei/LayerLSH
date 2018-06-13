package LSH;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by weixun on 2017/10/13.
 */
public class LSHHashTable {
    public HashMap<String, ArrayList<Integer>> LSHHashMap;

    public LSHHashTable(HashMap<String, ArrayList<Integer>> LSHHashMap){
        this.LSHHashMap = LSHHashMap;
    }

    public void setLSHHashMap(float[][] dataset, LSHHashPara[] conHashFunction, int n, int dimension, int l, int m, float w){

        for(int i=0; i<n; i++){
            float[] item = dataset[i];
            String key = "";

            for(int j=0; j<m; j++){
                LSHHashPara aHashPara = conHashFunction[l*m + j];
                int hashValue = LSHHashValue.getHashValue(item, aHashPara, dimension, w);
                key += hashValue;
            }

            if(LSHHashMap.containsKey(key)){
                LSHHashMap.get(key).add(i);
            }
            else {
                ArrayList<Integer> newList= new ArrayList<>();
                newList.add(i);
                LSHHashMap.put(key, newList);
            }
        }
    }
}
