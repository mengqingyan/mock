package com.mqy.mock.bean;

import com.mqy.mock.intercept.MethodInvocation;

import javax.sql.DataSource;


/**
 * @author mengqingyan 2018/10/25
 */
public class DataSourceMockBean implements MockBean {

    private DataSource dataSource;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return invocation.proceed(new Object[]{dataSource});
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
