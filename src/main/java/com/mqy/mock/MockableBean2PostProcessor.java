package com.mqy.mock;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import com.mqy.mock.init.InstantiationBean;
import com.mqy.mock.intercept.MockMethodInterceptor;
import com.mqy.mock.matcher.AnnotationMockBeanMatcher;
import com.mqy.mock.matcher.MockBeanMatcher;
import com.mqy.mock.matcher.MultiMockBeanMatcherDelegate;
import com.mqy.mock.matcher.TagPathMockBeanMatcher;
import com.mqy.mock.tag.MockVo;
import com.mqy.mock.util.ProxyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;


/**
 * @author mengqingyan 2018/8/17 0017
 */
public class MockableBean2PostProcessor extends InstantiationAwareBeanPostProcessorAdapter
        implements ApplicationListener<ContextRefreshedEvent>, InitializingBean, Ordered {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private final Set<Object> proxiedBeans = Collections.synchronizedSet(new HashSet<Object>());
    private List<BeanNameVo>             customBeansToAutowire = new ArrayList<>();

    @Autowired
    private ApplicationContext appCtx;

    private MultiMockBeanMatcherDelegate multiMockBeanMatcherDelegate;

    private MockContext mockContext;

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        if (InitializingBean.class.isAssignableFrom(beanClass)) {
            Object wrappedBean = null;
            Object instance = null;
            try {
                instance = beanClass.newInstance();
                wrappedBean = wrapProperly(instance, beanName);
            } catch (Exception e) {
                logger.error("wrap bean error", e);
                throw new RuntimeException(e);
            }
            if(wrappedBean != null) {
                AutowireCapableBeanFactory autowireCapableBeanFactory = appCtx.getAutowireCapableBeanFactory();
                autowireCapableBeanFactory.configureBean(wrappedBean, beanName);
            }
            return wrappedBean;
        }
        return super.postProcessBeforeInstantiation(beanClass, beanName);
    }

    @Override
    public Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
        Object wrappedBean = wrapProperly(bean, beanName);
        if (wrappedBean != null) {
            customBeansToAutowire.add(new BeanNameVo(wrappedBean, beanName));
            return wrappedBean;
        }
        return bean;
    }

    protected Object wrapProperly(Object bean, String beanName) {
        Object cacheKey = getCacheKey(AopUtils.getTargetClass(bean), beanName);
        if (!this.proxiedBeans.contains(cacheKey)) {
            if (toMock(bean)) {
                this.proxiedBeans.add(cacheKey);
                logger.info("begin to mock bean:{}", beanName);
                Object proxy = getProxy(bean);
                return proxy;
            }
        }
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean != null) {
            Object wrappedBean = wrapProperly(bean, beanName);
            if (wrappedBean != null) {
                AutowireCapableBeanFactory autowireCapableBeanFactory = appCtx.getAutowireCapableBeanFactory();
                autowireCapableBeanFactory.applyBeanPropertyValues(wrappedBean, beanName);
                autowireCapableBeanFactory.autowireBean(wrappedBean);
                return wrappedBean;
            }
        }
        return bean;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initMockBase();
        initInstantiationBean();

    }

    private void initInstantiationBean() {
        Map<String, InstantiationBean> instantiationBeansOfType = this.appCtx.getBeansOfType(InstantiationBean.class);
        for (InstantiationBean instantiationBean : instantiationBeansOfType.values()) {
            instantiationBean.init();
        }
    }

    private void initMockBase() throws Exception {

        TagPathMockBeanMatcher tagPathMockBeanMatcher = createBean(TagPathMockBeanMatcher.class);
        Map<String, MockVo> mockVoMap = appCtx.getBeansOfType(MockVo.class);
        tagPathMockBeanMatcher.registerMockTags(mockVoMap.values());

        AnnotationMockBeanMatcher annotationMockBeanMatcher = createBean(AnnotationMockBeanMatcher.class);

        this.multiMockBeanMatcherDelegate = new MultiMockBeanMatcherDelegate();
        List<MockBeanMatcher> mockBeanMatchers = new ArrayList<>();
        mockBeanMatchers.add(tagPathMockBeanMatcher);
        mockBeanMatchers.add(annotationMockBeanMatcher);
        this.multiMockBeanMatcherDelegate.registerMockBeanMatcher(mockBeanMatchers);

        this.mockContext = new MockContext();
        this.mockContext.setMultiMockBeanMatcherDelegate(multiMockBeanMatcherDelegate);
    }

    private <T> T createBean(Class<T> clazz) throws Exception {
        T bean = clazz.newInstance();
        if (bean instanceof ApplicationContextAware) {
            ApplicationContextAware appCtxAware = (ApplicationContextAware) bean;
            appCtxAware.setApplicationContext(this.appCtx);
        }
        if (bean instanceof InitializingBean) {
            InitializingBean initializingBean = (InitializingBean) bean;
            initializingBean.afterPropertiesSet();
        }
        return bean;
    }

    protected Object getCacheKey(Class<?> beanClass, String beanName) {
        return beanClass.getName() + "_" + beanName;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    protected Object getProxy(Object bean) {
        MockMethodInterceptor mockMethodInterceptor = new MockMethodInterceptor(bean, mockContext);
        return ProxyUtil.getProxy(bean.getClass(), mockMethodInterceptor);
    }

    protected boolean toMock(Object bean) {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        Class<?> tmpClass = targetClass;
        while (tmpClass != Object.class) {
            Method[] declaredMethods = tmpClass.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                int modifiers = declaredMethod.getModifiers();
                if (Modifier.isAbstract(modifiers) || Modifier.isNative(modifiers) || Modifier.isFinal(modifiers)
                        || Modifier.isPrivate(modifiers)) {
                    continue;
                }
                if (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers)) {
                    if (this.multiMockBeanMatcherDelegate.matches(targetClass, declaredMethod)) {
                        return true;
                    }
                }
            }
            tmpClass = tmpClass.getSuperclass();
        }
        return false;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        AutowireCapableBeanFactory autowireCapableBeanFactory = appCtx.getAutowireCapableBeanFactory();
        for (MockableBean2PostProcessor.BeanNameVo beanNameVo : customBeansToAutowire) {
            autowireCapableBeanFactory.autowireBean(beanNameVo.bean);
            autowireCapableBeanFactory.applyBeanPropertyValues(beanNameVo.bean, beanNameVo.beanName);
        }
    }

    private static final class BeanNameVo {
        private final Object bean;

        private final String beanName;

        public BeanNameVo(Object bean, String beanName) {
            this.bean = bean;
            this.beanName = beanName;
        }
    }
}
