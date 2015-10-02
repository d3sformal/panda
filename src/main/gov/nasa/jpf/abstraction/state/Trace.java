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

import java.util.Stack;

/**
 * Overall history of states visited on a particular path.
 */
public class Trace {
    private Stack<State> states = new Stack<State>();

    public State top() {
        return top(0);
    }

    public State top(int i) {
        return states.get(states.size() - i - 1);
    }

    public State get(int i) {
        return states.get(i);
    }

    public void pop() {
        states.pop();
    }

    public void push(State state) {
        states.push(state);
    }

    public int size() {
        return states.size();
    }

    public boolean isEmpty() {
        return states.isEmpty();
    }

    @Override
    public Trace clone() {
        Trace clone = new Trace();

        for (State s : states) {
            clone.push(s.clone());
        }

        return clone;
    }
}
