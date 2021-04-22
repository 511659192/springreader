// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.config;

import lombok.Getter;
import org.springframework.beans.BeanMetadataElement;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/20 8:17 下午
 **/
public class ConstructorArgumentValues {

    @Getter
    private final Map<Integer, ValueHolder> indexedArgumentValues = new LinkedHashMap<>();


    @Getter
    private final List<ValueHolder> genericArgumentValues = new ArrayList<>();

    public boolean isEmpty() {
        return true;
    }

    public int getArgumentCount() {
        return (this.indexedArgumentValues.size() + this.genericArgumentValues.size());
    }

    public static class ValueHolder implements BeanMetadataElement {

        @Nullable
        private Object value;

        @Nullable
        private String type;

        @Nullable
        private String name;
    }
}