package com.education.flashEng.payload.response;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailResponse {

    private Long id;

    private String fullName;

    private String username;

    private String email;

    private String country;

    public UserDetailResponse(String fullName, String username, String email, String country) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.country = country;
    }
}
