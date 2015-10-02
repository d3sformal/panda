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

class AmbA
{
    AmbB x;

    public AmbA() {
        x = new AmbB();
    }
}

class AmbB
{
    AmbC y;

    public AmbB() {
        y = new AmbC();
    }
}

class AmbC
{
    int z;
}

public class AmbiguityTest extends BaseTest
{
    static AmbA static_a = new AmbA();
    static AmbB static_b;
    static AmbC static_c = new AmbC();

    public static void main(String[] args) {
        AmbA a = new AmbA();
        AmbB b;
        AmbC c = new AmbC();

        // LOCAL / HEAP

        a.x.y.z = 0;
        c.z = 3;

        b = a.x;

        assertConjunction("b.y.z = 0: true");

        a.x.y = c;

        assertConjunction("b.y.z = 3: true");

        assertAliased("c", "b.y");

        // STATIC

        static_a.x.y.z = 0;
        static_c.z = 3;

        static_b = static_a.x;
        static_a.x.y = static_c;
    }
}
