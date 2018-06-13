package LSH;

import LayerDSH.LayerDSHPara;

/**
 * Created by weixun on 2017/10/13.
 */
public class LSHHashValue {

    public static int getHashValue(float[] item, LSHHashPara aHashPara, int dimension, float w){
        double res = 0;
        float[] a = aHashPara.a;
        for(int i=0; i<dimension; i++){
            res += item[i]*a[i];
        }

        int hashValue = (int)((res + aHashPara.b) / w) ;
        return hashValue;
    }
}
