// Copyright (C) 2021 Meituan
// All rights reserved
package com.example.springresources;

import com.alibaba.fastjson.JSON;
import com.sun.source.tree.Tree;
import com.sun.source.util.Trees;

import java.util.HashMap;
import java.util.TreeMap;

/**
 *
 * @author yangmeng
 * @version 1.0
 * @created 2021/6/18 9:23 上午
 **/
public class Test {

    public static void main(String[] args) {

        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();


        TreeMap<Integer, Integer> treeMap = new TreeMap<>();

        for (int i = 0; i < 10; i++) {
            treeMap.put(i, i);
        }


    }
}