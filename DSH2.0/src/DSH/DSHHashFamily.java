package DSH;

import util.CKNNSearch;

import java.util.ArrayList;

/**
 * Created by weixun on 2017/10/9.
 */
public class DSHHashFamily {

    public ArrayList<float[]> hashFamily;

    public DSHHashFamily(ArrayList<float[]> hashFamily){
        /**
         * @Descri * @param hashFamilyram hashFamily:生成的hash函数族
         * @return
         */
        this.hashFamily = hashFamily;

    }

    public void setHashFamily(float[][] dataset, int n, int dimension, int hashFunctionNum, int c, int k,
                                            float sampleProportion, int alpha, float p1, float p2) {

        //--------------------------①挑选sample---------------------
        int sampleNum = (int)(n * sampleProportion);
        int[] sample = new int[sampleNum];
        Sample dshSample = new Sample(sampleNum);
        dshSample.getSample(sample, n);

        //--------------------------②生成CKNNList--------------------
        int[][] CKNNList = new int[sampleNum][c*k];
        CKNNSearch CKNN = new CKNNSearch(CKNNList);
        CKNN.setCKNNList(dataset, sample, n, dimension, sampleNum, c, k);

        //--------------------------③构造矩阵---------------------------
        MyMatrix matrix = new MyMatrix(n, sampleNum);
        double[][] weightMatrix = matrix.initialMatrix(CKNN.CKNNList, c, k);

        //--------------------------④计算hash函数-----------------------
        DSHGetHashFunction dshGetHashFunction = new DSHGetHashFunction(dimension);
        int count = 0;
        boolean isStop = true;
        while (count < hashFunctionNum && isStop){
            //---------------生成hash函数--------------------------
            float[] aHashFunction = dshGetHashFunction.getHashFunction(dataset, sample, weightMatrix, n, sampleNum);
            System.out.println("Hash函数" + count+ "计算完成");
            hashFamily.add(aHashFunction);
            //---------------更新权重矩阵---------------------------
            matrix.updataMatrix(weightMatrix, dataset, sample, aHashFunction, CKNNList, dimension, c, k, alpha, p1,p2);
            //---------------判断是否终止循环------------------------
            isStop = judgeStop(hashFamily, count, dimension);
            count++;
        }
    }

    private boolean judgeStop(ArrayList<float[]> hashFamily, int count, int dimension){
        boolean isStop = true;
        if(count > 0){
            double dis = 0d;
            float[] lastFunction = hashFamily.get(count);
            float[] formalFunction = hashFamily.get(count-1);
            for(int i=0; i<dimension; i++){
                dis += Math.abs(lastFunction[i]-formalFunction[i]);
            }
            if(dis <= 0.000000001){
                isStop = false;
            }
        }
        return isStop;
    }

}
