package propositionalvalidity;

import java.io.*;
import java.util.*;

public class PV {
    static class Result {
        enum Type {
            SAT,
            UNSAT
        }

        Type type;
        Map<String, Boolean> assign;

        Result(boolean b, Map<String, Boolean> a) {
            type = b ? Type.SAT : Type.UNSAT;
            assign = a;
        }
    }

    static interface Formula {
        Formula negate();
    }

    static class TRUE implements Formula {
        public Formula negate() {
            return new FALSE();
        }

        public String toString() {
            return "TRUE";
        }
    }

    static class FALSE implements Formula {
        public Formula negate() {
            return new TRUE();
        }

        public String toString() {
            return "FALSE";
        }
    }

    static class VAR implements Formula {
        String name;

        VAR(String n) {
            name = n;
        }

        public Formula negate() {
            return new NEGATION(this);
        }

        public String toString() {
            return name;
        }
    }

    static class NEGATION implements Formula {
        Formula a;

        NEGATION(Formula f) {
            a = f;
        }

        public Formula negate() {
            return a;
        }

        public String toString() {
            return "NEG(" + a + ")";
        }
    }

    static class AND implements Formula {
        Formula a;
        Formula b;

        AND(Formula f1, Formula f2) {
            a = f1;
            b = f2;
        }

        public Formula negate() {
            return new OR(a.negate(), b.negate());
        }

        public String toString() {
            return "AND(" + a + ", " + b + ")";
        }
    }

    static class OR implements Formula {
        Formula a;
        Formula b;

        OR(Formula f1, Formula f2) {
            a = f1;
            b = f2;
        }

        public Formula negate() {
            return new AND(a.negate(), b.negate());
        }

        public String toString() {
            return "OR(" + a + ", " + b + ")";
        }
    }

    static class Parser {
        String formula;
        int pos = 0;

        Parser(String formula) {
            this.formula = formula;
        }

        void skipWhitespace() {
            while (pos < formula.length() && Character.isWhitespace(formula.charAt(pos))) {
                ++pos;
            }
        }

        boolean matchAND() {
            skipWhitespace();

            return pos + 3 < formula.length() && formula.substring(pos, pos + 3).equals("AND");
        }

        boolean matchOR() {
            skipWhitespace();

            return pos + 2 < formula.length() && formula.substring(pos, pos + 2).equals("OR");
        }

        boolean matchNEG() {
            skipWhitespace();

            return pos + 3 < formula.length() && formula.substring(pos, pos + 3).equals("NEG");
        }

        boolean matchTRUE() {
            skipWhitespace();

            return pos + 4 < formula.length() && formula.substring(pos, pos + 4).equals("TRUE");
        }

        boolean matchFALSE() {
            skipWhitespace();

            return pos + 5 < formula.length() && formula.substring(pos, pos + 5).equals("FALSE");
        }

        boolean matchVAR() {
            skipWhitespace();

            if (pos < formula.length() && Character.isLetter(formula.charAt(pos))) {
                return true;
            }

            return false;
        }

        void advanceAND() {
            pos += 3;
        }

        void advanceOR() {
            pos += 2;
        }

        void advanceNEG() {
            pos += 3;
        }

        void advanceTRUE() {
            pos += 4;
        }

        void advanceFALSE() {
            pos += 5;
        }

        void open() {
            if (pos >= formula.length() || formula.charAt(pos) != '(') {
                throw new RuntimeException("Expected ( at " + pos);
            }
            pos++;
        }

        void close() {
            if (pos >= formula.length() || formula.charAt(pos) != ')') {
                throw new RuntimeException("Expected ) at " + pos);
            }
            pos++;
        }

        void comma() {
            if (pos >= formula.length() || formula.charAt(pos) != ',') {
                throw new RuntimeException("Expected , at " + pos);
            }
            pos++;
        }

