package com.mqy.mock.tag;

import com.mqy.mock.MockableBean2PostProcessor;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;


/**
 * @author mengqingyan 2018/8/15 0015
 */
public class MockProcessorBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
    @Override
    protected Class<?> getBeanClass(Element element) {
        return MockableBean2PostProcessor.class;
    }
}
