package LSH;

/**
 * Created by weixun on 2017/10/16.
 */
public class LSHTransKey{

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
