// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.annotation;

import com.google.common.cache.Cache;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.LookupOverride;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.util.CacheUtils;
import org.springframework.util.ClassUtils;

import javax.annotation.Nullable;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.util.CacheUtils.get;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/8 10:58 上午
 **/
@Slf4j
public class AutowiredAnnotationBeanPostProcessor implements SmartInstantiationAwareBeanPostProcessor, MergedBeanDefinitionPostProcessor,
        PriorityOrdered, BeanFactoryAware {
    private int order = Ordered.LOWEST_PRECEDENCE - 2;

    @Nullable
    @Setter
    private ConfigurableListableBeanFactory beanFactory;

    private final Set<Class<? extends Annotation>> autowiredAnnotationTypes = new LinkedHashSet<>(4);

    private final Set<String> lookupMethodsChecked = Collections.newSetFromMap(new ConcurrentHashMap<>(256));

    Cache<Class<?>, Constructor<?>[]> candidateConstructorsCache = CacheUtils.newBuilder().build();

    Cache<String, InjectionMetadata> injectionMetadataCache = CacheUtils.newBuilder().build();


    public AutowiredAnnotationBeanPostProcessor() {
        this.autowiredAnnotationTypes.add(Autowired.class);
        this.autowiredAnnotationTypes.add(Value.class);
    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        InjectionMetadata metadata = findAutowiringMetadata(beanName, beanType, null);
        metadata.checkConfigMembers(beanDefinition);
    }


    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName) {
        if (!this.lookupMethodsChecked.contains(beanName)) {
            if (AnnotationUtils.isCandidateClass(beanClass, Lookup.class)) {
                Class<?> targetClass = beanClass;
                do {
                    Method[] methods = ClassUtils.getDeclaredMethods(targetClass);
                    Arrays.stream(methods).forEach(method -> {
                        Lookup lookup = method.getAnnotation(Lookup.class);
                        if (lookup == null) {
                            return;
                        }

                        LookupOverride lookupOverride = new LookupOverride(method, lookup.value());
                        RootBeanDefinition mbd = ((RootBeanDefinition) this.beanFactory.getMergedBeanDefinition(beanName));
                        mbd.getMethodOverrides().addOverride(lookupOverride);
                    });
                    targetClass = beanClass.getSuperclass();
                } while (targetClass != null && targetClass != Object.class);
            }

            this.lookupMethodsChecked.add(beanName);
        }

        return get(candidateConstructorsCache, beanClass, () -> {
            Constructor<?>[] rawCandidateCtors = beanClass.getDeclaredConstructors();
            List<Constructor<?>> candidates = new ArrayList<>(rawCandidateCtors.length);
            for (Constructor<?> candidate : rawCandidateCtors) {
                MergedAnnotation<?> ann = findAutowiredAnnotation(candidate);
                if (ann == null) {
                    Class<?> userClass = ClassUtils.getUserClass(beanClass);
                    if (userClass != beanClass) {
                        Constructor<?> superCtors = ClassUtils.getUserClassConstructors(userClass, candidate.getParameterTypes());
                        ann = findAutowiredAnnotation(superCtors);
                    }
                }

                if (ann != null) {
                    candidates.add(candidate);
                }
            }

            if (!candidates.isEmpty()) {
                return candidates.toArray(new Constructor[0]);
            }

            if (rawCandidateCtors.length == 1 && rawCandidateCtors[0].getParameterCount() == 0) {
                return new Constructor[]{rawCandidateCtors[0]};
            }

            return new Constructor[0];
        });
    }

    /**
     *
     * @param ao Field Method
     * @return
     */
    @Nullable
    private MergedAnnotation<?> findAutowiredAnnotation(AccessibleObject ao) {
        MergedAnnotations mergedAnnotations = MergedAnnotations.from(ao);
        for (Class<? extends Annotation> annotationType : this.autowiredAnnotationTypes) {
            MergedAnnotation<? extends Annotation> mergedAnnotation = mergedAnnotations.get(annotationType);
            if (mergedAnnotation.isPresent()) {
                return mergedAnnotation;
            }
        }
        return null;
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
        InjectionMetadata metadata = findAutowiringMetadata(beanName, bean.getClass(), pvs);
        metadata.inject(bean, beanName, pvs);
        return pvs;
    }


    private InjectionMetadata findAutowiringMetadata(String beanName, Class<?> beanClass, PropertyValues pvs) {
        return get(injectionMetadataCache, beanName, () -> buildAutowiringMetadata(beanClass));
    }

    private InjectionMetadata buildAutowiringMetadata(Class<?> beanClass) {
        if (!AnnotationUtils.isCandidateClass(beanClass, this.autowiredAnnotationTypes)) {
            return InjectionMetadata.EMPTY;
        }

        final List<InjectionMetadata.InjectedElement> elements = new ArrayList<>();

        Arrays.stream(beanClass.getDeclaredFields()).forEach(field -> {
            MergedAnnotation<?> autowiredAnnotation = findAutowiredAnnotation(field);
            if (autowiredAnnotation != null) {
                elements.add(new AutowiredFieldElement(field, true));
            }
        });

        Arrays.stream(beanClass.getDeclaredMethods()).forEach(method -> {
            MergedAnnotation<?> autowiredAnnotation = findAutowiredAnnotation(method);
            if (autowiredAnnotation != null) {
                PropertyDescriptor pd = ClassUtils.findPropertyForMethod(method, beanClass);
                elements.add(new AutowiredMethodElement(method, true, pd));
            }
        });

        return InjectionMetadata.forElements(elements, beanClass);
    }

    private class AutowiredFieldElement extends InjectionMetadata.InjectedElement {
        private final boolean required;

        private volatile boolean cached;

        @Nullable
        private volatile Object cachedFieldValue;

        public AutowiredFieldElement(Field field, boolean required) {
            super(field, null);
            this.required = required;
        }

        @Override
        protected void inject(Object target, @Nullable String beanName, @Nullable PropertyValues pvs) {
            Field field = (Field) this.member;
            Object value = resolveFieldValue(field, target, beanName);
        }

        @Nullable
        private Object resolveFieldValue(Field field, Object bean, @Nullable String beanName) {
            DependencyDescriptor descriptor = new DependencyDescriptor(field, this.required);
            descriptor.setContainingClass(bean.getClass());
            TypeConverter typeConverter = beanFactory.getTypeConverter();

            Set<String> autowiredBeanNames = new LinkedHashSet<>(1);

            Object value = beanFactory.resolveDependency(descriptor, beanName, autowiredBeanNames, typeConverter);


            return value;

        }
    }

    private class AutowiredMethodElement extends InjectionMetadata.InjectedElement {

        private final boolean required;

        private volatile boolean cached;

        @Nullable
        private volatile Object[] cachedMethodArguments;

        public AutowiredMethodElement(Method method, boolean required, @Nullable PropertyDescriptor pd) {
            super(method, pd);
            this.required = required;
        }

        @Override
        protected void inject(Object target, @Nullable String requestingBeanName, @Nullable PropertyValues pvs) {
            super.inject(target, requestingBeanName, pvs);
        }
    }
}