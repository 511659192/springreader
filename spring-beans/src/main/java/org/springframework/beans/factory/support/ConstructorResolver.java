// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.support;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Constructor;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/22 5:28 下午
 **/
public class ConstructorResolver {

    private static final Object[] EMPTY_ARGS = new Object[0];

    private AbstractAutowireCapableBeanFactory beanFactory;

    public ConstructorResolver(AbstractAutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public BeanWrapper autowireConstructor(String beanName, RootBeanDefinition mbd, Constructor<?>[] ctors, Object[] explicitArgs) {
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl();
        this.beanFactory.initBeanWrapper(beanWrapper);

        Constructor<?> constructorToUse = null;
        ArgumentsHolder argsHolderToUse = null;

        Object[] argsToUse = explicitArgs;
        if (argsToUse == null) {
            Object[] argsToResolve = null;
            synchronized (mbd.constructorArgumentLock) {
                constructorToUse = (Constructor<?>) mbd.resolvedConstructorOrFactoryMethod;
                if (constructorToUse != null && mbd.constructorArgumentsResolved) {
                    argsToResolve = Optional.ofNullable(mbd.resolvedConstructorArguments).orElseGet(() -> mbd.preparedConstructorArguments);
                }
            }

            if (argsToResolve != null) {
                argsToUse = resolvePreparedArguments(beanName, mbd, beanWrapper, constructorToUse, argsToResolve);
            }
        }

        if (constructorToUse == null || argsToUse == null) {
            Constructor<?>[] candidates = ctors;
            if (candidates.length == 1 && explicitArgs == null && !mbd.hasConstructorArgumentValues()) {
                Constructor<?> candidate = candidates[0];
                if (candidate.getParameterCount() == 0) {
                    synchronized (mbd.constructorArgumentLock) {
                        mbd.resolvedConstructorOrFactoryMethod = candidate;
                        mbd.constructorArgumentsResolved = true;
                        mbd.resolvedConstructorArguments = EMPTY_ARGS;
                    }

                    Object beanInstance = this.beanFactory.instantiate(mbd, beanName, candidate, EMPTY_ARGS);
                    beanWrapper.setBeanInstance(beanInstance);
                    return beanWrapper;
                }
            }

            ConstructorArgumentValues argumentValues = mbd.getConstructorArgumentValues();
            ConstructorArgumentValues resolvedValues = new ConstructorArgumentValues();
            int minNrOfArgs = resolveConstructorArguments(beanName, mbd,beanWrapper, argumentValues, resolvedValues);

            for (Constructor<?> candidate : candidates) {
                int parameterCount = candidate.getParameterCount();
                Class<?>[] parameterTypes = candidate.getParameterTypes();
                ParameterNameDiscoverer pnd = this.beanFactory.getParameterNameDiscoverer();

            }


        }


        Object instantiate = this.beanFactory.instantiate(mbd, beanName, constructorToUse, argsToUse);
        beanWrapper.setBeanInstance(instantiate);

        return null;
    }

    private int resolveConstructorArguments(String beanName, RootBeanDefinition mbd, BeanWrapper beanWrapper, ConstructorArgumentValues argumentValues,
                                            ConstructorArgumentValues resolvedValues) {
        TypeConverter customTypeConverter = this.beanFactory.getCustomTypeConverter();
        TypeConverter converter = Optional.ofNullable(customTypeConverter).orElseGet((Supplier<? extends TypeConverter>) beanWrapper);
        BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this.beanFactory, beanName, mbd, converter);

        int minNrOfArgs = argumentValues.getArgumentCount();
        for (Map.Entry<Integer, ConstructorArgumentValues.ValueHolder> entry : argumentValues.getIndexedArgumentValues().entrySet()) {

        }

        for (ConstructorArgumentValues.ValueHolder valueHolder : argumentValues.getGenericArgumentValues()) {

        }

        return 1;
    }

    private Object[] resolvePreparedArguments(String beanName, RootBeanDefinition mbd, BeanWrapper beanWrapper, Constructor<?> constructorToUse, Object[] argsToResolve) {


        return new Object[0];
    }

    private static class ArgumentsHolder {
        public final Object[] rawArguments;
        public final Object[] arguments;
        public final Object[] preparedArguments;
        public boolean resolveNecessary = false;

        public ArgumentsHolder(int size) {
            this.rawArguments = new Object[size];
            this.arguments = new Object[size];
            this.preparedArguments = new Object[size];
        }

        public ArgumentsHolder(Object[] args) {
            this.rawArguments = args;
            this.arguments = args;
            this.preparedArguments = args;
        }
    }
}