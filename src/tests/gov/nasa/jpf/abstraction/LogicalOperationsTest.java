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

public class LogicalOperationsTest extends BaseTest {
    @FailingTest
    public static void testIAND1() {
        int o = 1;
        int t = 2;
        int error = t & o;
    }

    @Test
    public static void testIAND2() {
        int z = 0;
        int o = 1;
        int zz = (z & z);
        int zo = (z & o);
        int oz = (o & z);
        int oo = (o & o);
    }

    @FailingTest
    public static void testIOR1() {
        int z = 0;
        int t = 2;
        int error = (t | z);
    }

    @Test
    public static void testIOR2() {
        int z = 0;
        int o = 1;
        int zz = (z | z);
        int zo = (z | o);
        int oz = (o | z);
        int oo = (o | o);
    }

    @FailingTest
    public static void testIXOR1() {
        int z = 0;
        int t = 2;
        int error = (t ^ z);
    }

    @Test
    public static void testIXOR2() {
        int z = 0;
        int o = 1;
        int zz = (z ^ z);
        int zo = (z ^ o);
        int oz = (o ^ z);
        int oo = (o ^ o);
    }

    @FailingTest
    public static void testLAND1() {
        long o = 1;
        long t = 2;
        long error = t & o;
    }

    @Test
    public static void testLAND2() {
        long z = 0;
        long o = 1;
        long zz = (z & z);
        long zo = (z & o);
        long oz = (o & z);
        long oo = (o & o);
    }

    @FailingTest
    public static void testLOR1() {
        long z = 0;
        long t = 2;
        long error = (t | z);
    }

    @Test
    public static void testLOR2() {
        long z = 0;
        long o = 1;
        long zz = (z | z);
        long zo = (z | o);
        long oz = (o | z);
        long oo = (o | o);
    }

    @FailingTest
    public static void testLXOR1() {
        long z = 0;
        long t = 2;
        long error = (t ^ z);
    }

    @Test
    public static void testLXOR2() {
        long z = 0;
        long o = 1;
        long zz = (z ^ z);
        long zo = (z ^ o);
        long oz = (o ^ z);
        long oo = (o ^ o);
    }
}
