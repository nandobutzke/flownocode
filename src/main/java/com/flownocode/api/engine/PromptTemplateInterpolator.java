package com.flownocode.api.engine;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PromptTemplateInterpolator {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{([a-zA-Z_][a-zA-Z0-9_]*)\\}\\}");

    private PromptTemplateInterpolator() {}

    public static String interpolate(String template, ExecutionContext context) {
        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuilder rendered = new StringBuilder();
        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = context.get(key);
            if (value == null) {
                throw new IllegalArgumentException("Missing template variable: " + key);
            }
            matcher.appendReplacement(rendered, Matcher.quoteReplacement(String.valueOf(value)));
        }
        matcher.appendTail(rendered);
        return rendered.toString();
    }
}
