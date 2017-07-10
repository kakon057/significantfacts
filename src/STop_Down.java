/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stop_down;

import gnu.trove.list.array.TIntArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import static stop_down.FactMonitoring.dimension_attributes;
import static stop_down.FactMonitoring.measure_attributes;
import static stop_down.FactMonitoring.number_of_tuples;
import static stop_down.FactMonitoring.total_dimension_attributes_compared;
import static stop_down.FactMonitoring.total_measure_attributes_compared;
import static stop_down.FactMonitoring.tuples;

public class STop_Down extends FactMonitoring {

    public static HashMap<Index, STop_Down_Cuboid> cube;
    public static int num_of_cells;
    public static long num_of_non_empty_list;
    public static long total_skyline_tuples;
    public static int dominating_, dominated_, incomparable_;
    public static int c1, c2, c3;
    public static ArrayList<STuple> tuples;
    public static int count_dominating, count_dominated, count_incomparable;
    public static boolean flagDuplicate;

    public static void main(String[] args) throws FileNotFoundException, IOException {
        init();
        load_Data();
        cube = new HashMap<Index, STop_Down_Cuboid>();
        Runtime runtime = Runtime.getRuntime(); // Getting JAVA runtime
        for (int i = 0; i < number_of_tuples; i++) {
            //System.out.println("ID, M_SUBSPACE, SKYLINE_CONSTRAINTS");
            start_time = System.currentTimeMillis();

            try {
                tree.insert(tuples.get(i).measure_values, i);

                int p_id = tuples.get(i).prev_id;

                if (p_id != -1) {
                    tree.delete(tuples.get(p_id).measure_values, p_id);
                }
            } catch (Exception e) {
            }

            subspace_STop_Down(i);
            single_tuple_time = System.currentTimeMillis() - start_time;
            cumulative_time += single_tuple_time;
            //System.out.println("ID, TOTAL_CELL, TOTAL_SKYLINE, NON_EMPTY_LISTS, TIME");
            //System.out.println(i + "," + num_of_cells + "," + total_skyline_tuples + "," + num_of_non_empty_list + "," + single_tuple_time);
            //if(i%1000 == 0)
            System.out.println(i + "," + cumulative_time + "," + num_of_cells + "," + total_skyline_tuples + "," + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + "," + num_of_non_empty_list + "," + number_of_comparison + "," + number_of_traversal);
            /*if (tuples.get(i).maximal_skyline_constraints != null) {
                if (tuples.get(i).maximal_skyline_constraints.isEmpty()==false) {
                    for(int k=0; k<tuples.get(i).maximal_skyline_constraints.size(); k++)
                    tuples.get(i).maximal_skyline_constraints.get(k).print();
                }
            }*/
        }
        //System.out.println("Total Execution Time: " + cumulative_time + "ms");
    }

    public static void load_Data() throws FileNotFoundException, IOException {
        File data_file = new File("C:\\Users\\kakon\\Documents\\NetBeansProjects\\Bottom_Up\\src\\bottom_up\\k3.txt");

        /*File data_file = new File("../../data/sorted_latest.csv");*/
//        File data_file = new File("../../data/test.csv");
        BufferedReader reader = new BufferedReader(new FileReader(data_file));
        //reader.readLine(); //eating header line 

        String line, cell;
        int counter = 0;
        tuples = new ArrayList<STuple>();
        Tuple tuple;
        StringTokenizer tokenizer;
        ArrayList<HashMap<String, Short>> dimension_index = new ArrayList<HashMap<String, Short>>(); //This is for mapping tuples' values from string to int
        short[] dimension_index_values = new short[dimension_attributes]; // This stores the current cardinality in each dimension attribute.
        for (int d = 0; d < dimension_attributes; d++) // Initializing the mapping data structure
        {
            dimension_index.add(new HashMap<String, Short>());
            dimension_index_values[d] = 0;
        }
        Index temp_index = new Index(dimension_attributes);
        int[] temp_measure_values = new int[measure_attributes];

        while ((line = reader.readLine()) != null) {
            tokenizer = new StringTokenizer(line, ",");
            for (int i = 0, d = 0; i < total_dimension_attributes_compared.length; i++) {
                cell = tokenizer.nextToken();
                //System.out.println(cell);
                if (total_dimension_attributes_compared[i] != 0) {
                    if (dimension_index.get(d).containsKey(cell)) {
                        temp_index.indices[d] = dimension_index.get(d).get(cell);
                    } else {
                        dimension_index_values[d]++;
                        dimension_index.get(d).put(cell, dimension_index_values[d]);
                        temp_index.indices[d] = dimension_index_values[d];
                    }
                    d++;
                }
            }

            for (int i = 0, m = 0; i < total_measure_attributes_compared.length; i++) {
                cell = tokenizer.nextToken();
                if (total_measure_attributes_compared[i] == 1) {
                    temp_measure_values[m++] = Integer.parseInt(cell);
                } else if (total_measure_attributes_compared[i] == -1) {
                    temp_measure_values[m++] = -Integer.parseInt(cell);
                }
            }

            int p_id = Integer.parseInt(tokenizer.nextToken());
            tuples.add(new STuple(counter++, temp_measure_values, temp_index, p_id));

            if (counter == number_of_tuples) {
                break;
            }
        }
    }

