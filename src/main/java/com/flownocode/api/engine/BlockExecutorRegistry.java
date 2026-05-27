package com.flownocode.api.engine;

import com.flownocode.api.domain.enums.BlockType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class BlockExecutorRegistry {

    private final Map<BlockType, BlockExecutor> executors;

    public BlockExecutorRegistry(List<BlockExecutor> executors) {
        this.executors = executors.stream()
                .collect(Collectors.toMap(BlockExecutor::getType, Function.identity()));
    }

    public BlockExecutor get(BlockType type) {
        BlockExecutor executor = executors.get(type);
        if (executor == null) {
            throw new IllegalArgumentException("No executor registered for block type: " + type);
        }
        return executor;
    }
}
