package gov.nasa.jpf.abstraction.common;

public class PredicateNotCloneableException extends RuntimeException {
    public final static long serialVersionUID = 1L;

    public PredicateNotCloneableException(String msg) {
        super(msg);
    }
}
