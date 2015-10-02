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

class NullTestDriver {
    public static void main(String[] args) {
        new NullTest().bootstrap();
    }
}

public class NullTest extends BaseTest {
    @Test
    public static void test1() {
        Object[] array = new Object[2];
        int i = 0;
        boolean b = true;

        array[0] = new Object();
        assertNumberOfPossibleValues("array[0]", 1);
        assert array[0] != null : "Cannot guarantee array[0] to be not null"; // Should not fail - array[0] is definitely not null
    }

    @FailingTest
    public static void test2() {
        Object[] array = new Object[2];
        int i = 0;
        boolean b = true;

        array[0] = new Object();
        assertNumberOfPossibleValues("array[0]", 1);
        array[i] = new Object();
        assertNumberOfPossibleValues("array[1]", 2);
        assert array[1] != null : "Cannot guarantee array[1] to be not null"; // Should fail - cannot know what element was overwritten, array[1] still can be null
    }

    @FailingTest
    public static void test3() {
        Object[] array = new Object[2];
        int i = 0;
        boolean b = true;

        array[0] = new Object();
        assertNumberOfPossibleValues("array[0]", 1);
        assertConjunction("array[0] = null: false"); // Should not be provable - missing predicates
    }
}