    public static void subspace_STop_Down(int tuple_id) {
        int dom = Integer.MAX_VALUE;

        for (short i = (short) (measure_subspace_skip_bit.length - 1); i > 0; i--) {
            if (measure_subspace_skip_bit[i] == 1 || i == measure_subspace_skip_bit.length - 1) {

                skyline_constraints = 0;

                int p_id = tuples.get(tuple_id).prev_id;

                if (p_id == -1) {
                    top_Down(tuple_id, i, new Index(dimension_attributes));
                    continue;
                }

                if (i == measure_subspace_skip_bit.length - 1) {
                    dom = comparison2(tuples.get(p_id), tuples.get(tuple_id), i, true);
                }
                //System.out.println("here" + " " + tuple_id + " " + dom + " " + i + " " + tuples.get(tuple_id).tested_dominating);
                if ((i & tuples.get(tuple_id).tested_dominated) != 0) {
                    top_Down(tuple_id, i, new Index(dimension_attributes));
                } else if ((i & tuples.get(tuple_id).tested_dominating) != 0) {
                    System.out.println("here" + " " + tuple_id + " " + dom + " " + i + " " + tuples.get(tuple_id).tested_dominating);
                    ptop_Down(tuple_id, i, -dom);
                } else {
                    ptop_Down(tuple_id, i);
                }
            }
        }
    }

    public static void ptop_Down(int tuple_id, short subspace) {
        Tuple t = tuples.get(tuple_id);
        int p_id = tuples.get(tuple_id).prev_id;

        if (tuples.get(p_id).maximal_skyline_constraints == null) {
            return;
        }
        if (tuples.get(p_id).maximal_skyline_constraints.isEmpty()) {
            return;
        }

        for (int k = 0; k < tuples.get(p_id).maximal_skyline_constraints.size(); k++) {
            Index index_maximal_skyline_constraints = tuples.get(p_id).maximal_skyline_constraints.get(k);

            cube.get(index_maximal_skyline_constraints).skyline_tuples[subspace].remove(p_id);
            cube.get(index_maximal_skyline_constraints).skyline_tuples[subspace].remove(tuple_id);
        }
    }

