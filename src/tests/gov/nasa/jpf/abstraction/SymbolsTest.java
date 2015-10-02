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

class SymbA {
    SymbB b;
}

class SymbB {
    int i;
}

public class SymbolsTest extends BaseTest {

    public static void main(String[] args) {
        SymbA k[] = new SymbA[2];
        SymbA l[] = new SymbA[1];
        SymbA m = new SymbA();
        SymbA n = new SymbA();

        k[0] = new SymbA();
        k[1] = new SymbA();

        k[0].b = new SymbB();
        k[0].b.i = 1;

        k[1].b = new SymbB();
        k[1].b.i = 2;

        assertNumberOfPossibleValues("k[0]", 1);
        assertNumberOfPossibleValues("k[0].b", 1);

        m.b = new SymbB();
        m.b.i = 3;

        k[0].b = m.b;

        assertConjunction("k[0].b.i = 3: true");

        assertNumberOfPossibleValues("k[0].b", 1);

        m = n;
        k = new SymbA[10];

        k = l;
    }

}
