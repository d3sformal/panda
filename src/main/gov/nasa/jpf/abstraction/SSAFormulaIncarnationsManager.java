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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import gov.nasa.jpf.abstraction.common.Add;
import gov.nasa.jpf.abstraction.common.Divide;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Modulo;
import gov.nasa.jpf.abstraction.common.Multiply;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.Operation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Subtract;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.ArrayLengthRead;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.PackageAndClass;
import gov.nasa.jpf.abstraction.common.access.ReturnValue;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementWrite;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayLengthRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldWrite;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultReturnValue;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.common.access.meta.Arrays;
import gov.nasa.jpf.abstraction.common.access.meta.Field;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultArrays;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultField;
import gov.nasa.jpf.abstraction.common.impl.ArraysAssign;
import gov.nasa.jpf.abstraction.common.impl.FieldAssign;
import gov.nasa.jpf.abstraction.common.impl.VariableAssign;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;

public class SSAFormulaIncarnationsManager {
    private int frameNumber;
    private Stack<Integer> frameIncarnations;
    private Stack<HashMap<Root, Integer>> variableIncarnations;
    private HashMap<Field, Integer> fieldIncarnations;
    private int arraysIncarnations;

    public SSAFormulaIncarnationsManager() {
        this(0, new Stack<Integer>(), new Stack<HashMap<Root, Integer>>(), new HashMap<Field, Integer>(), 0);

        changeDepth(+1);
    }

    public SSAFormulaIncarnationsManager(int frameNumber, Stack<Integer> frameIncarnations, Stack<HashMap<Root, Integer>> variableIncarnations, HashMap<Field, Integer> fieldIncarnations, int arraysIncarnations) {
        this.frameNumber = frameNumber;
        this.frameIncarnations = frameIncarnations;
        this.variableIncarnations = variableIncarnations;
        this.fieldIncarnations = fieldIncarnations;
        this.arraysIncarnations = arraysIncarnations;
    }

    private int getVariableIncarnationNumber(Root expr, int depth) {
        if (!variableIncarnations.get(variableIncarnations.size() - depth - 1).containsKey(expr)) {
            return 0;
        }

        return variableIncarnations.get(variableIncarnations.size() - depth - 1).get(expr);
    }

    private void setVariableIncarnationNumber(Root expr, int depth, int n) {
        variableIncarnations.get(variableIncarnations.size() - depth - 1).put(expr, n);
    }

    private int getFieldIncarnationNumber(Field expr) {
        if (!fieldIncarnations.containsKey(expr)) {
            return 0;
        }

        return fieldIncarnations.get(expr);
    }

    private void setFieldIncarnationNumber(Field expr, int n) {
        fieldIncarnations.put(expr, n);
    }

    private int getArraysIncarnationNumber(Arrays expr) {
        return arraysIncarnations;
    }

    private void setArraysIncarnationNumber(Arrays expr, int n) {
        arraysIncarnations = n;
    }

    public void createNewSymbolIncarnation(AccessExpression expr, int depth) {
        if (expr instanceof Root) {
            setVariableIncarnationNumber((Root) expr, depth, getVariableIncarnationNumber((Root) expr, depth) + 1);
        } else if (expr instanceof ObjectFieldRead) {
            ObjectFieldRead fr = (ObjectFieldRead) expr;

            setFieldIncarnationNumber(fr.getField(), getFieldIncarnationNumber(fr.getField()) + 1);
        } else if (expr instanceof ArrayElementRead) {
            ArrayElementRead ar = (ArrayElementRead) expr;

            setArraysIncarnationNumber(ar.getArrays(), getArraysIncarnationNumber(ar.getArrays()) + 1);
        }
    }

    public Field getFieldIncarnation(Field field) {
        return DefaultField.create("ssa_" + getFieldIncarnationNumber(field) + "_" + field.getName());
    }

    public Arrays getArraysIncarnation(Arrays arrays) {
        return DefaultArrays.create("ssa_" + getArraysIncarnationNumber(arrays) + "_arr");
    }

    private int getFrame(int depth) {
        return frameIncarnations.get(frameIncarnations.size() - depth - 1);
    }

