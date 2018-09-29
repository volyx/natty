package io.volyx.netty.jax.rs.http.rest;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public final class ReflectionUtil {

    private ReflectionUtil() {
    }

    /**
     * Used to generate map of class fields where key is field value and value is field name.
     */
//    public static Map<Integer, String> generateMapOfValueNameInteger(Class<?> clazz) {
//        Map<Integer, String> valuesName = new HashMap<>();
//        try {
//            for (Field field : clazz.getFields()) {
//                valuesName.put((Integer) field.get(int.class), field.getName());
//            }
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//        return valuesName;
//    }

    public static Object castTo(Class type, String value) {
        if (type == byte.class || type == Byte.class) {
            return Byte.valueOf(value);
        }
        if (type == short.class || type == Short.class) {
            return Short.valueOf(value);
        }
        if (type == int.class || type == Integer.class) {
            return Integer.valueOf(value);
        }
        if (type == long.class || type == Long.class) {
            return Long.valueOf(value);
        }
        if (type == boolean.class || type == Boolean.class) {
            return Boolean.valueOf(value);
        }
        return value;
    }
}
