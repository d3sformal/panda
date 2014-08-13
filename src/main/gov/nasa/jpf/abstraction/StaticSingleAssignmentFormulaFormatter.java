package gov.nasa.jpf.abstraction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.Predicate;
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

public class StaticSingleAssignmentFormulaFormatter {
    private Stack<HashMap<Root, Integer>> variableIncarnations;
    private HashMap<Field, Integer> fieldIncarnations;
    private int arraysIncarnations;

    public StaticSingleAssignmentFormulaFormatter() {
        this(new HashMap<Root, Integer>(), new HashMap<Field, Integer>(), 0);
    }

    public StaticSingleAssignmentFormulaFormatter(Stack<HashMap<Root, Integer>> variableIncarnations, HashMap<Field, Integer> fieldIncarnations, int arraysIncarnations) {
        this.variableIncarnations = variableIncarnations;
        this.fieldIncarnations = fieldIncarnations;
        this.arraysIncarnations = arraysIncarnations;
    }

    public StaticSingleAssignmentFormulaFormatter(HashMap<Root, Integer> variableIncarnations, HashMap<Field, Integer> fieldIncarnations, int arraysIncarnations) {
        this.variableIncarnations = new Stack<HashMap<Root, Integer>>();

        this.variableIncarnations.push(variableIncarnations);

        this.fieldIncarnations = fieldIncarnations;
        this.arraysIncarnations = arraysIncarnations;
    }

    private int getIncarnationNumber(Root expr, int depth) {
        if (!variableIncarnations.get(variableIncarnations.size() - depth - 1).containsKey(expr)) {
            return 0;
        }

        return variableIncarnations.get(variableIncarnations.size() - depth - 1).get(expr);
    }

    private void setIncarnationNumber(Root expr, int depth, int n) {
        variableIncarnations.get(variableIncarnations.size() - depth - 1).put(expr, n);
    }

    private int getIncarnationNumber(Field expr) {
        if (!fieldIncarnations.containsKey(expr)) {
            return 0;
        }

        return fieldIncarnations.get(expr);
    }

    private void setIncarnationNumber(Field expr, int n) {
        fieldIncarnations.put(expr, n);
    }

    private int getIncarnationNumber(Arrays expr) {
        return arraysIncarnations;
    }

    private void setIncarnationNumber(Arrays expr, int n) {
        arraysIncarnations = n;
    }

    public void reincarnateSymbol(AccessExpression expr, int depth) {
        if (expr instanceof Root) {
            setIncarnationNumber((Root) expr, depth, getIncarnationNumber((Root) expr, depth) + 1);
        } else if (expr instanceof ObjectFieldRead) {
            ObjectFieldRead fr = (ObjectFieldRead) expr;

            setIncarnationNumber(fr.getField(), getIncarnationNumber(fr.getField()) + 1);
        } else if (expr instanceof ArrayElementRead) {
            ArrayElementRead ar = (ArrayElementRead) expr;

            setIncarnationNumber(ar.getArrays(), getIncarnationNumber(ar.getArrays()) + 1);
        }
    }

    public Field incarnateSymbol(Field field) {
        return DefaultField.create("ssa_" + getIncarnationNumber(field) + "_" + field.getName());
    }

    public Arrays incarnateSymbol(Arrays arrays) {
        return DefaultArrays.create("ssa_" + getIncarnationNumber(arrays) + "_arr");
    }

    private int getDepth() {
        return variableIncarnations.size() - 1;
    }

    public void changeDepth(int delta) {
        if (delta == 0) {
        } else if (delta > 0) {
            variableIncarnations.push(new HashMap<Root, Integer>());
        } else if (delta < 0) {
            variableIncarnations.pop();
        }
    }

    public AccessExpression incarnateSymbol(AccessExpression expr, int depth) {
        if (expr instanceof PackageAndClass) {
            return expr;
        } else if (expr instanceof AnonymousObject) {
            return expr;
        } else if (expr instanceof ReturnValue) {
            return DefaultReturnValue.create("ssa_" + getIncarnationNumber((Root) expr, depth) + "_depth_" + (getDepth() - depth) + "_" + expr.getRoot().getName());
        } else if (expr instanceof Root) {
            return DefaultRoot.create("ssa_" + getIncarnationNumber((Root) expr, depth) + "_depth_" + (getDepth() - depth) + "_" + expr.getRoot().getName());
        } else {
            AccessExpression prefix = incarnateSymbol(expr.cutTail(), depth);

            if (expr instanceof ObjectFieldRead) {
                ObjectFieldRead fr = (ObjectFieldRead) expr;

                return DefaultObjectFieldRead.create(prefix, incarnateSymbol(fr.getField()));
            } else if (expr instanceof ArrayElementRead) {
                ArrayElementRead ar = (ArrayElementRead) expr;

                Set<AccessExpression> exprs = new HashSet<AccessExpression>();
                Map<AccessExpression, Expression> replacements = new HashMap<AccessExpression, Expression>();

                ar.getIndex().addAccessExpressionsToSet(exprs);

                for (AccessExpression e : exprs) {
                    replacements.put(e, incarnateSymbol(e, depth));
                }

                return DefaultArrayElementRead.create(prefix, incarnateSymbol(ar.getArrays()), ar.getIndex().replace(replacements));
            } else if (expr instanceof ArrayLengthRead) {
                return DefaultArrayLengthRead.create(prefix);
            }
        }

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public StaticSingleAssignmentFormulaFormatter clone() {
        Stack<HashMap<Root, Integer>> stackClone = new Stack<HashMap<Root, Integer>>();

        for (HashMap<Root, Integer> vars : variableIncarnations) {
            stackClone.push((HashMap<Root, Integer>) vars.clone());
        }

        return new StaticSingleAssignmentFormulaFormatter(
            stackClone,
            (HashMap<Field, Integer>) fieldIncarnations.clone(),
            arraysIncarnations
        );
    }
}
