package gov.nasa.jpf.abstraction.predicate.smt;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;

public class SMTSpecialValue extends DefaultRoot {
    private static SMTSpecialValue instance;

    private SMTSpecialValue() {
        super("value");
    }

    public static SMTSpecialValue create() {
        if (instance == null) {
            instance = new SMTSpecialValue();
        }

        return instance;
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }
}
