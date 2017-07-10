/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stop_down;

public class STop_Down_Cuboid extends Cuboid
{
    public int flag; // This variable is used to keep record of number of parents visited.
    public int store;// This variable is needed to keep track of whether we need to store a tuple in a cell or not even if it is in the skyline.
    public int[] result;// This is for keeping track of whether the tuple is in the skyline or not in this cell.
   // public long overhead_Revised_Top_Down_Cuboid;
       
    public STop_Down_Cuboid(short measure_attributes) {
        super(measure_attributes);
        store = 1;
        flag = 0;
        //long time1=System.currentTimeMillis();
        short total_measure_subspace = (short) Math.pow(2, measure_attributes);
        result = new int[total_measure_subspace];        
       /* for (int i = 0; i < total_measure_subspace; i++) {
            result[i] = 0;
        }  */
        //overhead_Revised_Top_Down_Cuboid=System.currentTimeMillis()-time1;
    }        
}
