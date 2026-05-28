package com.flownocode.api.engine.executor;

import com.flownocode.api.domain.Block;
import com.flownocode.api.domain.enums.BlockType;
import com.flownocode.api.engine.BlockResult;
import com.flownocode.api.engine.ExecutionContext;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SetVariableBlockExecutorTest {

    private final SetVariableBlockExecutor executor = new SetVariableBlockExecutor();

    @Test
    void shouldSetVariableInContextAndReturnNextBlock() {
        UUID nextBlockId = UUID.randomUUID();
        Block block = block(Map.of(
            "variable", "counter", 
            "value", 10
        ), nextBlockId);
        ExecutionContext context = new ExecutionContext(Map.of());

        BlockResult result = executor.execute(block, context);

        assertEquals(10, context.get("counter"));
        assertTrue(result.hasNext());
        assertEquals(nextBlockId, result.nextBlockId());
    }

    @Test
    void shouldOverwriteExistingVariableInContext() {
        Block block = block(Map.of(
            "variable", "counter", 
            "value", 10
        ), UUID.randomUUID());
        ExecutionContext context = new ExecutionContext(Map.of("counter", 5));

        executor.execute(block, context);

        assertEquals(10, context.get("counter"));
    }

    @Test
    void shouldSupportSetVariableType() {
        assertEquals(BlockType.SET_VARIABLE, executor.getType());
    }

    private Block block(Map<String, Object> config, UUID nextBlockId) {
        Block block = new Block();
        block.setId(UUID.randomUUID());
        block.setType(BlockType.SET_VARIABLE);
        block.setNextBlockId(nextBlockId);
        block.setConfig(config);
        return block;
    }
}
