package com.joutvhu.dynamic.commons;

import com.joutvhu.dynamic.commons.util.DynamicTemplateResolver;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public abstract class DynamicQueryTemplateProvider implements
        ResourceLoaderAware,
        InitializingBean {
    protected String encoding = "UTF-8";
    protected String templateLocation = "classpath:/query";
    protected String suffix = ".dsql";
    protected ResourceLoader resourceLoader;

    /**
     * Create a template and return it without caching it.
     *
     * @param name    of the template
     * @param content the query template
     * @return the template
     */
    public abstract DynamicQueryTemplate createTemplate(String name, String content);

    /**
     * Find a template in cache.
     *
     * @param name of the template
     * @return the template
     */
    public abstract DynamicQueryTemplate findTemplate(String name);

    /**
     * Setup encoding for the process of reading the query template files.
     *
     * @param encoding of query template file, default is "UTF-8"
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Specify the location of the query template files.
     *
     * @param templateLocation is location of the query template files, default is "classpath:/query"
     */
    public void setTemplateLocation(String templateLocation) {
        this.templateLocation = templateLocation;
    }

    /**
     * Specify filename extension of the query template files.
     *
     * @param suffix is filename extension of the query template files, default is ".dsql"
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    /**
     * Specify {@link ResourceLoader} to load the query template files.
     */
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Put a template to cache.
     *
     * @param name    of the template
     * @param content the query template
     */
    protected abstract void putTemplate(String name, String content);

    private String getTemplateLocationPattern() {
        String pattern = StringUtils.isNotBlank(templateLocation) ? templateLocation : "classpath:";
        if (!pattern.endsWith(suffix)) {
            if (!pattern.endsWith("/**/*"))
                pattern += "/**/*";
            pattern += suffix;
        }
        return pattern;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // Set suffix to null to disable loading of external query templates
        if (suffix == null) return;

        String pattern = getTemplateLocationPattern();
        PathMatchingResourcePatternResolver resourcePatternResolver =
                new PathMatchingResourcePatternResolver(resourceLoader);
        Resource[] resources = resourcePatternResolver.getResources(pattern);

        for (Resource resource : resources) {
            DynamicTemplateResolver.of(resource).encoding(encoding).load(this::putTemplate);
        }
    }
}
