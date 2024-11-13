package com.education.flashEng.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudySessionRequest {
    private String difficulty;
    private Long wordId;
}
