package com.poprosturonin.app;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Main controller lists all available sites API.
 */
@RestController
public class MainController {
    private List<String> urls;

    @RequestMapping(path = "/")
    @ResponseBody
    public List<String> sites() {
        return urls;
    }

    @EventListener({ContextRefreshedEvent.class})
    void contextRefreshedEvent() {
        try {
            urls = getAllSitesUrls();
        } catch (ClassNotFoundException e) {
            urls = new ArrayList<>();
        }
    }

    private List<String> getAllSitesUrls() throws ClassNotFoundException {
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
