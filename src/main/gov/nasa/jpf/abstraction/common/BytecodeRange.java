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

public abstract class BytecodeRange implements Iterable<Integer> {
    public BytecodeRange merge(BytecodeRange r) {
        if (r instanceof BytecodeUnlimitedRange) {
            return merge((BytecodeUnlimitedRange) r);
        }

        if (r instanceof BytecodeInterval) {
            return merge((BytecodeInterval) r);
        }

        if (r instanceof BytecodeIntervals) {
            return merge((BytecodeIntervals) r);
        }

        throw new RuntimeException("Unsupported bytecode range: `" + r + "`");
    }

    public BytecodeUnlimitedRange merge(BytecodeUnlimitedRange ur) {
        return ur;
    }

    public abstract BytecodeRange merge(BytecodeInterval i);
    public abstract BytecodeRange merge(BytecodeIntervals is);

    public abstract boolean contains(int pc);
}
