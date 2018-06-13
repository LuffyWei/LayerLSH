package io;

import DSH.DSHHashTable;
import LSH.LSHHashTable;
import LayerDSH.LayerDSHHashTable;
import LayerLSH.LayerLSHHashTable;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by weixun on 2017/10/10.
 */
public class WriteFile {
    String outputPath;

    public WriteFile(String outputPath){
        this.outputPath = outputPath;
    }

    public void writeHashFunction(ArrayList<float[]> hashFamily) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(this.outputPath));
        for(float[] aHashFunction: hashFamily){
            writer.write(toString(aHashFunction));
            writer.newLine();
        }
        writer.flush();
        writer.close();
    }

    public void writeKNNList(int[][] realKNNList, int queryNum) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(this.outputPath));
        for(int[] aKNNList: realKNNList){
            writer.write(toString(aKNNList));
            writer.newLine();
        }
        writer.flush();
        writer.close();
    }

    public void writeRecall(double[] recall, int queryNum) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(this.outputPath));
        for(double aRecall : recall){
            writer.write(aRecall+"");
            writer.newLine();
        }
        writer.flush();
        writer.close();
    }

    public void writeHashTable(DSHHashTable[] dshHashTable, int l) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(this.outputPath));
        for(int i=0; i<l; i++){
            writer.write("table " + i + " :");
            writer.newLine();
            HashMap<String, ArrayList<Integer>> aDSHHashTable = dshHashTable[i].DSHHashMap;
            for(Map.Entry<String, ArrayList<Integer>> entry : aDSHHashTable.entrySet()){
                String s = entry.getKey();
                s += " : "+ entry.getValue().size();
                writer.write(s);
                writer.newLine();
            }
        }
        writer.flush();
        writer.close();
    }

    public void writeHashTable(LayerDSHHashTable[] layerDSHHashTable, int l) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(this.outputPath));
        for(int i=0; i<l; i++){
            writer.write("table " + i + " :");
            writer.newLine();
            HashMap<String, ArrayList<Integer>> aLayerDSHHashTable = layerDSHHashTable[i].LayerDSHHashMap;
            for(Map.Entry<String, ArrayList<Integer>> entry : aLayerDSHHashTable.entrySet()){
                if(entry.getValue() != null){
                    String s = entry.getKey();
                    s += " : "+ entry.getValue().size();
                    writer.write(s);
                    writer.newLine();
                }
            }
        }
        writer.flush();
        writer.close();
    }


    public void writeHashTable(LSHHashTable[] lshHashTable, int l) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(this.outputPath));
        for(int i=0; i<l; i++){
            writer.write("table " + i + " :");
            writer.newLine();
            HashMap<String, ArrayList<Integer>> aLSHHashTable = lshHashTable[i].LSHHashMap;
            for(Map.Entry<String, ArrayList<Integer>> entry : aLSHHashTable.entrySet()){
                String s = entry.getKey();
                s += " : "+ entry.getValue().size();
                writer.write(s);
                writer.newLine();
            }
        }
        writer.flush();
        writer.close();
    }

    public void writeHashTable(LayerLSHHashTable[] layerLSHHashTable, int l) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(this.outputPath));
        for(int i=0; i<l; i++){
            writer.write("table " + i + " :");
            writer.newLine();
            HashMap<String, ArrayList<Integer>> aLayerLSHHashTable = layerLSHHashTable[i].LayerLSHHashMap;
            for(Map.Entry<String, ArrayList<Integer>> entry : aLayerLSHHashTable.entrySet()){
                if(entry.getValue() != null){
                    String s = entry.getKey();
                    s += " : "+ entry.getValue().size();
                    writer.write(s);
                    writer.newLine();
                }

            }
        }
        writer.flush();
        writer.close();
    }

    public void writeAvValue(float[][] av, int n) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(this.outputPath));
        for(int i=0; i<n; i++){
            String s = toString(av[i]);
            writer.write(s);
            writer.newLine();
        }
        writer.flush();
        writer.close();
    }

    public void writeAvRowSum(float[] avRowSum, int n) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(this.outputPath));
        for(int i=0; i<n; i++){
            writer.write(avRowSum[i] + "");
            writer.newLine();
        }
        writer.flush();
        writer.close();
    }


    private String toString(float[] hashFunction){
        String s ="";
        for(float i: hashFunction){
            s += i+" ";
        }
        return s;
    }

    private String toString(int[] hashFunction){
        String s ="";
        for(int i: hashFunction){
            s += i+" ";
        }
        return s;
    }
}
