package tm;

import java.io.*;
import java.util.*;

public class TM {

    static class Input extends LinkedList<String> {
        private static final long serialVersionUID = 0L;

        int pos = 0;
        String blank;

        public Input(String blank) {
            this.blank = blank;
        }

        public void move(Dir d) {
            switch (d) {
                case L:
                    if (pos > 0) {
                        --pos;
                    } else {
                        addFirst(blank);
                    }
                    break;
                case N:
                    break;
                case R:
                    ++pos;

                    if (pos == size()) {
                        addLast(blank);
                    }
                    break;
            }
        }

        public String read() {
            return get(pos);
        }

        public void write(String sym) {
            set(pos, sym);
        }

        @Override
        public String toString() {
            StringBuilder ret = new StringBuilder();

            for (String i : this) ret.append(i);

            return ret.toString();
        }
    }

    static enum Dir {
        L, N, R;
    }

    static class Pair<T0, T1> {
        T0 a;
        T1 b;

        public Pair(T0 a, T1 b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public int hashCode() {
            return (a.hashCode() << 4) + b.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Pair) {
                Pair<?, ?> p = (Pair<?, ?>) o;
                return a.equals(p.a) && b.equals(p.b);
            }

            return false;
        }

        @Override
        public String toString() {
            return "(" + a + ", " + b + ")";
        }
    }

    static class Triple<T0, T1, T2> {
        T0 a;
        T1 b;
        T2 c;

        public Triple(T0 a, T1 b, T2 c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        @Override
        public int hashCode() {
            return (((a.hashCode() << 4) + b.hashCode()) << 4) + c.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Triple) {
                Triple<?, ?, ?> t = (Triple<?, ?, ?>) o;
                return a.equals(t.a) && b.equals(t.b) && c.equals(t.c);
            }

            return false;
        }

        @Override
        public String toString() {
            return "(" + a + ", " + b + ", " + c + ")";
        }
    }

    static class Parser {
        private char[] def;
        private String input;

        public Parser(String file) throws IOException {
            BufferedReader br = new BufferedReader(new FileReader(file));

            def = br.readLine().toCharArray();
            input = br.readLine();
        }

        private void error() {
            throw new RuntimeException("Invalid input at char " + pos + ": " + new String(def, 0, pos) + (pos < def.length ? def[pos] : ""));
        }

        private int pos = 0;

        private void skipWhitespace() {
            while (pos < def.length && Character.isWhitespace(def[pos])) {
                ++pos;
            }
        }

        private String readId() {
            skipWhitespace();

            int next = pos;

            while (next < def.length && Character.isLetterOrDigit(def[next])) {
                ++next;
            }

            String id = new String(def, pos, next - pos);

            if (pos == next) {
                error();
            }

            pos = next;

            return id;
        }

        private Dir readDir() {
            skipWhitespace();

            if (pos < def.length) {
                switch (def[pos]) {
                    case 'L': ++pos; return Dir.L;
                    case 'N': ++pos; return Dir.N;
                    case 'R': ++pos; return Dir.R;
                }
            }

            error();

            return null;
        }

        private boolean readSetOpen() {
            skipWhitespace();

            if (pos < def.length && def[pos] == '{') {
                ++pos;

                return true;
            }

            return false;
        }

        private boolean readSetClose() {
            skipWhitespace();

            if (pos < def.length && def[pos] == '}') {
                ++pos;

                return true;
            }

            return false;
        }

        private boolean readTupleOpen() {
            skipWhitespace();

            if (pos < def.length && def[pos] == '(') {
                ++pos;

                return true;
            }

            return false;
        }

        private boolean readTupleClose() {
            skipWhitespace();

            if (pos < def.length && def[pos] == ')') {
                ++pos;

                return true;
            }

            return false;
        }

        private boolean readComma() {
            skipWhitespace();

            if (pos < def.length && def[pos] == ',') {
                ++pos;

                return true;
            }

            return false;
        }

        private boolean readArrow() {
            skipWhitespace();

            if (pos + 1 < def.length && def[pos] == '-' && def[pos + 1] == '>') {
                pos += 2;

                return true;
            }

            return false;
        }

        private Set<String> getSet() {
            Set<String> set = new HashSet<String>();

            if (!readSetOpen()) {
                error();
            }

            do {
                set.add(readId());
            } while (readComma());

            if (!readSetClose()) {
                error();
            }

            return set;
        }


