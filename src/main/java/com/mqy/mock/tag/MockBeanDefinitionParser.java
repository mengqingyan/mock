package com.mqy.mock.tag;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

/**
 * @author mengqingyan 2018/8/15 0015
 */
public class MockBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
    @Override
    protected Class<?> getBeanClass(Element element) {
        return MockVo.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        String pattern = element.getAttribute("pattern");
        String mockBean = element.getAttribute("mockBean");
        builder.addPropertyValue("pattern", pattern);
        builder.addPropertyValue("mockBean", mockBean);
    }

    @Override
    protected boolean shouldGenerateIdAsFallback() {
        return true;
    }
}
