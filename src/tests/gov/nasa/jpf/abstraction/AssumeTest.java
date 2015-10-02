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

public class AssumeTest extends BaseTest {
    public static void main(String[] args) {
        int c;

        c = m1();

        assertKnownValuation("c = 0: true");

        c = m2();

        assertKnownValuation("c = 1: true");

        m3(c);
    }

    public static int m1() {
        return 0;
    }

    public static int m2() {
        return 1;
    }

    public static void m3(int c) {
        assertKnownValuation("c = 2: true");

        c = 3;

        assertKnownValuation("c = 2: false");
    }
}
