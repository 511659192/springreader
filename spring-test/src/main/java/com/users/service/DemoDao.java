// Copyright (C) 2021 Meituan
// All rights reserved
package com.users.service;

import org.springframework.stereotype.Repository;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/7 5:08 下午
 **/
@Repository
public class DemoDao {

    public String query() {
        return "world";
    }
}