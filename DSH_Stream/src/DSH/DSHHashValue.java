package DSH;

/**
 * Created by weixun on 2017/10/9.
 */
public class DSHHashValue {
    /**
     * @Description: DSH中hash值得计算方式:
     * h(o) = 0, if h(o) ≤ 0; else h(o) = 1
     */
    public static int getHashValue(float[] sample, float[] hashFunction, int dimension){
        int realHashValue = 0;
        double hashValue = 0d;
        for(int i=0; i<dimension; i++){
            hashValue += sample[i] * hashFunction[i];
        }

        if(hashValue > 0){
            realHashValue = 1;
        }
        else {
            realHashValue = 0;
        }
        return realHashValue;
    }
}
