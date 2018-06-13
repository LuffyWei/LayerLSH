package LayerLSH;

/**
 * Created by weixun on 2017/10/16.
 */
public class LayerLSHTransKey {

    public static String getTransKey(String newKey, int m, int childM, int index){
        String transKey = newKey.substring(0,m+1);
        if(index == 0){
            transKey = transKey + (char)(newKey.charAt(m+1+index)+1) + newKey.substring(m+1+index+1, m+1+childM);
        }
        else if(index>0 && index <childM-1){
            transKey = transKey + newKey.substring(m+1, m+1+index) + (char)(newKey.charAt(m+1+index)+1) + newKey.substring(m+1+index+1, m+1+childM);
        }
        else {
            transKey = transKey + newKey.substring(m+1, m+1+index) + (char)(newKey.charAt(m+1+index) +1);
        }

        return transKey;
    }

    public static String getTransKey(String key, int m, int index){
        String transKey = "";
        if(index ==0){
            transKey = (char)(key.charAt(index)+1) + key.substring(index+1, m);
        }
        else if(index >0 && index < m){
            transKey = key.substring(0, index) + (char)(key.charAt(index) +1) + key.substring(index+1, m);
        }
        else {
            transKey = key.substring(0,index) + (char)(key.charAt(index) +1);
        }
        return transKey;
    }
}
