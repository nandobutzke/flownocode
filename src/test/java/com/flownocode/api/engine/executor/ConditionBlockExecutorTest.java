package com.flownocode.api.engine.executor;

import com.flownocode.api.domain.Block;
import com.flownocode.api.domain.enums.BlockType;
import com.flownocode.api.engine.BlockResult;
import com.flownocode.api.engine.ExecutionContext;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConditionBlockExecutorTest {

    private final ConditionBlockExecutor executor = new ConditionBlockExecutor();

    private static final UUID TRUE_NEXT = UUID.randomUUID();
    private static final UUID FALSE_NEXT = UUID.randomUUID();

    @Test
    void shouldGoToTrueBranchWhenConditionIsMet() {
        Block block = conditionBlock("value", "==", 0);
        ExecutionContext context = new ExecutionContext(Map.of("value", 0L));

        BlockResult result = executor.execute(block, context);

        assertEquals(TRUE_NEXT, result.nextBlockId());
    }

    @Test
    void shouldGoToFalseBranchWhenConditionIsNotMet() {
        Block block = conditionBlock("value", "==", 0);
        ExecutionContext context = new ExecutionContext(Map.of("value", 1L));

        BlockResult result = executor.execute(block, context);

        assertEquals(FALSE_NEXT, result.nextBlockId());
    }

    @Test
    void shouldThrowOnUnsupportedOperator() {
        Block block = conditionBlock("value", "??", 0);
        ExecutionContext context = new ExecutionContext(Map.of("value", 1));

        assertThrows(IllegalArgumentException.class, () -> executor.execute(block, context));
    }

    @Test
    void shouldSupportConditionType() {
        assertEquals(BlockType.CONDITION, executor.getType());
    }

    private Block conditionBlock(Object left, String operator, Object right) {
        Map<String, Object> config = new HashMap<>();
        config.put("left", left);
        config.put("operator", operator);
        config.put("right", right);
        config.put("trueNextBlockId", TRUE_NEXT.toString());
        config.put("falseNextBlockId", FALSE_NEXT.toString());

        Block block = new Block();
        block.setId(UUID.randomUUID());
        block.setType(BlockType.CONDITION);
        block.setConfig(config);
        return block;
    }
}
