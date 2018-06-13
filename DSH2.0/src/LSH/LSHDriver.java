package LSH;

import io.ReadFile;
import io.WriteFile;
import util.CKNNSearch;
import util.Ratio;
import util.Recall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by weixun on 2017/10/13.
 */
public class LSHDriver {

    int n;
    int dimension;
    int queryNum;
    int c;
    int k;
    int l;
    int m;

    float w;
    public void setW(float w) {
        this.w = w;
    }
    float alpha;

    String datasetPath;
    String queryPath;
    String LSHResultPath;
    String KNNListPath;
    String LSHRecallPath;
    String LSHRatioPath;
    String lshHashTablePath;

    public LSHDriver(int n, int dimension, int queryNum, int c, int k, int l, int m, float alpha,
                     String datasetPath, String queryPath, String LSHResultPath,
                     String KNNListPath, String LSHRecallPath, String LSHRatioPath, String lshHashTablePath){

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
        this.l = l;
        this.m = m;
        this.w = 0f;
        this.alpha = alpha;

        this.datasetPath = datasetPath;
        this.queryPath = queryPath;
        this.LSHResultPath = LSHResultPath;
        this.KNNListPath = KNNListPath;
        this.LSHRecallPath = LSHRecallPath;
        this.LSHRatioPath = LSHRatioPath;
        this.lshHashTablePath = lshHashTablePath;
    }

    public void driver() throws IOException {

        //----------------------①读取data和query------------------------------
        float[][] dataset = new float[n][dimension];
        int[] query  = new int[queryNum];
        ReadFile reader_1 = new ReadFile(datasetPath);
        reader_1.readDataset(dataset, n, dimension);
        ReadFile reader_2 = new ReadFile(queryPath);
        reader_2.readQuery(query, queryNum, dimension);

        //----------------------②串联hash函数确定-------------------------------
        LSHHashPara[] conHashFunction = new LSHHashPara[l*m];
        LSHHashFamily lshHashFamily = new LSHHashFamily(conHashFunction);
        float generateW = lshHashFamily.setHashFamily(dataset, n, dimension, l, m);
        setW(generateW);
//        System.out.println(w);
//        System.out.println(conHashFunction[0].toString());


        //----------------------③生成hashTable---------------------------------
        LSHHashTable[] lshHashTable = new LSHHashTable[l];
        for(int i=0; i<l; i++){
            HashMap<String, ArrayList<Integer>> aLSHHashMap = new HashMap<>();
            LSHHashTable aLSHHashTable = new LSHHashTable(aLSHHashMap);
            aLSHHashTable.setLSHHashMap(dataset, conHashFunction, n, dimension, i, m, w);
            lshHashTable[i] = aLSHHashTable;
        }
        WriteFile writer_1 = new WriteFile(lshHashTablePath);
        writer_1.writeHashTable(lshHashTable, l);


        //----------------------④进行query查询----------------------------------
        int[] LSHSearchSize = new int[queryNum];
        int[][] LSHKNNList = new int[queryNum][k];
        LSHQuerySearch lshQuerySearch = new LSHQuerySearch(LSHKNNList);
        lshQuerySearch.setKNNList(dataset, query, conHashFunction, lshHashTable, LSHSearchSize, n, dimension, queryNum,
                k, l, m, w, alpha);
        WriteFile writer_2 = new WriteFile(this.LSHResultPath);
        writer_2.writeKNNList(LSHKNNList, queryNum);

        //----------------------⑤准确率检测--------------------------------------
//        int[][] realKNNList = new int[queryNum][k];
//        CKNNSearch CKNN = new CKNNSearch(realKNNList);
//        CKNN.setCKNNList(dataset, query, n, dimension, queryNum, 1, k);
//        WriteFile writer_2 = new WriteFile(this.KNNListPath);
//        writer_2.writeKNNList(realKNNList, queryNum);
        int[][] realKNNList = new int[queryNum][k];
        ReadFile reader_3 = new ReadFile(KNNListPath);
        reader_3.readKNNList(realKNNList, k);
        //----------------------recall召回率计算----------------------------------
        double[] LSHRecall = new double[queryNum];
        Recall recallClass = new Recall(LSHRecall);
        recallClass.setRecall(LSHKNNList, realKNNList, queryNum, k);
        WriteFile writer_3 = new WriteFile(this.LSHRecallPath);
        writer_3.writeRecall(LSHRecall, queryNum);

        //----------------------ratio错误率计算-----------------------------------
        double[] LSHRatio = new double[queryNum];
        Ratio ratioClass = new Ratio(LSHRatio);
        ratioClass.setRatio(dataset, LSHKNNList, realKNNList, query, dimension, queryNum, k);
        WriteFile writer_4 = new WriteFile(this.LSHRatioPath);
        writer_4.writeRecall(LSHRatio, queryNum);

        int aveLSHSearchSize = 0;
        double aveLSHRecall = 0;
        double aveLSHRatio = 0;
        for(int i=0; i<queryNum; i++){
            aveLSHSearchSize += LSHSearchSize[i];
            aveLSHRecall += LSHRecall[i];
            aveLSHRatio += LSHRatio[i];
        }
        System.out.println("LSH Candidate Num = " + aveLSHSearchSize/queryNum);
        System.out.println("LSH recall = " + aveLSHRecall/queryNum);
        System.out.println("LSH ratio = " + aveLSHRatio/queryNum);
    }

    public static void main(String[] args) throws IOException {
        int n = Integer.parseInt(args[0]);
        int dimension = Integer.parseInt(args[1]);
        int queryNum = Integer.parseInt(args[2]);
        int c = Integer.parseInt(args[3]);
        int k = Integer.parseInt(args[4]);
        int l = Integer.parseInt(args[5]);
        int m = Integer.parseInt(args[6]);
//        float w = Float.parseFloat(args[7]);
        float alpha = Float.parseFloat(args[7]);

        String datasetPath = "C:\\Users\\weixun\\Desktop\\LayerDSHdata\\color.txt";
        String queryPath = "C:\\Users\\weixun\\Desktop\\LayerDSHdata\\color_dense_query.txt";
        String LSHResultPath = "C:\\Users\\weixun\\Desktop\\LSHResult.txt";
//        String KNNListPath = "C:\\Users\\weixun\\Desktop\\KNNList.txt";
        String KNNListPath = "data\\KNNList.txt";
        String LSHRecallPath = "C:\\Users\\weixun\\Desktop\\LSHRecall.txt";
        String LSHRatioPath = "C:\\Users\\weixun\\Desktop\\LSHRatio.txt";
        String lshHashTablePath = "C:\\Users\\weixun\\Desktop\\lshHashTable.txt";

        LSHDriver lshDriver = new LSHDriver(n, dimension, queryNum, c, k, l, m, alpha,
                datasetPath, queryPath, LSHResultPath, KNNListPath, LSHRecallPath, LSHRatioPath, lshHashTablePath);

        lshDriver.driver();

    }

}
