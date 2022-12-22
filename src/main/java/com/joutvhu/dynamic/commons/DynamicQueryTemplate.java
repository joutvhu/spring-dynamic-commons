package com.joutvhu.dynamic.commons;

import java.util.Map;

public interface DynamicQueryTemplate {
    String process(Map<String, Object> params);
}
