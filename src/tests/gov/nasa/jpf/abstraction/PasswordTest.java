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

// Taken from SVCOMP (ETAPS 2014)

public class PasswordTest extends BaseTest {
    private static final int SIZE = 5;

    @Test
    @Config(items = {
        "+panda.refinement=true"
    })
    public static void test() {
        int[] password = new int[SIZE];
        int[] guess = new int[SIZE];

        boolean result = true;

        for (int i = 0; i < SIZE; ++i) {
            if (password[i] != guess[i]) {
                result = false;
            }
        }

        if (result) {
            for (int j = 0; j < SIZE; ++j) {
                assert password[j] == guess[j];
            }
        }
    }
}
