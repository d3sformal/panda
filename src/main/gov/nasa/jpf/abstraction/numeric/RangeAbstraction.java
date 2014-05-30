//
// Copyright (C) 2012 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
package gov.nasa.jpf.abstraction.numeric;

import gov.nasa.jpf.abstraction.AbstractBoolean;
import gov.nasa.jpf.abstraction.AbstractValue;
import gov.nasa.jpf.abstraction.Abstraction;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The abstract domain for given two integer or floating-point values MIN and
 * MAX is the set of all integers in interval [MIN, MAX], and also LESS and GREATER
 * to express the fact that a value is less than MIN or greater than MAX,
 * respectively. This abstraction can be used for integer values only.
 *
 * When the result of an abstract operation cannot be defined unambiguously
 * (e.g. LESS + LESS can be LESS, GREATER or any value between them), special
 * "composite tokens" which represent a set of abstract values are returned. They
 * are not represented by static members since their number increases exponentially
 * with value of (MAX-MIN).
 *
 * Remember, that this abstraction does not handle such floating-point values as
 * NaN and INF.
 */
public class RangeAbstraction extends Abstraction {

    // the key for state matching is enumeration of { LESS, MIN, MIN+1, ..., MAX-1, MAX, GREATER }
    // e.g LESS.get_key() == 0, MIN.get_key() == 1, ..., MAX.get_key() == MAX-MIN+1, ...

    // since a composite abstract value will never be the result of any bytecode
    // and thus will never take part in the state matching, all such values
    // have -1 as their key.

    public int MIN = 0;
    public int MAX = 0;

    public RangeAbstraction(int min, int max) {
        MIN = min;
        MAX = max;
    }

    public RangeValue create(Set<Integer> values) {
        RangeValue res = new RangeValue(-1);

        if (values.size() == 0)
            throw new RuntimeException("Invalid value");
        if (values.size() > 1) // isComposite
            res.setKey(-1);
        else
            for (Integer v : values)
                res.setKey(v-MIN+1);
        for (Integer v : values)
            res.getValues().add(v);

        res.abs = this;

        return res;
    }

    /**
     *
     * @return The number of abstract values in the domain.
     */
    public int getDomainSize() {
        return MAX-MIN+3;
    }

    @Override
    public RangeValue abstractMap(int v) {
        if (v < MIN)
            v = MIN-1;
        else if (v > MAX)
            v = MAX+1;
        RangeValue res = new RangeValue(v-MIN+1);
        res.getValues().add(v);
        res.abs = this;
        return res;
    }

    private double ___min(double... args) {
        double res = args[0];
        for (int i = 1; i < args.length; ++i)
            if (args[i] < res)
                    res = args[i];
        return res;
    }

    private double ___max(double... args) {
        double res = args[0];
        for (int i = 1; i < args.length; ++i)
            if (args[i] > res)
                    res = args[i];
        return res;
    }

    @Override
    public AbstractValue _bitwise_and(AbstractValue left, AbstractValue right) {
        // result is extremely difficult to predict, so this returns TOP
        Set<Integer> values = new HashSet<Integer>();
        for (int v = ((RangeAbstraction)left.abs).MIN-1; v <= ((RangeAbstraction)left.abs).MAX+1; ++v)
            values.add(v);
        return ((RangeAbstraction)left.abs).create(values);
    }

    @Override
    public AbstractValue _bitwise_or(AbstractValue left, AbstractValue right) {
        // result is extremely difficult to predict, so this returns TOP
        Set<Integer> values = new HashSet<Integer>();
        for (int v = ((RangeAbstraction)left.abs).MIN-1; v <= ((RangeAbstraction)left.abs).MAX+1; ++v)
            values.add(v);
        return ((RangeAbstraction)left.abs).create(values);
    }

    @Override
    public AbstractValue _bitwise_xor(AbstractValue left, AbstractValue right) {
        // result is extremely difficult to predict, so this returns TOP
        Set<Integer> values = new HashSet<Integer>();
        for (int v = ((RangeAbstraction)left.abs).MIN-1; v <= ((RangeAbstraction)left.abs).MAX+1; ++v)
            values.add(v);
        return ((RangeAbstraction)left.abs).create(values);
    }

