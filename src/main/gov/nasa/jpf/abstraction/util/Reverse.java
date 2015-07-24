package gov.nasa.jpf.abstraction.util;

import java.util.Iterator;

public class Reverse<E> implements Iterable<E> {
    Reversible<E> r;

    public Reverse(Reversible<E> r) {
        this.r = r;
    }

    public Iterator<E> iterator() {
        return r.reverseIterator();
    }
}
