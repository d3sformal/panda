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
package gov.nasa.jpf.abstraction.assertions;

import java.util.HashSet;
import java.util.Set;

public class SameAliasingOnEveryVisitAssertion implements LocationAssertion {
    private Set<AliasingMap> aliasings = new HashSet<AliasingMap>();

    public SameAliasingOnEveryVisitAssertion update(AliasingMap aliasing) {
        if (!aliasings.contains(aliasing)) {
            aliasings.add(aliasing);
        }

        return this;
    }

    @Override
    public boolean isViolated() {
        return aliasings.size() > 1;
    }

    @Override
    public String getError() {
        return "Different aliasings: " + aliasings;
    }
}
