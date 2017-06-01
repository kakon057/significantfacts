/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package baseline_i;

public class KeySizeException extends Exception {

    protected KeySizeException() {
	super("Key size mismatch");
    }
    
    // arbitrary; every serializable class has to have one of these
    public static final long serialVersionUID = 2L;
    
}