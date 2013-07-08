package gov.nasa.jpf.abstraction.predicate.smt;

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
import java.util.List;
import java.util.Map;

public class SMT {
	
	private BufferedWriter in = null;
	private BufferedReader out = null;
	
	public SMT() throws IOException {
		try {
			String[] args = new String[] {System.getProperty("user.dir") + "/bin/mathsat"};
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
				System.err.println("'" + output + "' " + output.matches("^sat$"));
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
	
	public Map<Predicate, TruthValue> valuatePredicates(List<Predicate> predicates) throws IOException {
		Map<Predicate, TruthValue> valuation = new HashMap<Predicate, TruthValue>();

		String input = "";

		for (Predicate predicate : predicates) {
			//TODO
			String condition = "true";
			
			input +=
				" (push 1)" +
				" (assert " + condition + ")" +
				" (check-sat)" +
				" (pop 1)" +
				" (push 1)" +
				" (assert (not " + condition + "))" +
				" (check-sat)" +
				" (pop 1)";
		}
		
		input += " (exit)";
		
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
