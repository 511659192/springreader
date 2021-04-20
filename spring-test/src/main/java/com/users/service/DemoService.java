// Copyright (C) 2021 Meituan
// All rights reserved
package com.users.service;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.StringJoiner;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/7 5:08 下午
 **/
@Service
@Data
public class DemoService {

    private String name;

    public String hello() {
        return "world";
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DemoService.class.getSimpleName() + "[", "]").add("name='" + name + "'").toString();
    }
}