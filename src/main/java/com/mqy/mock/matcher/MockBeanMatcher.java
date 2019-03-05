package com.mqy.mock.matcher;

import java.lang.reflect.Method;

/**
 * @author mengqingyan 2018/8/6 0006
 */
public interface MockBeanMatcher {

    boolean matches(Class<?> proxiedClazz, Method proxiedMethod);

    Object getMockBean(Class<?> proxiedClazz, Method proxiedMethod);
}
