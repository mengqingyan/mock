package com.mqy.mock.matcher;

import java.lang.reflect.Method;
import java.util.Collection;

import com.mqy.mock.tag.MockVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * @author mengqingyan 2018/8/15 0015
 */
public class TagPathMockBeanMatcher extends PathMockBeanMatcher implements ApplicationContextAware {
    private ApplicationContext appCtx;

    public void registerMockTags(Collection<MockVo> mockVos) {
        for (MockVo mockVo : mockVos) {
            String mockBeanId = mockVo.getMockBean();
            super.registerMockBean(mockVo.getPattern(), mockBeanId);
        }
    }

    @Override
    public Object getMockBean(Class<?> proxiedClazz, Method proxiedMethod) {
        String mockBeanId = (String) super.getMockBean(proxiedClazz, proxiedMethod);
        if (StringUtils.isBlank(mockBeanId)) {
            return null;
        }
        Object mockBean = appCtx.getBean(mockBeanId);
        return mockBean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appCtx = applicationContext;
    }
}
