package util;

/**
 * Created by weixun on 2018/3/28.
 */
public class SubString {
    public static int getLength(String s){
        int length = 0;
        int index = 0;
        while (index < s.length()){
            if(s.charAt(index) == '-'){
                length ++;
                index+=2;
            }
            else {
                length ++;
                index ++;
            }
        }

        return length;
    }
    public static String getSubString(String s, int num){
        String subString = "";
        int index = 0;
        int count = 0;
        while(count<num){
            if(s.charAt(index) != '-'){
                subString += s.charAt(index);
                index++;
                count++;
            }
            else {
                subString += s.charAt(index);
                subString += s.charAt(index+1);
                index += 2;
                count++;
            }

        }
        return subString;
    }
}
