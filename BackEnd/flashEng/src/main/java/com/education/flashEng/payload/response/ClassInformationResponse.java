package com.education.flashEng.payload.response;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassInformationResponse {
    private Long classId;

    private String className;

    private Integer numberOfMembers;

    private Integer numberOfSets;
}
