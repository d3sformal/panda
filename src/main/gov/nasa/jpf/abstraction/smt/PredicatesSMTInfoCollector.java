package gov.nasa.jpf.abstraction.smt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.Add;
import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Disjunction;
import gov.nasa.jpf.abstraction.common.Divide;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Expressions;
import gov.nasa.jpf.abstraction.common.IfThenElse;
import gov.nasa.jpf.abstraction.common.Implication;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.MethodAssumePostPredicateContext;
import gov.nasa.jpf.abstraction.common.MethodAssumePrePredicateContext;
import gov.nasa.jpf.abstraction.common.MethodExpressionContext;
import gov.nasa.jpf.abstraction.common.MethodPredicateContext;
import gov.nasa.jpf.abstraction.common.Modulo;
import gov.nasa.jpf.abstraction.common.Multiply;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.ObjectExpressionContext;
import gov.nasa.jpf.abstraction.common.ObjectPredicateContext;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.PredicateContext;
import gov.nasa.jpf.abstraction.common.Predicates;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitable;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.StaticExpressionContext;
import gov.nasa.jpf.abstraction.common.StaticPredicateContext;
import gov.nasa.jpf.abstraction.common.Subtract;
import gov.nasa.jpf.abstraction.common.Tautology;
import gov.nasa.jpf.abstraction.common.Undefined;
import gov.nasa.jpf.abstraction.common.UninterpretedShiftLeft;
import gov.nasa.jpf.abstraction.common.UninterpretedShiftRight;
import gov.nasa.jpf.abstraction.common.UpdatedPredicate;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.ArrayElementWrite;
import gov.nasa.jpf.abstraction.common.access.ArrayLengthRead;
import gov.nasa.jpf.abstraction.common.access.ArrayLengthWrite;
import gov.nasa.jpf.abstraction.common.access.Fresh;
import gov.nasa.jpf.abstraction.common.access.Method;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldWrite;
import gov.nasa.jpf.abstraction.common.access.PackageAndClass;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.SpecialVariable;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultFresh;
import gov.nasa.jpf.abstraction.common.access.impl.Select;
import gov.nasa.jpf.abstraction.common.access.impl.Store;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultArrayLengths;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultArrays;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultField;
import gov.nasa.jpf.abstraction.common.impl.ArraysAssign;
import gov.nasa.jpf.abstraction.common.impl.FieldAssign;
import gov.nasa.jpf.abstraction.common.impl.New;
import gov.nasa.jpf.abstraction.common.impl.NullExpression;
import gov.nasa.jpf.abstraction.common.impl.VariableAssign;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.concrete.EmptyExpression;
import gov.nasa.jpf.abstraction.state.universe.Reference;

/**
 * A visitor used to traverse predicates and collect information for the SMT
 *
 * - set of all used variables (to be able to define them before they appear in the input)
 * - set of classes whose names need to be valid symbols
 * - set of field names (the same reason as above)
 * - set of fresh objects (object references)
 * - also a set of additional predicates that need to be added to the query to SMT ... array lengths are always >= 0
 */
public class PredicatesSMTInfoCollector implements PredicatesComponentVisitor {
    private Set<String> classes = new HashSet<String>();
    private Set<String> vars = new HashSet<String>();
    private Set<String> specials = new HashSet<String>();
    private Set<String> fields = new HashSet<String>();
    private Set<String> arrays = new HashSet<String>();
    private Set<Integer> fresh = new HashSet<Integer>();
    private boolean hasFresh = false;

    private PredicatesComponentVisitable currentCollectable = null;
    private Map<PredicatesComponentVisitable, Set<Predicate>> additionalPredicates = new HashMap<PredicatesComponentVisitable, Set<Predicate>>();

    private Set<AccessExpression> objects = new HashSet<AccessExpression>();

    @Override
    public void visit(Predicates predicates) {
        for (PredicateContext context : predicates.contexts) {
            context.accept(this);
        }
    }

    @Override
    public void visit(Expressions expressions) {
    }

    @Override
    public void visit(ObjectPredicateContext context) {
        for (Predicate predicate : context.getPredicates()) {
            predicate.accept(this);
        }
    }

