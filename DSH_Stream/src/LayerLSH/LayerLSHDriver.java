package LayerLSH;

import LSH.*;
import io.ReadFile;
import io.WriteFile;


import java.io.*;
import java.util.*;

/**
 * Created by weixun on 2017/10/15.
 */
public class LayerLSHDriver {

    int n;
    int dimension;
    int k;
    int l;
    int m;

    float w;
    float alpha;
    float beta;
    float p1;
    float p2;

    String trainPath;
    String testPath;
    String queryPath;
    String KNNListPath;
    String throughputPath;
    String costPath;

    int count = 1;
    boolean isIteration = true;
    int baseIndex = 0;
    int currentIndex = 0;
    ArrayList<Float> throughput = new ArrayList<>();

    ArrayList<ArrayList<Integer>> cost = new ArrayList<>(20);
    int[] queryList = new int[20];
    public void setW(float w) {
        this.w = w;
    }


    public LayerLSHDriver(int n, int dimension, int k, int l, int m, float alpha, float beta, float p1, float p2,
                          String trainPath, String testPath, String queryPath, String KNNListPath, String throughputPath, String costPath){

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
         * @param beta ：期望的搜索precision
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
        this.k = k;
        this.l = l;
        this.m = m;
        this.w = 0f;
        this.alpha = alpha;
        this.beta = beta;
        /**LayerLSH流处理用到的两个参数： p1, p2
         * 背景：原始LayerLSH中，当bucket的size达到T的时候，就开始分裂。
         * p1: 正式判断分裂（从头到尾判断，查看是否有big bucket）时，分裂阈值时p1*T
         *
         * p2: 加入p2的目的是：考虑的流处理中有删除操作，在某个很短的时间片以内，此时某个桶如果达到分裂阈值T，但是在该时间片以内这个桶又删除部分点，
         *     桶的大小又小于分裂阈值T,这样就避免了一次同分裂开销（因为按照原始LayerLSH，一旦达到分裂阈值就要分裂。）
         * */
        this.p1 = p1;
        this.p2 = p2;

        this.trainPath =trainPath;
        this.testPath = testPath;
        this.queryPath = queryPath;
        this.KNNListPath = KNNListPath;
        this.throughputPath = throughputPath;
        this.costPath = costPath;

    }

