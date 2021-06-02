// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.springframework.util.ClassUtils;
import org.springframework.util.SpringAsmInfo;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/22 7:50 下午
 **/
public interface ParameterNameDiscoverer {


    @Nullable
    String[] getParameterNames(Constructor<?> ctor);


    class PrioritizedParameterNameDiscoverer implements ParameterNameDiscoverer {
        List<ParameterNameDiscoverer> parameterNameDiscoverers = new ArrayList<>(2);

        public void addDiscoverer(ParameterNameDiscoverer pnd) {
            this.parameterNameDiscoverers.add(pnd);
        }

        @Override
        @Nullable
        public String[] getParameterNames(Constructor<?> ctor) {
            for (ParameterNameDiscoverer pnd : this.parameterNameDiscoverers) {
                String[] result = pnd.getParameterNames(ctor);
                if (result != null) {
                    return result;
                }
            }
            return null;
        }
    }

    class DefaultParameterNameDiscoverer extends PrioritizedParameterNameDiscoverer {
        public DefaultParameterNameDiscoverer() {
            addDiscoverer(new StandardReflectionParameterNameDiscoverer());
            addDiscoverer(new LocalVariableTableParameterNameDiscoverer());
        }
    }

    class StandardReflectionParameterNameDiscoverer implements ParameterNameDiscoverer {

        @Nullable
        @Override
        public String[] getParameterNames(Constructor<?> ctor) {
            Parameter[] parameters = ctor.getParameters();
            return Arrays.stream(parameters).map(parameter -> parameter.isNamePresent() ? parameter.getName() : null).toArray(String[]::new);
        }
    }


    class LocalVariableTableParameterNameDiscoverer implements ParameterNameDiscoverer {
        @Nullable
        @Override
        public String[] getParameterNames(Constructor<?> ctor) {
            try {
                Class<?> declaringClass = ctor.getDeclaringClass();
                InputStream is = declaringClass.getResourceAsStream(ClassUtils.getClassFileName(declaringClass));
                ClassReader classReader = new ClassReader(is);
                Map<Executable, String[]> map = new ConcurrentHashMap<>(32);
                classReader.accept(new ParameterNameDiscoveringVisitor(declaringClass, map), 0);
                return map.values().stream().toArray(String[]::new);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private class ParameterNameDiscoveringVisitor extends ClassVisitor {

            private final Class<?> declaringClass;
            private final Map<Executable, String[]> map;

            public ParameterNameDiscoveringVisitor(Class<?> declaringClass, Map<Executable, String[]> map) {
                super(SpringAsmInfo.ASM_VERSION);
                this.declaringClass = declaringClass;
                this.map = map;
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                return null;
            }
        }
    }



}