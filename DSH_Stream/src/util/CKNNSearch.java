package util;

import java.util.Arrays;

/**
 * Created by weixun on 2017/10/9.
 */
public class CKNNSearch {

    public int[][] CKNNList;

    public CKNNSearch(int[][] CKNNList){
        this.CKNNList = CKNNList;
    }

    public void setCKNNList(float[][] dataset, int[] sample, int n, int dimension, int sampleNum, int c, int k){
        int ck = c*k;

        for(int i=0; i<sampleNum; i++){
            int[] aCKNNList = new int[ck];
            ItemDis[] itemDisList = new ItemDis[ck];

            int sampleId = sample[i];
            float[] aSample = dataset[sampleId];
            if(sampleId < ck){
                int count = 0;
                for(int j=0; j<=ck; j++){
                    if(j != sampleId){
                        float[] item = dataset[j];
                        double dis = EucDistance.getDistance(aSample, item, dimension);
                        itemDisList[count++] = new ItemDis(j, dis);
                    }
                }
                Arrays.sort(itemDisList);
                for(int j=ck+1; j<n; j++){
                    float[] item = dataset[j];
                    double dis = EucDistance.getDistance(aSample, item, dimension);
                    if(dis < itemDisList[ck-1].dis){
                        ItemDis aItemDis = new ItemDis(j, dis);
                        int insertIndex = getInsertIndex(itemDisList, aItemDis, ck);
                        for(int s=ck-1; s>insertIndex; s--){
                            itemDisList[s] = itemDisList[s-1];
                        }
                        itemDisList[insertIndex] = aItemDis;
                    }
                }
            }
            else {
                for(int j=0; j<ck; j++){
                    float[] item = dataset[j];
                    double dis = EucDistance.getDistance(aSample, item, dimension);
                    itemDisList[j] = new ItemDis(j, dis);
                }
                Arrays.sort(itemDisList);
                for(int j=ck; j<n; j++){
                    if(j != sampleId){
                        float[] item = dataset[j];
                        double dis = EucDistance.getDistance(aSample, item, dimension);
                        if(dis < itemDisList[ck-1].dis){
                            ItemDis aItemDis = new ItemDis(j, dis);
                            int insertIndex = getInsertIndex(itemDisList, aItemDis, ck);
                            for(int s=ck-1; s>insertIndex; s--){
                                itemDisList[s] = itemDisList[s-1];
                            }
                            itemDisList[insertIndex] = aItemDis;
                        }
                    }
                }
            }

            for(int j=0; j<ck; j++){
                aCKNNList[j] = itemDisList[j].id;
            }
            CKNNList[i] = aCKNNList;
        }

    }

    private int getInsertIndex(ItemDis[] itemDisList, ItemDis aItemDis, int ck){
        int insertIndex = 0;
        for(int i=0; i<ck; i++){
            if(aItemDis.dis < itemDisList[i].dis) {
                insertIndex = i;
                break;
            }
        }
        return insertIndex;
    }

}
