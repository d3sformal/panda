package gov.nasa.jpf.abstraction.predicate.smt;

import gov.nasa.jpf.abstraction.predicate.common.AccessPath;
import gov.nasa.jpf.abstraction.predicate.common.AccessPathElement;
import gov.nasa.jpf.abstraction.predicate.common.AccessPathSubElement;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SMT {
	
	private BufferedWriter in = null;
	private BufferedReader out = null;
	
	public SMT() throws IOException {
		try {
			String[] args = new String[] {
				System.getProperty("user.dir") + "/bin/mathsat",
				"-theory.arr.enabled=true"
			};
			String[] env  = new String[] {};

			Process mathsat = Runtime.getRuntime().exec(args, env);

			OutputStream instream = mathsat.getOutputStream();
			OutputStreamWriter inwriter = new OutputStreamWriter(instream);

			in = new BufferedWriter(inwriter);

			InputStream outstream = mathsat.getInputStream();
			InputStreamReader outreader = new InputStreamReader(outstream);

			out = new BufferedReader(outreader);
		} catch (IOException e) {
			System.err.println("SMT will not start.");
			
			throw e;
		}
	}
	
	private Boolean[] isSatisfiable(String input) throws IOException {
		List<Boolean> values = new ArrayList<Boolean>();
			
		String output = "";
		
		try {
			in.write(input);
			in.flush();
		} catch (IOException e) {
			System.err.println("SMT refuses input.");
			
			throw e;
		} finally {
			if (in != null) {
				in.close();
			}
		}

		try {
			while ((output = out.readLine()) != null) {
				values.add(output.matches("^sat$"));
			}
		} catch (IOException e) {
			System.err.println("SMT refuses to provide output.");
			
			throw e;
		} finally {
			if (out != null) {
				out.close();
			}
		}
		
		return values.toArray(new Boolean[values.size()]);
	}
	
	private static String predicateToString(Predicate predicate) {
		PredicatesSMTStringifier stringifier = new PredicatesSMTStringifier();
		
		predicate.accept(stringifier);
		
		return stringifier.getString();
	}
	
	public Map<Predicate, TruthValue> valuatePredicates(List<Predicate> predicates) throws IOException {
		Map<Predicate, TruthValue> valuation = new HashMap<Predicate, TruthValue>();
		
		Set<String> vars = new HashSet<String>();
		Set<String> fields = new HashSet<String>();

		String input = "(set-logic QF_AUFLIA)";
		
		input += "(declare-fun arr () (Array Int (Array Int Int)))";
		
		for (Predicate predicate : predicates) {
			for (AccessPath path : predicate.getPaths()) {
				AccessPathElement element = path.getRoot();
				
				vars.add("var_" + path.getRoot().getName());
				
				while (element != null) {
					if (element instanceof AccessPathSubElement) {
						AccessPathSubElement subElement = (AccessPathSubElement) element;

						fields.add("field_" + subElement.getName());
					}
					
					element = element.getNext();
				}
			}
		}
		
		for (String var : vars) {
			input += "(declare-fun " + var + " () Int)";
		}
		
		for (String field : fields) {
			input += "(declare-fun " + field + " (Int) Int)";
		}

		for (Predicate predicate : predicates) {
			String condition = predicateToString(predicate);
			
			input +=
				"(push 1)" +
				"(assert " + condition + ")" +
				"(check-sat)" +
				"(pop 1)" +
				"(push 1)" +
				"(assert (not " + condition + "))" +
				"(check-sat)" +
				"(pop 1)";
		}
		
		input += "(exit)";
		
		System.err.println(input);
		
		Boolean[] sat = isSatisfiable(input);
		int i = 0;
		
		for (Predicate predicate : predicates) {
			if (sat[i] && sat[i + 1]) {
				valuation.put(predicate, TruthValue.UNKNOWN);
			} else if (sat[i]) {
				valuation.put(predicate, TruthValue.SATISFIABLE);
			} else if (sat[i + 1]) {
				valuation.put(predicate, TruthValue.UNSATISFIABLE);
			} else {
				valuation.put(predicate, TruthValue.UNDEFINED);
			}
			
			i += 2;
		}
		
		return valuation;
	}
}
