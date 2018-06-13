package LayerLSH;

import LSH.LSHHashFamily;
import LSH.LSHHashPara;
import LSH.LSHHashValue;
import LayerDSH.LayerDSHSearchNeighbor;
import util.SubString;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

/**
 * Created by weixun on 2018/3/18.
 */
public class LayLSHStream {

    float[][] dataset;
    LayerLSHHashTable[] layerLSHHashTable;
    LSHHashPara[] conHashFunction;
    ArrayList<HashMap<String, LayerLSHChildBucketPara>> tableChildBucketPara;
    ArrayList<HashMap<String, LSHHashPara[]>> tableChildHashFunction;

    int dim;
    int l;
    int m;
    int k;
    float w;
    float alpha;
    float beta;
    float p1;
    float p2;

    int T;
    int n;


    /**
     *流处理：插入，删除，查找，重新分桶
     *
     * */
    public LayLSHStream(float[][] dataset, LayerLSHHashTable[] layerLSHHashTable,
                        LSHHashPara[] conHashFunction,
                        ArrayList<HashMap<String, LayerLSHChildBucketPara>> tableChildBucketPara,
                        ArrayList<HashMap<String, LSHHashPara[]>> tableChildHashFunction,
                        int dim, int l,
                        int m, int k, float w, float alpha, float beta, float p1, float p2) {
        this.dataset = dataset;
        this.layerLSHHashTable = layerLSHHashTable;
        this.conHashFunction = conHashFunction;
        this.tableChildBucketPara = tableChildBucketPara;
        this.tableChildHashFunction = tableChildHashFunction;
        this.dim = dim;
        this.l = l;
        this.m= m;
        this.k = k;
        this.w = w;
        this.alpha = alpha;
        this.beta = beta;
        this.p1 = p1;
        this.p2 = p2;

        this.T = (int) Math.ceil(k/beta);
        int n = dataset.length;
    }

    public void Insert(float[] item, int index){
        synchronized (this){
            for(int i=0; i<l; i++){
                String key = "";
                for(int j=0; j<m; j++){
                    LSHHashPara aPara = conHashFunction[i*m+j];
                    key += LSHHashValue.getHashValue(item, aPara, dim, w);
                }
                String basekey = key;
                HashMap<String, ArrayList<Integer>> aLayerLSHHashMap = layerLSHHashTable[i].LayerLSHHashMap;
                HashMap<String, LayerLSHChildBucketPara> aTableChildBucketPara = tableChildBucketPara.get(i);
                HashMap<String, LSHHashPara[]> aTableChildHashFunction = tableChildHashFunction.get(i);
                if(layerLSHHashTable[i].LayerLSHHashMap.containsKey(key)){
                    if(aLayerLSHHashMap.get(key) != null){
                        aLayerLSHHashMap.get(key).add(index);
                        //判断是否需要分裂
                        //如果在时间片以内
                        //如果父桶size大于分裂阈值T*p2，则进行分裂。分裂方式与LayerLSH一样。
                        if(aLayerLSHHashMap.get(key).size() > (int)(p2*T)){
                            split_bucket(aLayerLSHHashMap, aTableChildBucketPara, aTableChildHashFunction, key, aLayerLSHHashMap.get(key).size());
                        }

                    }
                    else {
                        LayerLSHChildBucketPara aChildPara = aTableChildBucketPara.get(key);
                        LSHHashPara[] aBucketChildHashFunction = aTableChildHashFunction.get(key);
                        for(int s1=0; s1<aChildPara.l; s1++){
                            key = key+s1;
                            for(int s2=0; s2<aChildPara.m; s2++){
                                key += LSHHashValue.getHashValue(item, aBucketChildHashFunction[s1*aChildPara.m+s2], dim, aChildPara.w);
                            }
                            if(aLayerLSHHashMap.containsKey(key)){
                                aLayerLSHHashMap.get(key).add(index);
                                //如果子桶size大于分裂阈值，也要进行分裂
                                //不过这里的分裂与LayerLSH分裂不同，而是先将各个子桶合并成为父桶，再重新进行LayerLSH的分裂过程。
                                if(aLayerLSHHashMap.get(key).size() > (int)(p2*T)){
                                    reSplit(basekey, i);
                                }
                            }
                            else{
                                ArrayList<Integer> newList = new ArrayList<>();
                                newList.add(index);
                                aLayerLSHHashMap.put(key, newList);
                            }
                        }
                    }

                }
                else {
                    ArrayList<Integer> newList = new ArrayList<>();
                    newList.add(index);
                    layerLSHHashTable[i].LayerLSHHashMap.put(key, newList);
                }
            }
        }
    }

