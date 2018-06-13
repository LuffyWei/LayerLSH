package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

/**
 * Created by weixun on 2017/10/10.
 */
public class KNNSearch {

    public int[] KNNList;

    public KNNSearch(int[] KNNList){
        this.KNNList = KNNList;
    }

    public void setKNNList(float[][] dataset, ArrayList<Integer> searchList, float[] aQuery, int dimension, int k){
        int size = searchList.size();
//        System.out.println(size);
        TreeSet<ItemDis> KnnItemdis = new TreeSet<>();
        for(int j=0; j<k; j++){
            int itemId = searchList.get(j);
            float[] item = dataset[itemId];
            double dis = EucDistance.getDistance(item, aQuery, dimension);
            KnnItemdis.add(new ItemDis(itemId, dis));
        }
        for(int j=k; j<size; j++){
            int itemId = searchList.get(j);
            float[] item = dataset[itemId];
            double dis = EucDistance.getDistance(item, aQuery, dimension);
            if(dis < KnnItemdis.last().dis){
                KnnItemdis.pollLast();
                KnnItemdis.add(new ItemDis(itemId, dis));
            }
        }

        int count = 0;
        for(ItemDis itemDis : KnnItemdis){
            KNNList[count++] = itemDis.id;
        }
    }

    public void setKNNList(float[][] dataset, ArrayList<Integer> searchList, float[] aQuery, int[][] KNN, int dimension,
                            int k ,int index){
        int size = searchList.size();
//        System.out.println(size);
        ItemDis[] DSHSearchDisList = new ItemDis[size];
        for(int j=0; j<size; j++){
            int pointId = searchList.get(j);
            double dis = EucDistance.getDistance(aQuery, dataset[pointId], dimension);
            ItemDis itemDis = new ItemDis(pointId, dis);
            DSHSearchDisList[j] = itemDis;
        }
        Arrays.sort(DSHSearchDisList);

        for(int j=0; j<k; j++){
            KNN[index][j] = DSHSearchDisList[j].id;
        }

    }
}
