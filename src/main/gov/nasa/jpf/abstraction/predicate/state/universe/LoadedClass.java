package gov.nasa.jpf.abstraction.predicate.state.universe;

import gov.nasa.jpf.abstraction.common.access.PackageAndClass;

public class LoadedClass extends StructuredValueSlot implements Identifier {
    public static class SlotKey implements UniverseSlotKey {
    }

    private PackageAndClass accessExpression;
    public static SlotKey slotKey = new SlotKey();

    public LoadedClass(PackageAndClass accessExpression) {
        this.accessExpression = accessExpression;

        setParent(this);
        setSlotKey(slotKey);
    }

    @Override
    public LoadedClass createShallowCopy() {
        LoadedClass copy = new LoadedClass(getAccessExpression());

        for (StructuredValueIdentifier value : getPossibleStructuredValues()) {
            copy.addPossibleStructuredValue(value);
        }

        return copy;
    }

    public PackageAndClass getAccessExpression() {
        return accessExpression;
    }
}
