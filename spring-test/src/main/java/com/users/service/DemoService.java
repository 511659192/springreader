// Copyright (C) 2021 Meituan
// All rights reserved
package com.users.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/7 5:08 下午
 **/
@Component
@Data
public class DemoService {

    @Autowired
    private DemoDao demoDao;


    public DemoDao getDemoDao() {
        return demoDao;
    }

    public void setDemoDao(DemoDao demoDao) {
        this.demoDao = demoDao;
    }

    public String hello() {
        return demoDao.query();
    }
}