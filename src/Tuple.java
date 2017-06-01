/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package baseline_i;

public class Tuple {

    public int id; //id of a tuple, starts with 0 and it refers to the first tuple in the data file.
    public int tested_by; //with which tuple it was last tested by. Helps to reduce comparison in Bottom_Up
    public byte test_result; //what was the test result
    public short subspace; //In which measure subspace the comparison was accomplished.
    public int[] measure_values; // measure att values of this tuple 
    public Index dimension_index; // dimensional att values[after mapping from String to int] of this tuple.
    public int prev_id;

    Tuple(int id, int[] measure_values, Index dimension_index, int prev_id) {
        this.id = id;
        tested_by = -1;
        subspace = 0;
        this.dimension_index = new Index();
        this.dimension_index.indices = dimension_index.indices.clone();
        this.measure_values = measure_values.clone();
        this.prev_id=prev_id;
    }

    public void print() {
        System.out.print(id + ":");

        System.out.print("<");
        for (short i : this.dimension_index.indices) {
            System.out.print(i + ",");
        }
        System.out.print(">");

        System.out.print("<");
        for (int i : this.measure_values) {
            System.out.print(i + ",");
        }
        System.out.print(">"+" ");
        System.out.println(prev_id);
    }
}
