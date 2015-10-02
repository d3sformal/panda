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

import gov.nasa.jpf.annotation.MJI;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.NativePeer;

public class JPF_gov_nasa_jpf_abstraction_statematch_StateMatchingTest extends NativePeer {
    @MJI
    public void assertVisitedAtMost__I__V(MJIEnv env, int clsObjRef, int times) {
        /** Should never be reached */
        throw new RuntimeException("Should never be reached!");
    }

    @MJI
    public void assertRevisitedAtLeast__I__V(MJIEnv env, int clsObjRef, int times) {
        /** Should never be reached */
        throw new RuntimeException("Should never be reached!");
    }

    @MJI
    public void assertSameValuationOnEveryVisit___3Ljava_lang_String_2__V(MJIEnv env, int clsObjRef, int rString0) {
        /** Should never be reached */
        throw new RuntimeException("Should never be reached!");
    }

    @MJI
    public void assertDifferentValuationOnEveryVisit___3Ljava_lang_String_2__V(MJIEnv env, int clsObjRef, int rString0) {
        /** Should never be reached */
        throw new RuntimeException("Should never be reached!");
    }

    @MJI
    public void assertVisitedAtMostWithValuation__I_3Ljava_lang_String_2__V(MJIEnv env, int clsObjRef, int times, int rString1) {
        /** Should never be reached */
        throw new RuntimeException("Should never be reached!");
    }

    @MJI
    public void assertRevisitedAtLeastWithValuation__I_3Ljava_lang_String_2__V(MJIEnv env, int clsObjRef, int times, int rString1) {
        /** Should never be reached */
        throw new RuntimeException("Should never be reached!");
    }

    @MJI
    public void assertSameAliasingOnEveryVisit___3Ljava_lang_String_2__V(MJIEnv env, int clsObjRef, int rString0) {
        /** Should never be reached */
        throw new RuntimeException("Should never be reached!");
    }
}
