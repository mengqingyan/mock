package com.mqy.mock.bean;


import com.mqy.mock.intercept.MethodInvocation;

/**
 * @author mengqingyan 2017/8/14
 */
public class IntMockBean implements MockBean {
    private int param;

    public void setParam(int param) {
        this.param = param;
    }

    @Override
    public Object invoke(MethodInvocation invocation) {
        return param;
    }
}
