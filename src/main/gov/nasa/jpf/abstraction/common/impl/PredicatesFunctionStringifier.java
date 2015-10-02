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
package gov.nasa.jpf.abstraction.common.impl;

import gov.nasa.jpf.abstraction.common.IfThenElse;
import gov.nasa.jpf.abstraction.common.PredicatesStringifier;
import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.ArrayElementWrite;
import gov.nasa.jpf.abstraction.common.access.ArrayLengthRead;
import gov.nasa.jpf.abstraction.common.access.ArrayLengthWrite;
import gov.nasa.jpf.abstraction.common.access.Fresh;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldWrite;
import gov.nasa.jpf.abstraction.common.access.PackageAndClass;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.impl.Select;
import gov.nasa.jpf.abstraction.common.access.impl.Store;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultArrays;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultField;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;

/**
 * Used to transform predicates into string representation in function notation
 *
 * @see gov.nasa.jpf.abstraction.common.Notation
 */
public class PredicatesFunctionStringifier extends PredicatesStringifier {

    @Override
    public void visit(Root expression) {
        ret.append(expression.getName());
    }

    @Override
    public void visit(Fresh expression) {
        ret.append(expression.getName());
    }

    @Override
    public void visit(ObjectFieldRead expression) {
        boolean isStatic = expression.getObject() instanceof PackageAndClass;

        ret.append((isStatic ? "sfread" : "fread") + "(");

        expression.getField().accept(this);

        ret.append(", ");

        expression.getObject().accept(this);

        ret.append(")");
    }

    @Override
    public void visit(ObjectFieldWrite expression) {
        boolean isStatic = expression.getObject() instanceof PackageAndClass;

        ret.append((isStatic ? "sfwrite" : "fwrite") + "(");

        ret.append(expression.getName());

        ret.append(", ");

        expression.getObject().accept(this);

        ret.append(", ");

        expression.getNewValue().accept(this);

        ret.append(")");
    }

    @Override
    public void visit(ArrayElementRead expression) {
        ret.append("aread(");

        expression.getArrays().accept(this);

        ret.append(", ");

        expression.getArray().accept(this);

        ret.append(", ");

        expression.getIndex().accept(this);

        ret.append(")");
    }

    @Override
    public void visit(ArrayElementWrite expression) {
        ret.append("awrite(");

        expression.getArrays().accept(this);

        ret.append(", ");

        expression.getArray().accept(this);

        ret.append(", ");

        expression.getIndex().accept(this);

        ret.append(", ");

        expression.getNewValue().accept(this);

        ret.append(")");
    }

    @Override
    public void visit(ArrayLengthRead expression) {
        ret.append("alength(");

        expression.getArrayLengths().accept(this);

        ret.append(", ");

        expression.getArray().accept(this);

        ret.append(")");
    }

    @Override
    public void visit(ArrayLengthWrite expression) {
        ret.append("alengthupdate(");

        expression.getArrayLengths().accept(this);

        ret.append(", ");

        expression.getArray().accept(this);

        ret.append(", ");

        expression.getNewValue().accept(this);

        ret.append(")");
    }

    @Override
    public void visit(AnonymousObject expression) {
        ret.append("object(" + expression.getReference() + ")");
    }

    @Override
    public void visit(AnonymousArray expression) {
        ret.append("array(" + expression.getReference() + ")");
    }

    @Override
    public void visit(IfThenElse expression) {
        ret.append("ite(");
        expression.cond.accept(this);
        ret.append(", ");
        expression.a.accept(this);
        ret.append(", ");
        expression.b.accept(this);
        ret.append(")");
    }

    @Override
    public void visit(DefaultArrays meta) {
        ret.append(meta.getName());
    }

    @Override
    public void visit(DefaultField meta) {
        ret.append(meta.getName());
    }

    @Override
    public void visit(Select select) {
        ret.append("select(");
        if (select.isRoot()) {
            ret.append("arr");
        } else {
            select.getFrom().accept(this);
        }
        ret.append(", ");
        select.getIndex().accept(this);
        ret.append(")");
    }

    @Override
    public void visit(Store store) {
        ret.append("store(");
        if (store.isRoot()) {
            ret.append("arr");
        } else {
            store.getTo().accept(this);
        }
        ret.append(", ");
        store.getIndex().accept(this);
        ret.append(", ");
        store.getValue().accept(this);
        ret.append(")");
    }
}
