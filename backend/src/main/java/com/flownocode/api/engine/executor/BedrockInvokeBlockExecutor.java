package com.flownocode.api.engine.executor;

import com.flownocode.api.config.BedrockProperties;
import com.flownocode.api.domain.Block;
import com.flownocode.api.domain.enums.BlockType;
import com.flownocode.api.engine.BlockExecutor;
import com.flownocode.api.engine.BlockResult;
import com.flownocode.api.engine.ExecutionContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.ConversationRole;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseRequest;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseResponse;
import software.amazon.awssdk.services.bedrockruntime.model.InferenceConfiguration;
import software.amazon.awssdk.services.bedrockruntime.model.Message;

@Component
@RequiredArgsConstructor
public class BedrockInvokeBlockExecutor implements BlockExecutor {

    private final BedrockRuntimeClient bedrockRuntimeClient;
    private final BedrockProperties bedrockProperties;

    @Override
    public BlockResult execute(Block block, ExecutionContext context) {
        var config = block.getConfig();
        String promptVariable = requiredString(config.get("promptVariable"), "promptVariable");
        String outputVariable = requiredString(config.get("outputVariable"), "outputVariable");

        Object promptValue = context.get(promptVariable);
        if (promptValue == null) {
            throw new IllegalArgumentException("Missing context variable: " + promptVariable);
        }

        ConverseResponse response = bedrockRuntimeClient.converse(ConverseRequest.builder()
                .modelId(bedrockProperties.modelId())
                .messages(Message.builder()
                        .role(ConversationRole.USER)
                        .content(ContentBlock.fromText(String.valueOf(promptValue)))
                        .build())
                .inferenceConfig(InferenceConfiguration.builder()
                        .maxTokens(bedrockProperties.maxTokens())
                        .build())
                .build());

        context.put(outputVariable, extractText(response));

        return BlockResult.next(block.getNextBlockId());
    }

    @Override
    public BlockType getType() {
        return BlockType.BEDROCK_INVOKE;
    }

    private static String extractText(ConverseResponse response) {
        var content = response.output().message().content();
        if (content == null || content.isEmpty() || content.get(0).text() == null || content.get(0).text().isBlank()) {
            throw new IllegalStateException("Bedrock returned empty content");
        }
        return content.get(0).text();
    }

    private static String requiredString(Object value, String field) {
        if (!(value instanceof String text) || text.isBlank()) {
            throw new IllegalArgumentException("BEDROCK_INVOKE config." + field + " is required");
        }
        return text;
    }
}
