package com.mqy.mock.bean;


import com.mqy.mock.intercept.MethodInvocation;

/**
 * @author mengqingyan 2017/8/14
 */
public class BooleanMockBean implements MockBean {
    private String param = "false";

    public void setParam(String param) {
        this.param = param;
    }

    @Override
    public Object invoke(MethodInvocation invocation) {
        if (param.equals("true")) {
            return true;
        } else {
            return false;
        }
    }
}
