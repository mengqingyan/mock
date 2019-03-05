package com.mqy.mock.bean;

import java.util.List;

import com.mqy.mock.datasource.MultipleDataSource;
import com.mqy.mock.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/**
 * @author mengqingyan 2018/10/25
 */
public class RouteDataSourceMockBean implements MockBean {

    private String currentDataSourceKey;

    private String resetDataSourceKey;


    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        String dataSourceKey = MultipleDataSource.getDataSourceKey();
        boolean toSetDataSource = true;
        boolean synchronizationActive = TransactionSynchronizationManager.isSynchronizationActive();
        List<TransactionSynchronization> synchronizations = null;
        if (StringUtils.equals(currentDataSourceKey, dataSourceKey)) {
            toSetDataSource = false;
        } else {
            MultipleDataSource.setDataSourceKey(currentDataSourceKey);
            if (synchronizationActive) {
                synchronizations = TransactionSynchronizationManager.getSynchronizations();
                for (TransactionSynchronization synchronization : synchronizations) {
                    synchronization.suspend();
                }
                TransactionSynchronizationManager.clearSynchronization();
                TransactionSynchronizationManager.initSynchronization();
            }
        }

        Object proceed;
        try {
            proceed = invocation.proceed();
        } finally {
            if (toSetDataSource) {
                MultipleDataSource.setDataSourceKey(resetDataSourceKey);
                if (synchronizationActive) {
                    TransactionSynchronizationManager.clearSynchronization();
                    TransactionSynchronizationManager.initSynchronization();

                    for (TransactionSynchronization synchronization : synchronizations) {
                        try {
                            synchronization.resume();
                        } catch (Exception e) {
                            synchronization.suspend();
                            synchronization.resume();
                        }
                        TransactionSynchronizationManager.registerSynchronization(synchronization);
                    }
                }
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

}
