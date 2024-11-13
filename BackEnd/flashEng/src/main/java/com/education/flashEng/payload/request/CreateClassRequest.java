package com.education.flashEng.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateClassRequest {

    @NotBlank(message = "Class name is required")
    private String name;

}
