package LSH;

import java.util.ArrayList;

/**
 * Created by weixun on 2017/10/13.
 */
public class LSHHashFamily {

    LSHHashPara[] conHashFunction;

    public LSHHashFamily(LSHHashPara[] conHashFunction){
        this.conHashFunction = conHashFunction;
    }

    /**
     * 先自动生成满足高斯分布的向量a，然后再通过a与原始数据点的乘积的均值设置w，然后再设置参数b
     * @param dataset
     * @param n
     * @param dimension
     * @param l
     * @param m
     * @return
     */
    public float setHashFamily(float[][] dataset, int n, int dimension, int l, int m){
        int hashFunctionNum = conHashFunction.length;
        //----------------------生成a------------------------------------------
        for(int i=0; i<hashFunctionNum; i++){
            float[] a = new float[dimension];
            float b= 0f;
            LSHHashPara aLSHHashPara = new LSHHashPara(a,b);
            aLSHHashPara.setLSHPara_a(dimension);
            conHashFunction[i] = aLSHHashPara;
        }

        //-----------------------设置w值----------------------------------------
        LSHSetW lshSetW = new LSHSetW();
        lshSetW.setW(dataset, conHashFunction, n, dimension, l, m);
        float w = lshSetW.w;

        //----------------------生成b--------------------------------------------
        for(int i=0; i<hashFunctionNum; i++){
            conHashFunction[i].setLSHPara_b(w);
        }

        return w;
    }


    public float setHashFamily(float[][] dataset, ArrayList<Integer> aBigBucket, int dimension, int l, int m){
        int hashFunctionNum = conHashFunction.length;
        //----------------------生成a------------------------------------------
        for(int i=0; i<hashFunctionNum; i++){
            float[] a = new float[dimension];
            float b= 0f;
            LSHHashPara aLSHHashPara = new LSHHashPara(a,b);
            aLSHHashPara.setLSHPara_a(dimension);
            conHashFunction[i] = aLSHHashPara;
        }

        //-----------------------设置w值----------------------------------------
        LSHSetW lshSetW = new LSHSetW();
        lshSetW.setW(dataset, conHashFunction, aBigBucket, dimension, l, m);
        float w = lshSetW.w;

        //----------------------生成b--------------------------------------------
        for(int i=0; i<hashFunctionNum; i++){
            conHashFunction[i].setLSHPara_b(w);
        }

        return w;
    }

    public float setHashFamily(int dimension, float w){
        int hashFunctionNum = conHashFunction.length;
        //----------------------生成a------------------------------------------
        for(int i=0; i<hashFunctionNum; i++){
            float[] a = new float[dimension];
            float b= 0f;
            LSHHashPara aLSHHashPara = new LSHHashPara(a,b);
            aLSHHashPara.setLSHPara_a(dimension);
            conHashFunction[i] = aLSHHashPara;
        }

        //----------------------生成b--------------------------------------------
        for(int i=0; i<hashFunctionNum; i++){
            conHashFunction[i].setLSHPara_b(w);
        }

        return w;
    }

}
