package com.mqy.service;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author mengqingyan 2019/3/5
 */
@ContextConfiguration(locations = { "classpath:/spring-application-mock.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class TestSample {

    @Autowired
    private SampleService sampleService;

    @BeforeClass
    public static void init() {
        System.setProperty("spring.profiles.active", "mockEnable,mockSampleService");
    }


    @Test
    public void testInsert() {

        int insert = sampleService.insert();
        System.out.println("insert res: " + insert);
    }
}
