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

public class DivergenceTest extends BaseTest {
    @Test
    @Config(items = {
        "+panda.refinement=true",
        "+panda.branch.adjust_concrete_values=false",
        "+panda.branch.prune_infeasible=true"
    })
    public static void test() {
        int x1 = Verifier.unknownInt();
        int x2 = Verifier.unknownInt();

        if (x1 < 0 || x2 < 0) return;
        if (x1 > 2 || x2 > 2) return;

        int d1 = 1;
        int d2 = 1;

        boolean c1 = Verifier.unknownBool();

        while (x1 > 0 && x2 > 0) {
            if (c1) {
                x1 = x1 - d1;
            } else {
                x2 = x2 - d2;
            }

            c1 = Verifier.unknownBool();
        }

        assert x1 == 0 || x2 == 0;
    }
}
