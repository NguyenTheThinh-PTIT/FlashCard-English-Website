package com.education.flashEng.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClassJoinRequestRequest {

    @NotNull(message = "User id is required")
    private Long userId;

    @NotNull(message = "Class id is required")
    private Long classId;

}