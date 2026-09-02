package com.flownocode.api.engine.executor;

import com.flownocode.api.domain.Block;
import com.flownocode.api.domain.enums.BlockType;
import com.flownocode.api.engine.BlockResult;
import com.flownocode.api.engine.ExecutionContext;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PromptTemplateBlockExecutorTest {

    private final PromptTemplateBlockExecutor executor = new PromptTemplateBlockExecutor();

    @Test
    void shouldInterpolateStringAndNumberPlaceholders() {
        UUID nextBlockId = UUID.randomUUID();
        Block block = block(Map.of(
                "variable", "prompt",
                "template", "Analyze {{input}} for {{task}}"
        ), nextBlockId);

        ExecutionContext context = new ExecutionContext(Map.of(
                "input", 17,
                "task", "primality"
        ));

        BlockResult result = executor.execute(block, context);

        assertEquals("Analyze 17 for primality", context.get("prompt"));
        assertTrue(result.hasNext());
        assertEquals(nextBlockId, result.nextBlockId());
    }

    @Test
    void shouldCopyTemplateWhenThereAreNoPlaceholders() {
        Block block = block(Map.of(
                "variable", "prompt",
                "template", "Say hello"
        ), UUID.randomUUID());

        ExecutionContext context = new ExecutionContext(Map.of());

        executor.execute(block, context);

        assertEquals("Say hello", context.get("prompt"));
    }

    @Test
    void shouldOverwriteExistingVariable() {
        Block block = block(Map.of(
                "variable", "prompt",
                "template", "new {{input}}"
        ), UUID.randomUUID());

        ExecutionContext context = new ExecutionContext(Map.of(
                "prompt", "old",
                "input", "value"
        ));

        executor.execute(block, context);

        assertEquals("new value", context.get("prompt"));
    }

    @Test
    void shouldFailWhenPlaceholderIsMissing() {
        Block block = block(Map.of(
                "variable", "prompt",
                "template", "Need {{missing}}"
        ), UUID.randomUUID());

        ExecutionContext context = new ExecutionContext(Map.of("input", 1));

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> executor.execute(block, context)
        );
        assertEquals("Missing template variable: missing", error.getMessage());
    }

    @Test
    void shouldSupportPromptTemplateType() {
        assertEquals(BlockType.PROMPT_TEMPLATE, executor.getType());
    }

    private Block block(Map<String, Object> config, UUID nextBlockId) {
        Block block = new Block();
        block.setId(UUID.randomUUID());
        block.setType(BlockType.PROMPT_TEMPLATE);
        block.setNextBlockId(nextBlockId);
        block.setConfig(config);
        return block;
    }
}
