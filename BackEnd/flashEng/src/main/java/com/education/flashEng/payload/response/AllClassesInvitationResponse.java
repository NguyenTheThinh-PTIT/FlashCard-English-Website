package com.education.flashEng.payload.response;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllClassesInvitationResponse {
    private String inviterUsername;
    private String inviteeUsername;
    private String message;
    private Long invitationId;
}
