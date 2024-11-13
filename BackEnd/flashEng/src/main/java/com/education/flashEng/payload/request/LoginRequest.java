package com.education.flashEng.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {

    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[a-zA-Z0-9@.]*$", message = "Username must not contain special characters")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

}
