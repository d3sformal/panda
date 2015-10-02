/*
 * Copyright (C) 2015, Charles University in Prague.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gov.nasa.jpf.abstraction.state;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gov.nasa.jpf.abstraction.util.Pair;

/**
 * Stack keeping Predicate Valuation scopes
 *
 * method call = push
 * method return = pop
 */
public class PredicateValuationStack implements Scopes, Iterable<MethodFramePredicateValuation> {

    private List<Pair<String, MethodFramePredicateValuation>> scopes = new ArrayList<Pair<String, MethodFramePredicateValuation>>();

    @Override
    public MethodFramePredicateValuation top() {
        return top(0);
    }

    @Override
    public void pop() {
        scopes.remove(scopes.size() - 1);
    }

    @Override
    public void push(String name, Scope scope) {
        if (scope instanceof MethodFramePredicateValuation) {
            scopes.add(new Pair<String, MethodFramePredicateValuation>(name, (MethodFramePredicateValuation) scope));
        } else {
            throw new RuntimeException("Invalid scope type being pushed!");
        }
    }

    @Override
    public void replace(int i, Scope scope) {
        if (scope instanceof MethodFramePredicateValuation) {
            scopes.set(scopes.size() - i - 1, new Pair<String, MethodFramePredicateValuation>(scopes.get(scopes.size() - i - 1).getFirst(), (MethodFramePredicateValuation) scope));
        } else {
            throw new RuntimeException("Invalid scope type being replaced!");
        }
    }

    @Override
    public int count() {
        return scopes.size();
    }

    @Override
    public PredicateValuationStack clone() {
        PredicateValuationStack clone = new PredicateValuationStack();

        for (Pair<String, MethodFramePredicateValuation> scope : scopes) {
            clone.push(scope.getFirst(), scope.getSecond().clone());
        }

        return clone;
    }

    @Override
    public MethodFramePredicateValuation top(int i) {
        return scopes.get(scopes.size() - i - 1).getSecond();
    }

    @Override
    public void print() {
        for (Pair<String, MethodFramePredicateValuation> scope : scopes) {
            System.out.println(scope.getFirst());
        }
    }

    @Override
    public Iterator<MethodFramePredicateValuation> iterator() {
        final Iterator<Pair<String, MethodFramePredicateValuation>> iterator = scopes.iterator();

        return new Iterator<MethodFramePredicateValuation>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public MethodFramePredicateValuation next() {
                return iterator.next().getSecond();
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }

}
