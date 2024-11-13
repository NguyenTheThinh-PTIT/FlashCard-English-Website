package com.education.flashEng.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSetRequest {
    @NotNull(message = "Set id is required")
    private Long setId;

    @NotBlank(message = "Set name is required")
    private String name;

    @NotBlank(message = "Set description is required")
    private String description;

    private Long classId;

    @NotBlank(message = "Set Privacy Status is required")
    private String privacyStatus;
}