    public void delete(float[] item, int index){
        synchronized (this){
            for(int i=0; i<l; i++) {
                String key = "";
                for (int j = 0; j < m; j++) {
                    LSHHashPara aPara = conHashFunction[i * m + j];
                    key += LSHHashValue.getHashValue(item, aPara, dim, w);
                }
                HashMap<String, ArrayList<Integer>> aLayerLSHHashMap = layerLSHHashTable[i].LayerLSHHashMap;
                HashMap<String, LayerLSHChildBucketPara> aTableChildBucketPara = tableChildBucketPara.get(i);
                HashMap<String, LSHHashPara[]> aTableChildHashFunction = tableChildHashFunction.get(i);
                if(aLayerLSHHashMap.containsKey(key)){
                    if(aLayerLSHHashMap.get(key) != null){
                        aLayerLSHHashMap.get(key).remove((Integer) index);
//                    System.out.println("删除成功");
                        if(aLayerLSHHashMap.get(key).isEmpty()){
                            aLayerLSHHashMap.remove(key);
                        }
                    }
                    else {
                        LayerLSHChildBucketPara aChildPara = aTableChildBucketPara.get(key);
                        LSHHashPara[] aBucketChildHashFunction = aTableChildHashFunction.get(key);
                        for(int s1=0; s1<aChildPara.l; s1++){
                            key = key+s1;
                            for(int s2=0; s2<aChildPara.m; s2++){
                                key += LSHHashValue.getHashValue(item, aBucketChildHashFunction[s1*aChildPara.m +s2], dim, aChildPara.w);
                            }
                            if(aLayerLSHHashMap.containsKey(key)){
                                aLayerLSHHashMap.get(key).remove((Integer) index);
//                            System.out.println("删除成功");
                                if(aLayerLSHHashMap.get(key).isEmpty()){
                                    aLayerLSHHashMap.remove(key);
                                }
                            }
                        }
                    }

                }

            }
        }
    }

    public void reSplit(String key, int tableIndex){
        ArrayList<Integer> formalBucket = new ArrayList<>();
        ArrayList<String> formalKey = new ArrayList<>();
//        String key = "";
//        for (int j = 0; j < m; j++) {
//            LSHHashPara aLSHHashPara = conHashFunction[tableIndex * m + j];
//            int hashValue = LSHHashValue.getHashValue(item, aLSHHashPara, dim, w);
//            key += hashValue;
//        }
        //以bigBucket的第一子table为基准，恢复bigBucket
        HashMap<String, ArrayList<Integer>> aLayerLSHHashMap = layerLSHHashTable[tableIndex].LayerLSHHashMap;
        //获取分裂父桶的参数。
        LayerLSHChildBucketPara aBucketChildPara  = tableChildBucketPara.get(tableIndex).get(key);

        //这个key不存在
//        if(aBucketChildPara == null){
//            System.out.println(key);
//        }

        //父桶共生成成childL个子table。
        int childL = aBucketChildPara.l;
        //子table中key的命名规则是：basekey+i+childkey，那么判断是第几子桶就根据key中m+1位的i来判断。
        String[] childBaseKey = new String[childL];
        for(int s=0; s<childL; s++){
            childBaseKey[s] = key+s;
        }
        for (HashMap.Entry<String, ArrayList<Integer>> entry : aLayerLSHHashMap.entrySet()) {
            if (SubString.getLength(entry.getKey()) > m) {
                String subKey = SubString.getSubString(entry.getKey(), m+1);
                if (subKey.equals(key + 0)) {
                    ArrayList<Integer> childBucket = entry.getValue();
                    formalBucket.addAll(childBucket);
                }
                //HashMap边遍历的时候不能边删除，所以只能先记录再删除。
                //这个地方写复杂了，可以直接判断前m位是否等于key(参数，也就是父桶的名字)
                for(int s=0; s<childL; s++){
                    if(subKey.equals(childBaseKey[s])){
                        formalKey.add(entry.getKey());
                        break;
                    }
                }
            }
        }
        for (String s : formalKey) {
            aLayerLSHHashMap.remove(s);
        }

        LayerLSHChildBucketPara aNewBucketChildPara = (new LayerLSHChildBucketPara(l,m)).getChildBucketPara(k, formalBucket.size(), alpha, beta);
        int newChildL = aNewBucketChildPara.l;
        int newChildM = aNewBucketChildPara.m;
        aBucketChildPara.setW(w);
        tableChildBucketPara.get(tableIndex).put(key, aNewBucketChildPara);

        LSHHashPara[] aBucketChildHashFunction = new LSHHashPara[newChildL*newChildM];
        LSHHashFamily aBucketHashFamily = new LSHHashFamily(aBucketChildHashFunction);
        aBucketHashFamily.setHashFamily(dim, w);
        HashMap<String, LSHHashPara[]> aTableChildHashFunction = tableChildHashFunction.get(tableIndex);
        aTableChildHashFunction.put(key, aBucketChildHashFunction);

        for(int itemId : formalBucket){
            float[] point = dataset[itemId];
            for(int s1=0; s1<newChildL; s1++){
                String newKey = key + s1;
                for(int s2=0; s2<newChildM; s2++){
                    LSHHashPara aChildLSHHashPara = aBucketChildHashFunction[s1*newChildM + s2];
                    int childHashValue = LSHHashValue.getHashValue(point, aChildLSHHashPara, dim, aBucketChildPara.w);
                        newKey += childHashValue;
                }
                if(aLayerLSHHashMap.containsKey(newKey)){
                    aLayerLSHHashMap.get(newKey).add(itemId);
                }
                else {
                    ArrayList<Integer> newList = new ArrayList<>();
                    newList.add(itemId);
                    aLayerLSHHashMap.put(newKey, newList);
                }
            }
        }
    }

