package com.mqy.mock.intercept;

import java.lang.reflect.Method;

import com.mqy.mock.MockContext;
import org.springframework.aop.support.AopUtils;


import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * @author mengqingyan 2018/8/17 0017
 */
public class MockMethodInterceptor implements MethodInterceptor {

    private Object      target;

    private MockContext mockContext;

    public MockMethodInterceptor(Object target, MockContext mockContext) {
        this.target = target;
        this.mockContext = mockContext;
    }

    public Object intercept(final Object obj, final Method method, final Object[] args, final MethodProxy proxy)
            throws Throwable {

        Class<?> declaringClass = method.getDeclaringClass();
        if (declaringClass.equals(Object.class)) {
            return proxy.invokeSuper(obj, args);
        }
        final Class<?> targetClass = AopUtils.getTargetClass(target);
        MethodInvocation methodInvocation = new MockMethodInvocation(method, args, target) {
            @Override
            public Object proceed() throws Throwable {
                return proxy.invokeSuper(obj, args);
            }

            @Override
            public Object proceed(Object[] args) throws Throwable {
                return proxy.invokeSuper(obj, args);
            }

            @Override
            public Object getProxy() {
                return obj;
            }
        };

        Object result = mockContext.invokeMock(targetClass, method, methodInvocation);
        return result;
    }
}