package com.flownocode.api.engine.executor;

import com.flownocode.api.domain.Block;
import com.flownocode.api.domain.enums.BlockType;
import com.flownocode.api.engine.BlockExecutor;
import com.flownocode.api.engine.BlockResult;
import com.flownocode.api.engine.ExecutionContext;
import org.springframework.stereotype.Component;

@Component
public class EndBlockExecutor implements BlockExecutor {

    @Override
    public BlockResult execute(Block block, ExecutionContext context) {
        context.setResult(block.getConfig().get("result"));
        return BlockResult.stop();
    }

    @Override
    public BlockType getType() {
        return BlockType.END;
    }
}
