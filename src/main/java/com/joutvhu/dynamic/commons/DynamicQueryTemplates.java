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
    private static StringTemplateLoader sqlTemplateLoader = new StringTemplateLoader();
    private static Configuration cfg = TemplateConfiguration.instanceWithDefault()
            .templateLoader(sqlTemplateLoader)
            .configuration();

    private final Log logger = LogFactory.getLog(getClass());

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

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setTemplateLocation(String templateLocation) {
        this.templateLocation = templateLocation;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

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
                    logger.warn("Found duplicate template key, will replace the value, key: " + templateName);
                sqlTemplateLoader.putTemplate(templateName, content);
            });
        }
    }
}
