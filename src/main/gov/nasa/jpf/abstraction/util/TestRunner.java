package gov.nasa.jpf.abstraction.util;

import java.lang.reflect.Method;

public class TestRunner {
    public static void main(String[] args) throws Throwable {
        String test = args[0];

        Class<?> cls = Class.forName(test);
        Method bootstrap = cls.getMethod("bootstrap");

        Object obj = cls.newInstance();

        try {
            bootstrap.invoke(obj);
        } catch (Throwable e) {
            throw e.getCause();
        }
    }
}
