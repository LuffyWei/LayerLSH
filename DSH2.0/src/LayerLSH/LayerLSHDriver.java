package LayerLSH;

import LSH.*;
import io.ReadFile;
import io.WriteFile;
import util.CKNNSearch;
import util.Ratio;
import util.Recall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by weixun on 2017/10/15.
 */
public class LayerLSHDriver {

    int n;
    int dimension;
    int queryNum;
    int c;
    int k;
    int l1;
    int l2;
    int m;

    float w;
    float alpha;
    float beta;

    public void setW(float w) {
        this.w = w;
    }
    String datasetPath;
    String queryPath;
    String LSHResultPath;
    String KNNListPath;
    String LSHRecallPath;
    String LSHRatioPath;
    String LayerLSHResultPath;
    String LayerLSHRecallPath;
    String LayerLSHRatioPath;
    String lshHashTablePath;
    String layerLSHHashTablePath;

    public LayerLSHDriver(int n, int dimension, int queryNum, int c, int k, int l1, int l2, int m, float alpha, float beta, int w,
                     String datasetPath, String queryPath, String LSHResultPath,
                     String KNNListPath, String LSHRecallPath, String LSHRatioPath, String LayerLSHResultPath,
                          String LayerLSHRecallPath, String LayerLSHRatioPath, String lshHashTablePath,
                          String layerLSHHashTablePath){

        /**
         * @Descri
         * @param n :数据集大小
         * @param dimension ：数据集的维度
         * @param queryNum ：query的数目
         * @param c ： LSH进行cknn搜索时的参数c
         * @param k : LSH进行knn搜索时的参数k
         * @param l ：hashTable的数目
         * @param m ：hash签名的长度
         * @param w ：LSH中宽度限制参数w
         * @param alpha ：期望的搜索recall
         * @param beta ：期望的precision
         * @param datasetPath ：数据集的路径
         * @param queryPath ：query的路径
         * @param LSHResultPath ：LSH进行KNN搜索得到的结果存储路径
         * @param KNNListPath ：KNN搜索的标准结果
         * @param LSHRecallPath ：LSH进行knn搜索的召回率recall检测
         * @param LSHRatioPath ：LSH进行knn搜索的错误率ratio检测
         * @return
         */

        this.n = n;
        this.dimension = dimension;
        this.queryNum = queryNum;
        this.c = c;
        this.k = k;
        this.l1 = l1;
        this.l2 = l2;
        this.m = m;
//        this.w = 0f;
        this.w = w;
        this.alpha = alpha;
        this.beta = beta;

        this.datasetPath = datasetPath;
        this.queryPath = queryPath;
        this.LSHResultPath = LSHResultPath;
        this.KNNListPath = KNNListPath;
        this.LSHRecallPath = LSHRecallPath;
        this.LSHRatioPath = LSHRatioPath;
        this.LayerLSHResultPath = LayerLSHResultPath;
        this.LayerLSHRecallPath = LayerLSHRecallPath;
        this.LayerLSHRatioPath = LayerLSHRatioPath;
        this.lshHashTablePath = lshHashTablePath;
        this.layerLSHHashTablePath = layerLSHHashTablePath;
    }

