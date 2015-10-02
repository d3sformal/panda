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
