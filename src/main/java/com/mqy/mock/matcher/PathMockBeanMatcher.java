package com.mqy.mock.matcher;

import java.lang.reflect.Method;
import java.util.*;

import com.mqy.mock.util.MethodUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;


/**
 * @author mengqingyan 2018/8/6 0006
 */
public class PathMockBeanMatcher implements MockBeanMatcher {

    protected Logger            logger      = LoggerFactory.getLogger(getClass());

    private Map<String, Object> mockBeanMap = new HashMap<>();

    private PathMatcher         pathMatcher = new AntPathMatcher();

    @Override
    public boolean matches(Class<?> proxiedClazz, Method proxiedMethod) {
        String methodPath = MethodUtil.getMethodPath(proxiedClazz, proxiedMethod);
        if (mockBeanMap.containsKey(methodPath)) {
            return true;
        }
        for (String registeredPattern : mockBeanMap.keySet()) {
            if (getPathMatcher().match(registeredPattern, methodPath)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object getMockBean(Class<?> proxiedClazz, Method proxiedMethod) {
        String methodPath = MethodUtil.getMethodPath(proxiedClazz, proxiedMethod);
        Object mockBean = mockBeanMap.get(methodPath);
        if (mockBean == null) {
            List<String> matchingPatterns = new ArrayList<>();
            for (String registeredPattern : this.mockBeanMap.keySet()) {
                if (getPathMatcher().match(registeredPattern, methodPath)) {
                    matchingPatterns.add(registeredPattern);
                }
            }
            String bestPatternMatch = null;
            Comparator<String> patternComparator = getPathMatcher().getPatternComparator(methodPath);
            if (!matchingPatterns.isEmpty()) {
                Collections.sort(matchingPatterns, patternComparator);
                bestPatternMatch = matchingPatterns.get(0);
            }
            if (bestPatternMatch != null) {
                mockBean = this.mockBeanMap.get(bestPatternMatch);
            }
        }
        return mockBean;
    }

    public void setMockBeanMap(Map<String, Object> mockBeanMap) {
        this.mockBeanMap = mockBeanMap;
    }

    protected final void registerMockBean(String path, Object mockBean) {
        this.mockBeanMap.put(path, mockBean);
    }

    public void setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    public PathMatcher getPathMatcher() {
        return pathMatcher;
    }

}
