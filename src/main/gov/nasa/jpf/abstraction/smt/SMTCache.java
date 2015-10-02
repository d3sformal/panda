/*
 * Copyright (C) 2015, Charles University in Prague.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
