package com.mqy.mock.bean;

import com.mqy.mock.datasource.MultipleDataSource;
import com.mqy.mock.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;


/**
 * @author mengqingyan 2018/10/25
 */
public class NewTransactionMockBean implements MockBean {

    private final Logger        logger = LoggerFactory.getLogger(getClass());

    private String              currentDataSourceKey;

    private String              resetDataSourceKey;

    private TransactionTemplate newTransactionTemplate;

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {

        String dataSourceKey = MultipleDataSource.getDataSourceKey();
        boolean toSetDataSource = true;
        if (StringUtils.equals(currentDataSourceKey, dataSourceKey)) {
            toSetDataSource = false;
        } else {
            MultipleDataSource.setDataSourceKey(currentDataSourceKey);
        }

        Object proceed = null;
        try {
            if (toSetDataSource && TransactionSynchronizationManager.isSynchronizationActive()) {
                proceed = newTransactionTemplate.execute(new TransactionCallback<Object>() {
                    @Override
                    public Object doInTransaction(TransactionStatus status) {
                        try {
                            return invocation.proceed();
                        } catch (Throwable throwable) {
                            logger.error("新事务执行失败", throwable);
                            throw new RuntimeException(throwable);
                        }
                    }
                });
            } else {
                proceed = invocation.proceed();
            }

        } finally {
            if (toSetDataSource) {
                MultipleDataSource.setDataSourceKey(resetDataSourceKey);
            }
        }
        return proceed;
    }

    public void setCurrentDataSourceKey(String currentDataSourceKey) {
        this.currentDataSourceKey = currentDataSourceKey;
    }

    public void setResetDataSourceKey(String resetDataSourceKey) {
        this.resetDataSourceKey = resetDataSourceKey;
    }

    public void setNewTransactionTemplate(TransactionTemplate newTransactionTemplate) {
        this.newTransactionTemplate = newTransactionTemplate;
    }
}