    @Override
    public void visit(MethodPredicateContext context) {
        for (Predicate predicate : context.getPredicates()) {
            predicate.accept(this);
        }
    }

    @Override
    public void visit(MethodAssumePrePredicateContext context) {
        for (Predicate predicate : context.getPredicates()) {
            predicate.accept(this);
        }
    }

    @Override
    public void visit(MethodAssumePostPredicateContext context) {
        for (Predicate predicate : context.getPredicates()) {
            predicate.accept(this);
        }
    }

    @Override
    public void visit(StaticPredicateContext context) {
        for (Predicate predicate : context.getPredicates()) {
            predicate.accept(this);
        }
    }

    @Override
    public void visit(ObjectExpressionContext context) {
    }

    @Override
    public void visit(MethodExpressionContext context) {
    }

    @Override
    public void visit(StaticExpressionContext context) {
    }

    @Override
    public void visit(Negation predicate) {
        predicate.predicate.accept(this);
    }

    @Override
    public void visit(LessThan predicate) {
        predicate.a.accept(this);
        predicate.b.accept(this);
    }

    @Override
    public void visit(Equals predicate) {
        predicate.a.accept(this);
        predicate.b.accept(this);
    }

    @Override
    public void visit(Tautology predicate) {
    }

    @Override
    public void visit(Contradiction predicate) {
    }

    @Override
    public void visit(Conjunction predicate) {
        predicate.a.accept(this);
        predicate.b.accept(this);
    }

    @Override
    public void visit(Disjunction predicate) {
        predicate.a.accept(this);
        predicate.b.accept(this);
    }

    @Override
    public void visit(Implication predicate) {
        predicate.a.accept(this);
        predicate.b.accept(this);
    }

    @Override
    public void visit(VariableAssign predicate) {
        predicate.variable.accept(this);
        predicate.expression.accept(this);
    }

    @Override
    public void visit(FieldAssign predicate) {
        predicate.field.accept(this);
        predicate.newField.accept(this);
    }

    @Override
    public void visit(ArraysAssign predicate) {
        predicate.arrays.accept(this);
        predicate.newArrays.accept(this);
    }

    @Override
    public void visit(New predicate) {
        predicate.object.accept(this);
    }

    @Override
    public void visit(Add expression) {
        expression.a.accept(this);
        expression.b.accept(this);
    }

    @Override
    public void visit(Subtract expression) {
        expression.a.accept(this);
        expression.b.accept(this);
    }

    @Override
    public void visit(Multiply expression) {
        expression.a.accept(this);
        expression.b.accept(this);
    }

    @Override
    public void visit(Divide expression) {
        expression.a.accept(this);
        expression.b.accept(this);
    }

    @Override
    public void visit(Modulo expression) {
        expression.a.accept(this);
        expression.b.accept(this);
    }

    @Override
    public void visit(UninterpretedShiftLeft expression) {
        expression.a.accept(this);
        expression.b.accept(this);
    }

    @Override
    public void visit(UninterpretedShiftRight expression) {
        expression.a.accept(this);
        expression.b.accept(this);
    }

    private void addAdditionalPredicate(Predicate predicate) {
        if (!additionalPredicates.containsKey(currentCollectable)) {
            additionalPredicates.put(currentCollectable, new HashSet<Predicate>());
        }

        additionalPredicates.get(currentCollectable).add(predicate);
    }

    private void addObject(AccessExpression expression) {
        if (!(expression.getRoot() instanceof Fresh)) {
            objects.add(expression);
        }
    }

    @Override
    public void visit(Constant expression) {
    }

    @Override
    public void visit(AnonymousObject expression) {
        fresh.add(expression.getReference().getReferenceNumber());
        hasFresh = true;
    }

    @Override
    public void visit(AnonymousArray expression) {
        fresh.add(expression.getReference().getReferenceNumber());
        hasFresh = true;

        expression.getArrayLength().accept(this);
    }

    public Set<Predicate> getAdditionalPredicates(PredicatesComponentVisitable collectable) {
        if (!additionalPredicates.containsKey(collectable)) {
            return new HashSet<Predicate>();
        }

        Set<Predicate> instances = new HashSet<Predicate>();

        for (Predicate p : additionalPredicates.get(collectable)) {
            for (int f : fresh) {
                ElementInfo ei = ThreadInfo.getCurrentThread().getElementInfo(f);

                if (ei != null) {
                    instances.add(p.replace(SpecialVariable.create("fresh"), AnonymousObject.create(new Reference(ei))));
                }
            }
        }

        return instances;
    }

