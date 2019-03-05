package com.mqy.mock.intercept;

import java.lang.reflect.Method;

/**
 * @author mengqingyan 2017/8/18
 */
public interface MethodInvocation {

    Method getMethod();

    Object[] getArguments();

    Object proceed() throws Throwable;

    Object proceed(Object[] args) throws Throwable;

    Object getProxy();
}
