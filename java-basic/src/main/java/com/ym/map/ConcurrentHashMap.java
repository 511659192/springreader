// Copyright (C) 2021 Meituan
// All rights reserved
package com.ym.map;

import com.google.common.base.Preconditions;
import sun.misc.Unsafe;

import static org.apache.commons.lang3.ArrayUtils.add;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;

/**
 *
 * @author yangmeng
 * @version 1.0
 * @created 2021/6/22 5:34 下午
 **/
public class ConcurrentHashMap<K, V> {

    transient volatile Node<K,V>[] table;
    private transient volatile Node<K,V>[] nextTable;
    private transient volatile long baseCount;

    public static void main(String[] args) {
        final int HASH_BITS = 0x7fffffff; // usable bits of normal node hash
        System.out.println(Integer.toBinaryString(HASH_BITS));
        int a = 2 >>> 16;
        System.out.println(a);
        System.out.println(2 ^ a);
    }

    private int sizeCtl;

    public ConcurrentHashMap(int initialCapacity) {
        int cap = tableSizeFor(initialCapacity + (initialCapacity >>> 1) + 1);
        this.sizeCtl = cap;
    }

    public V put(K key, V value) {
        return putValue(key, value, false);
    }

    private V putValue(K key, V value, boolean onlyIfAbsent) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(value);
        int hash = reHash(key.hashCode());

        for (Node<K, V>[] table = this.table; ; ) {
            if (isEmpty(table)) {
                table = initTable();
                continue;
            }

            int len = table.length;
            int slot = (len - 1) & hash;
            Node<K, V> findNode = tabAt(table, slot);

            if (findNode == null) { // slot上没有节点 创建节点
                Node<K, V> newNode = new Node<>(hash, key, value, null);
                boolean addNewNode = casTabAt(table, slot, null, newNode);
                if (addNewNode) {
                    break;
                }
            }

            int nodeHash = findNode.hash;
            if (nodeHash == MOVED) { // 迁移数据
                table = helpTransfer(table, findNode);
                continue;
            }

            synchronized (findNode) {
                if (tabAt(table, slot) != findNode) {
                    continue;
                }

                if (nodeHash < 0) { // 不支持红黑树
                    continue;
                }

                for (Node<K, V> linkNode = findNode; ; ) {
                    if (linkNode.keyEqual(findNode) && !onlyIfAbsent) {
                        linkNode.val = value;
                        break;
                    }

                    if (linkNode.next == null) {
                        linkNode.next = new Node<>(hash, key, value, null);
                        break;
                    }

                }
            }
        }

        addCount(1L);
        return null;

    }



    private void addCount(long newCnt) {
        long baseCnt = this.baseCount;
        long addedBaseCnt = baseCnt + newCnt;
        boolean b = U.compareAndSwapLong(this, BASECOUNT, baseCnt, addedBaseCnt);
        if (!b) {
            throw new RuntimeException("error");
        }
        Node<K,V>[] nodeTable, nextNodeTable;
        int len, sizeCtl;

        while (addedBaseCnt >= (sizeCtl = this.sizeCtl) && (nodeTable = this.table) != null && (len = nodeTable.length) < MAXIMUM_CAPACITY) {
            if (sizeCtl >= 0 ) {

            }
        }






    }

    private Node<K, V>[] helpTransfer(Node<K, V>[] table, Node<K, V> findNode) {
        return new Node[0];
    }

    private Node<K, V>[] initTable() {
        int sizeCtl;
        while (isEmpty(this.table)) {
            if ((sizeCtl = this.sizeCtl) < 0) {
                Thread.yield();
                continue;
            }

            boolean succ = U.compareAndSwapInt(this, SIZ_ECTL, sizeCtl, -1);
            if (!succ) {
                continue;
            }

            int size = sizeCtl > 0 ? sizeCtl : DEFAULT_CAPACITY;
            this.table = (Node<K, V>[]) new Node<?, ?>[size];
            this.sizeCtl = size - (size >> 2);
            return this.table;
        }

        return null;
    }


    private static final int tableSizeFor(int c) {
        int n = c - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    static final int reHash(int h) {
        return (h ^ (h >>> 16)) & HASH_BITS;
    }

    static final <K,V> Node<K,V> tabAt(Node<K,V>[] tab, int slot) {
        return (Node<K,V>) U.getObjectVolatile(tab, ((long)slot << NODE_ARRAY_SHIFT) + NODE_ARRAY_BASE);
    }

    static final <K, V> boolean casTabAt(Node<K, V>[] tab, int slot, Node<K, V> formerNode, Node<K, V> node) {
        return U.compareAndSwapObject(tab, ((long) slot << NODE_ARRAY_SHIFT) + NODE_ARRAY_BASE, formerNode, node);
    }


    static final int MOVED = -1; // hash for forwarding nodes

    private static final int MAXIMUM_CAPACITY = 1 << 30;
    private static final int DEFAULT_CAPACITY = 16;
    static final int HASH_BITS = 0x7fffffff; // usable bits of normal node hash
    private static final sun.misc.Unsafe U;
    private static final long SIZ_ECTL;
    private static final long NODE_ARRAY_BASE;
    private static final int NODE_ARRAY_SHIFT;
    private static final long TRANSFERINDEX;
    private static final long BASECOUNT;

    static {
        try {
            U = Unsafe.getUnsafe();
            Class<ConcurrentHashMap> k = ConcurrentHashMap.class;
            SIZ_ECTL = U.objectFieldOffset(k.getDeclaredField("sizeCtl"));
            TRANSFERINDEX = U.objectFieldOffset(k.getDeclaredField("transferIndex"));
            BASECOUNT = U.objectFieldOffset(k.getDeclaredField("baseCount"));

            Class<?> arrayClass = Node[].class;
            NODE_ARRAY_BASE = U.arrayBaseOffset(arrayClass);
            int scale = U.arrayIndexScale(arrayClass);
            if ((scale & (scale - 1)) != 0)
                throw new RuntimeException("data type scale not a power of two");
            NODE_ARRAY_SHIFT = 31 - Integer.numberOfLeadingZeros(scale);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}