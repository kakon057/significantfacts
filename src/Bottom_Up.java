/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bottom_up;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import gnu.trove.list.array.TIntArrayList;

public class Bottom_Up extends FactMonitoring {

    public static HashMap<Index, Cuboid> cube;
    public static int num_of_cells;
    public static long total_skyline_tuples;

    public static void main(String[] args) throws FileNotFoundException, IOException {
        init();
        load_Data();
        cube = new HashMap<Index, Cuboid>();
        Runtime runtime = Runtime.getRuntime(); // Getting JAVA runtime
        for (int i = 0; i < number_of_tuples; i++) {
            start_time = System.currentTimeMillis();

            try {
                tree.insert(tuples.get(i).measure_values, i);

                int p_id = tuples.get(i).prev_id;

                if (p_id != -1) {
                    tree.delete(tuples.get(p_id).measure_values, p_id);
                }
            } catch (Exception e) {
            }

            subspace_Bottom_Up(i);

            single_tuple_time = System.currentTimeMillis() - start_time;
            cumulative_time += single_tuple_time;
            //System.out.println("ID, TOTAL_CELL, TOTAL_SKYLINE, TIME");
            //System.out.println(i + "," + num_of_cells + "," + total_skyline_tuples + "," + single_tuple_time);
            //if(i%1000 == 0)
            System.out.println(i + "," + cumulative_time + "," + num_of_cells + "," + total_skyline_tuples + "," + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + "," + number_of_comparison + "," + number_of_traversal);
        }
        //System.out.println("Total Execution Time: " + cumulative_time + "ms");
    }

    public static void subspace_Bottom_Up(int tuple_id) {
        for (short i = 1; i < measure_subspace_skip_bit.length; i++) {
            if (measure_subspace_skip_bit[i] == 1) {
                skyline_constraints = 0;

                int p_id = tuples.get(tuple_id).prev_id;
                //System.out.println(tuple_id+" "+p_id);

                if (p_id == -1) {
                    bottom_Up(tuple_id, i);
                    continue;
                }

                int dom = comparison(tuples.get(tuple_id), tuples.get(p_id), i, true);

                if (dom == -1) {
                    bottom_Up(tuple_id, i);
                } else if (dom == 0) {
                    pbottom_Up(tuple_id, i);
                } else {
                    pbottom_Up(tuple_id, i, dom);
                }
                // System.out.println(tuple_id + "," + i + "," + skyline_constraints);
            }
        }
    }

    public static void cuboid_mark(int tuple_id, short measure_subspace, Index index, boolean affected_by_prev_id) {
        cube.get(index).last_updated_by = tuple_id;
        cube.get(index).last_updated_subspace = measure_subspace;
        cube.get(index).affected_by_prev_id = affected_by_prev_id;

        for (int d = 0; d < dimension_attributes; d++) {
            if (index.indices[d] != 0) {
                short temp = index.indices[d];
                index.indices[d] = 0;
                cuboid_mark(tuple_id, measure_subspace, index, affected_by_prev_id);
                index.indices[d] = temp;
            }
        }
    }

