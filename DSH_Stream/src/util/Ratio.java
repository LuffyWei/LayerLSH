package util;


/**
 * Created by weixun on 2017/10/10.
 */
public class Ratio {
    public double[] ratio;

    public Ratio(double[] ratio){
        this.ratio = ratio;
    }

    public void setRatio(float[][] dataset, int[][] queryKNNList, int[][] realKNNList, int[] query,
                             int dimension, int queryNum, int k){
        for(int i=0; i<queryNum; i++){
            double aRatio = 0;
            int[] aqueryKNNList = queryKNNList[i];
            int[] aRealKNNlist = realKNNList[i];
            for(int j=0; j<k; j++){
                double d1 = EucDistance.getDistance(dataset[aqueryKNNList[j]], dataset[query[i]], dimension);
                double d2 = EucDistance.getDistance(dataset[aRealKNNlist[j]], dataset[query[i]], dimension);
                if(d2 == 0d){
                    aRatio += 1;
                }
                else {
                    aRatio += d1/d2;
                }


            }
            ratio[i] = aRatio/k;
        }

    }
}
