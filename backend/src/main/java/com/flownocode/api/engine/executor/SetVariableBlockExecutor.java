package com.flownocode.api.engine.executor;

import com.flownocode.api.domain.Block;
import com.flownocode.api.domain.enums.BlockType;
import com.flownocode.api.engine.BlockExecutor;
import com.flownocode.api.engine.BlockResult;
import com.flownocode.api.engine.ExecutionContext;
import org.springframework.stereotype.Component;

@Component
public class SetVariableBlockExecutor implements BlockExecutor {

    @Override
    public BlockResult execute(Block block, ExecutionContext context) {
        var config = block.getConfig();
        String variable = (String) config.get("variable");
        Object value = config.get("value");

        context.put(variable, value);

        return BlockResult.next(block.getNextBlockId());
    }

    @Override
    public BlockType getType() {
        return BlockType.SET_VARIABLE;
    }
}
