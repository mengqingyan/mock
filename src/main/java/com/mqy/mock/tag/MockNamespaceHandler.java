package com.mqy.mock.tag;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author mengqingyan 2018/8/15 0015
 */
public class MockNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser("mock", new MockBeanDefinitionParser());
        registerBeanDefinitionParser("mock-enable", new MockProcessorBeanDefinitionParser());
    }
}
