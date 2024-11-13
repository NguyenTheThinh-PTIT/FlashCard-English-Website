package com.education.flashEng.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateSetRequest {
    @NotBlank(message = "Set name is required")
    private String name;

    private String description;

    private Long classId;

    @NotBlank(message = "Privacy status is required")
    private String privacyStatus;

}
