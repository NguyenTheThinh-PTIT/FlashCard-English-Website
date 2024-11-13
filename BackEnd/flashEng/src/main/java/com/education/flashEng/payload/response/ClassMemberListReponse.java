package com.education.flashEng.payload.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassMemberListReponse {

    private Long classId;
    private String className;
    private List<MemberInfo> memberList;

    @Data
    @Builder
    public static class MemberInfo {
        private Long userId;
        private String userName;
        private String role;
    }
}