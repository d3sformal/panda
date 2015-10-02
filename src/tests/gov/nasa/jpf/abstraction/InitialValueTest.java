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

public class InitialValueTest extends BaseTest {
    public InitialValueTest() {
        config.add("+panda.refinement=true");
    }

    @Test
    public static void test() {
        assert !InitialValues.z;
        assert InitialValues.b == 0;
        assert InitialValues.c == 0;
        assert InitialValues.d == 0;
        assert InitialValues.f == 0;
        assert InitialValues.i == 0;
        assert InitialValues.l == 0;
        assert InitialValues.s == 0;
        assert InitialValues.o == null; // Will be decided precisely using heap abstraction (cannot be violated, no refinement)
    }
}

class InitialValues {
    public static boolean z;
    public static byte b;
    public static char c;
    public static double d;
    public static float f;
    public static int i;
    public static long l;
    public static short s;
    public static Object o;
}
