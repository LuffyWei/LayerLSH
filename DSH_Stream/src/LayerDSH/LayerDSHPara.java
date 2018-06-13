package LayerDSH;

/**
 * Created by weixun on 2017/10/11.
 */
public class LayerDSHPara {
    int l;
    int m;

    public LayerDSHPara(int l, int m){
        this.l = l;
        this.m = m;
    }

    /**
     * 此处childM的计算公式是：
     * @param k
     * @param S
     * @param p1
     * @param beta
     * @return
     */
    public LayerDSHPara getChildPara(int k, int S, float p1, float beta){
        int childM = (int)(Math.ceil(Math.log(k/(S*beta)) / Math.log(p1)));
        int childL = (int)Math.ceil(Math.log(1-Math.pow(p1, m)) / Math.log(1-Math.pow(p1, childM+m)));

        LayerDSHPara childPara = new LayerDSHPara(childL, childM);
        return childPara;
    }
}
