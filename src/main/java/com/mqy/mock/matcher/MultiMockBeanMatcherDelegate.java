package com.mqy.mock.matcher;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mengqingyan 2018/8/6 0006
 */
public class MultiMockBeanMatcherDelegate implements MockBeanMatcher {

    private List<MockBeanMatcher> mockBeanMatcherList = new ArrayList<>();

    @Override
    public boolean matches(Class<?> proxiedClazz, Method proxiedMethod) {
        for (MockBeanMatcher mockBeanMatcher : mockBeanMatcherList) {
            if (mockBeanMatcher.matches(proxiedClazz, proxiedMethod)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object getMockBean(Class<?> proxiedClazz, Method proxiedMethod) {
        for (MockBeanMatcher mockBeanMatcher : mockBeanMatcherList) {
            Object mockBean = mockBeanMatcher.getMockBean(proxiedClazz, proxiedMethod);
            if (mockBean != null) {
                return mockBean;
            }
        }
        return null;
    }

    public void registerMockBeanMatcher(List<MockBeanMatcher> mockBeanMatchers) {
        mockBeanMatcherList.addAll(mockBeanMatchers);
    }

}
