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
package gov.nasa.jpf.abstraction.common;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BytecodeInterval extends BytecodeRange {
    private int min;
    private int max;

    public BytecodeInterval(int a, int b) {
        if (a >= b) {
            int c = b;
            b = a;
            a = c;
        }

        this.min = a;
        this.max = b;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    @Override
    public BytecodeRange merge(BytecodeInterval i) {
        if (overlaps(i)) {
            return new BytecodeInterval(min < i.min ? min : i.min, max > i.max ? max : i.max);
        }

        Set<BytecodeInterval> intervals = new HashSet<BytecodeInterval>();

        intervals.add(this);
        intervals.add(i);

        return new BytecodeIntervals(intervals);
    }

    @Override
    public BytecodeRange merge(BytecodeIntervals is) {
        return is.merge(this);
    }

    public boolean overlaps(BytecodeInterval i) {
        return min <= i.min && i.min <= max || min <= i.max && i.max <= max || i.contains(this);
    }

    public boolean contains(BytecodeInterval i) {
        return min <= i.min && i.max <= max;
    }

    @Override
    public boolean contains(int i) {
        return min <= i && i <= max;
    }

    @Override
    public String toString() {
        return min + ".." + max;
    }

    @Override
    public int hashCode() {
        return min;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BytecodeInterval) {
            BytecodeInterval i = (BytecodeInterval) o;

            return min == i.min && max == i.max;
        }

        return false;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            private int val = BytecodeInterval.this.min;

            @Override
            public boolean hasNext() {
                return val <= BytecodeInterval.this.max;
            }

            @Override
            public Integer next() {
                return val++;
            }

            @Override
            public void remove() {
                throw new RuntimeException("Removal not permitted");
            }
        };
    }
}
