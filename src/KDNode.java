/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package baseline_i;

import java.util.Vector;


// K-D Tree node class

class KDNode {

    // these are seen by KDTree
    protected HPoint k;

    Object v;

    protected KDNode left, right;

    protected boolean deleted;

    // Method ins translated from 352.ins.c of Gonnet & Baeza-Yates
    protected static KDNode ins(HPoint key, Object val, KDNode t, int lev, int K) throws KeyDuplicateException {

        if (t == null) {
            t = new KDNode(key, val);
        }

        else if (key.equals(t.k)) {

            // "re-insert"
            if (t.deleted) {
                t.deleted = false;
                t.v = val;
            }

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

            if (!t.deleted && key.equals(t.k)) {
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
    protected static void rsearch(HPoint lowk, HPoint uppk, KDNode t, int lev, int K, Vector<KDNode> v) {

        if (t == null)
            return;
        if (lowk.coord[lev] <= t.k.coord[lev]) {
            rsearch(lowk, uppk, t.left, (lev + 1) % K, K, v);
        }
        int j;
        for (j = 0; j < K && lowk.coord[j] <= t.k.coord[j] && uppk.coord[j] >= t.k.coord[j]; j++)
            ;
        if (j == K)
            v.add(t);
        if (uppk.coord[lev] > t.k.coord[lev]) {
            rsearch(lowk, uppk, t.right, (lev + 1) % K, K, v);
        }
    }

    // constructor is used only by class; other methods are static
    private KDNode(HPoint key, Object val) {

        k = key;
        v = val;
        left = null;
        right = null;
        deleted = false;
    }

    protected String toString(int depth) {
        String s = k + "  " + v + (deleted ? "*" : "");
        if (left != null) {
            s = s + "\n" + pad(depth) + "L " + left.toString(depth + 1);
        }
        if (right != null) {
            s = s + "\n" + pad(depth) + "R " + right.toString(depth + 1);
        }
        return s;
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

