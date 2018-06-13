package LayerLSH;

/**
 * Created by weixun on 2017/10/15.
 */
public class LayerLSHChildBucketPara {
    /**
     * 为了保证分桶以后准确率，会根据大桶调整子桶的参数，即l,m,w
     * l,m的计算方式参照data文件夹中图片里的公式
     * w的选取与lsh的方式一样，也是先生成a，然后计算a*x的均值，将均值作为w
     */
    int l;
    int m;

    float w = 0f;
    public void setW(float w) {
        this.w = w;
    }

    public LayerLSHChildBucketPara(int l, int m){
        this.l = l;
        this.m = m;
    }

    public LayerLSHChildBucketPara getChildBucketPara(int k, int S, float alpha, float beta){
        int childM = (int)(Math.ceil(Math.log(k/(S*beta)) / Math.log(alpha)));
        int childL = (int)Math.ceil(Math.log(1-Math.pow(alpha, m)) / Math.log(1-Math.pow(alpha, childM+m)));

        LayerLSHChildBucketPara childPara = new LayerLSHChildBucketPara(childL, childM);
        return childPara;
    }
}
