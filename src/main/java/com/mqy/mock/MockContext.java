package com.mqy.mock;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.mqy.mock.bean.MockBean;
import com.mqy.mock.bean.ReflectionMockBean;
import com.mqy.mock.intercept.MethodInvocation;
import com.mqy.mock.matcher.MultiMockBeanMatcherDelegate;
import com.mqy.mock.util.MethodUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author mengqingyan 2017/8/14
 */
public class MockContext {
    private Logger                       logger  = LoggerFactory.getLogger(getClass());

    private MultiMockBeanMatcherDelegate multiMockBeanMatcherDelegate;

    private Map<CacheKey, Object>        mockMap = new ConcurrentHashMap<>();

    private Object                       empty   = new Object();

    public Object invokeMock(Class<?> proxiedClazz, Method proxiedMethod, MethodInvocation invocation)
            throws Throwable {


        Object mockObj = this.getMockObject(proxiedClazz, proxiedMethod);
        if (mockObj == null || mockObj == empty) {
            return invocation.proceed();
        }
        if(logger.isDebugEnabled()) {
            logger.debug(proxiedMethod.toGenericString() + ":Using Mock!");
        }

        if (mockObj instanceof MockBean) {
            MockBean mockBean = (MockBean) mockObj;
            return mockBean.invoke(invocation);
        }
        List<Method> mockClassMethods = MethodUtil.getClassAllPublicProtectedMethodExcludeObject(mockObj.getClass());
        if (hasTheMethod(proxiedMethod, mockClassMethods)) {
            MockBean reflectionMockBean = new ReflectionMockBean(mockObj);
            CacheKey cacheKey = new CacheKey(proxiedClazz, proxiedMethod);
            mockMap.put(cacheKey, reflectionMockBean);
            return reflectionMockBean.invoke(invocation);
        }
        if (hasSomeMethod(proxiedClazz, mockClassMethods)) {
            CacheKey cacheKey = new CacheKey(proxiedClazz, proxiedMethod);
            mockMap.put(cacheKey, empty);
            return invocation.proceed();
        }
        return mockObj;
    }

    private boolean hasSomeMethod(Class<?> proxiedClazz, List<Method> mockClassMethods) {
        List<Method> proxiedClassMethods = MethodUtil.getClassAllPublicProtectedMethodExcludeObject(proxiedClazz);
        for (Method proxiedMethod : proxiedClassMethods) {
            if (hasTheMethod(proxiedMethod, mockClassMethods)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasTheMethod(Method proxiedMethod, List<Method> mockClassMethods) {
        String proxiedMethodName = proxiedMethod.getName();
        for (Method mockClassMethod : mockClassMethods) {
            if (proxiedMethodName.equals(mockClassMethod.getName()) && hasSameArgType(proxiedMethod, mockClassMethod)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasSameArgType(Method proxiedMethod, Method mockMethod) {
        Class<?>[] proxyParameterTypes = proxiedMethod.getParameterTypes();
        Class<?>[] mockParameterTypes = mockMethod.getParameterTypes();
        if (proxyParameterTypes.length != mockParameterTypes.length) {
            return false;
        }
        for (int i = 0; i < proxyParameterTypes.length; i++) {
            if (!proxyParameterTypes[i].equals(mockParameterTypes[i])) {
                return false;
            }
        }
        return true;
    }

    private Object getMockObject(Class<?> proxiedClazz, Method proxiedMethod) {
        CacheKey cacheKey = new CacheKey(proxiedClazz, proxiedMethod);

        Object o = mockMap.get(cacheKey);
        if (o != null) {
            return o;
        }

        o = multiMockBeanMatcherDelegate.getMockBean(proxiedClazz, proxiedMethod);

        if (o == null) {
            o = empty;
        }
        mockMap.put(cacheKey, o);
        return o;
    }

    public void setMultiMockBeanMatcherDelegate(MultiMockBeanMatcherDelegate multiMockBeanMatcherDelegate) {
        this.multiMockBeanMatcherDelegate = multiMockBeanMatcherDelegate;
    }

    private final class CacheKey {
        private final Class<?> clazz;

        private final Method   method;

        public CacheKey(Class<?> clazz, Method method) {
            this.clazz = clazz;
            this.method = method;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            CacheKey cacheKey = (CacheKey) o;
            return Objects.equals(clazz, cacheKey.clazz) && Objects.equals(method, cacheKey.method);
        }

        @Override
        public int hashCode() {

            return Objects.hash(clazz, method);
        }
    }
}
