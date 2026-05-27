package com.flownocode.api.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class FlowResponse {

    private UUID id;
    private String name;
    private UUID startBlockId;
    private int blockCount;
    private LocalDateTime createdAt;
}
