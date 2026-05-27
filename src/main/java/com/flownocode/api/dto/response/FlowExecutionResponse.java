package com.flownocode.api.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
@Builder
public class FlowExecutionResponse {

    private UUID flowId;
    private String flowName;
    private Map<String, Object> output;
}