    public void driver() throws IOException {

        //----------------------①读取data和query------------------------------
        float[][] dataset = new float[n][dimension];
        ReadFile reader_1 = new ReadFile(trainPath);
        reader_1.readDataset(dataset, n, dimension);
        //选取1/4的数据作为训练集，生成hashtable.
        int trainNum = (int)(n/4);
//        Sample sample = new Sample(1000);
//        int[] query = new int[1000];
//        sample.getSample(query, trainNum);
//        BufferedWriter writer_1 = new BufferedWriter(new FileWriter(queryPath));
//        for(int i=0;i<1000; i++){
//            writer_1.write(query[i]+"");
//            writer_1.newLine();
//        }
//        writer_1.flush();
//        writer_1.close();

        //----------------------②串联hash函数确定-------------------------------
        LSHHashPara[] conHashFunction = new LSHHashPara[l*m];
        LSHHashFamily lshHashFamily = new LSHHashFamily(conHashFunction);
        //kdd : 145751 74
        float generateW = lshHashFamily.setHashFamily(dataset, trainNum, dimension, l, m);
        setW(generateW);
        System.out.println(w);

        //----------------------③生成hashTable---------------------------------
        LSHHashTable[] lshHashTable = new LSHHashTable[l];
        for(int i=0; i<l; i++){
            HashMap<String, ArrayList<Integer>> aLSHHashMap = new HashMap<>();
            LSHHashTable aLSHHashTable = new LSHHashTable(aLSHHashMap);
            aLSHHashTable.setLSHHashMap(dataset, conHashFunction, trainNum, dimension, i, m, w);
            lshHashTable[i] = aLSHHashTable;
        }
//        WriteFile writer_1 = new WriteFile(lshHashTablePath);
//        writer_1.writeHashTable(lshHashTable, l);


        //----------------------④进行query查询----------------------------------
//        int[] LSHSearchSize = new int[queryNum];
//        int[][] LSHKNNList = new int[queryNum][k];
//        LSHQuerySearch lshQuerySearch = new LSHQuerySearch(LSHKNNList);
//        lshQuerySearch.setKNNList(dataset, query, conHashFunction, lshHashTable, LSHSearchSize, n, dimension, queryNum,
//                k, l, m, w, alpha);
//        WriteFile writer_2 = new WriteFile(this.LSHResultPath);
//        writer_2.writeKNNList(LSHKNNList, queryNum);

        //----------------------⑤准确率检测--------------------------------------
//        int[][] realKNNList = new int[queryNum][k];
//        CKNNSearch CKNN = new CKNNSearch(realKNNList);
//        CKNN.setCKNNList(dataset, query, n, dimension, queryNum, 1, k);
//        WriteFile writer_2 = new WriteFile(this.KNNListPath);
//        writer_2.writeKNNList(realKNNList, queryNum);
//        int[][] realKNNList = new int[queryNum][k];
//        ReadFile reader_3 = new ReadFile(KNNListPath);
//        reader_3.readKNNList(realKNNList, k);
        //----------------------recall召回率计算----------------------------------
//        double[] LSHRecall = new double[queryNum];
//        Recall recallClass = new Recall(LSHRecall);
//        recallClass.setRecall(LSHKNNList, realKNNList, queryNum, k);
//        WriteFile writer_3 = new WriteFile(this.LSHRecallPath);
//        writer_3.writeRecall(LSHRecall, queryNum);

        //----------------------ratio错误率计算-----------------------------------
//        double[] LSHRatio = new double[queryNum];
//        Ratio ratioClass = new Ratio(LSHRatio);
//        ratioClass.setRatio(dataset, LSHKNNList, realKNNList, query, dimension, queryNum, k);
//        WriteFile writer_4 = new WriteFile(this.LSHRatioPath);
//        writer_4.writeRecall(LSHRatio, queryNum);

//        int aveLSHSearchSize = 0;
//        double aveLSHRecall = 0;
//        double aveLSHRatio = 0;
//        for(int i=0; i<queryNum; i++){
//            aveLSHSearchSize += LSHSearchSize[i];
//            aveLSHRecall += LSHRecall[i];
//            aveLSHRatio += LSHRatio[i];
//        }
//        System.out.println("LSH Candidate Num = " + aveLSHSearchSize/queryNum);
//        System.out.println("LSH recall = " + aveLSHRecall/queryNum);
//        System.out.println("LSH ratio = " + aveLSHRatio/queryNum);

        //----------------------⑥进行分层----------------------------------------

        LayerLSHHashTable[] layerLSHHashTable = new LayerLSHHashTable[l];
        ArrayList<HashMap<String, LayerLSHChildBucketPara>> tableChildBucketPara = new ArrayList<>(l);
        ArrayList<HashMap<String, LSHHashPara[]>> tableChildHashFunction = new ArrayList<>(l);
        for(int i=0; i<l; i++){
            //原始hashTable中的hashMap
            HashMap<String, ArrayList<Integer>> aLSHHashMap = lshHashTable[i].LSHHashMap;
            //需要记录的各种子桶参数
            HashMap<String, ArrayList<Integer>> aLayerLSHHashMap = new HashMap<>();
            LayerLSHHashTable aLayerLSHHashTable = new LayerLSHHashTable(aLayerLSHHashMap);
            HashMap<String, LayerLSHChildBucketPara> aTableChildBucketPara = new HashMap<>();
            HashMap<String, LSHHashPara[]> aTableChildHashFunction = new HashMap<>();
            aLayerLSHHashTable.setLayerLSHHashMap(dataset, aLSHHashMap, aTableChildBucketPara, aTableChildHashFunction, trainNum, dimension, k, l, m, alpha, beta, w);
            layerLSHHashTable[i] = aLayerLSHHashTable;
            tableChildBucketPara.add(aTableChildBucketPara);
            tableChildHashFunction.add(aTableChildHashFunction);
        }
        System.out.println("初始化完成");

        /***------------------------query获取，从最密集的桶中获取-----------------------------*/
        //------------------------之所以这么做，是因为可以通过query的搜索空间的变化观察桶的分裂情况，如果选的query来自小桶，
        //                        这些小桶可能不会分裂，看不出流处理中桶的分裂-------------------------------------。
        ArrayList<Integer> maxBuckets = new ArrayList<>();
        for(int i=0; i<l; i++){
            int maxSize = Integer.MIN_VALUE;
            String maxKey = "";
            for(HashMap.Entry<String, ArrayList<Integer>> entry : layerLSHHashTable[i].LayerLSHHashMap.entrySet()){
                if(entry.getValue()!=null && entry.getValue().size() > maxSize){
                    maxKey = entry.getKey();
                    maxSize = entry.getValue().size();
                }
            }
            if(maxBuckets.size() == 0){
                maxBuckets.addAll(layerLSHHashTable[i].LayerLSHHashMap.get(maxKey));
            }
            else {
                maxBuckets.retainAll(layerLSHHashTable[i].LayerLSHHashMap.get(maxKey));
            }
        }
        for(int i=0; i<20; i++){
            queryList[i] = maxBuckets.get(i);
        }

        /***
         * ------------------------流处理---------------------------------
         */
        LayLSHStream stream = new LayLSHStream(dataset, layerLSHHashTable, conHashFunction, tableChildBucketPara, tableChildHashFunction,
                dimension, l, m, k, w, alpha, beta, p1, p2);

        //-----------------------使用多线程统计吞吐率-------------------
        Timer timer_throughput = new Timer();
        TimerTask task_throughput = new TimerTask(){
            @Override
            public void run() {
//                System.out.println(currentIndex);
                throughput.add((float)(currentIndex-baseIndex));
                baseIndex = currentIndex;
                if(count == 10){
                    //用于是否分裂的标志。
                    isIteration = false;
                    count = 0;
                }
                count += 1;
            }
        };
        timer_throughput.schedule(task_throughput, 0, 50);

        //---------------------再开辟一条线程进行查询---------------------
        for(int i=0; i<20; i++){
            cost.add(new ArrayList<Integer>());
        }
        Timer timer_query = new Timer();
        TimerTask task_query = new TimerTask() {
            @Override
            public void run() {
                System.out.println("查询");
                for(int i=0; i<20; i++){
                    int queryId = queryList[i];
                    float[] query = dataset[queryId];
                    cost.get(i).add(stream.Search(n, query, queryId));
                }
            }
        };
        timer_query.schedule(task_query, 0, 50);

        //--------------------流处理开始--------------------------------
        BufferedReader reader_2 = new BufferedReader(new FileReader(testPath));
        String line = reader_2.readLine();
        baseIndex = trainNum;
        //每过0.5秒判断是否分裂。
        while(line != null){
            float[] item = new float[dimension];
            String[] item_s = line.split(" ");
            for(int i=0; i<dimension; i++){
                item[i] = Float.parseFloat(item_s[i+1]);
            }
            currentIndex = Integer.parseInt(item_s[0]);
            //插入
            stream.Insert(item, currentIndex);
            //删除
            if(currentIndex % 5 == 0){
                stream.delete(item, currentIndex);
            }
            //分裂
            line = reader_2.readLine();
            if(isIteration == false){
                System.out.println("时间片结束，开始分裂");
                stream.split();
                isIteration = true;
            }

        }
        if(line == null){
            timer_throughput.cancel();
            timer_query.cancel();
        }
        reader_2.close();


        BufferedWriter writer_1 = new BufferedWriter(new FileWriter(throughputPath));
        for(float i : throughput){
            writer_1.write(i+"");
            writer_1.newLine();
        }
        writer_1.close();

        BufferedWriter writer_2 = new BufferedWriter(new FileWriter(costPath));
        for(int i=0; i<20; i++){
            String output = "";
            for(int c :cost.get(i)){
                output += "  "+ c;
            }
            writer_2.write(output);
            writer_2.newLine();
        }
        writer_2.close();

//        WriteFile writer_5 = new WriteFile(layerLSHHashTablePath);
//        writer_5.writeHashTable(layerLSHHashTable, l);
//
//        ----------------------⑦进行query搜索----------------------------------
//        int[] LayerLSHSearchSize = new int[queryNum];
//        int[][] LayerLSHKNNList = new int[queryNum][k];
//        LayerLSHQuerySearch layerLSHQuerySearch = new LayerLSHQuerySearch(LayerLSHKNNList);
//        layerLSHQuerySearch.setKNNList(dataset, query, layerLSHHashTable, conHashFunction, tableChildBucketPara, tableChildHashFunction,
//                LayerLSHSearchSize, n, dimension, queryNum, k, l, m, w, alpha);
//        WriteFile writer_6 = new WriteFile(LayerLSHResultPath);
//        writer_6.writeKNNList(LayerLSHKNNList, queryNum);
//        ----------------------⑧结果检测---------------------------------------
//        ----------------------recall召回率计算----------------------------------
//        double[] LayerLSHRecall = new double[queryNum];
//        Recall recallClass_1 = new Recall(LayerLSHRecall);
//        recallClass_1.setRecall(LayerLSHKNNList, realKNNList, queryNum, k);
//        WriteFile writer_7 = new WriteFile(this.LayerLSHRecallPath);
//        writer_7.writeRecall(LayerLSHRecall, queryNum);
//
//        ----------------------ratio错误率计算-----------------------------------
//        double[] LayerLSHRatio = new double[queryNum];
//        Ratio ratioClass_1 = new Ratio(LayerLSHRatio);
//        ratioClass_1.setRatio(dataset, LayerLSHKNNList, realKNNList, query, dimension, queryNum, k);
//        WriteFile writer_8 = new WriteFile(this.LayerLSHRatioPath);
//        writer_8.writeRecall(LayerLSHRatio, queryNum);
//
//        int aveLayerLSHSearchSize = 0;
//        double aveLayerLSHRecall = 0;
//        double aveLayerLSHRatio = 0;
//        for(int i=0; i<queryNum; i++){
//            aveLayerLSHSearchSize += LayerLSHSearchSize[i];
//            aveLayerLSHRecall += LayerLSHRecall[i];
//            aveLayerLSHRatio += LayerLSHRatio[i];
//        }
//        System.out.println("LayLSH Candidate Num = " + aveLayerLSHSearchSize/queryNum);
//        System.out.println("LayeLSH recall = " + aveLayerLSHRecall/queryNum);
//        System.out.println("LayerLSH ratio = " + aveLayerLSHRatio/queryNum);
//        //---------------流处理----------------------------------------

    }

