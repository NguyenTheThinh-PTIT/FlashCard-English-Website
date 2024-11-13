package com.education.flashEng.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassRoleChangeRequest {

        @NotNull(message = "Class Id is required")
        private Long classId;

        @NotNull(message = "User Id is required")
        private Long userId;

        @NotBlank(message = "Role cannot be blank")
        private String role;
}
