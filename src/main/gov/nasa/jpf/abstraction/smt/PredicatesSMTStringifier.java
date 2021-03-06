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
package gov.nasa.jpf.abstraction.smt;

import gov.nasa.jpf.abstraction.common.Add;
import gov.nasa.jpf.abstraction.common.Assign;
import gov.nasa.jpf.abstraction.common.Comparison;
import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Disjunction;
import gov.nasa.jpf.abstraction.common.Divide;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.IfThenElse;
import gov.nasa.jpf.abstraction.common.Implication;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.Modulo;
import gov.nasa.jpf.abstraction.common.Multiply;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.PredicatesStringifier;
import gov.nasa.jpf.abstraction.common.Subtract;
import gov.nasa.jpf.abstraction.common.UninterpretedShiftLeft;
import gov.nasa.jpf.abstraction.common.UninterpretedShiftRight;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
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
import gov.nasa.jpf.abstraction.common.access.meta.Arrays;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultArrays;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultField;
import gov.nasa.jpf.abstraction.common.impl.ArraysAssign;
import gov.nasa.jpf.abstraction.common.impl.FieldAssign;
import gov.nasa.jpf.abstraction.common.impl.New;
import gov.nasa.jpf.abstraction.common.impl.VariableAssign;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;

/**
 * Transforms predicates into syntax that the SMT understands (SMTLIB)
 */
public class PredicatesSMTStringifier extends PredicatesStringifier {

    @Override
    public void visit(Negation predicate) {
        ret.append("(not ");

        predicate.predicate.accept(this);

        ret.append(")");
    }

    private void inlineConjunction(Predicate predicate) {
        if (predicate.getClass().equals(Conjunction.class)) {
            Conjunction c = (Conjunction) predicate;

            if (c.a.getClass().equals(Conjunction.class) && (c.b instanceof Comparison || c.b instanceof Assign)) {
                while (c.a.getClass().equals(Conjunction.class) && (c.b instanceof Comparison || c.b instanceof Assign)) {
                    c.b.accept(this);
                    ret.append(" ");
                    c = (Conjunction) c.a;
                }
                c.accept(this);
            } else if (c.b.getClass().equals(Conjunction.class) && (c.a instanceof Comparison || c.a instanceof Assign)) {
                while (c.b.getClass().equals(Conjunction.class) && (c.a instanceof Comparison || c.a instanceof Assign)) {
                    c.a.accept(this);
                    ret.append(" ");
                    c = (Conjunction) c.b;
                }
                c.accept(this);
            } else {
                inlineConjunction(c.a);

                ret.append(" ");

                inlineConjunction(c.b);
            }
        } else {
            predicate.accept(this);
        }
    }

    @Override
    public void visit(Conjunction predicate) {
        ret.append("(and ");

        inlineConjunction(predicate.a);

        ret.append(" ");

        inlineConjunction(predicate.b);

        ret.append(")");
    }

    private void inlineDisjunction(Predicate predicate) {
        if (predicate.getClass().equals(Disjunction.class)) {
            Disjunction d = (Disjunction) predicate;

            inlineDisjunction(d.a);

            ret.append(" ");

            inlineDisjunction(d.b);
        } else {
            predicate.accept(this);
        }
    }

    @Override
    public void visit(Disjunction predicate) {
        ret.append("(or ");

        inlineDisjunction(predicate.a);

        ret.append(" ");

        inlineDisjunction(predicate.b);

        ret.append(")");
    }

    @Override
    public void visit(Implication predicate) {
        ret.append("(=> ");

        predicate.a.accept(this);

        ret.append(" ");

        predicate.b.accept(this);

        ret.append(")");
    }

    @Override
    public void visit(LessThan predicate) {
        ret.append("(< ");

        predicate.a.accept(this);

        ret.append(" ");

        predicate.b.accept(this);

        ret.append(")");
    }

