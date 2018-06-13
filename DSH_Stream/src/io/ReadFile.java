package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by weixun on 2017/10/4.
 */
public class ReadFile {

    String inputPath;

    public ReadFile(String path){
        this.inputPath = path;
    }

    public void readDataset(float[][] dataset, int n, int dimension) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(this.inputPath));
        String line = reader.readLine();
        int count = 0;
        while (line != null){
            String[] array = line.split(" ");
            for(int i=0; i<dimension; i++){
                dataset[count][i] = Float.parseFloat(array[i+1]);
            }
            count++;
            line = reader.readLine();
        }
        reader.close();
    }

    public void readQuery(int[] query, int queryNum, int dimension) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(this.inputPath));
        String line = reader.readLine();
        int count = 0;
        while (line != null){
            StringTokenizer st = new StringTokenizer(line, " ");
            query[count] = Integer.parseInt(st.nextToken()) -1;
            count++;
            line = reader.readLine();
        }
        reader.close();
    }

    public void readQuery(int[] query) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(this.inputPath));
        String line = reader.readLine();
        int count = 0;
        while (line != null){
            query[count] = Integer.parseInt(line);
            count++;
            line = reader.readLine();
        }
        reader.close();
    }

    public void readHashFunction(ArrayList<float[]> hashFamily, int dimension) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(this.inputPath));
        String line = reader.readLine();
        while (line != null){
            float[] aHashFunction = new float[dimension];
            String[] array = line.split(" ");
            for(int i=0; i<dimension; i++){
                aHashFunction[i] = Float.parseFloat(array[i]);
            }
            hashFamily.add(aHashFunction);
            line = reader.readLine();
        }
        reader.close();
    }

    public void readKNNList(int[][] KNNList,int k) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(this.inputPath));
        String line = reader.readLine();
        int count = 0;
        while (line != null){
            int[] aKNNList = new int[k];
            String[] array = line.split(" ");
            for(int i=0; i<k; i++){
                aKNNList[i] = Integer.parseInt(array[i]);
            }
            KNNList[count++] = aKNNList;
            line = reader.readLine();
        }
        reader.close();
    }

}
