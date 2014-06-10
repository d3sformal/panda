package gov.nasa.jpf.abstraction.predicate.state;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gov.nasa.jpf.abstraction.util.Pair;

/**
 * Stack keeping Symbol Table scopes
 *
 * method call = push
 * method return = pop
 */
public class SymbolTableStack implements Scopes, Iterable<MethodFrameSymbolTable> {

    private List<Pair<String, MethodFrameSymbolTable>> scopes = new ArrayList<Pair<String, MethodFrameSymbolTable>>();

    @Override
    public MethodFrameSymbolTable top() {
        return top(0);
    }

    @Override
    public void pop() {
        scopes.remove(scopes.size() - 1);
    }

    @Override
    public void push(String name, Scope scope) {
        if (scope instanceof MethodFrameSymbolTable) {
            scopes.add(new Pair<String, MethodFrameSymbolTable>(name, (MethodFrameSymbolTable) scope));
        } else {
            throw new RuntimeException("Invalid scope type being pushed!");
        }
    }

    @Override
    public int count() {
        return scopes.size();
    }

    @Override
    public SymbolTableStack clone() {
        SymbolTableStack clone = new SymbolTableStack();

        for (Pair<String, MethodFrameSymbolTable> scope : scopes) {
            clone.push(scope.getFirst(), scope.getSecond().clone());
        }

        return clone;
    }

    @Override
    public MethodFrameSymbolTable top(int i) {
        return scopes.get(scopes.size() - i - 1).getSecond();
    }

    @Override
    public void print() {
        for (Pair<String, MethodFrameSymbolTable> scope : scopes) {
            System.out.println(scope.getFirst());
        }
    }

    @Override
    public Iterator<MethodFrameSymbolTable> iterator() {
        final Iterator<Pair<String, MethodFrameSymbolTable>> iterator = scopes.iterator();

        return new Iterator<MethodFrameSymbolTable>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public MethodFrameSymbolTable next() {
                return iterator.next().getSecond();
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }

}
