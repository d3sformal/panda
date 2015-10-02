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
package gov.nasa.jpf.abstraction.state.universe;

import gov.nasa.jpf.vm.StaticElementInfo;

public class ClassName implements StructuredValueIdentifier {
    private StaticElementInfo staticElementInfo;

    public ClassName(StaticElementInfo staticElementInfo) {
        this.staticElementInfo = staticElementInfo;
    }

    public String getClassName() {
        return staticElementInfo.getClassInfo().getName();
    }

    public StaticElementInfo getStaticElementInfo() {
        return staticElementInfo;
    }

    @Override
    public int hashCode() {
        return getClassName().hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ClassName) {
            return getClassName().equals(((ClassName) object).getClassName());
        }

        return false;
    }

    @Override
    public String toString() {
        return getClassName();
    }

    @Override
    public int compareTo(Identifier id) {
        if (id instanceof ClassName) {
            ClassName cln = (ClassName) id;

            return getStaticElementInfo().getClassInfo().getName().compareTo(cln.getStaticElementInfo().getClassInfo().getName());
        }

        return Identifier.Ordering.compare(this, id);
    }
}
