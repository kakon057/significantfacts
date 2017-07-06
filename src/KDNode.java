import gnu.trove.list.array.TIntArrayList;
import java.util.Vector;


// K-D Tree node class

class KDNode {

    // these are seen by KDTree
    protected HPoint k;

    TIntArrayList v;

    protected KDNode left, right;

    //protected boolean deleted;

    // Method ins translated from 352.ins.c of Gonnet & Baeza-Yates
    protected static KDNode ins(HPoint key, int val, KDNode t, int lev, int K) throws KeyDuplicateException {

        if (t == null) {
            t = new KDNode(key, val);
        }

        else if (key.equals(t.k)) {

            // "re-insert"
            /*
            if (t.deleted) {
                t.deleted = false;
                t.v = val;
            }*/
            
            t.v.add(val);
            
            // else {
            // throw new KeyDuplicateException();
            // }
        }

        else if (key.coord[lev] > t.k.coord[lev]) {
            t.right = ins(key, val, t.right, (lev + 1) % K, K);
        } else {
            t.left = ins(key, val, t.left, (lev + 1) % K, K);
        }

        return t;
    }

    // Method srch translated from 352.srch.c of Gonnet & Baeza-Yates
    protected static KDNode srch(HPoint key, KDNode t, int K) {

        for (int lev = 0; t != null; lev = (lev + 1) % K) {

            if (key.equals(t.k)) {
                return t;
            } else if (key.coord[lev] > t.k.coord[lev]) {
                t = t.right;
            } else {
                t = t.left;
            }
        }

        return null;
    }

    // Method rsearch translated from 352.range.c of Gonnet & Baeza-Yates
    protected static void rsearch(HPoint lowk, HPoint uppk, KDNode t, int lev, int K
            , Vector<KDNode> v, int index) {

        if (t == null)
            return;
        if(t.k.coord[index]==lowk.coord[index])
            return;
        if (lowk.coord[lev] < t.k.coord[lev]) {
            rsearch(lowk, uppk, t.left, (lev + 1) % K, K, v, index);
        }
        int j, flag=0;
        for (j = 0; j < K && lowk.coord[j] <= t.k.coord[j] && uppk.coord[j] >= t.k.coord[j]; j++)
        {
            if(uppk.coord[j] == t.k.coord[j])
            {
                flag++;
            }
        }
        if (j == K&&flag!=K)
            v.add(t);
        if (uppk.coord[lev] > t.k.coord[lev]) {
            rsearch(lowk, uppk, t.right, (lev + 1) % K, K, v, index);
        }
    }

    // constructor is used only by class; other methods are static
    private KDNode(HPoint key, int val) {

        k = key;
        v = new TIntArrayList();
        v.add(val);
        left = null;
        right = null;
    }

    private static String pad(int n) {
        String s = "";
        for (int i = 0; i < n; ++i) {
            s += " ";
        }
        return s;
    }

    private static void hpcopy(HPoint hp_src, HPoint hp_dst) {
        for (int i = 0; i < hp_dst.coord.length; ++i) {
            hp_dst.coord[i] = hp_src.coord[i];
        }
    }
}