    public void driver() throws IOException {

        //----------------------①读取data和query------------------------------
        float[][] dataset = new float[n][dimension];
        int[] query  = new int[queryNum];
        ReadFile reader_1 = new ReadFile(datasetPath);
        reader_1.readDataset(dataset, n, dimension);
//        ReadFile reader_2 = new ReadFile(queryPath);
//        reader_2.readQuery(query, queryNum, dimension);
        ReadFile reader_2 = new ReadFile(queryPath);
        reader_2.readQuery(query);

        //----------------------②串联hash函数确定-------------------------------
        LSHHashPara[] conHashFunction = new LSHHashPara[l1*m];
        LSHHashFamily lshHashFamily = new LSHHashFamily(conHashFunction);
//        float generateW = lshHashFamily.setHashFamily(dataset, n, dimension, l1, m);
//        setW(generateW);
        lshHashFamily.setHashFamily(dimension, w);

        //----------------------③生成hashTable---------------------------------
        LSHHashTable[] lshHashTable = new LSHHashTable[l1];
        for(int i=0; i<l1; i++){
            HashMap<String, ArrayList<Integer>> aLSHHashMap = new HashMap<>();
            LSHHashTable aLSHHashTable = new LSHHashTable(aLSHHashMap);
            aLSHHashTable.setLSHHashMap(dataset, conHashFunction, n, dimension, i, m, w);
            lshHashTable[i] = aLSHHashTable;
        }
//        WriteFile writer_1 = new WriteFile(lshHashTablePath);
//        writer_1.writeHashTable(lshHashTable, l);


        //----------------------④进行query查询----------------------------------
        int[] LSHSearchSize = new int[queryNum];
        int[][] LSHKNNList = new int[queryNum][k];
        LSHQuerySearch lshQuerySearch = new LSHQuerySearch(LSHKNNList);
        lshQuerySearch.setKNNList(dataset, query, conHashFunction, lshHashTable, LSHSearchSize, n, dimension, queryNum,
                k, l1, m, w, alpha);
//        WriteFile writer_2 = new WriteFile(this.LSHResultPath);
//        writer_2.writeKNNList(LSHKNNList, queryNum);

        //----------------------⑤准确率检测--------------------------------------
//        int[][] realKNNList = new int[queryNum][k];
//        CKNNSearch CKNN = new CKNNSearch(realKNNList);
//        CKNN.setCKNNList(dataset, query, n, dimension, queryNum, 1, k);
//        WriteFile writer_9 = new WriteFile(this.KNNListPath);
//        writer_9.writeKNNList(realKNNList, queryNum);
        int[][] realKNNList = new int[queryNum][k];
        ReadFile reader_3 = new ReadFile(KNNListPath);
        reader_3.readKNNList(realKNNList, k);
        //----------------------recall召回率计算----------------------------------
        double[] LSHRecall = new double[queryNum];
        Recall recallClass = new Recall(LSHRecall);
        recallClass.setRecall(LSHKNNList, realKNNList, queryNum, k);
//        WriteFile writer_3 = new WriteFile(this.LSHRecallPath);
//        writer_3.writeRecall(LSHRecall, queryNum);

        //----------------------ratio错误率计算-----------------------------------
        double[] LSHRatio = new double[queryNum];
        Ratio ratioClass = new Ratio(LSHRatio);
        ratioClass.setRatio(dataset, LSHKNNList, realKNNList, query, dimension, queryNum, k);
//        WriteFile writer_4 = new WriteFile(this.LSHRatioPath);
//        writer_4.writeRecall(LSHRatio, queryNum);

        int aveLSHSearchSize = 0;
        double aveLSHRecall = 0;
        double aveLSHRatio = 0;
        int fault_1 = 0;
        for(int i=0; i<queryNum; i++){
            aveLSHSearchSize += LSHSearchSize[i];
            aveLSHRecall += LSHRecall[i];
            if(LSHRatio[i] <= 10){
                aveLSHRatio += LSHRatio[i];
            }
            else {
                fault_1 += 1;
            }
        }
        System.out.println("LSH Candidate Num = " + aveLSHSearchSize/queryNum);
        System.out.println("LSH recall = " + aveLSHRecall/queryNum);
        System.out.println("LSH ratio = " + aveLSHRatio/(queryNum-fault_1));

        //----------------------⑥进行分层----------------------------------------
        //重新生成l2个hashTable
        LSHHashPara[] conHashFunction_1 = new LSHHashPara[l2*m];
        LSHHashFamily lshHashFamily_1 = new LSHHashFamily(conHashFunction_1);
//        float generateW_1 = lshHashFamily_1.setHashFamily(dataset, n, dimension, l2, m);
//        setW(generateW_1);
        lshHashFamily_1.setHashFamily(dimension, w);

        LSHHashTable[] lshHashTable_1 = new LSHHashTable[l2];
        for(int i=0; i<l2; i++){
            HashMap<String, ArrayList<Integer>> aLSHHashMap = new HashMap<>();
            LSHHashTable aLSHHashTable = new LSHHashTable(aLSHHashMap);
            aLSHHashTable.setLSHHashMap(dataset, conHashFunction_1, n, dimension, i, m, w);
            lshHashTable_1[i] = aLSHHashTable;
        }

        LayerLSHHashTable[] layerLSHHashTable = new LayerLSHHashTable[l2];
        ArrayList<HashMap<String, LayerLSHChildBucketPara>> tableChildBucketPara = new ArrayList<>(l2);
        ArrayList<HashMap<String, LSHHashPara[]>> tableChildHashFunction = new ArrayList<>(l2);
        for(int i=0; i<l2; i++){
            //原始hashTable中的hashMap
            HashMap<String, ArrayList<Integer>> aLSHHashMap = lshHashTable_1[i].LSHHashMap;
            //需要记录的各种子桶参数
            HashMap<String, ArrayList<Integer>> aLayerLSHHashMap = new HashMap<>();
            LayerLSHHashTable aLayerLSHHashTable = new LayerLSHHashTable(aLayerLSHHashMap);
            HashMap<String, LayerLSHChildBucketPara> aTableChildBucketPara = new HashMap<>();
            HashMap<String, LSHHashPara[]> aTableChildHashFunction = new HashMap<>();
            aLayerLSHHashTable.setLayerLSHHashMap(dataset, aLSHHashMap, aTableChildBucketPara, aTableChildHashFunction, n, dimension, k, l2, m, alpha, beta, w);
            layerLSHHashTable[i] = aLayerLSHHashTable;
            tableChildBucketPara.add(aTableChildBucketPara);
            tableChildHashFunction.add(aTableChildHashFunction);
        }
//        WriteFile writer_5 = new WriteFile(layerLSHHashTablePath);
//        writer_5.writeHashTable(layerLSHHashTable, l);

        //----------------------⑦进行query搜索----------------------------------
        int[] LayerLSHSearchSize = new int[queryNum];
        int[][] LayerLSHKNNList = new int[queryNum][k];
        LayerLSHQuerySearch layerLSHQuerySearch = new LayerLSHQuerySearch(LayerLSHKNNList);
        layerLSHQuerySearch.setKNNList(dataset, query, layerLSHHashTable, conHashFunction_1, tableChildBucketPara, tableChildHashFunction,
                LayerLSHSearchSize, n, dimension, queryNum, k, l2, m, w, alpha);
//        WriteFile writer_6 = new WriteFile(LayerLSHResultPath);
//        writer_6.writeKNNList(LayerLSHKNNList, queryNum);
        //----------------------⑧结果检测---------------------------------------
        //----------------------recall召回率计算----------------------------------
        double[] LayerLSHRecall = new double[queryNum];
        Recall recallClass_1 = new Recall(LayerLSHRecall);
        recallClass_1.setRecall(LayerLSHKNNList, realKNNList, queryNum, k);
//        WriteFile writer_7 = new WriteFile(this.LayerLSHRecallPath);
//        writer_7.writeRecall(LayerLSHRecall, queryNum);

        //----------------------ratio错误率计算-----------------------------------
        double[] LayerLSHRatio = new double[queryNum];
        Ratio ratioClass_1 = new Ratio(LayerLSHRatio);
        ratioClass_1.setRatio(dataset, LayerLSHKNNList, realKNNList, query, dimension, queryNum, k);
//        WriteFile writer_8 = new WriteFile(this.LayerLSHRatioPath);
//        writer_8.writeRecall(LayerLSHRatio, queryNum);

        int aveLayerLSHSearchSize = 0;
        double aveLayerLSHRecall = 0;
        double aveLayerLSHRatio = 0;
        int fault_2 = 0;
        for(int i=0; i<queryNum; i++){
            aveLayerLSHSearchSize += LayerLSHSearchSize[i];
            aveLayerLSHRecall += LayerLSHRecall[i];
            if(LayerLSHRatio[i] <=10){
                aveLayerLSHRatio += LayerLSHRatio[i];
            }
            else {
                fault_2 += 1;
            }
        }
        System.out.println("LayLSH Candidate Num = " + aveLayerLSHSearchSize/queryNum);
        System.out.println("LayeLSH recall = " + aveLayerLSHRecall/queryNum);
        System.out.println("LayerLSH ratio = " + aveLayerLSHRatio/(queryNum-fault_2));

    }

