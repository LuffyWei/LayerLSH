package DSH;

import io.ReadFile;
import io.WriteFile;
import util.Ratio;
import util.Recall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by weixun on 2017/10/4.
 */
public class DSHDriver {

    int n;
    int dimension;
    int queryNum;
    int hashFunctionNum;
    float sampleProportion;
    int c;
    int k;
    int alpha;
    float p1;
    float p2;
    int l;
    int m;

    String datasetPath;
    String queryPath;
    String hashFunctionPath;
    String DSHResultPath;
    String KNNListPath;
    String DSHRecallPath;
    String DSHRatioPath;
    String dshHashTablePath;


    public DSHDriver(int n, int dimension, int queryNum, int hashFunctionNum, float sampleProportion, int c, int k,
                     int alpha, float p1, float p2, int l, int m,
                     String datasetPath, String queryPath, String hashFunctionPath, String DSHResultPath,
                     String KNNListPath, String DSHRecallPath, String DSHRatioPath, String dshHashTablePath
                     ){
        /**
         * @Descri
         * @param n :数据集大小
         * @param dimension ：数据集的维度
         * @param queryNum ：query的数目
         * @param hashFunctionNum ：DSH中生成的hash函数的期望数目
         * @param sampleProportion ：DSH中生成hash函数时挑选的sample的比例
         * @param c ： DSH进行cknn搜索时的参数c
         * @param k : DSH进行knn搜索时的参数k
         * @param alpha : DSH生成hash函数时，权重矩阵变换时的底数
         * @param p1 ：DSH进行knn搜索时期望的召回率p1
         * @param p2 : DSH进行cknn搜索时的错误率
         * @param l ：hashTable的数目
         * @param m ：hash签名的长度
         * @param datasetPath ：数据集的路径
         * @param queryPath ：query的路径
         * @param hashFunctionPath ：生成的hash函数的存取路径
         * @param DSHResultPath ：DSH进行KNN搜索得到的结果存储路径
         * @param KNNListPath ：KNN搜索的标准结果
         * @param DSHRecallPath ：DSH进行knn搜索的召回率recall检测
         * @param DSHRatioPath ：DSH进行knn搜索的错误率ratio检测
         * @return
         */
        this.n = n;
        this.dimension = dimension;
        this.queryNum = queryNum;
        this.hashFunctionNum = hashFunctionNum;
        this.sampleProportion = sampleProportion;
        this.c = c;
        this.k = k;
        this.alpha = alpha;
        this.p1 = p1;
        this.p2 = p2;
        this.l = l;
        this.m = m;

        this.datasetPath = datasetPath;
        this.queryPath = queryPath;
        this.hashFunctionPath = hashFunctionPath;
        this.DSHResultPath = DSHResultPath;
        this.KNNListPath = KNNListPath;
        this.DSHRecallPath = DSHRecallPath;
        this.DSHRatioPath = DSHRatioPath;
        this.dshHashTablePath = dshHashTablePath;
    }


