package gov.nasa.jpf.abstraction.predicate.state.universe;

import gov.nasa.jpf.abstraction.common.access.PackageAndClass;

public class LoadedClass extends StructuredValueSlot implements Identifier {
    private PackageAndClass accessExpression;

    public LoadedClass(PackageAndClass accessExpression) {
        this.accessExpression = accessExpression;
    }

    public PackageAndClass getAccessExpression() {
        return accessExpression;
    }
}
