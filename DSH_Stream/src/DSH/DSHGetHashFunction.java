package DSH;

import Jama.Matrix;
/**
 * Created by weixun on 2017/10/9.
 */
public class DSHGetHashFunction {
    int dimension;

    public DSHGetHashFunction(int dimension){
        this.dimension = dimension;
    }

    /**
     *TODO  根据DSH原理生成一个hash函数
     *		根据方程((XDn - QW)Xt + (QDq - XWt)Qt)a = iXXta,求出该方程最小特征值所对应的特征向量
     *		其中X是原始数据集组成的矩阵，即Xdn,d行n列，即每一列代表一个数据点
     *		Q是有sample组成的矩阵，即Qdq,d行q列，每一列代表一个数据点
     *		W是权重矩阵，即Wqn,q行n列
     *@return double[]
     */
    public float[] getHashFunction(float[][] dataset, int[] sample, double[][] weightMatrix,
                                    int n, int sampleNum){
        float[] aFunction = new float[dimension];

        //----------------------------①将数据集转化为X(d*n)的形式------------------
        double[][] arrayData = new double[dimension][n];
        for(int j=0; j<n; j++){
            for(int i=0; i<dimension; i++){
                arrayData[i][j] = dataset[j][i];
            }
        }
        //----------------------------②将sample转化为Q(d*q)的形式------------------
        double[][] arraySample = new double[dimension][sampleNum];
        for(int j=0; j<sampleNum; j++){
            for(int i=0; i<dimension; i++){
                arraySample[i][j] = dataset[sample[j]][i];
            }
        }
        //----------------------------③计算Dn,Dq,XDn,QDq--------------------------
        double[] Dn = sumOfColumn(weightMatrix, n, sampleNum);
        double[] Dq = sumOfRow(weightMatrix, n, sampleNum);
        double[][] ArrayXDn = specialMatrixMul(arrayData, Dn, dimension);
        double[][] ArrayQDq = specialMatrixMul(arraySample, Dq, dimension);
        //----------------------------④矩阵转换------------------------------------
        //矩阵A就是系数矩阵
        Matrix X = new Matrix(arrayData);
        Matrix Q = new Matrix(arraySample);
        Matrix W = new Matrix(weightMatrix);
        Matrix XDn = new Matrix(ArrayXDn);
        Matrix QDq = new Matrix(ArrayQDq);
        Matrix QW = Q.times(W);
        Matrix XWt = X.times(W.transpose());
        Matrix XDnQW = XDn.minus(QW);
        Matrix QDqXWt = QDq.minus(XWt);
        Matrix XDnQWXt = XDnQW.times(X.transpose());
        Matrix QDqXWtQt = QDqXWt.times(Q.transpose());
        Matrix XDnQWXtQDqXWtQt = XDnQWXt.plus(QDqXWtQt);
        Matrix XXt = X.times(X.transpose());
        Matrix A = (XXt.inverse()).times(XDnQWXtQDqXWtQt);
        //----------------------------⑤求得最小的特征值-----------------------------
        /**
         * 求解矩阵A的特征值以及对应的特征向量
         eig():特征值
         getD():特征值对应的对角矩阵
         getV():特征向量组成的矩阵
        * */
        Matrix lambda = A.eig().getD();
        Matrix eigenvector = A.eig().getV();
        double minLambda = Double.MAX_VALUE;
        int index = 0;
        for(int i=0; i<lambda.getRowDimension(); i++){
            double temp = lambda.get(i, i);
            if(minLambda > temp){
                minLambda = temp;
                index = i;
            }
        }
        //----------------------------⑥获取最小特征值对应的特征向量-------------------------
        for(int i=0; i<dimension; i++){
            aFunction[i] = (float)(eigenvector.get(i, index));
        }

        return aFunction;
    }

    //方程中XD以及QD'都是这样相乘的
    public double[][] specialMatrixMul(double[][] arrayData, double[] D, int dimension){
        int itemNum = D.length;
        for(int j=0; j<itemNum; j++){
            double Dj = D[j];
            for(int i=0; i<dimension; i++){
                arrayData[i][j] = arrayData[i][j]*Dj;
            }
        }
        return arrayData;
    }

    //Dq是一个对角矩阵，其中Dqjj = W的每一行之和
    public double[] sumOfRow(double[][] weightMatrix, int n , int sampleNum){
        double[] Dq = new double[sampleNum];
        for(int i=0; i<sampleNum; i++){
            double sum = 0d;
            for(int j=0; j<n; j++){
                sum += weightMatrix[i][j];
            }
            Dq[i] = sum;
        }

        return Dq;
    }

    //Dn是一个对角矩阵，其中Dnii = W中每一列之和
    public double[] sumOfColumn(double[][] weightMatrix, int n, int sampleNum){
        double[] Dn = new double[n];
        for(int j=0; j<n; j++){
            double sum = 0d;
            for(int i=0; i<sampleNum; i++){
                sum += weightMatrix[i][j];
            }
            Dn[j] = sum;
        }

        return Dn;
    }
}
