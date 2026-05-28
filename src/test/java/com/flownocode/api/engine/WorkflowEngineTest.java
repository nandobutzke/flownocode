package com.flownocode.api.engine;

import com.flownocode.api.domain.Block;
import com.flownocode.api.domain.Flow;
import com.flownocode.api.domain.enums.BlockType;
import com.flownocode.api.engine.executor.EndBlockExecutor;
import com.flownocode.api.engine.executor.SetVariableBlockExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WorkflowEngineTest {

    private WorkflowEngine engine;

    @BeforeEach
    void setUp() {
        BlockExecutorRegistry registry = new BlockExecutorRegistry(List.of(
                new SetVariableBlockExecutor(),
                new EndBlockExecutor()
        ));
        engine = new WorkflowEngine(registry);
    }

    @Test
    void shouldRunLinearFlowAndReturnResult() {
        UUID setBlockId = UUID.randomUUID();
        UUID endBlockId = UUID.randomUUID();

        Flow flow = new Flow();
        flow.setId(UUID.randomUUID());
        flow.setName("Test Flow");
        flow.setStartBlockId(setBlockId);

        Block setBlock = new Block();
        setBlock.setId(setBlockId);
        setBlock.setType(BlockType.SET_VARIABLE);
        setBlock.setNextBlockId(endBlockId);
        setBlock.setConfig(Map.of("variable", "counter", "value", 10));

        Block endBlock = new Block();
        endBlock.setId(endBlockId);
        endBlock.setType(BlockType.END);
        endBlock.setConfig(Map.of("result", true));

        flow.getBlocks().addAll(List.of(setBlock, endBlock));

        ExecutionContext context = new ExecutionContext(Map.of());
        engine.run(flow, context);

        assertEquals(10, context.get("counter"));
        assertEquals(true, context.getResult());
    }
}
