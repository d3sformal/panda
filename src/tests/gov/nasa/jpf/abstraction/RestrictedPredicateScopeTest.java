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

public class RestrictedPredicateScopeTest extends BaseTest {
    @Test
    public static void test() {
        int x = -1;
        int y = new C().m(x);

        assert y == 2;
    }
}

class C {
    public int m(int y) {
        int z;

        if (y < 0) {
            z = m(-y);
        } else {
            z = n(y);
        }

        // A LOT OF CODE

        return z;
    }

    private int n(int y) {
        return y * 2;
    }
}
