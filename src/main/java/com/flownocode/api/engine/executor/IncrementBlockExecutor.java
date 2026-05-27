package com.flownocode.api.engine.executor;

import com.flownocode.api.domain.Block;
import com.flownocode.api.domain.enums.BlockType;
import com.flownocode.api.engine.BlockExecutor;
import com.flownocode.api.engine.BlockResult;
import com.flownocode.api.engine.ConfigValueResolver;
import com.flownocode.api.engine.ExecutionContext;
import org.springframework.stereotype.Component;

@Component
public class IncrementBlockExecutor implements BlockExecutor {

    @Override
    public BlockResult execute(Block block, ExecutionContext context) {
        String variable = (String) block.getConfig().get("variable");

        Number current = ConfigValueResolver.resolveNumber(variable, context);

        context.put(variable, current.longValue() + 1);

        return BlockResult.next(block.getNextBlockId());
    }

    @Override
    public BlockType getType() {
        return BlockType.INCREMENT;
    }
}
