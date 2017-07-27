public class Top_Down_Cuboid extends Cuboid
{
    public int flag; // This variable is used to keep record of number of parents visited.
    public int store;// This variable is needed to keep track of whether we need to store a tuple in a cell or not even if it is in the skyline.
    public int result;// This is for keeping track of whether the tuple is in the skyline or not in this cell.    

    public Top_Down_Cuboid(short measure_attributes) {
        super(measure_attributes);
        store = 1;
        flag = 0;
        result = 1;
    }        
}
