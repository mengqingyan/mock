package com.mqy.mock.bean;


import com.mqy.mock.intercept.MethodInvocation;

/**
 * @author mengqingyan 2017/8/14
 */
public class FloatMockBean implements MockBean {
    private float param;

    public void setParam(float param) {
        this.param = param;
    }

    @Override
    public Object invoke(MethodInvocation invocation) {
        return param;
    }
}