    public static void bottom_Up(int tuple_id, short measure_subspace) {
        ArrayList<Index> queue = new ArrayList<Index>();
        queue.add(tuples.get(tuple_id).dimension_index);
        Index index; // This will be used to dequeue from queue
        byte dom;

        while (!queue.isEmpty()) {
            index = queue.remove(0); // FIFO
            if (index.count_Num_Of_Zeros() <= dimension_skip_level) {// We will skip computation(also not store in cube) for this dimension subspace, but will enque its parents.
                for (int d = 0; d < dimension_attributes; d++) {
                    if (index.indices[d] != 0) {
                        Index parent_index = new Index(index); //This can not be pre_mapped. Because, number of possible Index is number of cell. This is very huge number.
                        parent_index.indices[d] = 0;
                        queue.add(parent_index);
                    }
                }
            } else {
                if (cube.containsKey(index) == false) {
                    cube.put(index, new Cuboid(measure_subspace_skip_bit, measure_attributes));
                    num_of_cells++; // New cell entered into cube
                } else if (cube.get(index).last_updated_by == tuple_id && cube.get(index).last_updated_subspace == measure_subspace) {
                    continue;
                }

                number_of_traversal++;
                for (int i = 0; i < cube.get(index).skyline_tuples[measure_subspace].size(); i++) {
                    number_of_comparison++;
                    dom = comparison(tuples.get(tuple_id), tuples.get(cube.get(index).skyline_tuples[measure_subspace].get(i)), measure_subspace, false);
                    if (dom == 1) //t is dominated by t_ 
                    {
                        cuboid_mark(tuple_id, measure_subspace, index, false);
                        break;
                    } else if (dom == -1) //t_ is dominated by t
                    {
                        cube.get(index).skyline_tuples[measure_subspace].removeAt(i);

                        total_skyline_tuples--;
                        i--;
                    }
                    System.out.println(dom);
                    index.print();
                }
                if (cube.get(index).last_updated_by != tuple_id || cube.get(index).last_updated_subspace != measure_subspace) {
                    cube.get(index).last_updated_by = tuple_id;
                    cube.get(index).last_updated_subspace = measure_subspace;
                    skyline_constraints++;

                    for (int d = 0; d < dimension_attributes; d++) {
                        if (index.indices[d] != 0) {
                            Index parent_index = new Index(index); //This can not be pre_mapped. Because, number of possible Index is number of cell. This is very huge number.
                            parent_index.indices[d] = 0;
                            queue.add(parent_index);
                        }
                    }
                    cube.get(index).skyline_tuples[measure_subspace].add(tuple_id);

                    total_skyline_tuples++;
                }
            }
        }
    }

    public static void pbottom_Up(int tuple_id, short subspace) {
        ArrayList<Index> queue = new ArrayList<Index>();
        queue.add(tuples.get(tuple_id).dimension_index);
        Index index; // This will be used to dequeue from queue

        int p_id = tuples.get(tuple_id).prev_id;

        while (!queue.isEmpty()) {
            index = queue.remove(0); // FIFO
            if (index.count_Num_Of_Zeros() <= dimension_skip_level) {// We will skip computation(also not store in cube) for this dimension subspace, but will enque its parents.
                for (int d = 0; d < dimension_attributes; d++) {
                    if (index.indices[d] != 0) {
                        Index parent_index = new Index(index); //This can not be pre_mapped. Because, number of possible Index is number of cell. This is very huge number.
                        parent_index.indices[d] = 0;
                        queue.add(parent_index);
                    }
                }
            } else {
                if (cube.get(index).last_updated_by == tuple_id
                        && cube.get(index).last_updated_subspace == subspace
                        && cube.get(index).affected_by_prev_id) {
                    continue;
                }

                number_of_traversal++;

                if (cube.get(index).skyline_tuples[subspace].contains(p_id) == false) {
                    cuboid_mark(tuple_id, subspace, index, true);
                    continue;
                }

                cube.get(index).skyline_tuples[subspace].remove(p_id);
                cube.get(index).skyline_tuples[subspace].add(tuple_id); 

                if (cube.get(index).last_updated_by != tuple_id || cube.get(index).last_updated_subspace != subspace
                        || cube.get(index).affected_by_prev_id == false) {
                    cube.get(index).last_updated_by = tuple_id;
                    cube.get(index).last_updated_subspace = subspace;
                    skyline_constraints++;

                    for (int d = 0; d < dimension_attributes; d++) {
                        if (index.indices[d] != 0) {
                            Index parent_index = new Index(index); //This can not be pre_mapped. Because, number of possible Index is number of cell. This is very huge number.
                            parent_index.indices[d] = 0;
                            queue.add(parent_index);
                        }
                    }                  
                }
            }
        }
    }

