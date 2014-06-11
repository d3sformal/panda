package gov.nasa.jpf.abstraction.smt;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SMTCache {
    private static SMT.QueryResponse emptyResponse = new SMT.QueryResponse(null, null);

    private Map<String, SMT.QueryResponse> responses = new HashMap<String, SMT.QueryResponse>();

    public void put(String query, SMT.QueryResponse response) {
        responses.put(query, response);
    }

    public SMT.QueryResponse get(String query) {
        if (responses.containsKey(query)) {
            return responses.get(query);
        }

        return emptyResponse;
    }

    public Set<String> getQueries() {
        return responses.keySet();
    }
}
