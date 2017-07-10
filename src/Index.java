/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stop_down;

import java.util.Arrays;

public class Index {

    public short[] indices;
    public short num_of_parents;

    Index()
    {
        num_of_parents = 0;
    }
    
    public Index(short dimension_attributes) {
        indices = new short[dimension_attributes];
        for (short i = 0; i < dimension_attributes; i++) {
            indices[i] = 0;
        }
        num_of_parents = 0;
    }

    public Index(Index index) { //Copy Constructor
        this.indices = new short[index.indices.length];
        for (short i = 0; i < index.indices.length; i++) {
            this.indices[i] = index.indices[i];
        }
        num_of_parents = index.num_of_parents;
    }

    public void print() {
        for (short i = 0; i < indices.length; i++) {
            System.out.print(indices[i] + " ");
        }
        System.out.println();
    }

    public short count_Num_Of_Zeros() {
        short num_of_zeros = 0;
        for (short s : indices) {
            if (s == 0) {
                num_of_zeros++;
            }
        }
        return num_of_zeros;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Index other = (Index) obj;
        if (!Arrays.equals(this.indices, other.indices)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Arrays.hashCode(this.indices);
        return hash;
    }
}



