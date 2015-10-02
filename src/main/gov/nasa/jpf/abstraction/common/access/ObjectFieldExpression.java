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

import gov.nasa.jpf.abstraction.common.access.meta.Field;

/**
 * Expressions accessing object fields: fread(f, o); fwrite(f, o, e);
 */
public interface ObjectFieldExpression extends ObjectAccessExpression {
    public Field getField();

    @Override
    public ObjectFieldExpression createShallowCopy();
}
