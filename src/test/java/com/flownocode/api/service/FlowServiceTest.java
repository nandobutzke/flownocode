package com.flownocode.api.service;

import com.flownocode.api.domain.Block;
import com.flownocode.api.domain.Flow;
import com.flownocode.api.domain.enums.BlockType;
import com.flownocode.api.dto.request.CreateBlockRequest;
import com.flownocode.api.dto.request.CreateFlowRequest;
import com.flownocode.api.dto.response.FlowExecutionResponse;
import com.flownocode.api.dto.response.FlowResponse;
import com.flownocode.api.engine.ExecutionContext;
import com.flownocode.api.engine.WorkflowEngine;
import com.flownocode.api.exception.BusinessException;
import com.flownocode.api.exception.ResourceNotFoundException;
import com.flownocode.api.repository.FlowRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlowServiceTest {

    @Mock
    private FlowRepository flowRepository;

    @Mock
    private WorkflowEngine workflowEngine;

    @InjectMocks
    private FlowService flowService;

    // -------------------------------------------------------------------------
    // create()
    // -------------------------------------------------------------------------

    @Test
    void shouldCreateFlowSuccessfully() {
        UUID flowId = UUID.randomUUID();
        UUID blockId = UUID.randomUUID();

        CreateFlowRequest request = createFlowRequest(flowId, blockId, blockId);

        Flow savedFlow = buildFlow(flowId, blockId);
        when(flowRepository.save(any(Flow.class))).thenReturn(savedFlow);

        FlowResponse response = flowService.create(request);

        assertNotNull(response);
        assertEquals(flowId, response.getId());
        assertEquals("Test Flow", response.getName());
        verify(flowRepository).save(any(Flow.class));
    }

    @Test
    void shouldThrowBusinessExceptionWhenStartBlockIdDoesNotMatchAnyBlock() {
        UUID flowId = UUID.randomUUID();
        UUID blockId = UUID.randomUUID();
        UUID wrongStartBlockId = UUID.randomUUID();

        CreateFlowRequest request = createFlowRequest(flowId, blockId, wrongStartBlockId);

        assertThrows(BusinessException.class, () -> flowService.create(request));
    }

    // -------------------------------------------------------------------------
    // execute()
    // -------------------------------------------------------------------------

    @Test
    void shouldExecuteFlowAndReturnResult() {
        UUID flowId = UUID.randomUUID();
        UUID blockId = UUID.randomUUID();
        Flow flow = buildFlow(flowId, blockId);

        when(flowRepository.findById(flowId)).thenReturn(Optional.of(flow));

        doAnswer(invocation -> {
            ExecutionContext ctx = invocation.getArgument(1);
            ctx.setResult(true);
            return ctx;
        }).when(workflowEngine).run(any(Flow.class), any(ExecutionContext.class));

        FlowExecutionResponse response = flowService.execute(flowId, Map.of("input", 17));

        assertNotNull(response);
        assertEquals(flowId, response.getFlowId());
        assertEquals(true, response.getOutput().get("result"));
        verify(workflowEngine).run(any(Flow.class), any(ExecutionContext.class));
    }

    @Test
    void shouldThrowResourceNotFoundWhenFlowDoesNotExist() {
        UUID flowId = UUID.randomUUID();
        when(flowRepository.findById(flowId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> flowService.execute(flowId, Map.of("input", 17)));
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private CreateFlowRequest createFlowRequest(UUID flowId, UUID blockId, UUID startBlockId) {
        CreateBlockRequest blockRequest = new CreateBlockRequest();
        blockRequest.setId(blockId);
        blockRequest.setType(BlockType.END);
        blockRequest.setConfig(Map.of("result", true));

        CreateFlowRequest request = new CreateFlowRequest();
        request.setId(flowId);
        request.setName("Test Flow");
        request.setStartBlockId(startBlockId);
        request.setBlocks(List.of(blockRequest));
        return request;
    }

    private Flow buildFlow(UUID flowId, UUID blockId) {
        Block block = new Block();
        block.setId(blockId);
        block.setType(BlockType.END);
        block.setConfig(Map.of("result", true));

        Flow flow = new Flow();
        flow.setId(flowId);
        flow.setName("Test Flow");
        flow.setStartBlockId(blockId);
        flow.getBlocks().add(block);
        return flow;
    }
}