    public static void main(String[] args) throws IOException {
        /**
         * @Descri
         * @param n :数据集大小
         * @param dimension ：数据集的维度
         * @param queryNum ：query的数目
         * @param c ： LSH进行cknn搜索时的参数c
         * @param k : LSH进行knn搜索时的参数k
         * @param l ：hashTable的数目
         * @param m ：hash签名的长度
         * @param w ：LSH中宽度限制参数w
         * @param alpha ：期望的搜索准确率
         * @param beta ：期望的搜索比例
         * @param datasetPath ：数据集的路径
         * @param queryPath ：query的路径
         * @param LSHResultPath ：LSH进行KNN搜索得到的结果存储路径
         * @param KNNListPath ：KNN搜索的标准结果
         * @param LSHRecallPath ：LSH进行knn搜索的召回率recall检测
         * @param LSHRatioPath ：LSH进行knn搜索的错误率ratio检测
         * @return
         */
        int n = Integer.parseInt(args[0]);
        int dimension = Integer.parseInt(args[1]);
        int queryNum = Integer.parseInt(args[2]);
        int c = Integer.parseInt(args[3]);
        int k = Integer.parseInt(args[4]);
        int l1 = Integer.parseInt(args[5]);
        int l2 = Integer.parseInt(args[6]);
        int m = Integer.parseInt(args[7]);
//        float w = Float.parseFloat(args[7]);
        float alpha = Float.parseFloat(args[8]);
        float beta = Float.parseFloat(args[9]);
        int w = Integer.parseInt(args[10]);
//        String datasetPath = args[10];
//        String queryPath = args[11];
//        String KNNListPath = args[12];
        System.out.println("l1= "+ l1 + "  l2= " + l2 + "  m= " + m+ "  w= "+w);
        String datasetPath = "C:\\Users\\weixun\\Desktop\\LayerDSHdata\\kdd.txt";
        String queryPath = "C:\\Users\\weixun\\Desktop\\LayerDSHdata\\kdd_random_query.txt";
        String LSHResultPath = "C:\\Users\\weixun\\Desktop\\LSHResult.txt";
//        String KNNListPath = "C:\\Users\\weixun\\Desktop\\KNNList.txt";
        String KNNListPath = "data\\KNNList.txt";
        String LSHRecallPath = "C:\\Users\\weixun\\Desktop\\LSHRecall.txt";
        String LSHRatioPath = "C:\\Users\\weixun\\Desktop\\LSHRatio.txt";
        String LayerLSHResultPath = "C:\\Users\\weixun\\Desktop\\LayerLSHResult.txt";
        String LayerLSHRecallPath = "C:\\Users\\weixun\\Desktop\\LayerLSHRecall.txt";
        String LayerLSHRatioPath = "C:\\Users\\weixun\\Desktop\\LayerLSHRatio.txt";
        String lshHashTablePath = "C:\\Users\\weixun\\Desktop\\lshHashTable.txt";
        String layerLSHHashTablePath = "C:\\Users\\weixun\\Desktop\\layerLSHHashTable.txt";

        LayerLSHDriver layerLSHDriver = new LayerLSHDriver(n, dimension, queryNum, c, k, l1, l2, m, alpha, beta,w,
                datasetPath, queryPath, LSHResultPath, KNNListPath, LSHRecallPath, LSHRatioPath, LayerLSHResultPath,
                LayerLSHRecallPath, LayerLSHRatioPath, lshHashTablePath, layerLSHHashTablePath);

        layerLSHDriver.driver();

    }
}
