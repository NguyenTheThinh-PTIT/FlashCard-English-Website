package com.education.flashEng.payload.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WordResponse {
    private Long id;
    private String word;
    private String ipa;
    private String definition;
    private String example;
    private String image;
    private String audio;
}
