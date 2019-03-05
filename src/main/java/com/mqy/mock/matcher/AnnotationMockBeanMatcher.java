package com.mqy.mock.matcher;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import com.mqy.mock.Mockable;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;


/**
 * @author mengqingyan 2018/8/6 0006
 */
public class AnnotationMockBeanMatcher implements MockBeanMatcher, ApplicationContextAware {
    private Set<String>        activeProfileSet;

    private ApplicationContext appCtx;

    @Override
    public boolean matches(Class<?> proxiedClazz, Method proxiedMethod) {
        Mockable mockable = proxiedMethod.getAnnotation(Mockable.class);
        if (mockable == null) {
            mockable = proxiedClazz.getAnnotation(Mockable.class);
        }
        if (mockable != null) {
            String profile = mockable.profile();
            if (this.activeProfileSet.contains(profile)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object getMockBean(Class<?> proxiedClazz, Method proxiedMethod) {
        Mockable mockable = proxiedMethod.getAnnotation(Mockable.class);
        if (mockable == null) {
            mockable = proxiedClazz.getAnnotation(Mockable.class);
        }
        if (mockable != null) {
            String profile = mockable.profile();
            if (this.activeProfileSet.contains(profile)) {
                String mockId = mockable.mockId();
                Object mockBean = appCtx.getBean(mockId);
                return mockBean;
            }
        }

        return null;
    }

    private void initEnvironment(Environment environment) {
        String[] activeProfiles = environment.getActiveProfiles();
        activeProfileSet = new HashSet<>();
        if (activeProfiles != null && activeProfiles.length > 0) {
            for (String profile : activeProfiles) {
                activeProfileSet.add(profile);
            }
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appCtx = applicationContext;
        this.initEnvironment(applicationContext.getEnvironment());
    }
}
