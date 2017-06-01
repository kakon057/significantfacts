/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bottom_up;

import gnu.trove.list.array.TIntArrayList;

public class Cuboid {

    public int last_updated_by;
    public short last_updated_subspace;
    public boolean affected_by_prev_id;
    public TIntArrayList[] skyline_tuples; //stores all the skyline tuples in all measure subspaces for a dimension cuboid.

    public Cuboid(byte[] measure_subspace_skip_bit, short measure_attributes) // To keep everything, set skip_level = -1. If skip_level = 1, this function will skip all the i's having <= 1 zero. 
    {
        last_updated_by = -1;
        last_updated_subspace = 0;
        skyline_tuples = new TIntArrayList[(short) Math.pow(2, measure_attributes)]; // This is not allocating space, just storing references. Our understanding is each reference takes 128B.         

        for (short i = 1; i < skyline_tuples.length; i++) {
            if (measure_subspace_skip_bit[i] == 1) {
                skyline_tuples[i] = new TIntArrayList();
            }
        }
    }
    
    Cuboid(short measure_attributes) 
    {
        last_updated_by = -1;
        last_updated_subspace = 0;
        skyline_tuples = new TIntArrayList[(short) Math.pow(2, measure_attributes)]; // This is not allocating space, just storing references. Our understanding is each reference takes 128B.
    }
}