    @Override
    public AbstractValue _div(AbstractValue left, AbstractValue right) {
        RangeValue left_value = (RangeValue)left;
        RangeValue right_value = (RangeValue)right;
        Set<Integer> values = new HashSet<Integer>(); // result
        Integer leftArr[] = left_value.getValues().toArray(new Integer[left_value.getValues().size()]);
        Integer rightArr[] = right_value.getValues().toArray(new Integer[right_value.getValues().size()]);

        /**
         * the idea is to iterate through all possible pairs of operands,
         * bound them by intervals [lv1, lv2] and [rv1, rv2] and perform
         * the operation on inequality lv1 <= lv <= lv2 and rv1 <= rv <= rv2;
         * then we check how the resulting [min, max] intersects [MIN, MAX]
         */
        for (Integer lv : leftArr) {
            double lv1 = lv, lv2 = lv;
            if (lv < ((RangeAbstraction)left.abs).MIN) {
                lv1 = Double.NEGATIVE_INFINITY;
                lv2 = ((RangeAbstraction)left.abs).MIN-1;
            } else if (lv > ((RangeAbstraction)left.abs).MAX) {
                lv1 = ((RangeAbstraction)left.abs).MAX+1;
                lv2 = Double.POSITIVE_INFINITY;
            }
            for (Integer rv : rightArr) {
                double rv1 = rv, rv2 = rv;
                if (rv < ((RangeAbstraction)left.abs).MIN) {
                    rv1 = Double.NEGATIVE_INFINITY;
                    rv2 = ((RangeAbstraction)left.abs).MIN-1;
                } else if (lv > ((RangeAbstraction)left.abs).MAX) {
                    rv1 = ((RangeAbstraction)left.abs).MAX+1;
                    rv2 = Double.POSITIVE_INFINITY;
                }
                double _min, _max;
                if (rv1 < 0 && 0 < rv2)
                    throw new RuntimeException("### WARNING: Division by ZERO may happen");
                if ((lv1 <= 0 && 0 <= lv2) || (rv1 < 0 && 0 < rv2)) {
                    _min = ___min(lv1/rv1, lv1/rv2, lv2/rv1, lv2/rv2, 0);
                    _max = ___max(lv1/rv1, lv1/rv2, lv2/rv1, lv2/rv2, 0);
                } else {
                    _min = ___min(lv1/rv1, lv1/rv2, lv2/rv1, lv2/rv2);
                    _max = ___max(lv1/rv1, lv1/rv2, lv2/rv1, lv2/rv2);
                }
                int min, max;
                if (_min < ((RangeAbstraction)left.abs).MIN)
                    min = ((RangeAbstraction)left.abs).MIN-1;
                else if (_min > ((RangeAbstraction)left.abs).MAX)
                    min = ((RangeAbstraction)left.abs).MAX+1;
                else
                    min = (int)_min;
                if (_max < ((RangeAbstraction)left.abs).MIN)
                    max = ((RangeAbstraction)left.abs).MIN-1;
                else if (_max > ((RangeAbstraction)left.abs).MAX)
                    max = ((RangeAbstraction)left.abs).MAX+1;
                else
                    max = (int)_max;
                for (int v = min; v <= max; ++v)
                    values.add(v);
            }
        }
        return ((RangeAbstraction)left.abs).create(values);
    }

    @Override
    public AbstractBoolean _eq(AbstractValue left, AbstractValue right) {
        RangeValue left_value = (RangeValue)left;
        RangeValue right_value = (RangeValue)right;
        List<Integer> leftList = Arrays.asList(left_value.getValues().toArray(new Integer[left_value.getValues().size()]));
        Integer rightArr[] = right_value.getValues().toArray(new Integer[right_value.getValues().size()]);

        // check if two lists contain:
        // * for true — any pair of equal elements
        // * for false — any pair of different elements (or their sizes are different)
        boolean t = false, f = leftList.size() != rightArr.length;
        for (int r : rightArr)
            if (leftList.contains(r))
                t = true;
            else
                f = true;
        if (f & t)
            return AbstractBoolean.TOP;
        else return (f)? AbstractBoolean.FALSE : AbstractBoolean.TRUE;
    }

