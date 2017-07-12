import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class FactMonitoring {
    
    public static short dimension_attributes; //number of dimension we are dealing with
    public static short measure_attributes; //number of measures we are dealing with
    public static short measure_skip_level; // If measure_skip_level = k, we will skip all the measure subspaces having <= k zero. measure_skip_level = -1 skips nothing.
    public static byte[] measure_subspace_skip_bit; // Not_Skipped = 1, Skipped = 0;
    public static short dimension_skip_level; // If dimension_skip_level = k, we will skip all the dimension subspaces having <= k zero. dimension_skip_level = -1 skips nothing.
    public static int number_of_tuples; //number of tuples we are dealing with
    public static ArrayList<Tuple> tuples; // tuples collection
    public static byte[] total_measure_attributes_compared, total_dimension_attributes_compared; //for generalization purpose
    public static byte[] measure_attributes_compared, dimension_attributes_compared;

    //*** *****PERFORMANCE MEASURES******
    public static int skyline_constraints;
    public static long start_time, single_tuple_time, cumulative_time;
    public static long number_of_comparison, number_of_traversal;
    //*** *****PERFORMANCE MEASURES******

    public static KDTree tree;
    
    public static void init() {
        /* if (args.length < 5) {
            System.out.println("Please provide command line arguments.\n\n1. dimension_attributes\n2. measure_attributes\n3. number_of_tuples\n4. dimension_skip_level\n5. measure_skip_level");
            System.exit(0);
	}*/
        dimension_attributes = 3;//Short.parseShort(args[0]);
        measure_attributes = 7;//Short.parseShort(args[1]);
        number_of_tuples = 15001;//Integer.parseInt(args[2]);
        dimension_skip_level = -1;//Short.parseShort(args[3]);
        measure_skip_level = 3;//Short.parseShort(args[4]);

        tree = new KDTree(measure_attributes);
        
        if (dimension_attributes == 3) {
            total_dimension_attributes_compared = new byte[]{1, 1, 1, 0, 0};
        }
        
        if (measure_attributes == 7) {
            total_measure_attributes_compared = new byte[]{1, 1, 1, 1, 1, -1, -1};
        }
        
        dimension_attributes_compared = new byte[dimension_attributes];
        for (int i = 0, j = 0; i < total_dimension_attributes_compared.length; i++) { //Getting Dimensions to be compared
            if (total_dimension_attributes_compared[i] != 0) {
                dimension_attributes_compared[j++] = total_dimension_attributes_compared[i];
            }
        }
        
        measure_attributes_compared = new byte[measure_attributes];
        for (int i = 0, j = 0; i < total_measure_attributes_compared.length; i++) { //Getting Measures to be compared
            if (total_measure_attributes_compared[i] != 0) {
                measure_attributes_compared[j++] = total_measure_attributes_compared[i];
            }
        }
        
        short total_measure_subspace = (short) Math.pow(2, measure_attributes);
        measure_subspace_skip_bit = new byte[total_measure_subspace];
        
        for (short i = 1; i < total_measure_subspace; i++) { //Setting skip beat of measure attributes
            if (count_Num_Of_Zeros(i) <= measure_skip_level) {
                measure_subspace_skip_bit[i] = 0;
            } else {
                measure_subspace_skip_bit[i] = 1;
            }
        }
    }
    
    public static void load_Data() throws FileNotFoundException, IOException {
        //File data_file = new File("C:\\Users\\kakon\\Documents\\NetBeansProjects\\Bottom_Up\\src\\bottom_up\\k3.txt");

        /*File data_file = new File("../../data/sorted_latest.csv");*/
//        File data_file = new File("../../data/test.csv");
        String line, cell;
        int counter = 0;
        tuples = new ArrayList<Tuple>();
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
        
        for (int gameID = 0; gameID < 25520; gameID++) {
            File data_file = new File("C:\\Users\\kakon\\Documents\\data\\factmonitoring\\" + gameID + ".csv");
            BufferedReader reader = new BufferedReader(new FileReader(data_file));
            reader.readLine(); //eating header line   
            HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
            
            while ((line = reader.readLine()) != null) {
                
                tokenizer = new StringTokenizer(line, ",");
                int current_id = Integer.parseInt(tokenizer.nextToken());
                
                map.put(current_id, counter);
                int p_id = Integer.parseInt(tokenizer.nextToken());
                
                if (p_id == 0) {
                    p_id = -1;
                } else {
                    p_id = map.get(p_id);
                }
                
                //System.out.println(counter + " " + p_id);
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
                
                tuples.add(new Tuple(counter++, temp_measure_values, temp_index, p_id, gameID));
                
                if (counter >= number_of_tuples) {
                    break;
                }
            }
            if (counter >= number_of_tuples) {
                break;
            }
        }
        
        //System.out.println(counter);
    }
    
    public static short count_Num_Of_Zeros(short number) {
        short num_of_zeros = 0;
        for (short i = 0; i < measure_attributes; i++) {
            if ((number & (short) Math.pow(2, i)) == 0) {
                num_of_zeros++;
            }
        }
        return num_of_zeros;
    }
    
    public static byte comparison(Tuple t, Tuple t_, short subspace, boolean isConsecutive) {
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
                    if (isConsecutive) {
                        return (byte) m;
                    }
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
        
        if (t__dom_t > 0 && t_dom_t_ == 0) // t is dominated by t_
        {
            t_.test_result = 1;
            return 1;
        }
        t_.test_result = -1;
        return -1; // t_ is dominated by t
    }
}
