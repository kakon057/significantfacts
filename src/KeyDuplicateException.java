/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package baseline_i;
 /**
  * KeyDuplicateException is thrown when the <TT>KDTree.insert</TT> method
  * is invoked on a key already in the KDTree.
  *
  * @author      Simon Levy
  * @version     %I%, %G%
  * @since JDK1.2 
  */

public class KeyDuplicateException extends Exception {

    protected KeyDuplicateException() {
	super("Key already in tree");
    }
    
    // arbitrary; every serializable class has to have one of these
    public static final long serialVersionUID = 1L;
}