    @Override
    public AbstractBoolean _ge(AbstractValue left, AbstractValue right) {
        RangeValue left_value = (RangeValue)left;
        RangeValue right_value = (RangeValue)right;
        // just check how two segments intersect
        int lMin = Collections.min(left_value.getValues()); // get the least and the biggest values
        int lMax = Collections.max(left_value.getValues());
        int rMin = Collections.min(right_value.getValues()); // get the least and the biggest values
        int rMax = Collections.max(right_value.getValues());
        boolean t = lMax >= rMin;
        boolean f = lMin < rMax || (lMin < ((RangeAbstraction)left.abs).MIN && rMax < ((RangeAbstraction)left.abs).MIN);
        if (f & t)
            return AbstractBoolean.TOP;
        else return (f)? AbstractBoolean.FALSE : AbstractBoolean.TRUE;
    }

    @Override
    public AbstractBoolean _gt(AbstractValue left, AbstractValue right) {
        RangeValue left_value = (RangeValue)left;
        RangeValue right_value = (RangeValue)right;
        // just check how two segments intersect
        int lMin = Collections.min(left_value.getValues()); // get the least and the biggest values
        int lMax = Collections.max(left_value.getValues());
        int rMin = Collections.min(right_value.getValues()); // get the least and the biggest values
        int rMax = Collections.max(right_value.getValues());
        boolean t = lMax > rMin || (lMax > ((RangeAbstraction)left.abs).MAX && rMin > ((RangeAbstraction)left.abs).MAX);
        boolean f = lMin <= rMax;
        if (f & t)
            return AbstractBoolean.TOP;
        else return (f)? AbstractBoolean.FALSE : AbstractBoolean.TRUE;
    }

    @Override
    public AbstractBoolean _le(AbstractValue left, AbstractValue right) {
        RangeValue left_value = (RangeValue)left;
        RangeValue right_value = (RangeValue)right;
        // just check how two segments intersect
        int lMin = Collections.min(left_value.getValues()); // get the least and the biggest values
        int lMax = Collections.max(left_value.getValues());
        int rMin = Collections.min(right_value.getValues()); // get the least and the biggest values
        int rMax = Collections.max(right_value.getValues());
        boolean t = lMin <= rMax;
        boolean f = lMax > rMin || (lMax > ((RangeAbstraction)left.abs).MAX && rMin > ((RangeAbstraction)left.abs).MAX);
        if (f & t)
            return AbstractBoolean.TOP;
        else return (f)? AbstractBoolean.FALSE : AbstractBoolean.TRUE;
    }

    @Override
    public AbstractBoolean _lt(AbstractValue left, AbstractValue right) {
        RangeValue left_value = (RangeValue)left;
        RangeValue right_value = (RangeValue)right;
        // just check how two segments intersect
        int lMin = Collections.min(left_value.getValues()); // get the least and the biggest values
        int lMax = Collections.max(left_value.getValues());
        int rMin = Collections.min(right_value.getValues()); // get the least and the biggest values
        int rMax = Collections.max(right_value.getValues());
        boolean t = lMin < rMax || (lMin < ((RangeAbstraction)left.abs).MIN && rMax < ((RangeAbstraction)left.abs).MIN);
        boolean f = lMax >= rMin;
        if (f & t)
            return AbstractBoolean.TOP;
        else return (f)? AbstractBoolean.FALSE : AbstractBoolean.TRUE;
    }

