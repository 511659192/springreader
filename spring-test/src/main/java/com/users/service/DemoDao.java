// Copyright (C) 2021 Meituan
// All rights reserved
package com.users.service;

import org.springframework.stereotype.Component;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/7 5:08 下午
 **/
@Component
public class DemoDao {

    public String query() {
        return "world";
    }
}