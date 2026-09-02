package com.flownocode.api.engine;

import com.flownocode.api.domain.Block;
import com.flownocode.api.domain.Flow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WorkflowEngine {

    private final BlockExecutorRegistry registry;

    public ExecutionContext run(Flow flow, ExecutionContext context) {
        Map<UUID, Block> blockMap = flow.getBlocks().stream()
                .collect(Collectors.toMap(Block::getId, Function.identity()));

        Block current = blockMap.get(flow.getStartBlockId());

        while (current != null) {
            BlockResult result = registry.get(current.getType()).execute(current, context);
            current = result.hasNext() ? blockMap.get(result.nextBlockId()) : null;
        }

        return context;
    }
}
