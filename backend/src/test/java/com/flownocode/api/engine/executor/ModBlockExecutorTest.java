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

class ModBlockExecutorTest {

    private final ModBlockExecutor executor = new ModBlockExecutor();

    @Test
    void shouldCalculateModWithContextVariables() {
        UUID nextBlockId = UUID.randomUUID();

        Block block = block(Map.of(
                "left", "a",
                "right", "b",
                "resultVariable", "result"
        ), nextBlockId);

        ExecutionContext context = new ExecutionContext(Map.of(
                "a", 10,
                "b", 3
        ));

        BlockResult result = executor.execute(block, context);

        assertEquals(1L, context.get("result"));
        assertTrue(result.hasNext());
        assertEquals(nextBlockId, result.nextBlockId());
    }

    @Test
    void shouldCalculateModWithLiteralRight() {
        Block block = block(Map.of(
                "left", "a",
                "right", 2,
                "resultVariable", "result"
        ), UUID.randomUUID());

        ExecutionContext context = new ExecutionContext(Map.of(
                "a", 9
        ));

        executor.execute(block, context);

        assertEquals(1L, context.get("result"));
    }

    @Test
    void shouldSupportModType() {
        assertEquals(BlockType.MOD, executor.getType());
    }

    private Block block(Map<String, Object> config, UUID nextBlockId) {
        Block block = new Block();
        block.setId(UUID.randomUUID());
        block.setType(BlockType.MOD);
        block.setNextBlockId(nextBlockId);
        block.setConfig(config);
        return block;
    }
}
