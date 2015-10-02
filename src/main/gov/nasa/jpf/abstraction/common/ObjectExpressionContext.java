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
package gov.nasa.jpf.abstraction.common;

import java.util.LinkedList;
import java.util.List;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.PackageAndClass;

/**
 * Corresponds to one object section in the input file
 *
 * It is targeted at a concrete method (e.g. [object pkg.subpkg.Class])
 *
 * [object ...]
 * b
 * a
 * ...
 *
 * <<< SOME OTHER SECTION OR EOF (End of File)
 *
 * @see gov.nasa.jpf.abstraction.grammar (grammar file Predicates.g4)
 */
public class ObjectExpressionContext extends ExpressionContext {

    private PackageAndClass packageAndClass;

    public ObjectExpressionContext(PackageAndClass packageAndClass, List<Expression> expressions) {
        super(expressions);

        this.packageAndClass = packageAndClass;
    }

    public PackageAndClass getPackageAndClass() {
        return packageAndClass;
    }

    @Override
    public ObjectPredicateContext getPredicateContextOfProperType() {
        return new ObjectPredicateContext(packageAndClass, new LinkedList<Predicate>());
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }
}
