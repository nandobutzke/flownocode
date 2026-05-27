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

    private static final UUID TRUE_NEXT  = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID FALSE_NEXT = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

    @Test
    void shouldGoToFalseNextWhenRemainderIsNotZero() {
        Block block = conditionBlock("remainder", "==", 0);
        ExecutionContext context = new ExecutionContext(Map.of("remainder", 1L));

        BlockResult result = executor.execute(block, context);

        assertEquals(FALSE_NEXT, result.nextBlockId()); // 1 == 0 → false
    }

    @Test
    void shouldGoToTrueNextWhenRemainderIsZero() {
        Block block = conditionBlock("remainder", "==", 0);
        ExecutionContext context = new ExecutionContext(Map.of("remainder", 0L));

        BlockResult result = executor.execute(block, context);

        assertEquals(TRUE_NEXT, result.nextBlockId()); // 0 == 0 → true
    }

    @Test
    void shouldGoToTrueNextWhenDivisorIsLessThanInput() {
        Block block = conditionBlock("divisor", "<", "input");
        ExecutionContext context = new ExecutionContext(Map.of("divisor", 3L, "input", 17));

        BlockResult result = executor.execute(block, context);

        assertEquals(TRUE_NEXT, result.nextBlockId()); // 3 < 17 → true
    }

    @Test
    void shouldGoToFalseNextWhenDivisorReachesInput() {
        Block block = conditionBlock("divisor", "<", "input");
        ExecutionContext context = new ExecutionContext(Map.of("divisor", 17L, "input", 17));

        BlockResult result = executor.execute(block, context);

        assertEquals(FALSE_NEXT, result.nextBlockId()); // 17 < 17 → false
    }

    @Test
    void shouldThrowOnUnsupportedOperator() {
        Block block = conditionBlock("divisor", "??", "input");
        ExecutionContext context = new ExecutionContext(Map.of("divisor", 1, "input", 5));

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
        config.put("trueNextBlockId",  TRUE_NEXT.toString());
        config.put("falseNextBlockId", FALSE_NEXT.toString());

        Block block = new Block();
        block.setId(UUID.randomUUID());
        block.setType(BlockType.CONDITION);
        block.setConfig(config);
        return block;
    }
}
