// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.io.support;

import com.google.common.collect.Sets;
import com.google.common.io.Files;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/30 4:17 下午
 **/
public class PathMatchingResourcePatternResolver implements ResourcePatternResolver {

    Logger log = LoggerFactory.getLogger(getClass());

    @Getter
    private ResourceLoader resourceLoader;

    @Getter
    private PathMatcher pathMatcher = new AntPathMatcher();

    public PathMatchingResourcePatternResolver(ResourceLoader resourceLoader) {
//        log.info("");
        this.resourceLoader = resourceLoader;
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.resourceLoader.getClassLoader();
    }

    @Override
    public Resource[] getResources(String locationPattern) {
//        log.info("locationPattern:{}", locationPattern);
        if (locationPattern.startsWith("classpath*:")) {
            if (this.pathMatcher.isPattern(locationPattern.substring("classpath*:".length()))) {
                return findPathMatchingResources(locationPattern);
            } else {
                return findAllClassPathResources(locationPattern.substring("classpath*:".length()));
            }
        } else {
            Resource resource = getResourceLoader().getResource(locationPattern);
            return new Resource[]{resource};
        }
    }

    @Override
    public Resource getResource(String location) {
        return getResourceLoader().getResource(location);
    }

    private Resource[] findAllClassPathResources(String locationPattern) {
        Set<Resource> result = new LinkedHashSet<>(16);
        try {
            ClassLoader classLoader = getClassLoader();
            Enumeration<URL> urlEnumeration = classLoader.getResources(locationPattern);
            while (urlEnumeration.hasMoreElements()) {
                URL url = urlEnumeration.nextElement();
                UrlResource urlResource = new UrlResource(url);
                result.add(urlResource);
            }
            return result.toArray(new Resource[]{});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String findRootPath(String path) {
        if (this.pathMatcher.isPattern(path)) {
            String substring = path.substring(0, path.lastIndexOf("/"));
            return findRootPath(substring);
        }
        return path;
    }

    private Resource[] findPathMatchingResources(String locationPattern) {
        try {
            String rootPath = "classpath*:" + findRootPath(locationPattern.substring("classpath*:".length()));
            Resource[] rootPackageSource = this.getResources(rootPath);
            Set<Resource> result = new LinkedHashSet<>(16);
            for (Resource rootPathResource : rootPackageSource) {
                URL rootPathResourceURL = rootPathResource.getURL();
                String protocol = rootPathResourceURL.getProtocol();

                if (Objects.equals(protocol, "file")) {
                    Set<Resource> resources = doFindPathMatchingFileResources(rootPathResource, locationPattern);
                    result.addAll(resources);
                }
            }

            return result.toArray(new Resource[]{});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected Set<Resource> doFindPathMatchingFileResources(Resource rootDirResource, String fullPattern) {
        File file = rootDirResource.getFile().getAbsoluteFile();
        HashSet<File> allFiles = Sets.<File>newHashSet();
        getAllFiles(file, allFiles);
//        log.info("all file size:{}", CollectionUtils.size(allFiles));

        Set<Resource> fileResources = Sets.newHashSet();
        for (File subFile : allFiles) {
            String fileAbsolutePath = subFile.getAbsolutePath();
            boolean match = this.pathMatcher.match(fileAbsolutePath, fullPattern);
            if (match) {
                fileResources.add(new FileSystemResource(subFile));
            }
        }

        return fileResources;
    }

    protected void getAllFiles(File parentFile, Set<File> result) {
        for (File subFile : parentFile.listFiles()) {
            if (subFile.isDirectory()) {
                getAllFiles(subFile, result);
            } else {
                if (subFile.getName().endsWith(".class")) {
                    result.add(subFile);
                }
            }
        }
    }
}