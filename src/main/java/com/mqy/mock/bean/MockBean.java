package com.mqy.mock.bean;


import com.mqy.mock.intercept.MethodInvocation;

/**
 * 可以获取复杂对象
 * @author mengqingyan 2017/8/11
 */
public interface MockBean {
    Object invoke(MethodInvocation invocation) throws Throwable;
}