    public void split_bucket( HashMap<String, ArrayList<Integer>> aLayerLSHHashMap,
                       HashMap<String, LayerLSHChildBucketPara> aTableChildBucketPara,
                       HashMap<String, LSHHashPara[]> aTableChildHashFunction,
                       String key, int s){
        //先生成子桶参数
        LayerLSHChildBucketPara aBucketChildPara = (new LayerLSHChildBucketPara(l,m)).getChildBucketPara(k, s, alpha, beta);
        aBucketChildPara.setW(w);
        LSHHashPara[] aBucketChildHashFunction = new LSHHashPara[aBucketChildPara.l*aBucketChildPara.m];
        LSHHashFamily hashFamily = new LSHHashFamily(aBucketChildHashFunction);
        hashFamily.setHashFamily(dim,w);
        aTableChildBucketPara.put(key, aBucketChildPara);
        aTableChildHashFunction.put(key, aBucketChildHashFunction);
        //再将big bucket进行分裂
        ArrayList<Integer> aBigBucket = aLayerLSHHashMap.get(key);
        for(int itemId : aBigBucket){
            float[] item = dataset[itemId];
            for(int i=0; i<aBucketChildPara.l; i++){
                String newKey = key + i;
                for(int j=0; j<aBucketChildPara.m; j++){
                    LSHHashPara aLSHHashPara = aBucketChildHashFunction[i*aBucketChildPara.m +j];
                    int hashValue = LSHHashValue.getHashValue(item, aLSHHashPara, dim, aBucketChildPara.w);
                    newKey += hashValue;
                }
                if(aLayerLSHHashMap.containsKey(newKey)){
                    aLayerLSHHashMap.get(newKey).add(itemId);
                }
                else {
                    ArrayList<Integer> newList = new ArrayList<>();
                    newList.add(itemId);
                    aLayerLSHHashMap.put(newKey, newList);
                }
            }
        }
        aLayerLSHHashMap.put(key, null);
    }



