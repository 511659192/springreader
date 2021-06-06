// Copyright (C) 2021 Meituan
// All rights reserved
package com.example.springsources;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Administrator
 * @version 1.0
 * @created 2021/6/5 22:02
 **/
public class DemoApplication {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        DemoService bean = context.getBean(DemoService.class);
        bean.sayHello();
    }
}