package LSH;

import jsc.distributions.Normal;
import jsc.distributions.Uniform;

import java.util.Random;

/**
 * Created by weixun on 2017/10/13.
 */
public class LSHHashPara {
    float[] a;
    float b;

    public LSHHashPara(float[] a, float b){
        this.a = a;
        this.b = b;
    }

    public void setLSHPara_a(int dimension){
        Normal norm = new Normal(0,1);

        for(int j=0; j<dimension; j++){
            a[j] = (float) norm.random();
        }

    }

    public void setLSHPara_b(float w){
        Uniform uniform = new Uniform(0, w);
        b = (float) uniform.random();
    }
//    public void setLSHPara(int dimension, double w){
//        Random random = new Random();
//        for(int j=0; j<dimension; j++){
//            a[j] = (float)(random.nextGaussian());
//        }
//        b = (float)(random.nextDouble()*w);
//    }

    @Override
    public String toString() {
        String s = "";
        for(int i=0; i<a.length; i++){
            s += a[i]+":";
        }
        return s + " :||"+b;
    }
}
