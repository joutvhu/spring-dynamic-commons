package com.joutvhu.dynamic.commons.directive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class TrimSymbol {
    private String prefix;
    private List<String> prefixOverrides;
    private String suffix;
    private List<String> suffixOverrides;

    public TrimSymbol(String prefix, List<String> prefixOverrides, String suffix, List<String> suffixOverrides) {
        this.prefix = prefix;
        this.prefixOverrides = prefixOverrides;
        this.suffix = suffix;
        this.suffixOverrides = suffixOverrides;

        if (prefix == null && prefixOverrides.isEmpty() && suffix == null && suffixOverrides.isEmpty())
            throw new NullPointerException("The trim directive requires at least one of the following parameters: " +
                    "prefix, prefixOverrides, suffix, suffixOverrides");
    }

    public TrimSymbol(String prefix, String suffix, List<String> overrides) {
        this(prefix, overrides, suffix, overrides);
    }

    public TrimSymbol(String prefix, String suffix, String... overrides) {
        this(prefix, suffix, Arrays.asList(overrides));
    }

    public static List<String> getOverrides(boolean prefix, String... overrides) {
        List<String> result = new ArrayList<>();
        for (String o : overrides) {
            result.add(prefix ? o + " " : " " + o);
            result.add(prefix ? o + "\n" : "\n" + o);
            result.add(prefix ? o + "\t" : "\t" + o);
        }
        return result;
    }

    public String process(String content) {
        for (String prefix : prefixOverrides)
            content = Pattern.compile("^[ \\t\\n]*" + escapeRegular(prefix), Pattern.CASE_INSENSITIVE)
                    .matcher(content)
                    .replaceAll("");
        for (String suffix : suffixOverrides)
            content = Pattern.compile(escapeRegular(suffix) + "[ \\t\\n]*$", Pattern.CASE_INSENSITIVE)
                    .matcher(content)
                    .replaceAll("");

        content = content.trim();
        if (!content.isEmpty()) {
            if (prefix != null)
                content = prefix + " " + content;
            if (suffix != null)
                content = content + " " + suffix;
        }
        content = " " + content + " ";
        return content;
    }

    private String escapeRegular(String regex) {
        return Pattern.compile("([-/\\\\^$*+?.()|\\[\\]{}])").matcher(regex).replaceAll("\\\\$1");
    }
}
