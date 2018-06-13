package DSH;

import util.ItemDis;

import java.util.Comparator;

/**
 * Created by weixun on 2017/10/9.
 */
public class MyComparator implements Comparator<ItemDis>{

    @Override
    public int compare(ItemDis o1, ItemDis o2) {
        return Double.compare(o1.dis, o2.dis);
    }
}
