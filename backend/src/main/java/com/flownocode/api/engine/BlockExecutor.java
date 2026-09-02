package com.flownocode.api.engine;

import com.flownocode.api.domain.Block;
import com.flownocode.api.domain.enums.BlockType;

public interface BlockExecutor {

    BlockResult execute(Block block, ExecutionContext context);

    BlockType getType();
}