    public void changeDepth(int delta) {
        if (delta == 0) {
        } else if (delta > 0) {
            frameIncarnations.push(frameNumber++);
            variableIncarnations.push(new HashMap<Root, Integer>());
        } else if (delta < 0) {
            frameIncarnations.pop();
            variableIncarnations.pop();
        }
    }

    public AccessExpression getSymbolIncarnation(AccessExpression expr, int depth) {
        if (expr instanceof PackageAndClass) {
            return expr;
        } else if (expr instanceof AnonymousObject) {
            return expr;
        } else if (expr instanceof ReturnValue) {
            return DefaultReturnValue.create("ssa_" + getVariableIncarnationNumber((Root) expr, depth) + "_frame_" + getFrame(depth) + "_" + expr.getRoot().getName());
        } else if (expr instanceof Root) {
            return DefaultRoot.create("ssa_" + getVariableIncarnationNumber((Root) expr, depth) + "_frame_" + getFrame(depth) + "_" + expr.getRoot().getName());
        } else {
            AccessExpression prefix = getSymbolIncarnation(expr.cutTail(), depth);

            if (expr instanceof ObjectFieldRead) {
                ObjectFieldRead fr = (ObjectFieldRead) expr;

                return DefaultObjectFieldRead.create(prefix, getFieldIncarnation(fr.getField()));
            } else if (expr instanceof ArrayElementRead) {
                ArrayElementRead ar = (ArrayElementRead) expr;

                Set<AccessExpression> exprs = new HashSet<AccessExpression>();
                Map<AccessExpression, Expression> replacements = new HashMap<AccessExpression, Expression>();

                ar.getIndex().addAccessExpressionsToSet(exprs);

                for (AccessExpression e : exprs) {
                    replacements.put(e, getSymbolIncarnation(e, depth));
                }

                return DefaultArrayElementRead.create(prefix, getArraysIncarnation(ar.getArrays()), ar.getIndex().replace(replacements));
            } else if (expr instanceof ArrayLengthRead) {
                return DefaultArrayLengthRead.create(prefix);
            }
        }

        return null;
    }

    public Expression clear(Expression expr) {
        if (expr instanceof AccessExpression) {
            return clear((AccessExpression) expr);
        }
        if (expr instanceof Operation) {
            return clear((Operation) expr);
        }
        return expr;
    }

    public AccessExpression clear(AccessExpression expr) {
        if (expr instanceof Root) {
            Root r = (Root) expr;

            return DefaultRoot.create(r.getName().replaceAll("ssa_[0-9]*_frame_[0-9]*_", ""));
        }
        if (expr instanceof ObjectFieldRead) {
            ObjectFieldRead fr = (ObjectFieldRead) expr;

            return DefaultObjectFieldRead.create(clear(fr.getObject()), clear(fr.getField()));
        }
        if (expr instanceof ArrayElementRead) {
            ArrayElementRead ar = (ArrayElementRead) expr;

            return DefaultArrayElementRead.create(clear(ar.getArray()), clear(ar.getIndex()));
        }
        return expr;
    }

    public Field clear(Field f) {
        if (f instanceof DefaultField) {
            return DefaultField.create(f.getName().replaceAll("ssa_[0-9]*_", ""));
        }

        return f;
    }

    public Expression clear(Operation expr) {
        if (expr instanceof Add) {
            return Add.create(clear(expr.a), clear(expr.b));
        }
        if (expr instanceof Subtract) {
            return Subtract.create(clear(expr.a), clear(expr.b));
        }
        if (expr instanceof Multiply) {
            return Multiply.create(clear(expr.a), clear(expr.b));
        }
        if (expr instanceof Divide) {
            return Divide.create(clear(expr.a), clear(expr.b));
        }
        if (expr instanceof Modulo) {
            return Modulo.create(clear(expr.a), clear(expr.b));
        }

        return expr;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SSAFormulaIncarnationsManager clone() {
        Stack<HashMap<Root, Integer>> stackClone = new Stack<HashMap<Root, Integer>>();

        for (HashMap<Root, Integer> vars : variableIncarnations) {
            stackClone.push((HashMap<Root, Integer>) vars.clone());
        }

        return new SSAFormulaIncarnationsManager(
            frameNumber,
            (Stack<Integer>) frameIncarnations.clone(),
            stackClone,
            (HashMap<Field, Integer>) fieldIncarnations.clone(),
            arraysIncarnations
        );
    }
}