    public static void ptop_Down(int tuple_id, short subspace, int measure) {
        int p_id = tuples.get(tuple_id).prev_id;
System.out.println(measure);
        if (tuples.get(p_id).maximal_skyline_constraints == null) {
            System.out.println(measure);
            return;
        }
        if (tuples.get(p_id).maximal_skyline_constraints.isEmpty()) {
            return;
        }

        TIntArrayList range_query_result = new TIntArrayList();
System.out.println(measure);
        try {
            int tempArray[] = tuples.get(tuple_id).measure_values.clone();
System.out.println(measure);
            for (short m = 0, quotient = subspace; m < measure_attributes; m++, quotient = (short) (quotient / 2)) {
                if (quotient % 2 != 0) {
                    tempArray[m] = 0;
                }
            }
            tempArray[measure] = tuples.get(tuple_id).measure_values[measure];

            range_query_result = tree.range(tempArray, tuples.get(p_id).measure_values, measure);

        } catch (Exception e) {
        }

        for (int k = 0; k < tuples.get(p_id).maximal_skyline_constraints.size(); k++) {
            Index index_maximal_skyline_constraints = tuples.get(p_id).maximal_skyline_constraints.get(k);

            cube.get(index_maximal_skyline_constraints).skyline_tuples[subspace].remove(p_id);
            total_skyline_tuples--;

            //TIntArrayList contextual_range_query_result = new TIntArrayList();
            range_query_result.add(tuple_id);

            for (int j = 0; j < range_query_result.size(); j++) {
                int d = 0;
                int id = (int) range_query_result.get(j);

                for (; d < dimension_attributes; d++) {
                    if (index_maximal_skyline_constraints.indices[d] == 0) {
                        continue;
                    }
                    if (tuples.get(id).dimension_index.indices[d]
                            != index_maximal_skyline_constraints.indices[d]) {
                        break;
                    }
                }
                if (d == dimension_attributes) {
                    //contextual_range_query_result.add(id);

                    top_Down(id, subspace, index_maximal_skyline_constraints);
                    System.out.println(id + " " + cube.get(index_maximal_skyline_constraints).skyline_tuples[subspace].size());

                }
            }
        }
    }

    public static void top_Down(int tuple_id, short measure_subspace, Index primaryIndex, int p_id) {
        STuple t = tuples.get(tuple_id);
        ArrayList<Index> queue = new ArrayList<Index>();
        queue.add(primaryIndex);
        Index index;
        while (!queue.isEmpty()) {
            index = queue.remove(0);
            if (cube.containsKey(index) == false) {
                num_of_cells++;
                cube.put(index, new STop_Down_Cuboid(measure_attributes));
//                overhead_single_tuple+=cube.get(index).overhead_Revised_Top_Down_Cuboid;
            }

            STop_Down_Cuboid cuboid = cube.get(index);
            if (cuboid.result[measure_subspace] == 0 || measure_subspace == measure_subspace_skip_bit.length - 1) {
                number_of_traversal++;
                if (cuboid.skyline_tuples[measure_subspace] != null) {
                    cuboid.skyline_tuples[measure_subspace].remove(p_id);
                    cuboid.skyline_tuples[measure_subspace].add(tuple_id);
                }
                if (cuboid.result[measure_subspace] == 0) {
                    skyline_constraints++;
                }
            }
            cuboid.result[measure_subspace] = 0; //restoring initial values
            cuboid.flag = 0; //restoring initial values
            cuboid.store = 1; //restoring initial values
        }
    }