    public Set<String> getVars() {
        return vars;
    }

    public Set<String> getSpecials() {
        return specials;
    }

    public Set<String> getFields() {
        return fields;
    }

    public Set<String> getArrays() {
        return arrays;
    }

    public Set<String> getClasses() {
        return classes;
    }

    public Set<Integer> getFresh() {
        return fresh;
    }

    public boolean hasFresh() {
        return hasFresh;
    }

    public Set<AccessExpression> getObjects() {
        return objects;
    }

    @Override
    public void visit(Root expression) {
        vars.add(expression.getName());

        addObject(expression);
    }

    @Override
    public void visit(SpecialVariable expression) {
        specials.add(expression.getName());
    }

    @Override
    public void visit(Fresh expression) {
        throw new RuntimeException("unsupported term in SMT input");
    }

    @Override
    public void visit(ObjectFieldRead expression) {
        expression.getObject().accept(this);
        expression.getField().accept(this);

        addObject(expression);
    }

    @Override
    public void visit(ObjectFieldWrite expression) {
        expression.getObject().accept(this);
        expression.getField().accept(this);
        expression.getNewValue().accept(this);
    }

    @Override
    public void visit(ArrayElementRead expression) {
        expression.getArray().accept(this);
        expression.getArrays().accept(this);
        expression.getIndex().accept(this);

        AccessExpression ae = DefaultArrayElementRead.create(SpecialVariable.create("fresh"), expression.getIndex());

        addAdditionalPredicate(Equals.createUnminimized(ae, NullExpression.create()));
        addAdditionalPredicate(Equals.createUnminimized(ae, Constant.create(0)));

        addObject(expression);
    }

    @Override
    public void visit(ArrayElementWrite expression) {
        expression.getArray().accept(this);
        expression.getArrays().accept(this);
        expression.getIndex().accept(this);
        expression.getNewValue().accept(this);
    }

    @Override
    public void visit(ArrayLengthRead expression) {
        Predicate predicate = Negation.create(LessThan.createUnminimized(expression, Constant.create(0)));

        // In case of reasoning about fresh arrays
        if (expression.getArray() instanceof AnonymousArray) {
            addAdditionalPredicate(Equals.createUnminimized(expression, ((AnonymousArray) expression.getArray()).getArrayLength()));
        }

        addAdditionalPredicate(predicate);

        expression.getArray().accept(this);
        expression.getArrayLengths().accept(this);

        addObject(expression);
    }

    @Override
    public void visit(ArrayLengthWrite expression) {
        expression.getArray().accept(this);
        expression.getArrayLengths().accept(this);
        expression.getNewValue().accept(this);
    }

    @Override
    public void visit(IfThenElse expression) {
        expression.cond.accept(this);
        expression.a.accept(this);
        expression.b.accept(this);
    }

    @Override
    public void visit(DefaultArrays meta) {
        arrays.add(meta.getName());
    }

    @Override
    public void visit(DefaultArrayLengths meta) {
    }

    @Override
    public void visit(DefaultField meta) {
        fields.add(meta.getName());
    }

    @Override
    public void visit(Undefined expression) {
        throw new SMTException("UNDEFINED IN THE INPUT");
    }

    @Override
    public void visit(NullExpression expression) {
    }

    @Override
    public void visit(EmptyExpression expression) {
        throw new SMTException("EMPTY EXPRESSION IN THE INPUT");
    }

    @Override
    public void visit(UpdatedPredicate predicate) {
        predicate.apply().accept(this);
    }

    public void collect(PredicatesComponentVisitable collectable) {
        if (collectable instanceof UpdatedPredicate) {
            UpdatedPredicate updated = (UpdatedPredicate) collectable;

            currentCollectable = updated.getPredicate();
        } else {
            currentCollectable = collectable;
        }

        collectable.accept(this);
    }

    @Override
    public void visit(PackageAndClass expression) {
        classes.add(expression.getName());
    }

    @Override
    public void visit(Method expression) {
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
