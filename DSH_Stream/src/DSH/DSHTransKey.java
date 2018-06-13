package DSH;

/**
 * Created by weixun on 2017/10/15.
 */
public class DSHTransKey {
    /**
     * 由于DSH的hash签名中只含有0,1，所以在获取其相近的桶时，将原hash签名某一位由0变为1或由1变为0
     * @param key
     * @param index
     * @param m
     * @return
     */
    public static String getTransKey(String key, int index, int m){
        String transkey = null;
        if(index == 0){
            if(key.charAt(index) == '0'){
                transkey = "1"+key.substring(index+1,m);
            }
            else {
                transkey = "0"+key.substring(index+1,m);
            }
        }
        else if (index>=1 && index<m-1) {
            if(key.charAt(index) == '0'){
                transkey = key.substring(0,index)+ "1" +key.substring(index+1,m);
            }
            else {
                transkey = key.substring(0,index)+ "0" +key.substring(index+1,m);
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