    public static void pbottom_Up(int tuple_id, short subspace, int measure) {
        ArrayList<Index> queue = new ArrayList<Index>();
        queue.add(tuples.get(tuple_id).dimension_index);
        Index index; // This will be used to dequeue from queue
        byte dom;

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
            index = queue.remove(0); // FIFO
            if (index.count_Num_Of_Zeros() <= dimension_skip_level) {// We will skip computation(also not store in cube) for this dimension subspace, but will enque its parents.
                for (int d = 0; d < dimension_attributes; d++) {
                    if (index.indices[d] != 0) {
                        Index parent_index = new Index(index); //This can not be pre_mapped. Because, number of possible Index is number of cell. This is very huge number.
                        parent_index.indices[d] = 0;
                        queue.add(parent_index);
                    }
                }
            } else {
                if (cube.get(index).last_updated_by == tuple_id
                        && cube.get(index).last_updated_subspace == subspace
                        && cube.get(index).affected_by_prev_id) {
                    continue;
                }

                number_of_traversal++;

                if (cube.get(index).skyline_tuples[subspace].contains(p_id) == false) {
                    cuboid_mark(tuple_id, subspace, index, true);
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
                                != tuples.get(tuple_id).dimension_index.indices[d]) {
                            break;
                        }
                    }
                    if (d == dimension_attributes) {
                        contextual_range_query_result.add(id);
                    }
                }

                for (int j = 0; j < contextual_range_query_result.size(); j++) {
                    int t_id = (int) contextual_range_query_result.get(j);

                    if (t_id == tuple_id)//always j=contextual_range_query_result.size()-1
                    {
                        if (cube.get(index).last_updated_by == tuple_id
                                && cube.get(index).last_updated_subspace == subspace
                                && cube.get(index).affected_by_prev_id == false) {
                            System.out.println("here");
                            break;
                        }
                    }

                    boolean dominated = false;
                    int i = 0;
                    //int skylineSize=cube.get(index).skyline_tuples[subspace].size();      
                    //index.print();System.out.println(t_id);           
                    for (; i < cube.get(index).skyline_tuples[subspace].size(); i++) {
                        number_of_comparison++;

                        int id = cube.get(index).skyline_tuples[subspace].get(i);

                        dom = comparison(tuples.get(t_id), tuples.get(id), subspace, false);
                        if (dom == 1) //t is dominated by t_ 
                        {
                            if (t_id == tuple_id) {
                                cuboid_mark(tuple_id, subspace, index, false);
                            }
                            dominated = true;
                            break;
                        } else if (dom == -1) {
                            cube.get(index).skyline_tuples[subspace].removeAt(i);
                            total_skyline_tuples--;
                            i--;
                        }
                    }

                    if (dominated == false) {//index.print();System.out.println(t_id+" "+skylineSize);
                        cube.get(index).skyline_tuples[subspace].add(t_id);
                        //index.print();System.out.println(t_id+" "+skylineSize);
                        total_skyline_tuples++;
                    }
                }

                //cube.get(index).skyline_tuples[subspace].addAll(tempSkyline);
                if (cube.get(index).last_updated_by != tuple_id || cube.get(index).last_updated_subspace != subspace
                        || cube.get(index).affected_by_prev_id == false) {
                    cube.get(index).last_updated_by = tuple_id;
                    cube.get(index).last_updated_subspace = subspace;
                    skyline_constraints++;

                    for (int d = 0; d < dimension_attributes; d++) {
                        if (index.indices[d] != 0) {
                            Index parent_index = new Index(index); //This can not be pre_mapped. Because, number of possible Index is number of cell. This is very huge number.
                            parent_index.indices[d] = 0;
                            queue.add(parent_index);
                        }
                    }
                    //cube.get(index).skyline_tuples[subspace].add(tuple_id);                    
                }
            }
        }
    }
}
