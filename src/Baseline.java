import gnu.trove.list.array.TIntArrayList;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class Baseline_I extends FactMonitoring {

    public static Dimension_Lattice dimension_lattice;
    public static int number_of_tuples_dominating_new_tuple;

    public static void main(String[] args) throws FileNotFoundException, IOException {
        init();
        load_Data();

        dimension_lattice = new Dimension_Lattice(dimension_attributes);

        int temp_gameID = -1;

        for (int i = 0; i < number_of_tuples; i++) {
            start_time = System.currentTimeMillis();
            
            if (temp_gameID != tuples.get(i).game_id) {
                tree.clear();
                temp_gameID = tuples.get(i).game_id;
            }
            try {
                tree.insert(tuples.get(i).measure_values, i);

                int p_id = tuples.get(i).prev_id;

                if (p_id != -1) {
                    tree.delete(tuples.get(p_id).measure_values, p_id);
                }
            } catch (Exception e) {
            }
            //System.out.println("ID, M_SUBSPACE, SKYLINE_CONSTRAINTS, DOMINATED_BY"); // Reporting details of each tuple

            number_of_comparison = 0;
            subspace_Baseline_I(i);
            single_tuple_time = System.currentTimeMillis() - start_time;
            cumulative_time += single_tuple_time;
            //System.out.println("ID, TIME");
            //System.out.println(i + "," + single_tuple_time);
            if (i % 1000 == 0) {
                System.out.println(i + "," + single_tuple_time + "," + number_of_comparison);
            }
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

                int dom = comparison(tuples.get(tuple_id), tuples.get(p_id), i, true);

                if (dom == -1) {
                    baseline_I(tuple_id, i);
                } else {
                    pbaseline_I(tuple_id, i, dom);
                }
                skyline_constraints = dimension_lattice.skyline_Constraints(dimension_skip_level);
                //System.out.println(tuple_id + "," + i + "," + skyline_constraints + "," + number_of_tuples_dominating_new_tuple); // Reporting details of each tuple
            }
        }
    }

    public static void pbaseline_I(int tuple_id, short subspace, int measure) {
        int p_id = tuples.get(tuple_id).prev_id;

        TIntArrayList range_query_result = new TIntArrayList();

        try {
            int tempArray[] = tuples.get(tuple_id).measure_values.clone();

            for (short m = 0, quotient = subspace; m < measure_attributes; m++, quotient = (short) (quotient / 2)) {
                if (quotient % 2 != 0) {
                    tempArray[m] = 0;
                }
            }
            tempArray[measure] = tuples.get(tuple_id).measure_values[measure];

            range_query_result = tree.range(tempArray, tuples.get(p_id).measure_values, measure);

        } catch (Exception e) {
        }

        range_query_result.add(tuple_id);

        for (int j = 0; j < range_query_result.size(); j++) {
            baseline_I((int) range_query_result.get(j), subspace);
        }
    }

    public static void baseline_I(int tuple_id, short subspace) {
        Tuple t = tuples.get(tuple_id);
        int dom, child;

        for (int i = 0; i < tuple_id; i++) {
            try {
                if (tree.exists(tuples.get(i).measure_values, i) == false) {
                    continue;
                }
            } catch (Exception e) {
            }

            Tuple t_ = tuples.get(i);

            number_of_comparison++;
            //System.out.println(i);

            dom = comparison(t, t_, subspace, false);

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
