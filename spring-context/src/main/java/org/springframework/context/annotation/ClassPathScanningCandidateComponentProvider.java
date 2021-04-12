// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.context.annotation;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.GsonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/30 2:49 下午
 **/
public class ClassPathScanningCandidateComponentProvider implements EnvironmentCapable, ResourceLoaderAware {

    Logger log = LoggerFactory.getLogger(getClass());

    @Getter
    @Setter
    ResourceLoader resourceLoader;

    @Getter
    @Setter
    private Environment environment;

    private ResourcePatternResolver resourcePatternResolver;

    private MetadataReaderFactory metadataReaderFactory;

    private final List<TypeFilter> includeFilters = new ArrayList<>();

    private final List<TypeFilter> excludeFilters = new ArrayList<>();


    public ClassPathScanningCandidateComponentProvider() {
        this.includeFilters.add(new AnnotationTypeFilter(Component.class));
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        this.resourcePatternResolver = getResourcePatternResolver(resourceLoader);
        this.metadataReaderFactory = new SimpleMetadataReaderFactory(this.resourceLoader);
    }

    private ResourcePatternResolver getResourcePatternResolver(ResourceLoader resourceLoader) {
        // 从application继承  使用的是这个
        if (resourceLoader instanceof ResourcePatternResolver) {
            return (ResourcePatternResolver) resourceLoader;
        }

        return new PathMatchingResourcePatternResolver(resourceLoader);
    }

    Set<BeanDefinition> findCandidateComponents(String basePackage) {
        log.info("package:{}", basePackage);
        return scanCandidateComponents(basePackage);
    }

    Set<BeanDefinition> scanCandidateComponents(String basePackage) {
        log.info("package:{}", basePackage);
        String path = "classpath*:" + basePackage.replaceAll("\\.", "/") + "/**/*.class";
        Resource[] resources = this.resourcePatternResolver.getResources(path);
        log.info("resource size:{}", CollectionUtils.size(resources));


        Set<BeanDefinition> candidates = Sets.newHashSet();
        for (Resource resource : resources) {
            MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
            if (isCandidateComponent(metadataReader)) {
                ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
                sbd.setResource(resource);
                if (isCandidateComponent(sbd)) {
                    candidates.add(sbd);
                }
            }
        }

        return candidates;
    }

    /**
     * 在此处可以添加过滤器
     * @param metadataReader
     * @return
     */
    private boolean isCandidateComponent(MetadataReader metadataReader) {
        for (TypeFilter excludeFilter : this.excludeFilters) {
            if (excludeFilter.match(metadataReader, this.metadataReaderFactory)) {
                return false;
            }
        }

        for (TypeFilter includeFilter : this.includeFilters) {
            if (includeFilter.match(metadataReader, this.metadataReaderFactory)) {
                return true;
            }
        }

        return false;
    }

    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        return metadata.isIndependent() && metadata.isConcrete() || (metadata.isAbstract() && metadata.hasAnnotatedMethods(Lookup.class.getName()));
    }

}