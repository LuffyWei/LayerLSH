package LSH;

import io.WriteFile;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by weixun on 2017/10/14.
 */
public class LSHSetW {
    public float w;

    public LSHSetW(){}
    public LSHSetW(float w){
        this.w = w;
    }

    /**
     * 这里是对LSH中最重要的参数w的设定，根据自动生成的满足高斯分布的向量a与原始数据点x的乘积，求取所有乘积的平均值作为w
     * @param dataset
     * @param conHashFunction
     * @param n
     * @param dimension
     * @param l
     * @param m
     */
    public void setW(float[][] dataset, LSHHashPara[] conHashFunction, int n, int dimension, int l, int m)
//            throws IOException
    {
        String avPath = "C:\\Users\\weixun\\Desktop\\av.txt";
        String avRowSumPath = "C:\\Users\\weixun\\Desktop\\avRowSum.txt";

//        float[][] av = new float[n][l*m];
//        float[] avRowSum = new float[n];
        float result = 0;
        for(int i=0; i<n; i++){
            float[] item = dataset[i];
            float aAvRowSum = 0;
            for(int j=0; j<l*m; j++){
                float res = 0;
                LSHHashPara aLSHPara = conHashFunction[j];
                for(int s=0; s<dimension; s++){
                    res += item[s] * aLSHPara.a[s];
                }
//                av[i][j] = Math.abs(res);
                aAvRowSum += Math.abs(res);
            }
//            avRowSum[i] = aAvRowSum / (l*m);
            result += aAvRowSum / (l*m);
        }
        w = result /n;
//        System.out.println(w);
//        WriteFile writer_1 = new WriteFile(avPath);
//        writer_1.writeAvValue(av, n);
//        WriteFile writer_2 = new WriteFile(avRowSumPath);
//        writer_2.writeAvRowSum(avRowSum, n);

    }

    public void setW(float[][] dataset, LSHHashPara[] conHashFunction, ArrayList<Integer> aBigBucket, int dimension, int l, int m)
//            throws IOException
    {

        float result = 0;
        int size = aBigBucket.size();
        for (int i = 0; i < size; i++) {
            float[] item = dataset[aBigBucket.get(i)];
            float aAvRowSum = 0;
            for (int j = 0; j < l * m; j++) {
                float res = 0;
                LSHHashPara aLSHPara = conHashFunction[j];
                for (int s = 0; s < dimension; s++) {
                    res += item[s] * aLSHPara.a[s];
                }
                aAvRowSum += Math.abs(res);
                result += aAvRowSum / (l * m);
            }
            w = result / size;

        }
    }

}