    public void split(){
        synchronized (this){
            for(int i=0; i<l; i++){
                HashMap<String, ArrayList<Integer>> aLayerLSHHashMap = layerLSHHashTable[i].LayerLSHHashMap;
                HashMap<String, LayerLSHChildBucketPara> aTableChildBucketPara = tableChildBucketPara.get(i);
                HashMap<String, LSHHashPara[]> aTableChildHashFunction = tableChildHashFunction.get(i);
                ArrayList<String> bigBuckets  = new ArrayList<>();
                for(HashMap.Entry<String, ArrayList<Integer>> entry : aLayerLSHHashMap.entrySet()){

                    /** 正式判断是否分裂时，分裂阈值时p1*T */
                    if(entry.getValue() !=null && entry.getValue().size()> (int)(p1*T)){
                        bigBuckets.add(entry.getKey());
                    }
                }

                for(String bigBucketsId:bigBuckets){
                    if(SubString.getLength(bigBucketsId) == m){
                        ArrayList<Integer> aBigBucket = aLayerLSHHashMap.get(bigBucketsId);
                        LayerLSHChildBucketPara aBucketChildPara = (new LayerLSHChildBucketPara(l,m)).getChildBucketPara(k, aBigBucket.size(), alpha, beta);
                        int childL = aBucketChildPara.l;
                        int childM = aBucketChildPara.m;
                        aBucketChildPara.setW(w);
                        aTableChildBucketPara.put(bigBucketsId, aBucketChildPara);
                        //---------------随机生成hash函数并记录下来------------------------------------------------
                        LSHHashPara[] aBucketChildHashFunction = new LSHHashPara[childL*childM];
                        LSHHashFamily aBucketHashFamily = new LSHHashFamily(aBucketChildHashFunction);
                        aBucketHashFamily.setHashFamily(dim, w);
                        aTableChildHashFunction.put(bigBucketsId, aBucketChildHashFunction);
                        //---------------对桶进行划分-------------------------------------------------------------
                        for(int itemId : aBigBucket){
                            float[] item = dataset[itemId];
                            for(int s1=0; s1<childL; s1++){
                                String newKey = bigBucketsId + s1;
                                for(int s2=0; s2<childM; s2++){
                                    LSHHashPara aLSHHashPara = aBucketChildHashFunction[s1*childM +s2];
                                    int hashValue = LSHHashValue.getHashValue(item, aLSHHashPara, dim, aBucketChildPara.w);
                                    newKey += hashValue;
                                }
                                if(aLayerLSHHashMap.containsKey(newKey)){
                                    aLayerLSHHashMap.get(newKey).add(itemId);
                                }
                                else {
                                    ArrayList<Integer> newList = new ArrayList<>();
                                    newList.add(itemId);
                                    aLayerLSHHashMap.put(newKey, newList);
                                }
                            }
                        }
                        aLayerLSHHashMap.put(bigBucketsId, null);
                    }
                    else {
                        String subKey = SubString.getSubString(bigBucketsId, m);
                        reSplit(subKey, i);
                    }
                }

            }
        }
    }

    /**
     * 这个方法需要使用关键字synchronized包围，因为与主线程的split方法有冲突。
     * */
    public int Search(int n,float[] item, int index) {
        synchronized (this){
            BitSet bs = new BitSet(n);
            bs.set(index);
            int searchSize = 1;
            ArrayList<Integer> searchList = new ArrayList<>();
            for (int i = 0; i < l; i++) {
                String key = "";
                for (int j = 0; j < m; j++) {
                    LSHHashPara aLSHHashPara = conHashFunction[i * m + j];
                    int hashValue = LSHHashValue.getHashValue(item, aLSHHashPara, dim, w);
                    key += hashValue;
                }
                HashMap<String, ArrayList<Integer>> aLayerLSHHashMap = layerLSHHashTable[i].LayerLSHHashMap;
                HashMap<String, LayerLSHChildBucketPara> aTableChildBucketPara = tableChildBucketPara.get(i);
                HashMap<String, LSHHashPara[]> aTableChildHashFunction = tableChildHashFunction.get(i);
                if (aLayerLSHHashMap.containsKey(key)) {
                    if (aLayerLSHHashMap.get(key) != null) {
                        ArrayList<Integer> list = aLayerLSHHashMap.get(key);
                        searchList.addAll(list);
                        for(int id:list){
                            if(!bs.get(id)){
                                bs.set(id);
                                searchSize++;
                            }
                        }
                    } else {//如果桶存在且为空，说明原桶是大桶
                        LayerLSHChildBucketPara aBucketChildPara = aTableChildBucketPara.get(key);
                        LSHHashPara[] aBucketHashFunction = aTableChildHashFunction.get(key);
                        int childL = aBucketChildPara.l;
                        int childM = aBucketChildPara.m;
                        float childW = aBucketChildPara.w;

                        for (int s1 = 0; s1 < childL; s1++) {
                            String newKey = key + s1;
                            for (int s2 = 0; s2 < childM; s2++) {
                                LSHHashPara aLSHHashPara = aBucketHashFunction[s1 * childM + s2];
                                int hashValue = LSHHashValue.getHashValue(item, aLSHHashPara, dim, childW);
                                newKey += hashValue;
                            }

                            if (aLayerLSHHashMap.containsKey(newKey)) {
                                ArrayList<Integer> childList = aLayerLSHHashMap.get(newKey);
                                searchList.addAll(childList);
                                for(int id : childList){
                                    if(! bs.get(id)){
                                        bs.set(id);
                                        searchSize++;
                                    }
                                }
                            }
                        }

                    }
                }

            }

            return searchSize;
        }

    }

}
