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

// An example of a test driver replacing Junit automatic invocation
// Completely optional
class AssertTestDriver {
    public static void main(String[] args) {
        new AssertTest().bootstrap();
    }
}

public class AssertTest extends BaseTest {
    @Test
    public static void test1() {
        int x = 3;
        int y = 5;

        assertConjunction("x = 3: true");

        x = 4;

        assertDisjunction("x = 4: unknown", "0 = 0: false");

        assertAliased("a.b.c.d", "e.f.g"); // Exactly the same sets ...
        assertNotAliased("x", "y"); // Completely different sets of looked up values

        assertNumberOfPossibleValues("a.b.c.d[10].e", 0);
        assertNumberOfPossibleValues("x", 1);

        //...
    }
}
