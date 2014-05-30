package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.access.PackageAndClass;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;

import java.util.List;

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
 * @see gov.nasa.jpf.abstraction.predicate.grammar (grammar file Predicates.g4)
 */
public class ObjectContext extends Context {

    private PackageAndClass packageAndClass;

    public ObjectContext(PackageAndClass packageAndClass, List<Predicate> predicates) {
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
