// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.support;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.NamedBeanHolder;
import org.springframework.core.OrderComparator;
import org.springframework.core.ResolvableType;
import org.springframework.util.JsonUtils;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 2:07 下午
 **/
@Slf4j
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements ConfigurableListableBeanFactory, BeanDefinitionRegistry, Serializable {

    @Setter
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    private volatile List<String> beanDefinitionNames = new ArrayList<>(256);
    private Comparator<Object> dependencyComparator;
    private AutowireCandidateResolver autowireCandidateResolver;
    private final Map<Class<?>, Object> resolvableDependencies = new ConcurrentHashMap<>(16);
    private final Map<String, BeanDefinitionHolder> mergedBeanDefinitionHolders = new ConcurrentHashMap<>(256);
    private final Map<Class<?>, String[]> allBeanNamesByType = new ConcurrentHashMap<>(64);
    private final Map<Class<?>, String[]> singletonBeanNamesByType = new ConcurrentHashMap<>(64);
    private volatile Set<String> manualSingletonNames = new LinkedHashSet<>(16);
    private boolean allowEagerClassLoading;

    public DefaultListableBeanFactory(BeanFactory parent) {
        super(parent);
        log.info("");
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
        this.beanDefinitionMap.put(beanName, beanDefinition);
        this.beanDefinitionNames.add(beanName);
    }

    @Override
    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    @Override
    protected BeanDefinition getBeanDefinition(String beanName) {
        return this.beanDefinitionMap.get(beanName);
    }

    public void setDependencyComparator(@Nullable Comparator<Object> dependencyComparator) {
        this.dependencyComparator = dependencyComparator;
    }

    public void setAutowireCandidateResolver(AutowireCandidateResolver autowireCandidateResolver) {
        this.autowireCandidateResolver = autowireCandidateResolver;
    }

    @Override
    public boolean containsBeanDefinition(String className) {
        return this.beanDefinitionMap.containsKey(className);
    }

    @Override
    public void registerAlias(String name, String alias) {
    }

    public <T> T getBean(Class<T> requiredType, Object[] args) {
        ResolvableType resolvableType = ResolvableType.forRawClass(requiredType);
        Object resolved = resolveBean(resolvableType, args, false);
        return (T) Preconditions.checkNotNull(resolved);
    }

    @Nullable
    private <T> T resolveBean(ResolvableType requiredType, @Nullable Object[] args, boolean nonUniqueAsNull) {
        NamedBeanHolder<T> namedBean = resolveNamedBean(requiredType, args, nonUniqueAsNull);
        if (namedBean != null) {
            return namedBean.getBeanInstance();
        }

        BeanFactory parentBeanFactory = getParentBeanFactory();
        if (parentBeanFactory instanceof DefaultListableBeanFactory) {
            return ((DefaultListableBeanFactory) parentBeanFactory).resolveBean(requiredType, args, nonUniqueAsNull);
        }

        return null;
    }

    private <T> NamedBeanHolder<T> resolveNamedBean(ResolvableType requiredType, Object[] args, boolean nonUniqueAsNull) {
        String[] candidateNames = getBeanNamesForType(requiredType);

        if (candidateNames.length > 1) {
            // 如果没有定义bean 或者 bean是自动注入类型
            Predicate<String> predicate = candidateName -> !containsBeanDefinition(candidateName) || getBeanDefinition(candidateName).isAutowireCandidate();

            List<String> autowireCandidates = Arrays.stream(candidateNames).filter(predicate).collect(
                    Collectors.toList());

            if (CollectionUtils.isNotEmpty(autowireCandidates)) {
                candidateNames = autowireCandidates.toArray(new String[]{});
            }
        }


        if (candidateNames.length == 1) {
            String beanName = candidateNames[0];
            return resolveNamedBean(beanName, requiredType, args);
        }


        Map<String, Object> candidates = Maps.newLinkedHashMapWithExpectedSize(candidateNames.length);
        for (String beanName : candidateNames) {
            if (containsSingleton(beanName) && args == null) {
                Object beanInstance = getBean(beanName);
                candidates.put(beanName, beanInstance);
            } else {
                candidates.put(beanName, getType(beanName));
            }
        }

        String candidateName = determinePrimaryCandidate(candidates, requiredType.toClass());

        if (candidateName == null) {
            candidateName = determineHighestPriorityCandidate(candidates, requiredType.toClass());
        }

        if (candidateName != null) {
            Object beanInstance = candidates.get(candidateName);
            if (beanInstance == null) {
                return null;
            }

            if (beanInstance instanceof Class) {
                return resolveNamedBean(candidateName, requiredType, args);
            }

            return new NamedBeanHolder<T>(candidateName, ((T) beanInstance));
        }

        if (!nonUniqueAsNull) {
            throw new RuntimeException("expected single matching bean but found " + candidateNames.length + ": " + JsonUtils.toJson(beanDefinitionNames));
        }

        return null;
    }

    private String determineHighestPriorityCandidate(Map<String, Object> candidates, Class<?> requiredType) {
        String highestPriorityBeanName = null;
        Integer highestPriority = null;

        for (Map.Entry<String, Object> entry : candidates.entrySet()) {
            String candidateBeanName = entry.getKey();
            Object beanInstance = entry.getValue();

            if (beanInstance == null) {
                continue;
            }

            Integer candidatePriority = getPriority(beanInstance);
            if (candidatePriority == null) {
                continue;
            }

            if (highestPriorityBeanName == null) {
                highestPriorityBeanName = candidateBeanName;
                highestPriority = candidatePriority;
                continue;
            }

            checkArgument(highestPriority != candidatePriority, "Multiple beans found with the same priority ('" + highestPriority + "') among candidates: " + candidates.keySet());

            if (candidatePriority < highestPriority) {
                highestPriorityBeanName = candidateBeanName;
                highestPriority = candidatePriority;
            }
        }

        return highestPriorityBeanName;
    }

    @Nullable
    protected Integer getPriority(Object beanInstance) {
        Comparator<Object> comparator = getDependencyComparator();
        if (comparator instanceof OrderComparator) {
            return ((OrderComparator) comparator).getPriority(beanInstance);
        }
        return null;
    }


    @Nullable
    protected String determinePrimaryCandidate(Map<String, Object> candidates, Class<?> requiredType) {
        String primaryBeanName = null;

        for (Map.Entry<String, Object> entry : candidates.entrySet()) {
            String candidateBeanName = entry.getKey();
            Object beanInstance = entry.getValue();

            if (!isPrimary(candidateBeanName, beanInstance)) {
                continue;
            }

            if (primaryBeanName == null) {
                primaryBeanName = candidateBeanName;
                continue;
            }

            boolean candidateLocal = containsBeanDefinition(candidateBeanName);
            boolean primaryLocal = containsBeanDefinition(primaryBeanName);
            checkArgument(candidateLocal && primaryLocal, "more than one 'primary' bean found among candidates: " + candidates.keySet());

            if (primaryLocal) {
                primaryBeanName = candidateBeanName;
            }
        }


        return primaryBeanName;
    }

    protected boolean isPrimary(String beanName, Object beanInstance) {
        String transformedBeanName = transformedBeanName(beanName);
        if (containsBeanDefinition(transformedBeanName)) {
            return getMergedLocalBeanDefinition(beanName).isPrimary();
        }

        BeanFactory parentBeanFactory = getParentBeanFactory();
        if (parentBeanFactory == null || !(parentBeanFactory instanceof DefaultListableBeanFactory)) {
            return false;
        }

        return ((DefaultListableBeanFactory) parentBeanFactory).isPrimary(beanName, beanInstance);

    }

    @Nullable
    private <T> NamedBeanHolder<T> resolveNamedBean(String beanName, ResolvableType requiredType, @Nullable Object[] args) {
        Object bean = getBean(beanName, null, args);
        if (bean instanceof NullBean) {
            return null;
        }

        T beanInstance = adaptBeanInstance(beanName, bean, requiredType.toClass());
        return new NamedBeanHolder<T>(beanName, beanInstance);
    }

    private String[] getBeanNamesForType(ResolvableType requiredType) {
        return getBeanNamesForType(requiredType, true, true);
    }

    private String[] getBeanNamesForType(ResolvableType requiredType, boolean includeNonSingletions, boolean allowEagerInit) {
        Class<?> resolved = requiredType.resolve();
        if (resolved != null && !requiredType.hasGenerics()) {
            return getBeanNamesForType(resolved, includeNonSingletions, allowEagerInit);
        }

        return doGetBeanNamesForType(requiredType, includeNonSingletions, allowEagerInit);
    }

    public String[] getBeanNamesForType(Class<?> classType) {
        return this.getBeanNamesForType(classType, true, true);
    }

    @Override
    public String[] getBeanNamesForType(Class<?> clazz, boolean includeNonSingletons, boolean allowEagerInit) {
        Map<Class<?>, String[]> cache = (includeNonSingletons ? this.allBeanNamesByType : this.singletonBeanNamesByType);

        if (cache.get(clazz) != null) {
            return cache.get(clazz);
        }

        ResolvableType resolvableType = ResolvableType.forRawClass(clazz);
        String[] beanNameForType = doGetBeanNamesForType(resolvableType, includeNonSingletons, allowEagerInit);
        if (beanNameForType != null) {
            cache.put(clazz, beanNameForType);
        }
        return beanNameForType;
    }

    private <T> String[] doGetBeanNamesForType(ResolvableType resolvableType, boolean includeNonSingletions, boolean allowEagerInit) {
        List<String> beanDefinitionNames = this.beanDefinitionNames;
        return beanDefinitionNames.stream().filter(beanName -> {
            RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
            if (mbd.isAbstract()) {
                return false;
            }

            if (!allowEagerInit && (!mbd.hasBeanClass() && mbd.isLazyInit() && !isAllowEagerClassLoading())) {
                return false;
            }

            if (requiresEagerInitForType(mbd.getFactoryBeanName())) {
                return false;
            }

            boolean isFactoryBean = isFactoryBean(beanName);
            if (isFactoryBean) {
                return false;
            }

            return isTypeMatch(beanName, resolvableType, allowEagerInit);
        }).toArray(String[]::new);
    }

    private boolean requiresEagerInitForType(@Nullable String factoryBeanName) {
        return (factoryBeanName != null && isFactoryBean(factoryBeanName) && !containsSingleton(factoryBeanName));
    }

    public boolean isAllowEagerClassLoading() {
        return this.allowEagerClassLoading;
    }


    @Override
    public void registerResolvableDependency(Class<?> dependencyType, @Nullable Object autowiredValue) {
        if (autowiredValue != null) {
            if (!(autowiredValue instanceof ObjectFactory || dependencyType.isInstance(autowiredValue))) {
                throw new IllegalArgumentException("Value [" + autowiredValue + "] does not implement specified dependency type [" + dependencyType.getName() + "]");
            }
            this.resolvableDependencies.put(dependencyType, autowiredValue);

            log.info(" type:{} value:{}", dependencyType.getName(), autowiredValue.getClass().getName());
        }
    }

    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        super.registerSingleton(beanName, singletonObject);
        log.info(" singleton:{} class:{}", beanName, singletonObject.getClass().getName());
    }

    @Nullable
    public Comparator<Object> getDependencyComparator() {
        return this.dependencyComparator;
    }

    @Override
    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> classType) {
        return getBeansOfType(classType, true, false);
    }

    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> classType, boolean includeNonSingletons, boolean allowEagerInit) {
        Map<String, T> result = Maps.newHashMap();

        String[] beanNamesForType = getBeanNamesForType(classType, includeNonSingletons, allowEagerInit);
        for (String beanName : beanNamesForType) {
            T bean = getBean(beanName, classType);
            result.put(beanName, bean);
        }

        return result;
    }

    @Override
    public void preInstantiateSingletons() {

    }
}