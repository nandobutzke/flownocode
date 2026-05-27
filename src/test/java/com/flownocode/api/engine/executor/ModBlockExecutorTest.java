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
    void shouldCalculateRemainderFromContextVariables() {
        UUID nextBlockId = UUID.randomUUID();
        Block block = block(
                Map.of("left", "input", "right", "divisor", "resultVariable", "remainder"),
                nextBlockId
        );
        ExecutionContext context = new ExecutionContext(Map.of("input", 17, "divisor", 2));

        BlockResult result = executor.execute(block, context);

        assertEquals(1L, context.get("remainder")); // 17 % 2 = 1
        assertTrue(result.hasNext());
        assertEquals(nextBlockId, result.nextBlockId());
    }

    @Test
    void shouldCalculateRemainderWithLiteralRight() {
        Block block = block(
                Map.of("left", "input", "right", 2, "resultVariable", "remainder"),
                UUID.randomUUID()
        );
        ExecutionContext context = new ExecutionContext(Map.of("input", 9));

        executor.execute(block, context);

        assertEquals(1L, context.get("remainder")); // 9 % 2 = 1
    }

    @Test
    void shouldReturnZeroWhenDivisible() {
        Block block = block(
                Map.of("left", "input", "right", "divisor", "resultVariable", "remainder"),
                UUID.randomUUID()
        );
        ExecutionContext context = new ExecutionContext(Map.of("input", 9, "divisor", 3));

        executor.execute(block, context);

        assertEquals(0L, context.get("remainder")); // 9 % 3 = 0
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
