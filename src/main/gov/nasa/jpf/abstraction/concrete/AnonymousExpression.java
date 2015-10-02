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
package gov.nasa.jpf.abstraction.concrete;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.state.universe.Reference;

/**
 * Interface of all Objects / Arrays obtained by allocation (or duplicates of such)
 */
public interface AnonymousExpression extends Expression {
    public Reference getReference();

    /**
     * An anonymous expression can be either created by NEW or duplicated by DUP
     *
     * in the latter case it isDuplicate
     */
    public boolean isDuplicate();
}
