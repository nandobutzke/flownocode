package com.flownocode.api.service;

import com.flownocode.api.domain.Block;
import com.flownocode.api.domain.Flow;
import com.flownocode.api.dto.request.CreateBlockRequest;
import com.flownocode.api.dto.request.CreateFlowRequest;
import com.flownocode.api.dto.response.FlowExecutionResponse;
import com.flownocode.api.dto.response.FlowResponse;
import com.flownocode.api.engine.ExecutionContext;
import com.flownocode.api.engine.WorkflowEngine;
import com.flownocode.api.exception.BusinessException;
import com.flownocode.api.exception.DuplicateResourceException;
import com.flownocode.api.exception.ResourceNotFoundException;
import com.flownocode.api.repository.FlowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FlowService {

    private final FlowRepository flowRepository;
    private final WorkflowEngine workflowEngine;

    @Transactional
    public FlowResponse create(CreateFlowRequest request) {
        validateFlowDoesNotExist(request.getId());
        validateStartBlockExists(request);

        Flow flow = toEntity(request);
        Flow saved = flowRepository.save(flow);

        return toResponse(saved);
    }

    private void validateFlowDoesNotExist(UUID flowId) {
        if (flowRepository.existsById(flowId)) {
            throw new DuplicateResourceException("Flow", flowId);
        }
    }

    private void validateStartBlockExists(CreateFlowRequest request) {
        boolean exists = request.getBlocks().stream()
                .anyMatch(block -> block.getId().equals(request.getStartBlockId()));

        if (!exists) {
            throw new BusinessException("startBlockId does not match any block id in the blocks list");
        }
    }

    private Flow toEntity(CreateFlowRequest request) {
        Flow flow = new Flow();
        flow.setId(request.getId());
        flow.setName(request.getName());
        flow.setStartBlockId(request.getStartBlockId());

        List<Block> blocks = request.getBlocks().stream()
                .map(blockRequest -> toBlockEntity(blockRequest, flow))
                .toList();

        flow.getBlocks().addAll(blocks);

        return flow;
    }

    private Block toBlockEntity(CreateBlockRequest request, Flow flow) {
        Block block = new Block();
        block.setId(request.getId());
        block.setType(request.getType());
        block.setNextBlockId(request.getNextBlockId());
        block.setConfig(request.getConfig());
        block.setFlow(flow);
        return block;
    }

    @Transactional(readOnly = true)
    public FlowExecutionResponse execute(UUID flowId, Map<String, Object> variables) {
        Flow flow = flowRepository.findById(flowId)
                .orElseThrow(() -> new ResourceNotFoundException("Flow", flowId));

        ExecutionContext context = new ExecutionContext(variables);

        workflowEngine.run(flow, context);

        return FlowExecutionResponse.builder()
                .flowId(flow.getId())
                .flowName(flow.getName())
                .output(Map.of("result", context.getResult()))
                .build();
    }

    private FlowResponse toResponse(Flow flow) {
        return FlowResponse.builder()
                .id(flow.getId())
                .name(flow.getName())
                .startBlockId(flow.getStartBlockId())
                .blockCount(flow.getBlocks().size())
                .createdAt(flow.getCreatedAt() != null ? flow.getCreatedAt() : flow.getUpdatedAt())
                .build();
    }
}
