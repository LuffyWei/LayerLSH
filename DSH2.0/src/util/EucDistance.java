package util;

/**
 * Created by weixun on 2017/10/9.
 */
public class EucDistance {

    public static double getDistance(float[] item1, float[] item2, int dimension){
        double dis = 0d;
        for(int i=0; i<dimension; i++){
            dis += (item1[i] - item2[i]) * (item1[i] - item2[i]);
        }
        return Math.sqrt(dis);
    }

}
