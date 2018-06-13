package DSH;

import DSH.DSHHashValue;

import java.util.HashSet;

/**
 * Created by weixun on 2017/10/9.
 */
public class Matrix {
    int n;
    int sampleNum;

    public Matrix(int n, int sampleNum){
        this.n = n;
        this.sampleNum = sampleNum;
    }

    public double[][] initialMatrix(int[][] CKNNList, int c, int k){
        int ck = c*k;
        double[][] weightMatrix = new double[sampleNum][n];
        for(int i=0; i<sampleNum; i++){
            for(int j=0; j<n; j++){
                weightMatrix[i][j] = -1d;
            }
        }

        for(int i=0; i<sampleNum; i++){
            int[] aCKNNList = CKNNList[i];
            for(int j=0; j<k; j++){
                weightMatrix[i][aCKNNList[j]] = 1d;
            }
            for(int j=k; j<ck; j++){
                weightMatrix[i][aCKNNList[j]] = 0d;
            }
        }

        return weightMatrix;
    }

    public void updataMatrix(double[][] weightMatrix, float[][] dataset, int[] sample, float[] aHashFunction,
                             int[][] CKNNList, int dimension, int c, int k, int alpha, float p1, float p2){
        int ck = c*k;
        for(int i=0; i<sampleNum; i++){
            int sampleId = sample[i];
            float[] aSample = dataset[sampleId];
            int[] aCKNNList = CKNNList[i];
            int sampleHash = DSHHashValue.getHashValue(aSample, aHashFunction, dimension);

            //---------------------计算KNN pairs-----------------------
            for(int j=0; j<k; j++){
                int aKNNId = aCKNNList[j];
                float[] item = dataset[aKNNId];
                int itemHash = DSHHashValue.getHashValue(item, aHashFunction, dimension);
                int phi = 0;
                //计算φ（qi,oj）
                if(itemHash == sampleHash){
                    phi = 0;
                }
                else {
                    phi = 1;
                }
                weightMatrix[i][aKNNId] = weightMatrix[i][aKNNId] * (Math.pow(alpha, p1-1+phi));
            }

            //----------------------计算non CKNN pairs--------------------
            HashSet<Integer> aCKNNSet = new HashSet<>(ck);
            for(int s=0; s<ck; s++){
                aCKNNSet.add(aCKNNList[s]);
            }
            for(int j=0; j<n; j++){
                float[] item = dataset[j];
                int itemHash = DSHHashValue.getHashValue(item, aHashFunction, dimension);
                int phi = 0;
                //计算φ（qi,oj）
                if(itemHash == sampleHash){
                    phi = 0;
                }
                else {
                    phi = 1;
                }
                if(!aCKNNSet.contains(j)){
                    weightMatrix[i][j] = weightMatrix[i][j] * (Math.pow(alpha, -(p2-1+phi)));
                }
            }

        }

    }
}
