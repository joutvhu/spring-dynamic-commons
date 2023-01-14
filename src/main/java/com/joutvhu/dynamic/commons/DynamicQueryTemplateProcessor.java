package com.joutvhu.dynamic.commons;

import java.util.Map;

public interface DynamicQueryTemplateProcessor<T> {
    String processTemplate(DynamicQueryTemplate<T> template, Map<String, Object> params);
}
