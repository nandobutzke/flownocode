package com.flownocode.api.engine;

import com.flownocode.api.domain.Block;
import com.flownocode.api.domain.Flow;
import com.flownocode.api.domain.enums.BlockType;
import com.flownocode.api.engine.executor.ConditionBlockExecutor;
import com.flownocode.api.engine.executor.EndBlockExecutor;
import com.flownocode.api.engine.executor.IncrementBlockExecutor;
import com.flownocode.api.engine.executor.ModBlockExecutor;
import com.flownocode.api.engine.executor.SetVariableBlockExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration test for WorkflowEngine using real executors and
 * the prime number validation flow built in memory.
 *
 * Flow logic:
 *   SET_VARIABLE(divisor=2) → MOD(input%divisor→remainder) → CONDITION(remainder==0)
 *     true  → END(false)
 *     false → INCREMENT(divisor) → CONDITION(divisor<input)
 *                 true  → back to MOD
 *                 false → END(true)
 */
class WorkflowEngineTest {

    private WorkflowEngine engine;

    // Fixed UUIDs matching the prime number validation flow JSON
    private static final UUID FLOW_ID    = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final UUID BLOCK_1    = UUID.fromString("11111111-1111-1111-1111-111111111111"); // SET_VARIABLE
    private static final UUID BLOCK_2    = UUID.fromString("22222222-2222-2222-2222-222222222222"); // MOD
    private static final UUID BLOCK_3    = UUID.fromString("33333333-3333-3333-3333-333333333333"); // CONDITION (remainder==0)
    private static final UUID BLOCK_4    = UUID.fromString("44444444-4444-4444-4444-444444444444"); // END false
    private static final UUID BLOCK_5    = UUID.fromString("55555555-5555-5555-5555-555555555555"); // INCREMENT
    private static final UUID BLOCK_6    = UUID.fromString("66666666-6666-6666-6666-666666666666"); // CONDITION (divisor<input)
    private static final UUID BLOCK_7    = UUID.fromString("77777777-7777-7777-7777-777777777777"); // END true

    @BeforeEach
    void setUp() {
        BlockExecutorRegistry registry = new BlockExecutorRegistry(List.of(
                new SetVariableBlockExecutor(),
                new ModBlockExecutor(),
                new ConditionBlockExecutor(),
                new IncrementBlockExecutor(),
                new EndBlockExecutor()
        ));
        engine = new WorkflowEngine(registry);
    }

    @ParameterizedTest(name = "input={0} → isPrime={1}")
    @CsvSource({
            "4,  false",
            "7,  true",
            "9,  false",
            "17, true",
            "25, false",
            "97, true"
    })
    void shouldValidatePrimeNumbers(int input, boolean expectedPrime) {
        Flow flow = buildPrimeFlow();
        ExecutionContext context = new ExecutionContext(Map.of("input", input));

        engine.run(flow, context);

        assertEquals(expectedPrime, context.getResult());
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Flow buildPrimeFlow() {
        Flow flow = new Flow();
        flow.setId(FLOW_ID);
        flow.setName("Prime Number Validation Flow");
        flow.setStartBlockId(BLOCK_1);

        List<Block> blocks = List.of(
                setVariable(BLOCK_1, "divisor", 2, BLOCK_2),
                mod(BLOCK_2, "input", "divisor", "remainder", BLOCK_3),
                condition(BLOCK_3, "remainder", "==", 0, BLOCK_4, BLOCK_5),
                end(BLOCK_4, false),
                increment(BLOCK_5, "divisor", BLOCK_6),
                condition(BLOCK_6, "divisor", "<", "input", BLOCK_2, BLOCK_7),
                end(BLOCK_7, true)
        );

        flow.getBlocks().addAll(blocks);
        return flow;
    }

    private Block setVariable(UUID id, String variable, Object value, UUID next) {
        return block(id, BlockType.SET_VARIABLE, Map.of("variable", variable, "value", value), next);
    }

    private Block mod(UUID id, Object left, Object right, String resultVariable, UUID next) {
        return block(id, BlockType.MOD,
                Map.of("left", left, "right", right, "resultVariable", resultVariable), next);
    }

    private Block condition(UUID id, Object left, String operator, Object right, UUID trueNext, UUID falseNext) {
        Map<String, Object> config = new HashMap<>();
        config.put("left", left);
        config.put("operator", operator);
        config.put("right", right);
        config.put("trueNextBlockId", trueNext.toString());
        config.put("falseNextBlockId", falseNext.toString());
        return block(id, BlockType.CONDITION, config, null);
    }

    private Block increment(UUID id, String variable, UUID next) {
        return block(id, BlockType.INCREMENT, Map.of("variable", variable), next);
    }

    private Block end(UUID id, boolean result) {
        return block(id, BlockType.END, Map.of("result", result), null);
    }

    private Block block(UUID id, BlockType type, Map<String, Object> config, UUID nextBlockId) {
        Block block = new Block();
        block.setId(id);
        block.setType(type);
        block.setConfig(config);
        block.setNextBlockId(nextBlockId);
        return block;
    }
}
