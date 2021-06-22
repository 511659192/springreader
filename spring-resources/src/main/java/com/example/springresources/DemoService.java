// Copyright (C) 2021 Meituan
// All rights reserved
package com.example.springresources;

import org.springframework.stereotype.Component;

/**
 * @author Administrator
 * @version 1.0
 * @created 2021/6/5 22:00
 **/

@Component
public class DemoService {

    public String sayHello() {
        System.out.println("hello world");

        return "aaaa";
    }


    public String mockMethod(String bb) {
        return "mockMethod:" + bb;
    }
}