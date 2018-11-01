package io.volyx.netty.jax.rs.http.rest;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public final class ReflectionUtil {

    private ReflectionUtil() {
    }

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
