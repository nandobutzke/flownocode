package com.flownocode.api.controller;

import com.flownocode.api.config.OpenApiExamples;
import com.flownocode.api.dto.request.CreateFlowRequest;
import com.flownocode.api.dto.response.FlowExecutionResponse;
import com.flownocode.api.dto.response.FlowResponse;
import com.flownocode.api.exception.ErrorResponse;
import com.flownocode.api.service.FlowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/flows")
@RequiredArgsConstructor
@Tag(name = "Flows", description = "Workflow definition and execution")
public class FlowController {

    private final FlowService flowService;

    @PostMapping
    @Operation(
            summary = "Create a new flow",
            description = "Persists a complete workflow definition. All block ids must be unique in the request and must not already exist in the database."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Flow created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FlowResponse.class),
                            examples = @ExampleObject(
                                    name = "Prime Number Validation Flow",
                                    value = OpenApiExamples.CREATE_FLOW_RESPONSE
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Flow id or block id already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Conflict", value = OpenApiExamples.ERROR_CONFLICT)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Business rule violation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Business Error", value = OpenApiExamples.ERROR_BUSINESS)
                    )
            )
    })
    public ResponseEntity<FlowResponse> create(
            @Valid
            @RequestBody(
                    description = "Complete flow definition with blocks",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateFlowRequest.class),
                            examples = @ExampleObject(
                                    name = "Prime Number Validation Flow",
                                    value = OpenApiExamples.CREATE_FLOW_REQUEST
                            )
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody CreateFlowRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(flowService.create(request));
    }

    @PostMapping("/{flowId}/execute")
    @Operation(
            summary = "Execute a flow by ID",
            description = "Runs the workflow engine with the provided input variables."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Flow executed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FlowExecutionResponse.class),
                            examples = @ExampleObject(
                                    name = "input = 4 (not prime)",
                                    value = OpenApiExamples.EXECUTE_FLOW_RESPONSE
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Flow not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Not Found", value = OpenApiExamples.ERROR_NOT_FOUND)
                    )
            )
    })
    public ResponseEntity<FlowExecutionResponse> execute(
            @PathVariable UUID flowId,
            @RequestBody(
                    description = "Input variables for the flow execution",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "input = 4",
                                    value = OpenApiExamples.EXECUTE_FLOW_REQUEST
                            )
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody Map<String, Object> variables) {
        return ResponseEntity.ok(flowService.execute(flowId, variables));
    }
}
