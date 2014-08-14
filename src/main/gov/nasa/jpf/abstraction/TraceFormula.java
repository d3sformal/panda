package gov.nasa.jpf.abstraction;

import java.util.Stack;

import gov.nasa.jpf.vm.MethodInfo;

import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Tautology;

public class TraceFormula extends Stack<Step> {
    public static final long serialVersionUID = 1L;

    public Predicate toConjunction() {
        Predicate c = Tautology.create();

        for (Step s : this) {
            c = Conjunction.create(c, s.getPredicate());
        }

        return c;
    }

    public void cutAfterAssertion(MethodInfo m, int pc) {
        for (int i = size() - 1; i >= 0; --i) {
            Step s = get(i);

            if (!s.getMethod().equals(m) || s.getPC() > pc) {
                pop();
            } else {
                break;
            }
        }
    }

    public void append(Predicate p, MethodInfo m, int pc) {
        push(new Step(p, m, pc));
    }

    @Override
    public TraceFormula clone() {
        TraceFormula c = new TraceFormula();

        for (Step s : this) {
            c.push(s);
        }

        return c;
    }
}
