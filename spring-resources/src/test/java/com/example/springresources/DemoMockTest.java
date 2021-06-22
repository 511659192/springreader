// Copyright (C) 2021 Meituan
// All rights reserved
package com.example.springresources;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author yangmeng
 * @version 1.0
 * @created 2021/6/15 9:55 上午
 **/
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:beans.xml"})
public class DemoMockTest {


    @Autowired
    @Spy
    private DemoService demoService;

    @Test
    public void sayHello() {
        PowerMockito.when(this.demoService.sayHello()).thenReturn("aaaaaaaaaa");
        String s = demoService.sayHello();
        System.out.println("result:" + s);
    }
}