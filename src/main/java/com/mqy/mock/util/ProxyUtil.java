package com.mqy.mock.util;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import org.springframework.util.ClassUtils;

/**
 * @author mengqingyan 2018/8/17 0017
 */
public class ProxyUtil {

    public static <T> T getProxy(Class<T> clazz, Callback callback) {
        Class proxySuperClass = clazz;
        if (ClassUtils.isCglibProxyClass(clazz)) {
            proxySuperClass = clazz.getSuperclass();
        }

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(proxySuperClass);
        enhancer.setCallback(callback);
        return (T) enhancer.create();
    }
}