    @Override
    public AbstractValue _minus(AbstractValue left, AbstractValue right) {
        RangeValue left_value = (RangeValue)left;
        RangeValue right_value = (RangeValue)right;
        Set<Integer> values = new HashSet<Integer>(); // result
        Integer leftArr[] = left_value.getValues().toArray(new Integer[left_value.getValues().size()]);
        Integer rightArr[] = right_value.getValues().toArray(new Integer[right_value.getValues().size()]);

        /**
         * the idea is to iterate through all possible pairs of operands,
         * bound them by intervals [lv1, lv2] and [rv1, rv2] and perform
         * the operation on inequality lv1 <= lv <= lv2 and rv1 <= rv <= rv2;
         */
        for (Integer lv : leftArr) {
            double lv1 = lv, lv2 = lv;
            if (lv < ((RangeAbstraction)left.abs).MIN) {
                lv1 = Double.NEGATIVE_INFINITY;
                lv2 = ((RangeAbstraction)left.abs).MIN-1;
            } else if (lv > ((RangeAbstraction)left.abs).MAX) {
                lv1 = ((RangeAbstraction)left.abs).MAX+1;
                lv2 = Double.POSITIVE_INFINITY;
            }
            for (Integer rv : rightArr) {
                double rv1 = rv, rv2 = rv;
                if (rv < ((RangeAbstraction)left.abs).MIN) {
                    rv1 = Double.NEGATIVE_INFINITY;
                    rv2 = ((RangeAbstraction)left.abs).MIN-1;
                } else if (lv > ((RangeAbstraction)left.abs).MAX) {
                    rv1 = ((RangeAbstraction)left.abs).MAX+1;
                    rv2 = Double.POSITIVE_INFINITY;
                }
                double _min = lv1+rv1, _max = lv2+rv2;
                int min, max;
                if (_min < ((RangeAbstraction)left.abs).MIN)
                    min = ((RangeAbstraction)left.abs).MIN-1;
                else if (_min > ((RangeAbstraction)left.abs).MAX)
                    min = ((RangeAbstraction)left.abs).MAX+1;
                else
                    min = (int)_min;
                if (_max < ((RangeAbstraction)left.abs).MIN)
                    max = ((RangeAbstraction)left.abs).MIN-1;
                else if (_max > ((RangeAbstraction)left.abs).MAX)
                    max = ((RangeAbstraction)left.abs).MAX+1;
                else
                    max = (int)_max;
                for (int v = min; v <= max; ++v)
                    values.add(v);
            }
        }
        return ((RangeAbstraction)left.abs).create(values);
    }

    @Override
    public AbstractValue _mul(AbstractValue left, AbstractValue right) {
        RangeValue left_value = (RangeValue)left;
        RangeValue right_value = (RangeValue)right;
        Set<Integer> values = new HashSet<Integer>();
        Integer leftArr[] = left_value.getValues().toArray(new Integer[left_value.getValues().size()]);
        Integer rightArr[] = right_value.getValues().toArray(new Integer[right_value.getValues().size()]);

        /**
         * the idea is to iterate through all possible pairs of operands,
         * bound them by intervals [lv1, lv2] and [rv1, rv2] and perform
         * the operation on inequality lv1 <= lv <= lv2 and rv1 <= rv <= rv2
         */
        for (Integer lv : leftArr) {
            double lv1 = lv, lv2 = lv;
            if (lv < ((RangeAbstraction)left.abs).MIN) {
                lv1 = Double.NEGATIVE_INFINITY;
                lv2 = ((RangeAbstraction)left.abs).MIN-1;
            } else if (lv > ((RangeAbstraction)left.abs).MAX) {
                lv1 = ((RangeAbstraction)left.abs).MAX+1;
                lv2 = Double.POSITIVE_INFINITY;
            }
            for (Integer rv : rightArr) {
                double rv1 = rv, rv2 = rv;
                if (rv < ((RangeAbstraction)left.abs).MIN) {
                    rv1 = Double.NEGATIVE_INFINITY;
                    rv2 = ((RangeAbstraction)left.abs).MIN-1;
                } else if (lv > ((RangeAbstraction)left.abs).MAX) {
                    rv1 = ((RangeAbstraction)left.abs).MAX+1;
                    rv2 = Double.POSITIVE_INFINITY;
                }
                double _min, _max;
                if ((lv1 <= 0 && 0 <= lv2) || (rv1 <= 0 && 0 <= rv2)) {
                    _min = ___min(lv1*rv1, lv1*rv2, lv2*rv1, lv2*rv2, 0);
                    _max = ___max(lv1*rv1, lv1*rv2, lv2*rv1, lv2*rv2, 0);
                } else {
                    _min = ___min(lv1*rv1, lv1*rv2, lv2*rv1, lv2*rv2);
                    _max = ___max(lv1*rv1, lv1*rv2, lv2*rv1, lv2*rv2);
                }
                int min, max;
                if (_min < ((RangeAbstraction)left.abs).MIN)
                    min = ((RangeAbstraction)left.abs).MIN-1;
                else if (_min > ((RangeAbstraction)left.abs).MAX)
                    min = ((RangeAbstraction)left.abs).MAX+1;
                else
                    min = (int)_min;
                if (_max < ((RangeAbstraction)left.abs).MIN)
                    max = ((RangeAbstraction)left.abs).MIN-1;
                else if (_max > ((RangeAbstraction)left.abs).MAX)
                    max = ((RangeAbstraction)left.abs).MAX+1;
                else
                    max = (int)_max;
                for (int v = min; v <= max; ++v)
                    values.add(v);
            }
        }
        return ((RangeAbstraction)left.abs).create(values);
    }

