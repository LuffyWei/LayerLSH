package util;

import java.util.Comparator;

/**
 * Created by weixun on 2017/10/9.
 */
public class ItemDis implements Comparable{
    public int id;
    public double dis;

    public ItemDis(int id, double dis){
        this.id = id;
        this.dis = dis;
    }


    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;
        if(this.getClass() != obj.getClass()) return false;
        ItemDis otherObj = (ItemDis) obj;
        return this.dis == otherObj.dis && this.id == otherObj.id;
    }


    @Override
    public int compareTo(Object o) {
        ItemDis other = (ItemDis)o;
        return Double.compare(this.dis, other.dis);
    }
}
