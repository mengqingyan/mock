package com.mqy.mock.bean;

import com.mqy.mock.intercept.MethodInvocation;

import java.lang.reflect.Method;


/**
 * @author mengqingyan 2018/8/7 0007
 */
public class ReflectionMockBean implements MockBean {

    private final Object beanOfCustomMock;

    public ReflectionMockBean(Object beanOfCustomMock) {
        this.beanOfCustomMock = beanOfCustomMock;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object[] arguments = invocation.getArguments();
        Method method = invocation.getMethod();
        Method methodOfTarget = beanOfCustomMock.getClass().getMethod(method.getName(), method.getParameterTypes());

        Object result = methodOfTarget.invoke(beanOfCustomMock, arguments);
        return result;
    }
}
