/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stop_down;

import gnu.trove.list.array.TIntArrayList;
import java.util.Vector;

/**
 * KDTree is a class supporting KD-tree insertion, deletion, equality search,
 * range search, and nearest neighbor(s) using double-precision floating-point
 * keys. Splitting dimension is chosen naively, by depth modulo K. Semantics are
 * as follows:
 *
 * <UL>
 * <LI> Two different keys containing identical numbers should retrieve the same
 * value from a given KD-tree. Therefore keys are cloned when a node is
 * inserted.
 * <BR><BR>
 * <LI> As with Hashtables, values inserted into a KD-tree are <I>not</I>
 * cloned. Modifying a value between insertion and retrieval will therefore
 * modify the value stored in the tree.
 * </UL>
 *
 * @author Simon Levy, Bjoern Heckel
 * @version %I%, %G%
 * @since JDK1.2
 */
public class KDTree {

    // K = number of dimensions
    private int m_K;

    // root of KD-tree
    private KDNode m_root;

    // count of nodes
    private int m_count;

    /**
     * Creates a KD-tree with specified number of dimensions.
     *
     * @param k number of dimensions
     */
    public KDTree(int k) {

        m_K = k;
        m_root = null;
    }

    public void clear() {
        m_root = null;
    }

    /**
     * Insert a node in a KD-tree. Uses algorithm translated from 352.ins.c of
     *
     * <PRE>
     *   &#064;Book{GonnetBaezaYates1991,
     *     author =    {G.H. Gonnet and R. Baeza-Yates},
     *     title =     {Handbook of Algorithms and Data Structures},
     *     publisher = {Addison-Wesley},
     *     year =      {1991}
     *   }
     * </PRE>
     *
     * @param key key for KD-tree node
     * @param value value at that key
     *
     * @throws KeySizeException if key.length mismatches K
     * @throws KeyDuplicateException if key already in tree
     */
    public void insert(int[] key, int value)
            throws KeySizeException, KeyDuplicateException {

        if (key.length != m_K) {
            throw new KeySizeException();
        } else {
            try {
                m_root = KDNode.ins(new HPoint(key), value, m_root, 0, m_K);
            } catch (KeyDuplicateException e) {
                throw e;
            }
        }

        m_count++;
    }

    /**
     * Find KD-tree node whose key is identical to key. Uses algorithm
     * translated from 352.srch.c of Gonnet & Baeza-Yates.
     *
     * @param key key for KD-tree node
     *
     * @return object at key, or null if not found
     *
     * @throws KeySizeException if key.length mismatches K
     */
    public TIntArrayList search(int[] key) throws KeySizeException {

        if (key.length != m_K) {
            throw new KeySizeException();
        }

        KDNode kd = KDNode.srch(new HPoint(key), m_root, m_K);

        return (kd == null ? null : kd.v);
    }

    public boolean exists(int[] key, int value) throws KeySizeException {
        TIntArrayList list=search(key);
        
        if(list==null||list.isEmpty())
            return false;
        
        for(int i=0; i<list.size(); i++)
        {
            if(value==list.get(i))
                return true;
        }
        
        return false;
    }

    /**
     * Delete a node from a KD-tree. Instead of actually deleting node and
     * rebuilding tree, marks node as deleted. Hence, it is up to the caller to
     * rebuild the tree as needed for efficiency.
     *
     * @param key key for KD-tree node
     *
     * @throws KeySizeException if key.length mismatches K
     * @throws KeyMissingException if no node in tree has key
     */
    public void delete(int[] key, int value)
            throws KeySizeException, KeyMissingException {

        if (key.length != m_K) {
            throw new KeySizeException();
        } else {

            KDNode t = KDNode.srch(new HPoint(key), m_root, m_K);
            if (t == null) {
                throw new KeyMissingException();
            } else {
                for (int i = 0; i < t.v.size(); i++) {
                    if (value == t.v.get(i)) {
                        t.v.removeAt(i);
                        break;
                    }
                }
            }

            m_count--;
        }
    }

    /**
     * Range search in a KD-tree. Uses algorithm translated from 352.range.c of
     * Gonnet & Baeza-Yates.
     *
     * @param lowk lower-bounds for key
     * @param uppk upper-bounds for key
     *
     * @return array of Objects whose keys fall in range [lowk,uppk]
     *
     * @throws KeySizeException on mismatch among lowk.length, uppk.length, or K
     */
    public TIntArrayList range(int[] lowk, int[] uppk, int index)
            throws KeySizeException {

        if (lowk.length != uppk.length) {
            throw new KeySizeException();
        } else if (lowk.length != m_K) {
            throw new KeySizeException();
        } else {
            Vector<KDNode> v = new Vector<KDNode>();
            KDNode.rsearch(new HPoint(lowk), new HPoint(uppk),
                    m_root, 0, m_K, v, index);
            TIntArrayList o = new TIntArrayList();
            for (int i = 0; i < v.size(); ++i) {
                KDNode n = v.elementAt(i);

                for (int j = 0; j < n.v.size(); j++) {
                    o.add(n.v.get(j));
                }
            }
            return o;
        }
    }
}
