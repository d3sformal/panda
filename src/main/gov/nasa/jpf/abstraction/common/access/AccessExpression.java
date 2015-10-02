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
package gov.nasa.jpf.abstraction.common.access;

import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.common.Expression;

/**
 * Access expressions:
 *  a.b.c
 *  a[i]
 *  p.q.C.d[e].f
 */
public interface AccessExpression extends Expression {
    public void addAccessSubExpressionsToSet(Set<AccessExpression> out);

    @Override
    public void addAccessExpressionsToSet(Set<AccessExpression> out);

    public AccessExpression replaceSubExpressions(Map<AccessExpression, Expression> replacements);
    public void addAllPrefixesToSet(Set<AccessExpression> out);

    public Root getRoot();
    public AccessExpression get(int depth);

    public boolean isThis();
    public boolean isStatic();
    public boolean isLocalVariable();
    public boolean isReturnValue();
    public boolean isPrefixOf(AccessExpression expression);
    public boolean isSimilarToPrefixOf(AccessExpression path);
    public boolean isProperPrefixOf(AccessExpression expression);

    public int getLength();
    public AccessExpression cutTail();
    public AccessExpression reRoot(AccessExpression newPrefix);
    public AccessExpression reRoot(AccessExpression oldPrefix, AccessExpression newPrefix);

    public AccessExpression createShallowCopy();

    /**
     * Comparison of two access expressions ignoring indices, arrays
     */
    public boolean isEqualTo(AccessExpression expression);
    public boolean isEqualToSlow(AccessExpression expression);

    public boolean isSimilarTo(AccessExpression expression);
    public boolean isSimilarToSlow(AccessExpression expression);
}
