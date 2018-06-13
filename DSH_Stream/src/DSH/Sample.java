package DSH;

import java.util.BitSet;
import java.util.Random;

/**
 * Created by weixun on 2017/10/9.
 */
public class Sample {
    int sampleNum;

    public Sample(int sampleNum){
        this.sampleNum = sampleNum;
    }

    public void getSample(int[] sample, int n){
        BitSet bs = new BitSet(n);
        int count = 0;
        Random rand = new Random();
        while (count < sampleNum){
            int index = rand.nextInt(n);
            if(!bs.get(index)){
                sample[count] = index;
                count++;
            }
        }
    }
}
