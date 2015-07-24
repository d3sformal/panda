package gov.nasa.jpf.abstraction.util;

import java.util.Iterator;

public interface Reversible<E> {
    public Iterator<E> reverseIterator();
}