    @Override
    public AbstractValue _neg_impl(AbstractValue abs) {
        RangeValue value = (RangeValue)abs;
        Set<Integer> values = new HashSet<Integer>();
        for (Integer v : value.getValues()) {
            if (v > ((RangeAbstraction)abs.abs).MAX)
                values.add(((RangeAbstraction)abs.abs).MIN-1);
            if (v < ((RangeAbstraction)abs.abs).MIN)
                values.add(((RangeAbstraction)abs.abs).MAX+1);
            if (-v > ((RangeAbstraction)abs.abs).MAX)
                values.add(((RangeAbstraction)abs.abs).MAX+1);
            else if (-v < ((RangeAbstraction)abs.abs).MIN)
                values.add(((RangeAbstraction)abs.abs).MIN-1);
            else
                values.add(-v);
        }
        return ((RangeAbstraction)abs.abs).create(values);
    }

    @Override
    public AbstractValue _plus(AbstractValue left, AbstractValue right) {
        RangeValue left_value = (RangeValue)left;
        RangeValue right_value = (RangeValue)right;
        Set<Integer> values = new HashSet<Integer>(); //result
        Integer leftArr[] = left_value.getValues().toArray(new Integer[left_value.getValues().size()]);
        Integer rightArr[] = right_value.getValues().toArray(new Integer[right_value.getValues().size()]);

        /**
         * the idea is to iterate through all possible pairs of operands,
         * bound them by intervals [lv1, lv2] and [rv1, rv2] and perform
         * the operation on inequality lv1 <= lv <= lv2 and rv1 <= rv <= rv2
         */
        for (Integer lv : leftArr) {
            double lv1 = lv, lv2 = lv;
            if (lv < ((RangeAbstraction)left.abs).MIN) {
                lv1 = Double.NEGATIVE_INFINITY;
                lv2 = ((RangeAbstraction)left.abs).MIN-1;
            } else if (lv > ((RangeAbstraction)left.abs).MAX) {
                lv1 = ((RangeAbstraction)left.abs).MAX+1;
                lv2 = Double.POSITIVE_INFINITY;
            }
            for (Integer rv : rightArr) {
                double rv1 = rv, rv2 = rv;
                if (rv < ((RangeAbstraction)left.abs).MIN) {
                    rv1 = Double.NEGATIVE_INFINITY;
                    rv2 = ((RangeAbstraction)left.abs).MIN-1;
                } else if (lv > ((RangeAbstraction)left.abs).MAX) {
                    rv1 = ((RangeAbstraction)left.abs).MAX+1;
                    rv2 = Double.POSITIVE_INFINITY;
                }
                double _min = lv1+rv1, _max = lv2+rv2;
                int min, max;
                if (_min < ((RangeAbstraction)left.abs).MIN)
                    min = ((RangeAbstraction)left.abs).MIN-1;
                else if (_min > ((RangeAbstraction)left.abs).MAX)
                    min = ((RangeAbstraction)left.abs).MAX+1;
                else
                    min = (int)_min;
                if (_max < ((RangeAbstraction)left.abs).MIN)
                    max = ((RangeAbstraction)left.abs).MIN-1;
                else if (_max > ((RangeAbstraction)left.abs).MAX)
                    max = ((RangeAbstraction)left.abs).MAX+1;
                else
                    max = (int)_max;
                for (int v = min; v <= max; ++v)
                    values.add(v);
            }
        }
        return ((RangeAbstraction)left.abs).create(values);
    }

