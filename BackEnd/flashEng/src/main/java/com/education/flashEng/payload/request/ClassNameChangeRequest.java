package com.education.flashEng.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassNameChangeRequest {

    @NotNull(message = "Class Id is required")
    private Long classId;

    @NotBlank(message = "Name cannot be blank")
    private String name;
}
