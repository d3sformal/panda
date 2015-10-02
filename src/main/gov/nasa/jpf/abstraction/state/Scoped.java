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
package gov.nasa.jpf.abstraction.state;

import java.util.Map;

import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * An interface for all structures whose behaviour or data change depending on the current runtime method scope.
 */
public interface Scoped {
    public Scope createDefaultScope(ThreadInfo threadInfo, MethodInfo method);
    public void processMethodCall(ThreadInfo threadInfo, StackFrame before, StackFrame after);
    public void processMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after);
    public void processVoidMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after);
    public void restore(Map<Integer, ? extends Scopes> scopes);
    public Map<Integer, ? extends Scopes> memorize();
    public int count();
    public int depth();
    public Scope get(int depth);
    public void print();
    public void addThread(ThreadInfo threadInfo);
    public void scheduleThread(ThreadInfo threadInfo);
    public void scheduleThread(int threadID);
}
