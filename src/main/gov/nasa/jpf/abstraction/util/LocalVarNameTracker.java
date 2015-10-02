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

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.jvm.bytecode.ALOAD;
import gov.nasa.jpf.jvm.bytecode.ASTORE;

public class LocalVarNameTracker extends ListenerAdapter 
{
    @Override
    public void instructionExecuted(VM vm, ThreadInfo curTh, Instruction nextInsn, Instruction execInsn) 
    {
        String v1 = null;
        String v2 = null;
        String v3 = null;
        String v4 = null;

        if (execInsn instanceof ALOAD)
        {
            ALOAD loadInsn = (ALOAD) execInsn;

            try { 
                v1 = loadInsn.
                    getLocalVarInfo().
                    getName(); 
            } 
            catch (NullPointerException e) 
            {
                System.out.println("v1: NPE"); 
            }

            try { 
                v2 = curTh.
                    getTopFrame().
                    getLocalVarInfo(loadInsn.
                            getLocalVariableIndex()
                        ).
                    getName(); 
            } 
            catch (NullPointerException e) 
            { 
                System.out.println("v2: NPE"); 
            }

            try
            {
                v3 = loadInsn.
                    getMethodInfo().
                    getLocalVars()[loadInsn.
                            getLocalVariableIndex()
                        ].
                    getName();
            }
            catch (NullPointerException e) 
            { 
                System.out.println("v3: NPE"); 
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
                System.out.println("v3: AIOOB");
            }


            try
            {
                v4 = loadInsn.getLocalVariableName();
            }
            catch (NullPointerException e) 
            { 
                System.out.println("v4: NPE"); 
            }

            System.out.println("[POST load] v1 = " + v1 + ", v2 = " + v2 + ", v3 = " + v3 + ", v4 = " + v4); 
        }

        if (execInsn instanceof ASTORE)
        {
            ASTORE storeInsn = (ASTORE) execInsn;

            try { 
                v1 = storeInsn.
                    getLocalVarInfo().
                    getName(); 
            } 
            catch (NullPointerException e) 
            {
                System.out.println("v1: NPE"); 
            }

            try { 
                v2 = curTh.
                    getTopFrame().
                    getLocalVarInfo(storeInsn.
                            getLocalVariableIndex()
                        ).
                    getName(); 
            } 
            catch (NullPointerException e) 
            { 
                System.out.println("v2: NPE"); 
            }

            try
            {
                v3 = storeInsn.
                    getMethodInfo().
                    getLocalVars()[storeInsn.
                            getLocalVariableIndex()
                        ].
                    getName();
            }
            catch (NullPointerException e) 
            { 
                System.out.println("v3: NPE"); 
            }

            try
            {
                v4 = storeInsn.getLocalVariableName();
            }
            catch (NullPointerException e) 
            { 
                System.out.println("v4: NPE"); 
            }

            System.out.println("[POST store] v1 = " + v1 + ", v2 = " + v2 + ", v3 = " + v3 + ", v4 = " + v4); 
        }
    }

    public void executeInstruction(VM vm, ThreadInfo curTh, Instruction insn) 
    {
        String v1 = null;
        String v2 = null;
        String v3 = null;
        String v4 = null;

        if (insn instanceof ALOAD)
        {
            ALOAD loadInsn = (ALOAD) insn;

            try { 
                v1 = loadInsn.
                    getLocalVarInfo().
                    getName(); 
            } 
            catch (NullPointerException e) 
            {
                System.out.println("v1: NPE"); 
            }

            try { 
                v2 = curTh.
                    getTopFrame().
                    getLocalVarInfo(loadInsn.
                            getLocalVariableIndex()
                        ).
                    getName(); 
            } 
            catch (NullPointerException e) 
            { 
                System.out.println("v2: NPE"); 
            }

            try
            {
                v3 = loadInsn.
                    getMethodInfo().
                    getLocalVars()[loadInsn.
                            getLocalVariableIndex()
                        ].
                    getName();
            }
            catch (NullPointerException e) 
            { 
                System.out.println("v3: NPE"); 
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
                System.out.println("v3: AIOOB");
            }

            try
            {
                v4 = loadInsn.getLocalVariableName();
            }
            catch (NullPointerException e) 
            { 
                System.out.println("v4: NPE"); 
            }

            System.out.println("[PRE load] v1 = " + v1 + ", v2 = " + v2 + ", v3 = " + v3 + ", v4 = " + v4); 
        }

        if (insn instanceof ASTORE)
        {
            ASTORE storeInsn = (ASTORE) insn;

            try { 
                v1 = storeInsn.
                    getLocalVarInfo().
                    getName(); 
            } 
            catch (NullPointerException e) 
            {
                System.out.println("v1: NPE"); 
            }

            try { 
                v2 = curTh.
                    getTopFrame().
                    getLocalVarInfo(storeInsn.
                            getLocalVariableIndex()
                        ).
                    getName(); 
            } 
            catch (NullPointerException e) 
            { 
                System.out.println("v2: NPE"); 
            }

            try
            {
                v3 = storeInsn.
                    getMethodInfo().
                    getLocalVars()[storeInsn.
                            getLocalVariableIndex()
                        ].
                    getName();
            }
            catch (NullPointerException e) 
            { 
                System.out.println("v3: NPE"); 
            }

            try
            {
                v4 = storeInsn.getLocalVariableName();
            }
            catch (NullPointerException e) 
            { 
                System.out.println("v4: NPE"); 
            }

            System.out.println("[PRE store] v1 = " + v1 + ", v2 = " + v2 + ", v3 = " + v3 + ", v4 = " + v4); 
        }
    }
}