    public static void top_Down(int tuple_id, short measure_subspace, Index primaryIndex) {
        STuple t = tuples.get(tuple_id);
        ArrayList<Index> queue = new ArrayList<Index>();
        queue.add(primaryIndex);
        Index index;
        while (!queue.isEmpty()) {
            index = queue.remove(0);
            if (cube.containsKey(index) == false) {
                num_of_cells++;
                cube.put(index, new STop_Down_Cuboid(measure_attributes));
//                overhead_single_tuple+=cube.get(index).overhead_Revised_Top_Down_Cuboid;
            }

            STop_Down_Cuboid cuboid = cube.get(index);
            if (cuboid.result[measure_subspace] == 0 || measure_subspace == measure_subspace_skip_bit.length - 1) {
                number_of_traversal++;
                if (cuboid.skyline_tuples[measure_subspace] != null) {
                    for (int i = 0; i < cuboid.skyline_tuples[measure_subspace].size(); i++) {
                        if (cuboid.result[measure_subspace] == 1 && (dimension_skip_level == index.count_Num_Of_Zeros() - 1)) {
                            break;
                        }
                        int id = cuboid.skyline_tuples[measure_subspace].get(i);
                        STuple t_in_skyline = tuples.get(id);
                        int dom;

                        number_of_comparison++;

                        if (measure_subspace == measure_subspace_skip_bit.length - 1) {
                            dom = comparison2(t, t_in_skyline, measure_subspace, false);
                            // System.out.println(dominated_+" "+dominating_);
                        } else {
                            dom = comparison1(t, t_in_skyline, measure_subspace);
                        }

                        if (dom == -1) { // new Revised_Tuple dominates
                            for (int d = 0; d < dimension_attributes; d++) {
                                Index bottom = new Index(index);

                                if (bottom.indices[d] == 0) {
                                    if (t.dimension_index.indices[d] != t_in_skyline.dimension_index.indices[d]) {
                                        bottom.indices[d] = t_in_skyline.dimension_index.indices[d];

                                        if (cube.get(bottom) == null) { // Is it necessary ? YES. If we skip in Dimension lattice.
                                            bottom.indices[d] = 0;
                                            continue;
                                        }
                                        if (cube.get(bottom).skyline_tuples[measure_subspace] == null) {
                                            if (measure_subspace_skip_bit[measure_subspace] == 1) {
                                                num_of_non_empty_list++;
                                            }
                                            cube.get(bottom).skyline_tuples[measure_subspace] = new TIntArrayList();
                                        }
                                        flagDuplicate = false;

                                        checkDuplicate(t_in_skyline.id, measure_subspace, bottom, d, false);

                                        if (flagDuplicate == false) {
                                            cube.get(bottom).skyline_tuples[measure_subspace].add(id);
                                            if (measure_subspace_skip_bit[measure_subspace] == 1) {
                                                total_skyline_tuples++;
                                            }
                                            c1++;
                                        }

                                        bottom.indices[d] = 0;
                                    }
                                }
                            }
                            cuboid.skyline_tuples[measure_subspace].removeAt(i);
                            if (measure_subspace_skip_bit[measure_subspace] == 1) {
                                total_skyline_tuples--;
                            }
                            c2++;
                            i--;
                        } else if (measure_subspace == measure_subspace_skip_bit.length - 1) {
                            generatePrunedSet(tuple_id, id, index);
                        }
                    }
                }
                if (cuboid.result[measure_subspace] == 0) {

                    {
                        skyline_constraints++;
                    }
                }
                if (cuboid.result[measure_subspace] == 0 && cuboid.store == 1) {
                    if (cuboid.skyline_tuples[measure_subspace] == null) {
                        if (measure_subspace_skip_bit[measure_subspace] == 1) {
                            num_of_non_empty_list++;
                        }
                        cuboid.skyline_tuples[measure_subspace] = new TIntArrayList();
                    }
                    cuboid.skyline_tuples[measure_subspace].add(tuple_id);
                    if (measure_subspace_skip_bit[measure_subspace] == 1) {
                        total_skyline_tuples++;
                    }
                    c3++;
                }
            }
            for (int d = 0; d < dimension_attributes; d++) {
                if (index.indices[d] == 0) {
                    Index b = new Index(index);
                    b.num_of_parents = (short) (index.num_of_parents + 1);
                    b.indices[d] = t.dimension_index.indices[d];
                    int num_of_zeros = b.count_Num_Of_Zeros();

                    if (num_of_zeros == dimension_skip_level) {
                        break;
                    }

                    if (cube.get(b) == null) {

                        if (num_of_zeros > dimension_skip_level) {
                            num_of_cells++;
                            cube.put(b, new STop_Down_Cuboid(measure_attributes));
//                            overhead_single_tuple+=cube.get(b).overhead_Revised_Top_Down_Cuboid;
                        }
                    }
                    if (num_of_zeros > dimension_skip_level) {
                        cube.get(b).flag++;
                        if (cuboid.result[measure_subspace] == 0) {
                            cube.get(b).store = 0;
                        }

                        if (cube.get(b).flag == b.num_of_parents) {
                            queue.add(b);
                        }
                    }
                }
            }
            cuboid.result[measure_subspace] = 0; //restoring initial values
            cuboid.flag = 0; //restoring initial values
            cuboid.store = 1; //restoring initial values
        }
    }

