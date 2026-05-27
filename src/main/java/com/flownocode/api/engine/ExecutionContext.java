package com.flownocode.api.engine;

import java.util.HashMap;
import java.util.Map;

public class ExecutionContext {

    private final Map<String, Object> variables = new HashMap<>();
    private final Map<String, Object> input;
    private Object result;

    public ExecutionContext(Map<String, Object> input) {
        this.input = Map.copyOf(input);
        this.variables.putAll(input);
    }

    public void put(String key, Object value) {
        variables.put(key, value);
    }

    public Object get(String key) {
        return variables.get(key);
    }

    public Map<String, Object> getInput() {
        return input;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Map<String, Object> getVariables() {
        return Map.copyOf(variables);
    }
}