    @Override
    public void visit(Equals predicate) {
        ret.append("(= ");

        if (!(predicate.a instanceof ArrayElementWrite) && predicate.b instanceof ArrayElementWrite) {
            ret.append("(select arr ");

            predicate.a.accept(this);

            ret.append(")");
        } else {
            if (predicate.a instanceof ArrayElementWrite && !(predicate.b instanceof ArrayElementWrite)) {
                ret.append("(select ");

                predicate.a.accept(this);

                ArrayElementWrite aw = (ArrayElementWrite) predicate.a;

                ret.append(" ");

                aw.getArray().accept(this);

                ret.append(")");
            } else {
                predicate.a.accept(this);
            }
        }

        ret.append(" ");

        if (predicate.a instanceof ArrayElementWrite && !(predicate.b instanceof ArrayElementWrite)) {
            ret.append("(select arr ");

            predicate.b.accept(this);

            ret.append(")");
        } else {
            if (!(predicate.a instanceof ArrayElementWrite) && predicate.b instanceof ArrayElementWrite) {
                ret.append("(select ");

                predicate.b.accept(this);

                ArrayElementWrite aw = (ArrayElementWrite) predicate.b;

                ret.append(" ");

                aw.getArray().accept(this);

                ret.append(")");
            } else {
                predicate.b.accept(this);
            }
        }

        ret.append(")");
    }

    @Override
    public void visit(VariableAssign predicate) {
        ret.append("(= ");

        predicate.variable.accept(this);

        ret.append(" ");

        predicate.expression.accept(this);

        ret.append(")");
    }

    @Override
    public void visit(FieldAssign predicate) {
        ret.append("(= ");

        predicate.field.accept(this);

        ret.append(" ");

        predicate.newField.accept(this);

        ret.append(")");
    }

    @Override
    public void visit(ArraysAssign predicate) {
        ret.append("(= ");

        predicate.arrays.accept(this);

        ret.append(" ");

        predicate.newArrays.accept(this);

        ret.append(")");
    }

    @Override
    public void visit(New predicate) {
        ret.append("(= (ref ");

        predicate.object.accept(this);

        ret.append(") ");

        ret.append(predicate.object.getReference().getReferenceNumber());

        ret.append(")");
    }

    @Override
    public void visit(Add expression) {
        ret.append("(+ ");

        expression.a.accept(this);

        ret.append(" ");

        expression.b.accept(this);

        ret.append(")");
    }

    @Override
    public void visit(Subtract expression) {
        ret.append("(- ");

        expression.a.accept(this);

        ret.append(" ");

        expression.b.accept(this);

        ret.append(")");
    }

    @Override
    public void visit(Multiply expression) {
        ret.append("(* ");

        expression.a.accept(this);

        ret.append(" ");

        expression.b.accept(this);

        ret.append(")");
    }

    @Override
    public void visit(Divide expression) {
        ret.append("(/ ");

        expression.a.accept(this);

        ret.append(" ");

        expression.b.accept(this);

        ret.append(")");
    }

    @Override
    public void visit(Modulo expression) {
        // see gov.nasa.jpf.abstraction.common.Modulo for its implementation
        visit((Subtract)expression);
    }

    @Override
    public void visit(UninterpretedShiftLeft expression) {
        ret.append("(shl ");

        expression.a.accept(this);

        ret.append(" ");

        expression.b.accept(this);

        ret.append(")");
    }

    @Override
    public void visit(UninterpretedShiftRight expression) {
        ret.append("(shr ");

        expression.a.accept(this);

        ret.append(" ");

        expression.b.accept(this);

        ret.append(")");
    }

    @Override
    public void visit(Constant expression) {
        if (expression.value.intValue() < 0) {
            if (expression.value.intValue() == Integer.MIN_VALUE) {
                throw new RuntimeException("Don't know how to represent: " + Integer.MIN_VALUE);
            } else {
                ret.append("(- ");
                ret.append(-expression.value.intValue());
                ret.append(")");
            }
        } else {
            ret.append(expression.value.intValue());
        }
    }

    @Override
    public void visit(AnonymousArray expression) {
        ret.append("fresh_");
        ret.append(expression.getReference().getReferenceNumber());
    }

