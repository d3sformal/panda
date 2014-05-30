package gov.nasa.jpf.abstraction.predicate.smt;

import java.io.IOException;

/**
 * A distinguished type of exception corresponding to failure during SMT call
 */
public class SMTException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SMTException() {
    }

    public SMTException(String message) {
        super(message);
    }

    public SMTException(IOException e) {
        super(e);
    }

}
