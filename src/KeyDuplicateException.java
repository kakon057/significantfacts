public class KeyDuplicateException extends Exception {

    protected KeyDuplicateException() {
	super("Key already in tree");
    }
    
    // arbitrary; every serializable class has to have one of these
    public static final long serialVersionUID = 1L;
}