    @Override
    public AbstractValue _plus(AbstractValue left, int right) {
        RangeValue left_value = (RangeValue)left;

        if (right == 1) { // increment
            Set<Integer> result = new HashSet<Integer>();
            Integer values[] = left_value.getValues().toArray(new Integer[left_value.getValues().size()]);
            for (Integer v : values)
                if (v < ((RangeAbstraction)left.abs).MIN) {
                    result.add(v);
                    result.add(((RangeAbstraction)left.abs).MIN);
                } else if (v >= ((RangeAbstraction)left.abs).MAX)
                    result.add(((RangeAbstraction)left.abs).MAX);
                else
                    result.add(v+1);
            return ((RangeAbstraction)left.abs).create(result);
        } else if (right == -1) { //decrement
            Set<Integer> result = new HashSet<Integer>();
            Integer values[] = left_value.getValues().toArray(new Integer[left_value.getValues().size()]);
            for (Integer v : values)
                if (v > ((RangeAbstraction)left.abs).MAX) {
                    result.add(v);
                    result.add(((RangeAbstraction)left.abs).MAX);
                } else if (v <= ((RangeAbstraction)left.abs).MIN)
                    result.add(((RangeAbstraction)left.abs).MIN);
                else
                    result.add(v-1);
            return ((RangeAbstraction)left.abs).create(result);
        } else
            return _plus(left, left.abs.abstractMap(right));
    }

    @Override
    public AbstractValue _rem(AbstractValue left, AbstractValue right) {
        RangeValue left_value = (RangeValue)left;
        RangeValue right_value = (RangeValue)right;
        Set<Integer> values = new HashSet<Integer>();
        Integer leftArr[] = left_value.getValues().toArray(new Integer[left_value.getValues().size()]);
        Integer rightArr[] = right_value.getValues().toArray(new Integer[right_value.getValues().size()]);

        /**
         * the idea is to iterate through all possible pairs of operands,
         * bound them by intervals [lv1, lv2] and [rv1, rv2] and perform
         * the operation on inequality lv1 <= lv <= lv2 and rv1 <= rv <= rv2
         */
        for (Integer lv : leftArr) {
            for (Integer rv : rightArr) {
                if (rv == 1) {
                    values.add(0);
                    continue;
                }
                int min = 0, max = lv;
                if (lv < ((RangeAbstraction)left.abs).MIN || lv > ((RangeAbstraction)left.abs).MAX)
                    if (rv < ((RangeAbstraction)left.abs).MIN || rv > ((RangeAbstraction)left.abs).MAX)
                        max = ((RangeAbstraction)left.abs).MAX+1;
                    else
                        max = Math.min(((RangeAbstraction)left.abs).MAX+1, Math.abs(rv)-1);
                else
                    if (rv < ((RangeAbstraction)left.abs).MIN || rv > ((RangeAbstraction)left.abs).MAX)
                        min = Math.abs(rv);
                    else
                        min = max = lv % rv;
                if (min < ((RangeAbstraction)left.abs).MIN)
                    min = ((RangeAbstraction)left.abs).MIN-1;
                else if (min > ((RangeAbstraction)left.abs).MAX)
                    min = ((RangeAbstraction)left.abs).MAX+1;
                if (max < ((RangeAbstraction)left.abs).MIN)
                    max = ((RangeAbstraction)left.abs).MIN-1;
                else if (max > ((RangeAbstraction)left.abs).MAX)
                    max = ((RangeAbstraction)left.abs).MAX+1;
                for (int v = min; v <= max; ++v)
                    values.add(v);
            }
        }
        return ((RangeAbstraction)left.abs).create(values);
    }

