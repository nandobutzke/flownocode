package com.flownocode.api.engine;

import java.util.UUID;

public record BlockResult(UUID nextBlockId) {

    public static BlockResult next(UUID nextBlockId) {
        return new BlockResult(nextBlockId);
    }

    public static BlockResult stop() {
        return new BlockResult(null);
    }

    public boolean hasNext() {
        return nextBlockId != null;
    }
}
