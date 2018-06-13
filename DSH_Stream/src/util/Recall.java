package util;

import java.util.ArrayList;

/**
 * Created by weixun on 2017/10/10.
 */
public class Recall {
    double[] recall;

    public Recall(double[] recall){
        this.recall = recall;
    }

    public void setRecall(int[][] queryKNNList, int[][] realKNNList, int queryNum, int k){
        for(int i=0; i<queryNum; i++){
            int[] aQueryKNNList = queryKNNList[i];
            int[] aRealKNNList = realKNNList[i];

            ArrayList<Integer> aQueryKNN = new ArrayList<>(k);
            ArrayList<Integer> aRealKNN = new ArrayList<>(k);
            for(int j=0; j<k; j++){
                aQueryKNN.add(aQueryKNNList[j]);
                aRealKNN.add(aRealKNNList[j]);
            }
            aQueryKNN.retainAll(aRealKNN);
            double aRecall = aQueryKNN.size()/(double)k;
            recall[i] = aRecall;
        }

    }
}
