package com.flownocode.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CreateFlowRequest {

    @NotNull(message = "Flow id is required")
    private UUID id;

    @NotBlank(message = "Flow name is required")
    private String name;

    @NotNull(message = "startBlockId is required")
    private UUID startBlockId;

    @Valid
    @NotEmpty(message = "Flow must have at least one block")
    private List<CreateBlockRequest> blocks;
}
