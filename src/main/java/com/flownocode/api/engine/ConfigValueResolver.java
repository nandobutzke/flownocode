package com.flownocode.api.engine;

public class ConfigValueResolver {

    private ConfigValueResolver() {}

    public static Object resolve(Object configValue, ExecutionContext context) {
        if (configValue instanceof String key && context.get(key) != null) {
            return context.get(key);
        }
        return configValue;
    }

    public static Number resolveNumber(Object configValue, ExecutionContext context) {
        Object resolved = resolve(configValue, context);
        if (resolved instanceof Number number) {
            return number;
        }
        throw new IllegalArgumentException("Expected a numeric value but got: " + resolved);
    }
}
