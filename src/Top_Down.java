import gnu.trove.list.array.TIntArrayList;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Top_Down extends FactMonitoring {

    public static HashMap<Index, Top_Down_Cuboid> cube;
    public static int num_of_cells;
    public static long num_of_non_empty_list;
    public static long total_skyline_tuples;
    public static boolean flagDuplicate;

    public static void main(String[] args) throws FileNotFoundException, IOException {
        init();
        load_Data();
        cube = new HashMap<Index, Top_Down_Cuboid>();
        Runtime runtime = Runtime.getRuntime(); // Getting JAVA runtime
        int temp_gameID = -1;

        total_number_of_comparison = 0;

        for (int i = 0; i < number_of_tuples; i++) {
            number_of_comparison = 0;
            start_time = System.currentTimeMillis();

            if (temp_gameID != tuples.get(i).game_id) {
                tree.clear();
                temp_gameID = tuples.get(i).game_id;
            }

            int p_id = -1;
            try {
                tree.insert(tuples.get(i).measure_values, i);

                p_id = tuples.get(i).prev_id;

                if (p_id != -1) {
                    tree.delete(tuples.get(p_id).measure_values, p_id);
                }
            } catch (Exception e) {
            }

            subspace_Top_Down(i);
            single_tuple_time = System.currentTimeMillis() - start_time;
            cumulative_time += single_tuple_time;
            total_number_of_comparison += number_of_comparison;
            //System.out.println("ID, TOTAL_CELL, TOTAL_SKYLINE, NON_EMPTY_LISTS, TIME");
            //System.out.println(i + "," + num_of_cells + "," + total_skyline_tuples + "," + num_of_non_empty_list + "," + single_tuple_time);
            if (i % 1000 == 0) {

            System.out.println(i + "," + single_tuple_time + "," + cumulative_time + ","
                    + num_of_cells + "," + total_skyline_tuples + ","
                    + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + ","
                    + num_of_non_empty_list + "," + number_of_comparison + ","
                    + total_number_of_comparison + "," + number_of_traversal);
            }
            /*if (tuples.get(i).maximal_skyline_constraints != null) {
                if (tuples.get(i).maximal_skyline_constraints.isEmpty()==false) {
                    for(int k=0; k<tuples.get(i).maximal_skyline_constraints.size(); k++)
                    tuples.get(i).maximal_skyline_constraints.get(k).print();
                }
            }*/
        }
        //System.out.println("Total Execution Time: " + cumulative_time + "ms");
    }

    public static void subspace_Top_Down(int tuple_id) {
        for (short i = 1; i < measure_subspace_skip_bit.length; i++) {
            if (measure_subspace_skip_bit[i] == 1) {
                skyline_constraints = 0;

                int p_id = tuples.get(tuple_id).prev_id;

                if (p_id == -1) {//System.out.println("here");
                    top_Down(tuple_id, i);
                    continue;
                }
                int dom = comparison(tuples.get(tuple_id), tuples.get(p_id), i, true);

                switch (dom) {
                    case -1:
                        top_Down(tuple_id, i);
                        break;
                    case 0:
                        ptop_Down(tuple_id, i);
                        break;
                    default:
                        ptop_Down(tuple_id, i, dom);
                        break;
                }
            }
        }
    }

    public static void ptop_Down(int tuple_id, short subspace) {
        ArrayList<Index> queue = new ArrayList<Index>();
        queue.add(new Index(dimension_attributes));
        Index index;

        int p_id = tuples.get(tuple_id).prev_id;

        while (!queue.isEmpty()) {
            index = queue.remove(0);

            number_of_traversal++;

            if (cube.containsKey(index) == false) {
                continue;
            }

            Top_Down_Cuboid cuboid = cube.get(index);

            if (cuboid.skyline_tuples[subspace] == null) {
                cuboid.result = 1; //restoring initial values
                cuboid.flag = 0; //restoring initial values
                cuboid.store = 1; //restoring initial values
                continue;
            }
            if (cuboid.skyline_tuples[subspace].contains(p_id) == false) {
                cuboid.result = 1; //restoring initial values
                cuboid.flag = 0; //restoring initial values
                cuboid.store = 1; //restoring initial values
                continue;
            }

            cuboid.skyline_tuples[subspace].remove(p_id);
            cuboid.skyline_tuples[subspace].add(tuple_id);

            for (int d = 0; d < dimension_attributes; d++) {
                if (index.indices[d] == 0) {
                    Index b = new Index(index);
                    b.num_of_parents = (short) (index.num_of_parents + 1);
                    b.indices[d] = tuples.get(tuple_id).dimension_index.indices[d];
                    int num_of_zeros = b.count_Num_Of_Zeros();

                    if (num_of_zeros == dimension_skip_level) {
                        break;
                    }
                    if (cube.get(b) == null) {

                        if (num_of_zeros > dimension_skip_level) {
                            num_of_cells++;
                            cube.put(b, new Top_Down_Cuboid(measure_attributes));
                        }
                    }
                    if (num_of_zeros > dimension_skip_level) {
                        cube.get(b).flag++;
                        if (cuboid.result == 1) {
                            cube.get(b).store = 0;
                        }

                        if (cube.get(b).flag == b.num_of_parents) {
                            queue.add(b);
                        }
                    }
                }
            }
            cuboid.result = 1; //restoring initial values
            cuboid.flag = 0; //restoring initial values
            cuboid.store = 1; //restoring initial values
        }
    }

    public static void ptop_Down(int tuple_id, short subspace, int measure) {
        ArrayList<Index> queue = new ArrayList<Index>();
        queue.add(new Index(dimension_attributes));
        Index index;

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

        while (!queue.isEmpty()) {
            index = queue.remove(0);

            number_of_traversal++;

            if (cube.containsKey(index) == false) {
                continue;
            }

            Top_Down_Cuboid cuboid = cube.get(index);

            if (cuboid.skyline_tuples[subspace] == null) {
                cuboid.result = 1; //restoring initial values
                cuboid.flag = 0; //restoring initial values
                cuboid.store = 1; //restoring initial values
                continue;
            }
            if (cuboid.skyline_tuples[subspace].contains(p_id) == false) {
                cuboid.result = 1; //restoring initial values
                cuboid.flag = 0; //restoring initial values
                cuboid.store = 1; //restoring initial values
                continue;
            }

            cube.get(index).skyline_tuples[subspace].remove(p_id);
            total_skyline_tuples--;

            TIntArrayList contextual_range_query_result = new TIntArrayList();

            contextual_range_query_result.add(tuple_id);

            for (int j = 0; j < range_query_result.size(); j++) {
                int d = 0;
                int id = (int) range_query_result.get(j);

                for (; d < dimension_attributes; d++) {
                    if (index.indices[d] == 0) {
                        continue;
                    }
                    if (tuples.get(id).dimension_index.indices[d]
                            != index.indices[d]) {
                        break;
                    }
                }
                if (d == dimension_attributes) {
                    contextual_range_query_result.add(id);
                }
            }
            for (int j = 0; j < contextual_range_query_result.size(); j++) {
                int t_id = (int) contextual_range_query_result.get(j);
                Tuple t = tuples.get(t_id);

                for (int i = 0; i < cuboid.skyline_tuples[subspace].size(); i++) {
                    if ((cuboid.result == 0 && (dimension_skip_level == index.count_Num_Of_Zeros() - 1))) {
                        break;
                    }
                    int id = cuboid.skyline_tuples[subspace].get(i);
                    Tuple t_ = tuples.get(id);

                    number_of_comparison++;

                    int dom = comparison(t, t_, subspace, false);

                    if (dom == 1) { // new tuple is dominated
                        cuboid_mark(t_id, id, index);
                    } else if (dom == -1) { // new tuple dominates
                        for (int d = 0; d < dimension_attributes; d++) {
                            Index bottom = new Index(index);

                            if (bottom.indices[d] == 0) {
                                if (t.dimension_index.indices[d] != t_.dimension_index.indices[d]) {
                                    bottom.indices[d] = t_.dimension_index.indices[d];

                                    if (cube.get(bottom) == null) { // Is it necessary ? YES. If we skip in Dimension lattice.
                                        bottom.indices[d] = 0;
                                        continue;
                                    }
                                    if (cube.get(bottom).skyline_tuples[subspace] == null) {
                                        num_of_non_empty_list++;
                                        cube.get(bottom).skyline_tuples[subspace] = new TIntArrayList();
                                    }
                                    flagDuplicate = false;

                                    checkDuplicate(t_.id, subspace, bottom, d, false);

                                    if (flagDuplicate == false) {

                                        cube.get(bottom).skyline_tuples[subspace].add(id);
                                        total_skyline_tuples++;
                                    }
                                    bottom.indices[d] = 0;
                                }
                            }
                        }

                        cuboid.skyline_tuples[subspace].removeAt(i);
                        total_skyline_tuples--;
                        i--;
                    }
                }

                if (cuboid.result == 1) {
                    skyline_constraints++;
                }
                if (cuboid.result == 1 && cuboid.store == 1) {
                    if (cuboid.skyline_tuples[subspace] == null) {
                        num_of_non_empty_list++;
                        cuboid.skyline_tuples[subspace] = new TIntArrayList();
                    }
                    cuboid.skyline_tuples[subspace].add(t_id);
                    total_skyline_tuples++;
                }
            }

            for (int d = 0; d < dimension_attributes; d++) {
                if (index.indices[d] == 0) {
                    Index b = new Index(index);
                    b.num_of_parents = (short) (index.num_of_parents + 1);
                    b.indices[d] = tuples.get(tuple_id).dimension_index.indices[d];
                    int num_of_zeros = b.count_Num_Of_Zeros();

                    if (num_of_zeros == dimension_skip_level) {
                        break;
                    }
                    if (cube.get(b) == null) {

                        if (num_of_zeros > dimension_skip_level) {
                            num_of_cells++;
                            cube.put(b, new Top_Down_Cuboid(measure_attributes));
                        }
                    }
                    if (num_of_zeros > dimension_skip_level) {
                        cube.get(b).flag++;
                        if (cuboid.result == 1) {
                            cube.get(b).store = 0;
                        }

                        if (cube.get(b).flag == b.num_of_parents) {
                            queue.add(b);
                        }
                    }
                }
            }
            cuboid.result = 1; //restoring initial values
            cuboid.flag = 0; //restoring initial values
            cuboid.store = 1; //restoring initial values
        }
    }

    public static void top_Down(int tuple_id, short subspace) {
        Tuple t = tuples.get(tuple_id);
        ArrayList<Index> queue = new ArrayList<Index>();
        queue.add(new Index(dimension_attributes));
        Index index;
        while (!queue.isEmpty()) {
            index = queue.remove(0);

            number_of_traversal++;

            if (cube.containsKey(index) == false) {
                num_of_cells++;
                cube.put(index, new Top_Down_Cuboid(measure_attributes));
            }

            Top_Down_Cuboid cuboid = cube.get(index);
            if (cuboid.skyline_tuples[subspace] != null) {

                for (int i = 0; i < cuboid.skyline_tuples[subspace].size(); i++) {
                    if ((cuboid.result == 0 && (dimension_skip_level == index.count_Num_Of_Zeros() - 1))) {
                        break;
                    }
                    int id = cuboid.skyline_tuples[subspace].get(i);
                    Tuple t_ = tuples.get(id);

                    number_of_comparison++;

                    int dom = comparison(t, t_, subspace, false);

                    if (dom == 1) { // new tuple is dominated
                        cuboid_mark(tuple_id, id, index);
                    } else if (dom == -1) { // new tuple dominates
                        for (int d = 0; d < dimension_attributes; d++) {
                            Index bottom = new Index(index);

                            if (bottom.indices[d] == 0) {
                                if (t.dimension_index.indices[d] != t_.dimension_index.indices[d]) {
                                    bottom.indices[d] = t_.dimension_index.indices[d];

                                    if (cube.get(bottom) == null) { // Is it necessary ? YES. If we skip in Dimension lattice.
                                        bottom.indices[d] = 0;
                                        continue;
                                    }
                                    if (cube.get(bottom).skyline_tuples[subspace] == null) {
                                        num_of_non_empty_list++;
                                        cube.get(bottom).skyline_tuples[subspace] = new TIntArrayList();
                                    }
                                    flagDuplicate = false;

                                    checkDuplicate(t_.id, subspace, bottom, d, false);

                                    if (flagDuplicate == false) {

                                        cube.get(bottom).skyline_tuples[subspace].add(id);
                                        total_skyline_tuples++;
                                    }
                                    bottom.indices[d] = 0;
                                }
                            }
                        }

                        cuboid.skyline_tuples[subspace].removeAt(i);
                        total_skyline_tuples--;
                        i--;
                    }
                }
            }
            if (cuboid.result == 1) {
                skyline_constraints++;
            }
            if (cuboid.result == 1 && cuboid.store == 1) {
                if (cuboid.skyline_tuples[subspace] == null) {
                    num_of_non_empty_list++;
                    cuboid.skyline_tuples[subspace] = new TIntArrayList();
                }
                cuboid.skyline_tuples[subspace].add(tuple_id);

                total_skyline_tuples++;
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
                            cube.put(b, new Top_Down_Cuboid(measure_attributes));
                        }
                    }
                    if (num_of_zeros > dimension_skip_level) {
                        cube.get(b).flag++;
                        if (cuboid.result == 1) {
                            cube.get(b).store = 0;
                        }
                        if (cube.get(b).flag == b.num_of_parents) {

                            queue.add(b);
                        }
                    }
                }
            }
            cuboid.result = 1; //restoring initial values
            cuboid.flag = 0; //restoring initial values
            cuboid.store = 1; //restoring initial values
        }
    }

    public static void cuboid_mark(int tuple_id, int tuple_id_, Index index) {
        cube.get(index).result = 0;
        for (int d = 0; d < dimension_attributes; d++) {
            if (index.indices[d] == 0) {
                if (tuples.get(tuple_id).dimension_index.indices[d] == tuples.get(tuple_id_).dimension_index.indices[d]) {
                    index.indices[d] = tuples.get(tuple_id).dimension_index.indices[d];
                    if (cube.get(index) != null) { // Is it necessary ? YES. If we skip in Dimension lattice.
                        cuboid_mark(tuple_id, tuple_id_, index);
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
}