// Copyright (C) 2021 Meituan
// All rights reserved
package com.users.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/7 5:08 下午
 **/
@Service
@Data
public class DemoService {

    @Autowired
    private DemoDao demoDao;

    public String hello() {
        return demoDao.query();
    }
}