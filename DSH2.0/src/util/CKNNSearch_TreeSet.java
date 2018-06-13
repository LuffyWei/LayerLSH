package util;

import java.util.TreeSet;

/**
 * Created by weixun on 2017/10/9.
 */
public class CKNNSearch_TreeSet {
    public int[][] CKNNList;

    public CKNNSearch_TreeSet(int[][] CKNNList){
        this.CKNNList = CKNNList;
    }

    public int[][] getCKNNList(float[][] dataset, int[] sample, int n, int dimension, int sampleNum, int c, int k){
        int ck = c*k;

        for(int i=0; i<sampleNum; i++){
            int sampleId = sample[i];
            float[] aSample = dataset[sampleId];

            int[] aCKNNList = new int[ck];
            TreeSet<ItemDis> itemDisList = new TreeSet<>();
            if(sampleId < ck){
                for(int j=0; j<=ck; j++){
                    if(j != sampleId){
                        float[] item = dataset[j];
                        double dis = EucDistance.getDistance(aSample, item, dimension);
                        itemDisList.add(new ItemDis(j, dis));
                    }
                }
                for(int j=ck+1; j<n; j++){
                    float[] item = dataset[j];
                    double dis = EucDistance.getDistance(aSample, item, dimension);
                    if(dis < itemDisList.last().dis){
                        itemDisList.pollLast();
                        itemDisList.add(new ItemDis(j, dis));
                    }
                }
            }
            else {
                for(int j=0; j<ck; j++){
                    float[] item = dataset[j];
                    double dis = EucDistance.getDistance(aSample, item, dimension);
                    itemDisList.add(new ItemDis(j, dis));
                }
                for(int j=ck; j<n; j++){
                    if(j != sampleId){
                        float[] item = dataset[j];
                        double dis = EucDistance.getDistance(aSample, item, dimension);
                        if(dis < itemDisList.last().dis){
                            itemDisList.pollLast();
                            itemDisList.add(new ItemDis(j, dis));
                        }
                    }
                }
            }
            int count = 0;
            for(ItemDis itemdis : itemDisList){
                aCKNNList[count++] = itemdis.id;
            }
            CKNNList[i] = aCKNNList;
        }
        return CKNNList;
    }
}
