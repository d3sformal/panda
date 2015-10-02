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
package gov.nasa.jpf.abstraction.util;

public class Pair<S, T> {
    private S first;
    private T second;

    public Pair(S first, T second) {
        this.first = first;
        this.second = second;
    }

    public S getFirst() {
        return first;
    }

    public T getSecond() {
        return second;
    }

    @Override
    public int hashCode() {
        return first.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Pair) {
            Object first = ((Pair) object).first;
            Object second = ((Pair) object).second;

            return this.first.getClass() == first.getClass() && this.second.getClass() == second.getClass() && this.first.equals(first) && this.second.equals(second);
        }

        return false;
    }
}