        String getVar() {
            int next = pos;

            while (next < formula.length() && Character.isLetter(formula.charAt(next))) {
                ++next;
            }

            String var = formula.substring(pos, next);

            pos = next;

            return var;
        }

        Formula getFormula() {
            if (matchAND()) {
                advanceAND();
                open();
                Formula f1 = getFormula();
                comma();
                Formula f2 = getFormula();
                close();
                return new AND(f1, f2);
            }
            if (matchOR()) {
                advanceOR();
                open();
                Formula f1 = getFormula();
                comma();
                Formula f2 = getFormula();
                close();
                return new OR(f1, f2);
            }
            if (matchNEG()) {
                advanceNEG();
                open();
                Formula f = getFormula();
                close();
                return new NEGATION(f);
            }
            if (matchTRUE()) {
                advanceTRUE();
                return new TRUE();
            }
            if (matchFALSE()) {
                advanceFALSE();
                return new FALSE();
            }
            if (matchVAR()) {
                String var = getVar();
                return new VAR(var);
            }

            throw new RuntimeException("Error at " + pos + ": " + formula.substring(0, pos) + (pos < formula.length() ? formula.charAt(pos) : ""));
        }
    }

    static class Solver {
        void collectVars(Formula f, Set<String> vars) {
            if (f instanceof VAR) {
                VAR v = (VAR) f;
                vars.add(v.name);
            } else {
                if (f instanceof AND) {
                    AND a = (AND) f;
                    collectVars(a.a, vars);
                    collectVars(a.b, vars);
                }
                if (f instanceof OR) {
                    OR o = (OR) f;
                    collectVars(o.a, vars);
                    collectVars(o.b, vars);
                }
                if (f instanceof NEGATION) {
                    NEGATION n = (NEGATION) f;
                    collectVars(n.a, vars);
                }
            }
        }

        boolean check(Formula f, Map<String, Boolean> assign) {
            if (f instanceof TRUE) return true;
            if (f instanceof FALSE) return false;
            if (f instanceof VAR) return assign.get(((VAR) f).name);
            if (f instanceof NEGATION) return !check(((NEGATION) f).a, assign);
            if (f instanceof AND) {
                AND a = (AND) f;

                return check(a.a, assign) && check(a.b, assign);
            }
            if (f instanceof OR) {
                OR o = (OR) f;

                return check(o.a, assign) || check(o.b, assign);
            }
            return false;
        }

        boolean decide(Formula f, Set<String> vars, Map<String, Boolean> assign) {
            if (vars.isEmpty()) {
                return check(f, assign);
            } else {
                String var = vars.iterator().next();

                vars.remove(var);

                assign.put(var, true);
                if (decide(f, vars, assign)) {
                    return true;
                } else {
                    assign.put(var, false);
                    if (decide(f, vars, assign)) {
                        return true;
                    }
                }

                assign.remove(var);
                vars.add(var);

                return false;
            }
        }

        Result solve(Formula f) {
            Set<String> vars = new HashSet<String>();
            Map<String, Boolean> assign = new HashMap<String, Boolean>();

            collectVars(f, vars);

            boolean sat = decide(f, vars, assign);

            return new Result(sat, assign);
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Please specify the input file.");
        } else {
            try {
                BufferedReader br = new BufferedReader(new FileReader(args[0]));
                String line = br.readLine();
                String[] f = line.split("=>");
                Parser p1 = new Parser(f[0]);
                Parser p2 = new Parser(f[1]);

                Formula f1a = p1.getFormula();
                Formula f1b = p2.getFormula();
                Formula f2 = new AND(f1a, f1b.negate());

                System.out.println(f1a + " => " + f1b);
                System.out.println(f2);

                Solver s = new Solver();

                Result r = s.solve(f2);

                if (r.type == Result.Type.SAT) {
                    System.out.println("Invalid. Counterexample: " + r.assign);
                } else {
                    System.out.println("Valid.");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
