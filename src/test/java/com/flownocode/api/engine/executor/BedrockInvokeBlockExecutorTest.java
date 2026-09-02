package com.flownocode.api.engine.executor;

import com.flownocode.api.config.BedrockProperties;
import com.flownocode.api.domain.Block;
import com.flownocode.api.domain.enums.BlockType;
import com.flownocode.api.engine.BlockResult;
import com.flownocode.api.engine.ExecutionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.ConversationRole;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseOutput;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseRequest;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseResponse;
import software.amazon.awssdk.services.bedrockruntime.model.Message;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BedrockInvokeBlockExecutorTest {

    @Mock
    private BedrockRuntimeClient bedrockRuntimeClient;

    private BedrockInvokeBlockExecutor executor;

    @BeforeEach
    void setUp() {
        executor = new BedrockInvokeBlockExecutor(
                bedrockRuntimeClient,
                new BedrockProperties("amazon.nova-micro-v1:0", 256, "us-east-1")
        );
    }

    @Test
    void shouldInvokeConverseAndStoreOutput() {
        UUID nextBlockId = UUID.randomUUID();
        Block block = block(Map.of(
                "promptVariable", "prompt",
                "outputVariable", "modelOutput"
        ), nextBlockId);

        ExecutionContext context = new ExecutionContext(Map.of("prompt", "Classify 17"));

        when(bedrockRuntimeClient.converse(any(ConverseRequest.class))).thenReturn(converseResponse("prime"));

        BlockResult result = executor.execute(block, context);

        assertEquals("prime", context.get("modelOutput"));
        assertTrue(result.hasNext());
        assertEquals(nextBlockId, result.nextBlockId());

        ArgumentCaptor<ConverseRequest> captor = ArgumentCaptor.forClass(ConverseRequest.class);
        verify(bedrockRuntimeClient).converse(captor.capture());
        ConverseRequest request = captor.getValue();
        assertEquals("amazon.nova-micro-v1:0", request.modelId());
        assertEquals(256, request.inferenceConfig().maxTokens());
        assertEquals(ConversationRole.USER, request.messages().get(0).role());
        assertEquals("Classify 17", request.messages().get(0).content().get(0).text());
    }

    @Test
    void shouldFailWhenPromptVariableIsMissingFromContext() {
        Block block = block(Map.of(
                "promptVariable", "prompt",
                "outputVariable", "modelOutput"
        ), UUID.randomUUID());

        ExecutionContext context = new ExecutionContext(Map.of("other", "value"));

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> executor.execute(block, context)
        );
        assertEquals("Missing context variable: prompt", error.getMessage());
    }

    @Test
    void shouldSupportBedrockInvokeType() {
        assertEquals(BlockType.BEDROCK_INVOKE, executor.getType());
    }

    private static ConverseResponse converseResponse(String text) {
        return ConverseResponse.builder()
                .output(ConverseOutput.builder()
                        .message(Message.builder()
                                .role(ConversationRole.ASSISTANT)
                                .content(ContentBlock.fromText(text))
                                .build())
                        .build())
                .build();
    }

    private Block block(Map<String, Object> config, UUID nextBlockId) {
        Block block = new Block();
        block.setId(UUID.randomUUID());
        block.setType(BlockType.BEDROCK_INVOKE);
        block.setNextBlockId(nextBlockId);
        block.setConfig(config);
        return block;
    }
}
