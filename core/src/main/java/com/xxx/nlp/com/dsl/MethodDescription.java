package com.xxx.nlp.com.dsl;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MethodDescription {
    private static final ThreadLocal<Map<String, Object>> tl = new ThreadLocal<>();


    public static void main(String[] args) {
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("c", "countValue");
        varMap.put("n", "nameValue");

        tl.set(varMap);

        MethodDescription md = new MethodDescription();
        for (Method method : MethodDescription.class.getDeclaredMethods()) {
            //filter method by:Annotation(Path...)
            if (Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())) {
                Object result = null;
                try {
                    result = md.invoke(method, md);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(result);
            } else {
                System.err.println("ignore>" + method.getName());
            }
        }
    }

    protected Object invoke(Method method, Object target) throws InvocationTargetException, IllegalAccessException {
//        Parameter[] parameters = method.getParameters();
        Object[] args = Arrays.stream(method.getParameters()).map((param) -> {
            return getFieldValue(param);
        }).toArray();

//        for (Parameter param : parameters) {
//            Object result = getFieldValue(param);
//        }

        return method.invoke(target, args);
    }

    protected Object getFieldValue(Parameter param) {
        Annotation[] anno = param.getAnnotations();
        Object[] result = Arrays.stream(anno).filter((a) -> {
            return Param.class.equals(a.annotationType());
        }).map((a) -> {
            return fetchValue(a);
        }).toArray();

        if (result.length == 1) {
            return result[0];
        } else {
            throw new IllegalStateException("多个Annotation");
        }
    }

    protected Object fetchValue(Annotation annotation) {
        if (annotation instanceof Param) {
            String name = ((Param) annotation).value();
            Map<String, Object> varMap = tl.get();
            return varMap.get(name);
        } else {
            return "None";
        }
    }

    public String helloWorld(@Param("n") String name, @Param("c") String count) {
        return "hello:" + name + ",count:" + count;
    }
}
