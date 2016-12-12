package com.poprosturonin.utils;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class SiteDiscovery {
    public List<String> getAllSitesUrls() throws ClassNotFoundException {
        List<String> urls = new ArrayList<>();

        //We are using Spring class path scanning to search for our controllers
        final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile("com.poprosturonin.sites.*")));

        final Set<BeanDefinition> classes = provider.findCandidateComponents("com.poprosturonin.sites.*");

        for (BeanDefinition bean : classes) {
            Class<?> classObj = Class.forName(bean.getBeanClassName());

            //Get urls
            for (Annotation annotation : classObj.getAnnotations()) {
                if (annotation instanceof RequestMapping) {
                    urls.addAll(Arrays.asList(((RequestMapping) annotation).value()));
                }
            }
        }

        return urls;
    }
}
