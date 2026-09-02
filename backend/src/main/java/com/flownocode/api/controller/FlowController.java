package com.flownocode.api.controller;

import com.flownocode.api.dto.request.CreateFlowRequest;
import com.flownocode.api.dto.response.FlowExecutionResponse;
import com.flownocode.api.dto.response.FlowResponse;
import com.flownocode.api.service.FlowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/flows")
@RequiredArgsConstructor
@Tag(name = "Flows", description = "Workflow definition management")
public class FlowController {

    private final FlowService flowService;

    @PostMapping
    @Operation(summary = "Create a new flow")
    public ResponseEntity<FlowResponse> create(@Valid @RequestBody CreateFlowRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(flowService.create(request));
    }

    @PostMapping("/{flowId}/execute")
    @Operation(summary = "Execute a flow by ID")
    public ResponseEntity<FlowExecutionResponse> execute(
            @PathVariable UUID flowId,
            @RequestBody Map<String, Object> variables) {
        return ResponseEntity.ok(flowService.execute(flowId, variables));
    }
}
