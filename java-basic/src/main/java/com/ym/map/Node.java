// Copyright (C) 2021 Meituan
// All rights reserved
package com.ym.map;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author yangmeng
 * @version 1.0
 * @created 2021/6/22 5:49 下午
 **/
@AllArgsConstructor
public class Node<K, V> {
    final int hash;
    @Getter
    final K key;
    @Getter
    volatile V val;
    volatile Node<K,V> next;

    public final int hashCode()   { return key.hashCode() ^ val.hashCode(); }
    public final String toString(){ return key + "=" + val; }

    public boolean keyEqual(Node<K, V> b) {
        Node<K, V> a = this;
        if (a == b) {
            return true;
        }

        if (a != null && b == null) {
            return false;
        }

        return a.hash == b.hash && a.key.equals(b.key);
    }
}