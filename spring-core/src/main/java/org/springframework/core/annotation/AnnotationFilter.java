// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.util.Arrays;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/21 2:21 下午
 **/
@FunctionalInterface
public interface AnnotationFilter {

    AnnotationFilter PLAIN = packages("java.lang", "org.springframework.lang");

    AnnotationFilter ALL = new AnnotationFilter() {
        @Override
        public boolean matches(String typeName) {
            return true;
        }
    };

    static AnnotationFilter packages(String... packages) {
        return new PackagesAnnotationFilter(packages);
    }

    default boolean matches(Class<?> clazz) {
        return this.matches(clazz.getName());
    }

    default boolean matches(Annotation annotation) {
        return this.matches(annotation.annotationType());
    }

    boolean matches(String typeName);

    class PackagesAnnotationFilter implements AnnotationFilter {

        private final String[] prefixes;

        private final int hashCode;


        PackagesAnnotationFilter(String... packages) {
            this.prefixes = new String[packages.length];
            for (int i = 0; i < packages.length; i++) {
                String pkg = packages[i];
                this.prefixes[i] = pkg + ".";
            }
            Arrays.sort(this.prefixes);
            this.hashCode = Arrays.hashCode(this.prefixes);
        }

        @Override
        public boolean matches(String typeName) {
            return true;
        }
    }
}