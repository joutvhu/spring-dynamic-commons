package com.joutvhu.dynamic.commons;

import com.joutvhu.dynamic.commons.util.DynamicTemplateResolver;
import com.joutvhu.dynamic.commons.util.TemplateConfiguration;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;

/**
 * DynamicQueryTemplates
 *
 * @author Giao Ho
 * @since 1.0.0
 */
@NoArgsConstructor
public class DynamicQueryTemplates implements ResourceLoaderAware, InitializingBean {
    private static final Log log = LogFactory.getLog(DynamicQueryTemplates.class);

    private static StringTemplateLoader sqlTemplateLoader = new StringTemplateLoader();
    private static Configuration cfg = TemplateConfiguration.instanceWithDefault()
            .templateLoader(sqlTemplateLoader)
            .configuration();

    private String encoding = "UTF-8";
    private String templateLocation = "classpath:/query";
    private String suffix = ".dsql";
    private ResourceLoader resourceLoader;

    public Template findTemplate(String name) {
        try {
            return cfg.getTemplate(name, encoding);
        } catch (IOException e) {
            return null;
        }
    }

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

    @Override
    public void afterPropertiesSet() throws Exception {
        String pattern;
        if (StringUtils.isNotBlank(templateLocation))
            pattern = templateLocation.contains(suffix) ? templateLocation : templateLocation + "/**/*" + suffix;
        else pattern = "classpath:/**/*" + suffix;

        PathMatchingResourcePatternResolver resourcePatternResolver =
                new PathMatchingResourcePatternResolver(resourceLoader);
        Resource[] resources = resourcePatternResolver.getResources(pattern);

        for (Resource resource : resources) {
            DynamicTemplateResolver.of(resource).encoding(encoding).load((templateName, content) -> {
                Object src = sqlTemplateLoader.findTemplateSource(templateName);
                if (src != null)
                    log.warn("Found duplicate template key, will replace the value, key: " + templateName);
                sqlTemplateLoader.putTemplate(templateName, content);
            });
        }
    }
}
