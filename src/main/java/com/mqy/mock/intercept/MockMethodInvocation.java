package com.mqy.mock.intercept;

import java.lang.reflect.Method;

/**
 * @author mengqingyan 2017/8/18
 */
public abstract class MockMethodInvocation implements MethodInvocation {

    private final Method   method;

    private final Object[] args;

    private final Object   target;

    public MockMethodInvocation(Method method, Object[] args, Object target) {
        this.method = method;
        this.args = args;
        this.target = target;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object[] getArguments() {
        return args;
    }


}
