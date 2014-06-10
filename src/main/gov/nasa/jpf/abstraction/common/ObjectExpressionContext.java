package gov.nasa.jpf.abstraction.common;

import java.util.LinkedList;
import java.util.List;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.PackageAndClass;

/**
 * Corresponds to one object section in the input file
 *
 * It is targeted at a concrete method (e.g. [object pkg.subpkg.Class])
 *
 * [object ...]
 * b
 * a
 * ...
 *
 * <<< SOME OTHER SECTION OR EOF (End of File)
 *
 * @see gov.nasa.jpf.abstraction.predicate.grammar (grammar file Predicates.g4)
 */
public class ObjectExpressionContext extends ExpressionContext {

    private PackageAndClass packageAndClass;

    public ObjectExpressionContext(PackageAndClass packageAndClass, List<Expression> expressions) {
        super(expressions);

        this.packageAndClass = packageAndClass;
    }

    public PackageAndClass getPackageAndClass() {
        return packageAndClass;
    }

    @Override
    public ObjectPredicateContext getPredicateContext() {
        return new ObjectPredicateContext(packageAndClass, new LinkedList<Predicate>());
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }
}
