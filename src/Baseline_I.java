/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package baseline_i;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author naeemul
 */
public class Baseline_I extends FactMonitoring {

    public static Dimension_Lattice dimension_lattice;
    public static int number_of_tuples_dominating_new_tuple;

    public static void main(String[] args) throws FileNotFoundException, IOException {
        init();
        load_Data();

        dimension_lattice = new Dimension_Lattice(dimension_attributes);
        for (int i = 0; i < number_of_tuples; i++) {
            //System.out.println("ID, M_SUBSPACE, SKYLINE_CONSTRAINTS, DOMINATED_BY"); // Reporting details of each tuple
            start_time = System.currentTimeMillis();
            subspace_Baseline_I(i);
            single_tuple_time = System.currentTimeMillis() - start_time;
            cumulative_time += single_tuple_time;
            //System.out.println("ID, TIME");
            //System.out.println(i + "," + single_tuple_time);
            //if(i%1000 == 0)
                System.out.println(i + "," + cumulative_time+","+number_of_comparison);
        }
        //System.out.println("Total Execution Time: " + cumulative_time + "ms");
    }

    public static void subspace_Baseline_I(int tuple_id) {
        for (short i = 1; i < measure_subspace_skip_bit.length; i++) {
            if (measure_subspace_skip_bit[i] == 1) {
                dimension_lattice.clear();
                number_of_tuples_dominating_new_tuple = 0;
                skyline_constraints = 0;
                
                int p_id = tuples.get(tuple_id).prev_id;
                //System.out.println(tuple_id+" "+p_id);

                if (p_id == -1) {
                    baseline_I(tuple_id, i);
                    continue;
                }
                
                int dom=comparison(tuples.get(tuple_id), tuples.get(p_id), i);
                
                if (dom == -1) {
                    baseline_I(tuple_id, i);
                } else {
                    pbaseline_I(tuple_id, i);
                }
                skyline_constraints = dimension_lattice.skyline_Constraints(dimension_skip_level);
                //System.out.println(tuple_id + "," + i + "," + skyline_constraints + "," + number_of_tuples_dominating_new_tuple); // Reporting details of each tuple
            }
        }
    }

    public static void pbaseline_I(int tuple_id, short subspace) 
    {
        int p_id = tuples.get(tuple_id).prev_id;
        ArrayList<Index> queue = new ArrayList<Index>();

        ArrayList<Object> range_query_result = new ArrayList<Object>();

        try {
            int tempArray[] = new int[measure_attributes];

            tempArray[0] = 0;
            tempArray[1] = tuples.get(p_id).measure_values[1];
            range_query_result = tree.range(tempArray, tuples.get(tuple_id).measure_values);

        } catch (Exception e) {
        }

        for (int j = 0; j < range_query_result.size(); j++) {
            baseline_I((int)range_query_result.get(j), subspace);
        }
    }
    
    public static void baseline_I(int tuple_id, short subspace) {
        Tuple t = tuples.get(tuple_id);
        int dom, child;

        for (int i = 0; i < tuple_id; i++) {
            Tuple t_ = tuples.get(i);

		 number_of_comparison++;

            dom = comparison(t, t_, subspace);

            if (dom == 1) // t is dominated by t_
            {
                number_of_tuples_dominating_new_tuple++;
                child = dimension_lattice.find_Bottom(t, t_);
                if (dimension_lattice.lattice[child] != 0) {
                    dimension_lattice.remove_Ancestors(child);
                }
            }
        }
    }
}
