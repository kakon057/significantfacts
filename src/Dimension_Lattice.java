public class Dimension_Lattice {

    public int[] lattice;
    public int dimension_attributes;
    public static long number_of_traversal;

    public Dimension_Lattice(int dimension_attributes) {
        this.dimension_attributes = dimension_attributes;
        lattice = new int[(int) Math.pow(2, dimension_attributes)];

        for (int i = 0; i < lattice.length; i++) {
            lattice[i] = 1;
        }
    }

    public void clear() {
        for (int i = 0; i < lattice.length; i++) {
            lattice[i] = 1;
        }
    }

    public int find_Bottom(Tuple t, Tuple t_) {
        int child = 0;
        for (int i = dimension_attributes - 1, j; i >= 0; i--) {
            if (t.dimension_index.indices[i] == t_.dimension_index.indices[i]) {
                j = 1;
            } else {
                j = 0;
            }
            child += (j * (int) Math.pow(2, i));
        }
        return child;
    }

    public void remove_Ancestors(int child) {
        lattice[child] = 0;
        int bit_check;
        int ancestor;
        for (int i = 1; i <= child; i *= 2) {
            bit_check = child & i;
            if (bit_check != 0) {
                ancestor = child & ((int) Math.pow(2, dimension_attributes) - 1 - i);
                if (lattice[ancestor] != 0) {
                    remove_Ancestors(ancestor);
                }
            }
        }
    }

    public short count_Num_Of_Zeros(short number) {
        short num_of_zeros = 0;
        for (short i = 0; i < dimension_attributes; i++) {
            if ((number & (short) Math.pow(2, i)) == 0) {
                num_of_zeros++;
            }
        }
        return num_of_zeros;
    }

    public int skyline_Constraints(short dimension_skip_level) {
        int skyline_constraints = 0;
        for (int i = 0; i < lattice.length; i++) {
            if (lattice[i] == 1 && count_Num_Of_Zeros((short) i) > dimension_skip_level) {
                skyline_constraints++;
            }
        }
        return skyline_constraints;
    }
}
