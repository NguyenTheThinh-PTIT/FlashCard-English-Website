package com.education.flashEng.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {

    @Pattern(regexp = "^[a-zA-Z0-9 ]*$", message = "FullName must not contain special characters")
    private String fullName;

    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Username must not contain special characters")
    private String username;

    @Email(message = "Invalid email format")
    private String email;

    private String country;
}
