package gov.nasa.jpf.abstraction.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ScopedDefineMap {
    private Stack<Map<String, Object>> scopes = new Stack<Map<String, Object>>();

    public void put(String key, Object value) {
        scopes.peek().put(key, value);
    }

    public Object get(String key) {
        for (int i = scopes.size() - 1; i >= 0; --i) {
            if (scopes.get(i).containsKey(key)) {
                return scopes.get(i).get(key);
            }
        }

        return null;
    }

    public void enterNested() {
        scopes.push(new HashMap<String, Object>());
    }

    public void exitNested() {
        scopes.pop();
    }
}
