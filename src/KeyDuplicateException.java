/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stop_down;

public class KeyDuplicateException extends Exception {

    protected KeyDuplicateException() {
	super("Key already in tree");
    }
    
    // arbitrary; every serializable class has to have one of these
    public static final long serialVersionUID = 1L;
}
