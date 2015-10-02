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
package gov.nasa.jpf.abstraction;

import static gov.nasa.jpf.abstraction.Verifier.unknownInt;

// Taken from SVCOMP

public class StringCopyTest extends BaseTest {
    private static final int N = 5;

    @Test
    @Config(items = {
        "+panda.refinement=true",
        "+panda.storage.class=gov.nasa.jpf.abstraction.util.DebugCopyPreservingStateSet",
        "+vm.serializer.class=gov.nasa.jpf.abstraction.util.DebugPredicateAbstractionSerializer"
    })
    public static void test() {
        int[] src = new int[N];
        int[] dst = new int[N];

        for (int j = 0; j < N; ++j) {
            src[j] = unknownInt(); // PROBLEM: gets matched when 0 < j < N
        }

        int i = 0;

        while (src[i] != 0
            && i < src.length // Added to exclude REAL bug in the benchmark (depends on initialization of the arrays)
            ) {
            dst[i] = src[i];
            i = i + 1;
        }

        System.out.println(i);

        for (int j = 0; j < i; ++j) {
            assert dst[j] == src[j];
        }
    }
}
