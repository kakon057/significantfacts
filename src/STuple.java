/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stop_down;

public class STuple extends Tuple{
    public int tested_dominating, tested_dominated, tested_incomparable;
    public int tested_count_dominating, tested_count_dominated, tested_count_incomparable;  

    STuple(int id, int[] measure_values, Index dimension_index, int prev_id) {
        super(id, measure_values, dimension_index, prev_id);
        tested_dominating=0;
        tested_dominated=0;
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
        System.out.println(">");
    }   
}