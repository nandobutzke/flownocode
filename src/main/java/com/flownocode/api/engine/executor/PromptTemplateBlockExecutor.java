package com.flownocode.api.engine.executor;

import com.flownocode.api.domain.Block;
import com.flownocode.api.domain.enums.BlockType;
import com.flownocode.api.engine.BlockExecutor;
import com.flownocode.api.engine.BlockResult;
import com.flownocode.api.engine.ExecutionContext;
import com.flownocode.api.engine.PromptTemplateInterpolator;
import org.springframework.stereotype.Component;

@Component
public class PromptTemplateBlockExecutor implements BlockExecutor {

    @Override
    public BlockResult execute(Block block, ExecutionContext context) {
        var config = block.getConfig();
        String variable = requiredString(config.get("variable"), "variable");
        String template = requiredString(config.get("template"), "template");

        context.put(variable, PromptTemplateInterpolator.interpolate(template, context));

        return BlockResult.next(block.getNextBlockId());
    }

    @Override
    public BlockType getType() {
        return BlockType.PROMPT_TEMPLATE;
    }

    private static String requiredString(Object value, String field) {
        if (!(value instanceof String text) || text.isBlank()) {
            throw new IllegalArgumentException("PROMPT_TEMPLATE config." + field + " is required");
        }
        return text;
    }
}
