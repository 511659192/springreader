// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.util;

import com.google.gson.Gson;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/8 2:19 下午
 **/
public class GsonUtils {

    private final static Gson GSON = new Gson();

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

}