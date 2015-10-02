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

// Taken from CPAchecker repository
public class ContinueTest extends BaseTest {
    public ContinueTest() {
        config.add("+panda.refinement=true");
    }

    @Test
    public static void test() {
        int y = 0;
        int x = 0;
        int z = 0;

    label:
        while (y < 2) {
            y++;
            x = 0;

            while(x < 2) {
                x++;

                if(x > 1) {
                  continue label;
                }

                z++;
            }
        }

        System.out.println(y);

        assert z == 2; // 2 loops above, so z == 2 always true
    }
}