    public static void cuboid_mark(int tuple_id, int tuple_id_, Index index, int[] pruned_set) {
        for (int i = 0; i < pruned_set.length; i++) {
            cube.get(index).result[pruned_set[i]] = 1;
        }

        for (int d = 0; d < dimension_attributes; d++) {
            if (index.indices[d] == 0) {
                if (tuples.get(tuple_id).dimension_index.indices[d] == tuples.get(tuple_id_).dimension_index.indices[d]) {
                    index.indices[d] = tuples.get(tuple_id).dimension_index.indices[d];
                    if (cube.get(index) != null) {
                        cuboid_mark(tuple_id, tuple_id_, index, pruned_set);
                    }
                    index.indices[d] = 0;
                }
            }
        }
    }

    public static void checkDuplicate(int tuple_id, short measure_subspace, Index index, int i, boolean flag) {
        if (flag) {
            if (cube.get(index).skyline_tuples[measure_subspace] != null) {
                for (int j = 0; j < cube.get(index).skyline_tuples[measure_subspace].size(); j++) {
                    if (cube.get(index).skyline_tuples[measure_subspace].get(j) == tuple_id) {
                        flagDuplicate = true;
                        return;
                    }
                }
            }
        }

        for (int d = 0; d < dimension_attributes; d++) {
            if (index.indices[d] != 0 && i != d) {
                short temp = index.indices[d];
                index.indices[d] = 0;
                checkDuplicate(tuple_id, measure_subspace, index, i, true);
                index.indices[d] = temp;
            }
        }
    }

    private static void generatePrunedSet(int tuple_id, int tuple_id_, Index index) {
        int[] powerset_dominated = new int[(int) Math.pow(2, count_dominated) - 1];
        int[] powerset_incomparable = new int[(int) Math.pow(2, count_incomparable)];
        int[] pruned_set = new int[powerset_dominated.length * powerset_incomparable.length];

        for (int i = 0, j = 0; i < measure_attributes && j < powerset_incomparable.length; i++) {
            int k = 1 << i;

            int temp = k & incomparable_;

            if (temp != 0) {
                powerset_incomparable[j] = temp;

                int m = j + 1;

                for (int l = 0; l < j; l++, m++) {
                    powerset_incomparable[m] = temp | powerset_incomparable[l];
                    // System.out.println(powerset_incomparable[m]);
                }

                j = m;
            }
        }

        powerset_incomparable[powerset_incomparable.length - 1] = 0;
        //System.out.println(pruned_set.length);
        for (int i = 0, j = 0; i < measure_attributes && j < powerset_dominated.length; i++) {
            int k = 1 << i;

            int temp = k & dominated_;

            if (temp != 0) {
                powerset_dominated[j] = temp;

                int m = j + 1;

                for (int l = 0; l < j; l++, m++) {
                    powerset_dominated[m] = temp | powerset_dominated[l];
                }

                j = m;
            }
        }

        int k = 0;

        for (int i = 0; i < powerset_dominated.length; i++) {
            for (int j = 0; j < powerset_incomparable.length; j++) {
                pruned_set[k] = powerset_dominated[i] | powerset_incomparable[j];

                k++;
            }
        }

        cuboid_mark(tuple_id, tuple_id_, index, pruned_set);
    }

    public static byte comparison1(STuple t, STuple t_, short subspace) {
        if (t_.tested_by == t.id && t_.subspace == subspace) {
            return t_.test_result;
        }
        t_.tested_by = t.id;
        t_.subspace = subspace;
        int t_dom_t_ = 0;
        int t__dom_t = 0;

        for (short m = 0, quotient = subspace; m < measure_attributes; m++, quotient = (short) (quotient / 2)) {
            if (quotient % 2 != 0) {
                if (t.measure_values[m] > t_.measure_values[m]) {
                    t_dom_t_++;
                } else if (t_.measure_values[m] > t.measure_values[m]) {
                    t__dom_t++;
                }
                if (t_dom_t_ > 0 && t__dom_t > 0) // t is incomparable with t_
                {
                    t_.test_result = 0;
                    return 0;
                }
            }
        }

        if (t_dom_t_ == 0 && t__dom_t == 0) // t is incomparable with t_
        {
            t_.test_result = 0;
            return 0;
        }

        t_.test_result = -1;
        return -1; // t_ is dominated by t
    }