    @Override
    public void visit(AnonymousObject expression) {
        ret.append("fresh_");
        ret.append(expression.getReference().getReferenceNumber());
    }

    @Override
    public void visit(Root expression) {
        ret.append("var_");
        ret.append(expression.getName());
    }

    @Override
    public void visit(Fresh expression) {
        throw new RuntimeException("unsupported term in SMT input");
    }

    @Override
    public void visit(ObjectFieldRead expression) {
        ret.append("(select ");

        expression.getField().accept(this);

        ret.append(" ");

        expression.getObject().accept(this);

        ret.append(")");
    }

    @Override
    public void visit(ObjectFieldWrite expression) {
        ret.append("(store ");

        expression.getField().accept(this);

        ret.append(" ");

        expression.getObject().accept(this);

        ret.append(" ");

        expression.getNewValue().accept(this);

        ret.append(")");
    }

    @Override
    public void visit(ArrayElementRead expression) {
        ret.append("(select ");

        ret.append("(select ");

        expression.getArrays().accept(this);

        ret.append(" ");

        expression.getArray().accept(this);

        ret.append(")");

        ret.append(" ");

        expression.getIndex().accept(this);

        ret.append(")");
    }

    private Arrays findArrayElementWriteRootArrays(ArrayElementWrite ew) {
        if (ew.getArrays() instanceof ArrayElementWrite) {
            ArrayElementWrite ew2 = (ArrayElementWrite) ew.getArrays();

            if (ew2.getArray().equals(ew.getArray())) {
                return findArrayElementWriteRootArrays(ew2);
            }
        }

        return ew.getArrays();
    }

    private void visitArrayElementWriteShortcut(Arrays arrays, AccessExpression array) {
        if (arrays instanceof ArrayElementWrite) {
            ArrayElementWrite ew = (ArrayElementWrite) arrays;

            if (ew.getArray().equals(array)) {
                ret.append("(store ");

                visitArrayElementWriteShortcut(ew.getArrays(), array);

                ret.append(" ");

                ew.getIndex().accept(this);

                ret.append(" ");

                ew.getNewValue().accept(this);

                ret.append(")");

                return;
            }
        }

        ret.append("(select ");

        arrays.accept(this);

        ret.append(" ");

        array.accept(this);

        ret.append(")");
    }

    @Override
    public void visit(ArrayElementWrite expression) {
        ret.append("(store "); // Store the updated array into arrays

        findArrayElementWriteRootArrays(expression).accept(this);

        ret.append(" ");

        expression.getArray().accept(this);

        ret.append(" ");

        visitArrayElementWriteShortcut(expression, expression.getArray());

        ret.append(")");
    }

    @Override
    public void visit(ArrayLengthRead expression) {
        ret.append("(select ");

        expression.getArrayLengths().accept(this);

        ret.append(" ");

        expression.getArray().accept(this);

        ret.append(")");
    }

    @Override
    public void visit(ArrayLengthWrite expression) {
        ret.append("(store ");

        expression.getArrayLengths().accept(this);

        ret.append(" ");

        expression.getArray().accept(this);

        ret.append(" ");

        expression.getNewValue().accept(this);

        ret.append(")");
    }

    @Override
    public void visit(IfThenElse expression) {
        ret.append("(ite ");
        expression.cond.accept(this);
        ret.append(" ");
        expression.a.accept(this);
        ret.append(" ");
        expression.b.accept(this);
        ret.append(")");
    }

    @Override
    public void visit(DefaultArrays meta) {
        ret.append(meta.getName());
    }

    @Override
    public void visit(DefaultField meta) {
        ret.append("field_" + meta.getName());
    }

    @Override
    public void visit(PackageAndClass packageAndClass) {
        ret.append("class_" + packageAndClass.getName().replace("_", "__").replace('.', '_'));
    }

    @Override
    public void visit(Select select) {
        throw new RuntimeException("unsupported term in SMT input");
    }

    @Override
    public void visit(Store store) {
        throw new RuntimeException("unsupported term in SMT input");
    }
}
