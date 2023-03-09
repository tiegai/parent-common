package com.nike.ncp.common.mongo.reflection;

import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.Introspector;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public final class ReflectionUtil {

    private ReflectionUtil() {

    }
    private static Map<SerializableFunction<?, ?>, Field> cache = new ConcurrentHashMap<>();

    public static <E, R> String getFieldName(SerializableFunction<E, R> function) {
        Field field = ReflectionUtil.getField(function);
        return field.getName();
    }

    public static Field getField(SerializableFunction<?, ?> function) {
        return cache.computeIfAbsent(function, ReflectionUtil::findField);
    }

    public static Field findField(SerializableFunction<?, ?> function) {
        Field field = null;
        String fieldName = null;
        try {
            // Step 1 Get SerializedLambda
            Method method = function.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(function);
            // Step 2: implMethodName is the Getter method name corresponding to the Field.
            String implMethodName = serializedLambda.getImplMethodName();
            if (implMethodName.startsWith("get") && implMethodName.length() > 3) {
                fieldName = Introspector.decapitalize(implMethodName.substring(3));

            } else if (implMethodName.startsWith("is") && implMethodName.length() > 2) {
                fieldName = Introspector.decapitalize(implMethodName.substring(2));
            } else if (implMethodName.startsWith("lambda$")) {
                throw new IllegalArgumentException("SerializableFunction cannot pass lambda expressions, only method references can be used.");

            } else {
                throw new IllegalArgumentException(implMethodName + "Is not a Getter method reference");
            }
            // The Class obtained in step 3 is a string, and the package name is split by "/",which needs to be replaced by
            // "."to obtain the corresponding Class object.
            String declaredClass = serializedLambda.getImplClass().replace("/", ".");
            Class<?> aClass = Class.forName(declaredClass, false, ClassUtils.getDefaultClassLoader());

            // Step 4 The reflection tool Class in Spring gets the Field defined in the class.
            field = ReflectionUtils.findField(aClass, fieldName);

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Step 5 If the corresponding field is not found, an exception should be throw.
        if (field != null) {
            return field;
        }
        throw new NoSuchFieldError(fieldName);
    }
}
