package com.mqy.service;

import org.springframework.stereotype.Service;

/**
 * @author mengqingyan 2019/3/5
 */
@Service
public class SampleService {

    public int insert() {
        System.out.println("SampleService-begin-doInsert");
        return doInsert();
    }

    protected int doInsert() {
        System.out.println("SampleService-doInsert");
        return 1;
    }
}