    @Override
    public AbstractValue _shift_left(AbstractValue left, AbstractValue right) {
        // result is extremely difficult to predict, so this returns TOP
        Set<Integer> values = new HashSet<Integer>();
        for (int v = ((RangeAbstraction)left.abs).MIN-1; v <= ((RangeAbstraction)left.abs).MAX+1; ++v)
            values.add(v);
        return ((RangeAbstraction)left.abs).create(values);
    }

    @Override
    public AbstractValue _shift_right(AbstractValue left, AbstractValue right) {
        // result is extremely difficult to predict, so this returns TOP
        Set<Integer> values = new HashSet<Integer>();
        for (int v = ((RangeAbstraction)left.abs).MIN-1; v <= ((RangeAbstraction)left.abs).MAX+1; ++v)
            values.add(v);
        return ((RangeAbstraction)left.abs).create(values);
    }

    @Override
    public AbstractValue _unsigned_shift_right(AbstractValue left, AbstractValue right) {
        // result is extremely difficult to predict, so this returns TOP
        Set<Integer> values = new HashSet<Integer>();
        for (int v = ((RangeAbstraction)left.abs).MIN-1; v <= ((RangeAbstraction)left.abs).MAX+1; ++v)
            values.add(v);
        return ((RangeAbstraction)left.abs).create(values);
    }

    /**
     * @return Signs.ZERO if the operand is numerically equal to this
     *         AbstractValue; Signs.NEG if this AbstractValue is numerically less
     *         than the operand; and Signs.POS if this AbstractValue is
     *         numerically greater than the operand.
     */
    @Override
    public SignsValue _cmp(AbstractValue left, AbstractValue right) {
        boolean n = false, z = false, p = false;
        if (_gt(left, right) != AbstractBoolean.FALSE)
            p = true;
        if (_lt(left, right) != AbstractBoolean.FALSE)
            n = true;
        if (_gt(left, right) != AbstractBoolean.TRUE
                && _lt(left, right) != AbstractBoolean.TRUE)
            z = true;
        return SignsAbstraction.getInstance().create(n, z, p);
    }

    /**
     * @return Signs.ZERO if the operand is numerically equal to this
     *         AbstractValue; Signs.NEG if this AbstractValue is numerically less
     *         than the operand; and Signs.POS if this AbstractValue is
     *         numerically greater than the operand.
     */
    @Override
    public SignsValue _cmpg(AbstractValue left, AbstractValue right) {
        boolean n = false, z = false, p = false;
        if (_gt(left, right) != AbstractBoolean.FALSE)
            p = true;
        if (_lt(left, right) != AbstractBoolean.FALSE)
            n = true;
        if (_gt(left, right) != AbstractBoolean.TRUE
                && _lt(left, right) != AbstractBoolean.TRUE)
            z = true;
        return SignsAbstraction.getInstance().create(n, z, p);
    }

    /**
     * @return Signs.ZERO if the operand is numerically equal to this
     *         AbstractValue; Signs.NEG if this AbstractValue is numerically less
     *         than the operand; and Signs.POS if this AbstractValue is
     *         numerically greater than the operand.
     */
    @Override
    public SignsValue _cmpl(AbstractValue left, AbstractValue right) {
        boolean n = false, z = false, p = false;
        if (_gt(left, right) != AbstractBoolean.FALSE)
            p = true;
        if (_lt(left, right) != AbstractBoolean.FALSE)
            n = true;
        if (_gt(left, right) != AbstractBoolean.TRUE
                && _lt(left, right) != AbstractBoolean.TRUE)
            z = true;
        return SignsAbstraction.getInstance().create(n, z, p);
    }

}
