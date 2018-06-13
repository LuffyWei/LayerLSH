package LayerDSH;

/**
 * Created by weixun on 2017/10/15.
 */
public class LayerDSHTransKey {

    public static String getTransKey(String key, int index, int m, int childM){
        String transkey = "";
        if (index>=m+1 && index<m+childM) {
            if(key.charAt(index) == '0'){
                transkey = key.substring(0,index)+ "1" +key.substring(index+1,m+childM+1);
            }
            else {
                transkey = key.substring(0,index)+ "0" +key.substring(index+1,m+childM+1);
            }
        }
        else {
            if(key.charAt(index) == '0'){
                transkey = key.substring(0,index)+"1";
            }
            else {
                transkey = key.substring(0,index)+"0";
            }
        }
        return transkey;
    }
}
