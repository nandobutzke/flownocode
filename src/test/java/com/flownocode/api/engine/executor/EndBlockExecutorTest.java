package com.flownocode.api.engine.executor;

import com.flownocode.api.domain.Block;
import com.flownocode.api.domain.enums.BlockType;
import com.flownocode.api.engine.BlockResult;
import com.flownocode.api.engine.ExecutionContext;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class EndBlockExecutorTest {

    private final EndBlockExecutor executor = new EndBlockExecutor();

    @Test
    void shouldSetResultTrueAndStopExecution() {
        Block block = block(true);
        ExecutionContext context = new ExecutionContext(Map.of());

        BlockResult result = executor.execute(block, context);

        assertEquals(true, context.getResult());
        assertFalse(result.hasNext());
    }

    @Test
    void shouldSetResultFalseAndStopExecution() {
        Block block = block(false);
        ExecutionContext context = new ExecutionContext(Map.of());

        BlockResult result = executor.execute(block, context);

        assertEquals(false, context.getResult());
        assertFalse(result.hasNext());
    }

    @Test
    void shouldSupportEndType() {
        assertEquals(BlockType.END, executor.getType());
    }

    private Block block(boolean result) {
        Block block = new Block();
        block.setId(UUID.randomUUID());
        block.setType(BlockType.END);
        block.setConfig(Map.of(
                "result", result
        ));
        return block;
    }
}
