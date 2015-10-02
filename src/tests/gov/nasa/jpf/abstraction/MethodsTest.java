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

import static gov.nasa.jpf.abstraction.BaseTest.*;

public class MethodsTest extends BaseTest {
    public static void main(String[] args) {
        Methods m = new Methods();

        m.do1();
    }
}

class Methods {
    int a;
    int b;
    int x;
    static int C = 2;

    public Methods() {
        a = 7;
    }

    public void do1() {
        int c = 2;

        a = 1;
        b = do2(c + 1, a) + 1;

        assertConjunction("this.b >= 6: true", "this.a = 1: false", "class(gov.nasa.jpf.abstraction.Methods).C = 2: true");
    }

    public int do2(int c, int d) {
        a = -10;
        x = C;
        c++;

        return c + d;
    }
}
