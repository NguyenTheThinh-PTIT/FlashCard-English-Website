package com.education.flashEng.payload.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassJoinRequestResponse {

    private ClassInformationResponse classInformationResponse;

    private String requesterName;

    private Long requestId;
}