    public static byte comparison2(STuple t, STuple t_, short subspace, boolean isConsecutive) {
        dominated_ = dominating_ = incomparable_ = 0;
        count_dominated = count_dominating = count_incomparable = 0;
        if (t_.tested_by == t.id && t_.subspace == subspace) {
            //   long time1=System.currentTimeMillis();
            dominating_ = t_.tested_dominating;
            dominated_ = t_.tested_dominated;
            incomparable_ = t_.tested_incomparable;
            count_dominating = t_.tested_count_dominating;
            count_dominated = t_.tested_count_dominated;
            count_incomparable = t_.tested_count_incomparable;
            // overhead_single_tuple+=System.currentTimeMillis()-time1;
            return t_.test_result;
        }
        t_.tested_by = t.id;
        t_.subspace = subspace;
        int t_dom_t_ = 0;
        int t__dom_t = 0;

        for (short m = 0, quotient = subspace; m < measure_attributes; m++, quotient = (short) (quotient / 2)) {
            int k = 1 << m;
            if (quotient % 2 != 0) {

                if (t.measure_values[m] > t_.measure_values[m]) {
                    t_dom_t_++;
                    dominating_ |= k;
                    count_dominating++;
                } else if (t_.measure_values[m] > t.measure_values[m]) {
                    t__dom_t++;
                    dominated_ |= k;
                    count_dominated++;
                    if (isConsecutive) {
                        t_.tested_dominated = dominated_;
                        return (byte) m;
                    }
                } else {
                    count_incomparable++;
                    incomparable_ |= k;
                }
            }
        }

        if (t_dom_t_ > 0 && t__dom_t > 0) // t is incomparable with t_
        {
            //long time1=System.currentTimeMillis();
            t_.tested_dominating = dominating_;
            t_.tested_dominated = dominated_;
            t_.tested_incomparable = incomparable_;
            t_.tested_count_dominating = count_dominating;
            t_.tested_count_dominated = count_dominated;
            t_.tested_count_incomparable = count_incomparable;
            //overhead_single_tuple+=System.currentTimeMillis()-time1;
            t_.test_result = 0;
            return 0;
        }
        if (t_dom_t_ == 0 && t__dom_t == 0) // t is incomparable with t_
        {
            //long time1=System.currentTimeMillis();
            t_.tested_dominating = dominating_;
            t_.tested_dominated = dominated_;
            t_.tested_incomparable = incomparable_;
            t_.tested_count_dominating = count_dominating;
            t_.tested_count_dominated = count_dominated;
            t_.tested_count_incomparable = count_incomparable;
            //overhead_single_tuple+=System.currentTimeMillis()-time1;
            t_.test_result = 0;
            return 0;
        }

        if (t__dom_t > 0 && t_dom_t_ == 0) // t is dominated by t_
        {
            //long time1=System.currentTimeMillis();
            t_.tested_dominating = dominating_;
            t_.tested_dominated = dominated_;
            t_.tested_incomparable = incomparable_;
            t_.tested_count_dominating = count_dominating;
            t_.tested_count_dominated = count_dominated;
            t_.tested_count_incomparable = count_incomparable;
            //overhead_single_tuple+=System.currentTimeMillis()-time1;
            t_.test_result = 1;
            return 1;
        }

        t_.tested_dominating = dominating_;
        t_.tested_dominated = dominated_;
        t_.tested_incomparable = incomparable_;
        t_.tested_count_dominating = count_dominating;
        t_.tested_count_dominated = count_dominated;
        t_.tested_count_incomparable = count_incomparable;
        //overhead_single_tuple+=System.currentTimeMillis()-time1;
        t_.test_result = -1;
        System.out.println(t_.tested_dominating);
        return -1; // t_ is dominated by t
    }
}