        private Map<Pair<String, String>, Triple<String, String, Dir>> getFunction() {
            Map<Pair<String, String>, Triple<String, String, Dir>> fn = new HashMap<Pair<String, String>, Triple<String, String, Dir>>();

            if (!readSetOpen()) {
                error();
            }

            do {
                if (!readTupleOpen()) {
                    error();
                }

                String src = readId();

                if (!readComma()) {
                    error();
                }

                String in = readId();

                if (!readTupleClose()) {
                    error();
                }

                if (!readArrow()) {
                    error();
                }

                if (!readTupleOpen()) {
                    error();
                }

                String dest = readId();

                if (!readComma()) {
                    error();
                }

                String out = readId();

                if (!readComma()) {
                    error();
                }

                Dir d = readDir();

                if (!readTupleClose()) {
                    error();
                }

                fn.put(new Pair<String, String>(src, in), new Triple<String, String, Dir>(dest, out, d));
            } while (readComma());

            if (!readSetClose()) {
                error();
            }

            return fn;
        }

        public Set<String> getStates() {
            return getSet();
        }

        public Set<String> getAlphabet() {
            return getSet();
        }

        public Map<Pair<String, String>, Triple<String, String, Dir>> getTransitions(Set<String> states, Set<String> alphabet) {
            return getFunction();
        }

        public String getInit(Set<String> states) {
            return readId();
        }

        public String getBlank(Set<String> alphabet) {
            return readId();
        }

        public Set<String> getFinal(Set<String> states) {
            return getSet();
        }

        public TM parseTM() {
            Set<String> states;
            Set<String> alphabet;
            Map<Pair<String, String>, Triple<String, String, Dir>> trans;
            String init;
            String blank;
            Set<String> fin;

            if (!readTupleOpen()) {
                error();
            }

            states = getStates();

            if (!readComma()) {
                error();
            }

            alphabet = getAlphabet();

            if (!readComma()) {
                error();
            }

            trans = getTransitions(states, alphabet);

            if (!readComma()) {
                error();
            }

            init = getInit(states);

            if (!readComma()) {
                error();
            }

            blank = getBlank(alphabet);

            if (!readComma()) {
                error();
            }

            fin = getFinal(states);

            if (!readTupleClose()) {
                error();
            }

            return new TM(states, alphabet, trans, init, blank, fin);
        }

        public Input parseInput(Set<String> alphabet, String blank) {
            Input i = new Input(blank);

            for (String s : input.split(" ")) {
                if (!alphabet.contains(s)) {
                    throw new RuntimeException("Invalid input symbol `" + s + "`");
                }

                i.add(s);
            }

            return i;
        }
    }

    private Set<String> states;
    private Set<String> alphabet;
    private Map<Pair<String, String>, Triple<String, String, Dir>> trans;
    private String init;
    private String blank;
    private Set<String> fin;
    private Input input;

    public TM(Set<String> states, Set<String> alphabet, Map<Pair<String, String>, Triple<String, String, Dir>> trans, String init, String blank, Set<String> fin) {
        this.states = states;
        this.alphabet = alphabet;
        this.trans = trans;
        this.init = init;
        this.blank = blank;
        this.fin = fin;

        if (!states.contains(init)) {
            throw new RuntimeException("Unknown init state `" + init + "`");
        }

        if (!alphabet.contains(blank)) {
            throw new RuntimeException("Unknown blank symbol `" + blank + "`");
        }

        for (Pair<String, String> p : trans.keySet()) {
            Triple<String, String, Dir> t = trans.get(p);

            if (!states.contains(p.a)) {
                throw new RuntimeException("Unknown source state `" + p.a + "` in transition " + p + " -> " + t);
            }

            if (!alphabet.contains(p.b)) {
                throw new RuntimeException("Unknown symbol `" + p.b + "` in transition " + p + " -> " + t);
            }

            if (!states.contains(t.a)) {
                throw new RuntimeException("Unknown source state `" + t.a + "` in transition " + p + " -> " + t);
            }

            if (!alphabet.contains(t.b)) {
                throw new RuntimeException("Unknown symbol `" + t.b + "` in transition " + p + " -> " + t);
            }
        }

        for (String q : states) {
            for (String sym : alphabet) {
                Pair<String, String> p = new Pair<String, String>(q, sym);

                if (!trans.containsKey(p) && !fin.contains(q)) {
                    throw new RuntimeException("Missing transition for pair " + p);
                }
            }
        }

        for (String f : fin) {
            if (!states.contains(f)) {
                throw new RuntimeException("Unknown final state `" + f + "`");
            }
        }
    }

    public void run(Input input) {
        String cur = init;

        while (!finished(cur)) {
            Pair<String, String> src = new Pair<String, String>(cur, input.read());
            Triple<String, String, Dir> dest = trans.get(src);

            System.out.println(src + " -> " + dest);

            cur = dest.a;
            input.write(dest.b);
            input.move(dest.c);
        }

        System.out.println(input);
    }

    public boolean finished(String s) {
        return fin.contains(s);
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Please provide an input file.");
        } else {
            try {
                Parser p = new Parser(args[0]);
                TM tm = p.parseTM();

                tm.run(p.parseInput(tm.alphabet, tm.blank));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