    public void driver() throws IOException {

        //----------------------①读取data和query------------------------------
        float[][] dataset = new float[n][dimension];
        int[] query  = new int[queryNum];
        ReadFile reader_1 = new ReadFile(datasetPath);
        reader_1.readDataset(dataset, n, dimension);
        ReadFile reader_2 = new ReadFile(queryPath);
        reader_2.readQuery(query, queryNum, dimension);

        //----------------------②生成hash函数族--------------------------------
//        ArrayList<float[]> hashFamily = new ArrayList<>();
//        DSHHashFamily dshHashFamily = new DSHHashFamily(hashFamily);
//        dshHashFamily.setHashFamily(dataset, n, dimension, hashFunctionNum, c, k, sampleProportion, alpha, p1, p2);
//        WriteFile writer_1 = new WriteFile(hashFunctionPath);
//        writer_1.writeHashFunction(dshHashFamily.hashFamily);
//        System.out.println(hashFamily.hashCode());
//        System.out.println(dshHashFamily.hashFamily.hashCode());
        ArrayList<float[]> hashFamily = new ArrayList<>();
        ReadFile reader_3 = new ReadFile(hashFunctionPath);
        reader_3.readHashFunction(hashFamily, dimension);

        //----------------------③生成串联hashFunction--------------------------
        float[][] conHashFunction = new float[l*m][dimension];
        int realHashFunctionNum = hashFamily.size();
        Random rand = new Random();
        for(int i=0; i<l*m; i++){
            conHashFunction[i] = hashFamily.get(rand.nextInt(realHashFunctionNum));
        }

        //----------------------④生成hashTable--------------------------------
        DSHHashTable[] dshHashTable = new DSHHashTable[l];
        for(int i=0; i<l; i++){
            HashMap<String, ArrayList<Integer>> aDSHHashMap = new HashMap<>();
            DSHHashTable aDSHHashTable = new DSHHashTable(aDSHHashMap);
            aDSHHashTable.setHashTable(dataset, conHashFunction, n ,dimension, i, m);
            dshHashTable[i] = aDSHHashTable;
        }
        WriteFile writer_6 = new WriteFile(dshHashTablePath);
        writer_6.writeHashTable(dshHashTable, l);

        //----------------------⑤对query进行查询--------------------------------
        int[] DSHSearchSize = new int[queryNum];
        int[][] DSHKNNList = new int[queryNum][k];
        DSHQuerySearch dshQuerySearch = new DSHQuerySearch(DSHKNNList);
        dshQuerySearch.setDSHKNN(dataset, query, conHashFunction, dshHashTable, DSHSearchSize, n, dimension, k, queryNum, l, m, p1);
        WriteFile writer_5 = new WriteFile(this.DSHResultPath);
        writer_5.writeKNNList(DSHKNNList, queryNum);

        //----------------------⑥结果检测---------------------------------------

            //----------------------真实KNN搜索--------------------------------------
//        int[][] realKNNList = new int[queryNum][k];
//        CKNNSearch CKNN = new CKNNSearch(realKNNList);
//        CKNN.setCKNNList(dataset, query, n, dimension, queryNum, 1, k);
//        WriteFile writer_2 = new WriteFile(this.KNNListPath);
//        writer_2.writeKNNList(realKNNList, queryNum);
        int[][] realKNNList = new int[queryNum][k];
        ReadFile reader_4 = new ReadFile(KNNListPath);
        reader_4.readKNNList(realKNNList, k);

            //----------------------recall召回率计算----------------------------------
        double[] DSHRecall = new double[queryNum];
        Recall recallClass = new Recall(DSHRecall);
        recallClass.setRecall(DSHKNNList, realKNNList, queryNum, k);
        WriteFile writer_3 = new WriteFile(this.DSHRecallPath);
        writer_3.writeRecall(DSHRecall, queryNum);

            //----------------------ratio错误率计算-----------------------------------
        double[] DSHRatio = new double[queryNum];
        Ratio ratioClass = new Ratio(DSHRatio);
        ratioClass.setRatio(dataset, DSHKNNList, realKNNList, query, dimension, queryNum, k);
        WriteFile writer_4 = new WriteFile(this.DSHRatioPath);
        writer_4.writeRecall(DSHRatio, queryNum);

        int aveDSHSearchSize = 0;
        double aveDSHRecall = 0;
        double aveDSHRatio = 0;
        for(int i=0; i<queryNum; i++){
            aveDSHSearchSize += DSHSearchSize[i];
            aveDSHRecall += DSHRecall[i];
            aveDSHRatio += DSHRatio[i];
        }
        System.out.println("DSH Candidate Num = " + aveDSHSearchSize/queryNum);
        System.out.println("DSH recall = " + aveDSHRecall/queryNum);
        System.out.println("DSH ratio = " + aveDSHRatio/queryNum);
    }



    public static void main(String[] args) throws IOException {
        int n = Integer.parseInt(args[0]);
        int dimension = Integer.parseInt(args[1]);
        int queryNum = Integer.parseInt(args[2]);
        int hashFunctionNum = Integer.parseInt(args[3]);
        float sampleProportion = Float.parseFloat(args[4]);
        int c = Integer.parseInt(args[5]);
        int k = Integer.parseInt(args[6]);
        int alpha = Integer.parseInt(args[7]);
        float p1 = Float.parseFloat(args[8]);
        float p2 = Float.parseFloat(args[9]);
        int l = Integer.parseInt(args[10]);
        int m = Integer.parseInt(args[11]);

        String datasetPath = "C:\\Users\\weixun\\Desktop\\LayerDSHdata\\color.txt";
        String queryPath = "C:\\Users\\weixun\\Desktop\\LayerDSHdata\\color_dense_query.txt";
//        String hashFunctionPath = "C:\\Users\\weixun\\Desktop\\hashFunction.txt";
        String hashFunctionPath = "data\\hashFunction.txt";
        String DSHResultPath = "C:\\Users\\weixun\\Desktop\\DSHResult.txt";
//        String KNNListPath = "C:\\Users\\weixun\\Desktop\\KNNList.txt";
        String KNNListPath = "data\\KNNList.txt";
        String DSHRecallPath = "C:\\Users\\weixun\\Desktop\\DSHRecall.txt";
        String DSHRatioPath = "C:\\Users\\weixun\\Desktop\\DSHRatio.txt";
        String dshHashTablePath = "C:\\Users\\weixun\\Desktop\\dshHashTable.txt";

        DSHDriver dshDriver = new DSHDriver(n, dimension, queryNum, hashFunctionNum, sampleProportion, c, k,
                alpha, p1, p2, l, m,
                datasetPath, queryPath, hashFunctionPath, DSHResultPath, KNNListPath, DSHRecallPath, DSHRatioPath,
                dshHashTablePath);

        dshDriver.driver();
    }
}
