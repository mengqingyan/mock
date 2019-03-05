package com.mqy.mock;

import java.lang.annotation.*;

/**
 * 标注需要Mock的方法
 * @author mengqingyan 2017/8/8
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mockable {

    /**
     * 指定profile
     * @return
     */
    String profile();

    /**
     * mock标志
     * @return
     */
    String mockId();
}
