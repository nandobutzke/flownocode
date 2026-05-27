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
        Block block = block(Map.of("variable", "divisor", "value", 2), nextBlockId);
        ExecutionContext context = new ExecutionContext(Map.of());

        BlockResult result = executor.execute(block, context);

        assertEquals(2, context.get("divisor"));
        assertTrue(result.hasNext());
        assertEquals(nextBlockId, result.nextBlockId());
    }

    @Test
    void shouldOverwriteExistingVariableInContext() {
        Block block = block(Map.of("variable", "divisor", "value", 5), UUID.randomUUID());
        ExecutionContext context = new ExecutionContext(Map.of("divisor", 2));

        executor.execute(block, context);

        assertEquals(5, context.get("divisor"));
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
