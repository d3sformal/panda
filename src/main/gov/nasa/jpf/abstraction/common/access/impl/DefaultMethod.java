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
package gov.nasa.jpf.abstraction.common.access.impl;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.Method;
import gov.nasa.jpf.abstraction.common.access.PackageAndClass;

/**
 * A grammar element used to specify a target of a [method ...] context
 *
 * @see gov.nasa.jpf.abstraction.common.MethodContext
 */
public class DefaultMethod implements Method {

    private PackageAndClass packageAndClass;
    private String name;

    protected DefaultMethod(PackageAndClass packageAndClass, String name) {
        this.packageAndClass = packageAndClass;
        this.name = name;
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public PackageAndClass getPackageAndClass() {
        return packageAndClass;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return packageAndClass.toString() + "." + getName();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DefaultMethod) {
            DefaultMethod m = (DefaultMethod) o;

            return packageAndClass.equals(m.packageAndClass) && name.equals(m.name);
        }

        return false;
    }

    public static DefaultMethod create(PackageAndClass packageAndClass, String name) {
        if (packageAndClass == null || name == null) {
            return null;
        }

        return new DefaultMethod(packageAndClass, name);
    }

}
