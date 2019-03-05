package com.mqy.mock.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mengqingyan on 2017/8/13.
 */
public class MethodUtil {

    public static List<Method> getClassAllPublicProtectedMethodExcludeObject(Class<?> clazz) {
        List<Method> methods = new ArrayList<>();
        Class<?> tmpClazz = clazz;
        while (tmpClazz != Object.class) {
            Method[] declaredMethods = tmpClazz.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                int modifiers = declaredMethod.getModifiers();
                if (Modifier.isPrivate(modifiers)) {
                    continue;
                }
                methods.add(declaredMethod);
            }
            tmpClazz = tmpClazz.getSuperclass();
        }
        return methods;
    }

    public static String getMethodPath(Class<?> proxiedClazz, Method proxiedMethod) {
        StringBuilder pathBuilder = new StringBuilder();
        int mod = proxiedMethod.getModifiers() & Modifier.methodModifiers();
        if (mod != 0) {
            pathBuilder.append(Modifier.toString(mod)).append("/");
        }
        pathBuilder.append(getTypeName(proxiedMethod.getReturnType())).append("/")
                .append(proxiedClazz.getCanonicalName()).append("/").append(proxiedMethod.getName());
        String fullMethodName = pathBuilder.toString();
        return org.apache.commons.lang3.StringUtils.replace(fullMethodName, ".", "/");
    }

    private static String getTypeName(Class<?> type) {
        if (type.isArray()) {
            try {
                Class<?> cl = type;
                int dimensions = 0;
                while (cl.isArray()) {
                    dimensions++;
                    cl = cl.getComponentType();
                }
                StringBuffer sb = new StringBuffer();
                sb.append(cl.getName());
                for (int i = 0; i < dimensions; i++) {
                    sb.append("[]");
                }
                return sb.toString();
            } catch (Throwable e) {
                System.err.println("getTypeName异常：" + e.getMessage());
                throw e;
            }
        }
        return type.getSimpleName();
    }
}
