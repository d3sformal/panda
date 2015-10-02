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

import java.util.Iterator;

public class BytecodeUnlimitedRange extends BytecodeRange {
    private static BytecodeUnlimitedRange instance = new BytecodeUnlimitedRange();

    public static BytecodeUnlimitedRange getInstance() {
        return instance;
    }

    @Override
    public BytecodeRange merge(BytecodeInterval i) {
        return i.merge(this);
    }

    @Override
    public BytecodeRange merge(BytecodeIntervals is) {
        return is.merge(this);
    }

    @Override
    public boolean contains(int pc) {
        return true;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            private int val = Integer.MIN_VALUE;

            @Override
            public boolean hasNext() {
                return true;
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

    @Override
    public String toString() {
        return "*";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BytecodeUnlimitedRange;
    }
}
