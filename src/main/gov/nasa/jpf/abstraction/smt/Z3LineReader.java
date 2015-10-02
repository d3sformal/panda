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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;

import gov.nasa.jpf.abstraction.PandaConfig;
import gov.nasa.jpf.abstraction.common.PredicatesFactory;

public class Z3LineReader implements Closeable {
    private InputStreamReader is;
    private int par = 0;

    private int len = 0;
    private int size = 1;
    private char[] buf = new char[size];

    public Z3LineReader(InputStreamReader is) {
        this.is = is;
    }

    public String readLine() throws IOException {
        while (read()) {
            if (par == 0) {
                switch (buf[len - 1]) {
                    case '\n':
                        return out();
                    default:
                }
            }
        }

        if (len > 0) {
            return out();
        }

        return null;
    }

    private String out() {
        String s = new String(buf, 0, len - 1);
        len = 0;

        if (PandaConfig.getInstance().enabledVerbose(this.getClass())) {
            System.out.println("Z3: " + s);
        }

        return s;
    }

    private boolean read() throws IOException {
        if (len == size) {
            char[] buffer = new char[size *= 2];

            for (int i = 0; i < len; ++i) {
                buffer[i] = buf[i];
            }

            buf = buffer;
        }

        int c = is.read();

        switch (c) {
            case '(':
                ++par;
                break;
            case ')':
                --par;
                break;
            case -1:
                return false;
            default:
        }

        buf[len++] = (char)c;

        return true;
    }

    @Override
    public void close() throws IOException {
        is.close();
    }

    public static void main(String[] args) throws Exception {
        Z3LineReader r = new Z3LineReader(new java.io.FileReader(args[0]));

        String line;

        while ((line = r.readLine()) != null) {
            System.out.println("Z3: " + line);
            if (!line.equals("unsat")) {
                System.out.println("Panda: " + PredicatesFactory.createInterpolantFromString(line));
            }
        }
    }
}