    public static void main(String[] args) throws IOException {
        int n = Integer.parseInt(args[0]);
        int dimension = Integer.parseInt(args[1]);
        int k = Integer.parseInt(args[2]);
        int l = Integer.parseInt(args[3]);
        int m = Integer.parseInt(args[4]);
        float alpha = Float.parseFloat(args[5]);
        float beta = Float.parseFloat(args[6]);
//        float w = Float.parseFloat(args[7]);
        float p1 = Float.parseFloat(args[7]);
        float p2 = Float.parseFloat(args[8]);

        String trainPath = "C:\\Users\\weixun\\Desktop\\LayerDSHdata\\kdd.txt";
        String testPath = "C:\\Users\\weixun\\Desktop\\LayerDSHdata\\kdd_test.txt";
        String queryPath = "C:\\Users\\weixun\\Desktop\\LayerDSHdata\\kdd_stream_query.txt";
//        String KNNListPath = "C:\\Users\\weixun\\Desktop\\KNNList.txt";
        String KNNListPath = "C:\\Users\\weixun\\Desktop\\LayerDSHdata\\kdd_stream_KNN.txt";
        String throughputPath = "C:\\Users\\weixun\\Desktop\\throughput.txt";
        String costPath = "C:\\Users\\weixun\\Desktop\\cost.txt";

        LayerLSHDriver layerLSHDriver = new LayerLSHDriver(n, dimension, k, l, m, alpha, beta, p1, p2,
                trainPath, testPath, queryPath, KNNListPath, throughputPath, costPath);
        layerLSHDriver.driver();

    }
}
