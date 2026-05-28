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

class IncrementBlockExecutorTest {

    private final IncrementBlockExecutor executor = new IncrementBlockExecutor();

    @Test
    void shouldIncrementVariableByOne() {
        UUID nextBlockId = UUID.randomUUID();

        Block block = block("counter", nextBlockId);

        ExecutionContext context = new ExecutionContext(Map.of(
                "counter", 2
        ));

        BlockResult result = executor.execute(block, context);

        assertEquals(3L, context.get("counter"));
        assertTrue(result.hasNext());
        assertEquals(nextBlockId, result.nextBlockId());
    }

    @Test
    void shouldSupportIncrementType() {
        assertEquals(BlockType.INCREMENT, executor.getType());
    }

    private Block block(String variable, UUID nextBlockId) {
        Block block = new Block();
        block.setId(UUID.randomUUID());
        block.setType(BlockType.INCREMENT);
        block.setNextBlockId(nextBlockId);
        block.setConfig(Map.of(
                "variable", variable
        ));
        return block;
    }
}
