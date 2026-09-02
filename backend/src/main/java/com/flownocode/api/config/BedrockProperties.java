package com.flownocode.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.bedrock")
public record BedrockProperties(String modelId, int maxTokens, String region) {
}
