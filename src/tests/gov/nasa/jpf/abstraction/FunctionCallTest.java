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

// Taken from CPAchecker repository
public class FunctionCallTest extends BaseTest {
    public FunctionCallTest() {
        config.add("+panda.refinement=true");
    }

    public static void main(String[] args) {
        int n1 = 1;
        int n2 = 1;
        int n3 = 2;

        if (n1 == n2) {
            if (n1 != n3) {
                n3 = 1;
                des();
            }

            if (n1 == n3) {
                des();
                n1 = n1 + n2 + n3; // n1 = 3
            } else {
                assert false; // not reached
            }

            if (n1 == n1 + n2) {
                assert false; // not reached
            } else if (n1 == 2 * n2 + n3) {
                assert n3 == n2; // always true
            }
        }

        des();
    }

    public static void des() {
        int n1 = 1;
        int n2 = 2;

        if (n1 == n2) {
            assert (false); // not reached
        }
    }
}
