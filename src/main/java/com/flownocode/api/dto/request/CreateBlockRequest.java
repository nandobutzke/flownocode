package com.flownocode.api.dto.request;

import com.flownocode.api.domain.enums.BlockType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CreateBlockRequest {

    @NotNull(message = "Block id is required")
    private UUID id;

    @NotNull(message = "Block type is required")
    private BlockType type;

    private UUID nextBlockId;

    private Map<String, Object> config = new HashMap<>();
}
