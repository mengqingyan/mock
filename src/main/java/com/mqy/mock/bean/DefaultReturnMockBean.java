package com.mqy.mock.bean;


import com.mqy.mock.intercept.MethodInvocation;

/**
 * @author mengqingyan 2018/8/7 0007
 */
public class DefaultReturnMockBean implements MockBean {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Class<?> returnType = invocation.getMethod().getReturnType();
        Object returnValue = returnType.newInstance();
        return returnValue;
    }
}
