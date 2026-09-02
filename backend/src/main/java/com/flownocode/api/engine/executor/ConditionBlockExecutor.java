package com.flownocode.api.engine.executor;

import com.flownocode.api.domain.Block;
import com.flownocode.api.domain.enums.BlockType;
import com.flownocode.api.engine.BlockExecutor;
import com.flownocode.api.engine.BlockResult;
import com.flownocode.api.engine.ConfigValueResolver;
import com.flownocode.api.engine.ExecutionContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class ConditionBlockExecutor implements BlockExecutor {

    @Override
    public BlockResult execute(Block block, ExecutionContext context) {
        Map<String, Object> config = block.getConfig();

        Number left = ConfigValueResolver.resolveNumber(config.get("left"), context);
        Number right = ConfigValueResolver.resolveNumber(config.get("right"), context);
        String operator = (String) config.get("operator");

        UUID nextBlockId = evaluate(left.doubleValue(), operator, right.doubleValue())
                ? UUID.fromString((String) config.get("trueNextBlockId"))
                : UUID.fromString((String) config.get("falseNextBlockId"));

        return BlockResult.next(nextBlockId);
    }

    @Override
    public BlockType getType() {
        return BlockType.CONDITION;
    }

    private boolean evaluate(double left, String operator, double right) {
        return switch (operator) {
            case "==" -> left == right;
            case "!=" -> left != right;
            case ">"  -> left > right;
            case "<"  -> left < right;
            case ">=" -> left >= right;
            case "<=" -> left <= right;
            default   -> throw new IllegalArgumentException("Unsupported operator: " + operator);
        };
    }
}
