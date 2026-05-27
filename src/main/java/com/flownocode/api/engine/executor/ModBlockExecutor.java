package com.flownocode.api.engine.executor;

import com.flownocode.api.domain.Block;
import com.flownocode.api.domain.enums.BlockType;
import com.flownocode.api.engine.BlockExecutor;
import com.flownocode.api.engine.BlockResult;
import com.flownocode.api.engine.ConfigValueResolver;
import com.flownocode.api.engine.ExecutionContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ModBlockExecutor implements BlockExecutor {

    @Override
    public BlockResult execute(Block block, ExecutionContext context) {
        Map<String, Object> config = block.getConfig();

        Number left = ConfigValueResolver.resolveNumber(config.get("left"), context); // input
        Number right = ConfigValueResolver.resolveNumber(config.get("right"), context); // divisor
        String resultVariable = (String) config.get("resultVariable"); // remainder

        long remainder = left.longValue() % right.longValue(); // resto da divisão

        context.put(resultVariable, remainder);

        return BlockResult.next(block.getNextBlockId());
    }

    @Override
    public BlockType getType() {
        return BlockType.MOD;
    }
}
