package com.mqy.mock.init;

/**
 * 用于提前初始化bean。可以优先于某些bean，提前初始化
 * @author mengqingyan 2018/12/7
 */
public interface InstantiationBean {

    void init();
}
