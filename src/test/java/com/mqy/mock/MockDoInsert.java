package com.mqy.mock;

import com.mqy.mock.bean.MockBean;
import com.mqy.mock.intercept.MethodInvocation;
import org.springframework.stereotype.Component;

/**
 * @author mengqingyan 2019/3/5
 */
@Component("MockDoInsert")
public class MockDoInsert implements MockBean {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        String name = invocation.getMethod().getName();
        System.out.println("mock method: " + name);

        Object proceed = invocation.proceed();

        return Integer.valueOf(proceed.toString()) + 1;
    }
}
