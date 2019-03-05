package com.mqy.mock.bean;


import com.mqy.mock.intercept.MethodInvocation;

/**
 * @author mengqingyan 2017/8/14
 */
public class DoubleMockBean implements MockBean {
    private double param;

    public void setParam(double param) {
        this.param = param;
    }

    @Override
    public Object invoke(MethodInvocation invocation) {
        return param;
    }
}
