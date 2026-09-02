package com.flownocode.api.domain.enums;

/**
 * Represents the type of a block in the workflow engine.
 * New types can be added here as the engine evolves.
 */
public enum BlockType {

    START,
    SET_VARIABLE,
    SUM,
    MOD,
    CONDITION,
    INCREMENT,
    PROMPT_TEMPLATE,
    BEDROCK_INVOKE,
    END
}
