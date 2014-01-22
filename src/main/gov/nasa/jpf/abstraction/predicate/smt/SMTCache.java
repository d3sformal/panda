package gov.nasa.jpf.abstraction.predicate.smt;

import java.util.Map;
import java.util.HashMap;

public class SMTCache {
    private Map<String, Boolean> responses = new HashMap<String, Boolean>();

    public void put(String query, Boolean response) {
        responses.put(query, response);
    }

    public Boolean get(String query) {
        return responses.get(query);
    }
}
