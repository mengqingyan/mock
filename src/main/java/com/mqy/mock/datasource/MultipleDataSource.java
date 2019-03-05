package com.mqy.mock.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author mengqingyan 2018/5/14 0014
 */
public class MultipleDataSource extends AbstractRoutingDataSource {

    private static final Logger logger  = LoggerFactory.getLogger(MultipleDataSource.class);

    private static final ThreadLocal<String>           dataSourceKey         = new InheritableThreadLocal<>();



    public static void setDataSourceKey(String dataSource) {
        dataSourceKey.set(dataSource);
    }

    public static String getDataSourceKey() {
        return dataSourceKey.get();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String s = dataSourceKey.get();
        logger.info("switch to datasource: " + s);
        return s;
    }


}
