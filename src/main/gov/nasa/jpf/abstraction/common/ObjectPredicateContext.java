package gov.nasa.jpf.abstraction.common;

import java.util.List;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.PackageAndClass;

/**
 * Corresponds to one object section in the input file
 *
 * It is targeted at a concrete method (e.g. [object pkg.subpkg.Class])
 *
 * [object ...]
 * b = a - 1
 * a * b = 6
 * ...
 *
 * <<< SOME OTHER SECTION OR EOF (End of File)
 *
 * @see gov.nasa.jpf.abstraction.grammar (grammar file Predicates.g4)
 */
public class ObjectPredicateContext extends PredicateContext {

    private PackageAndClass packageAndClass;

    public ObjectPredicateContext(PackageAndClass packageAndClass, List<Predicate> predicates) {
        super(predicates);

        this.packageAndClass = packageAndClass;
    }

    public PackageAndClass getPackageAndClass() {
        return packageAndClass;
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }
}
