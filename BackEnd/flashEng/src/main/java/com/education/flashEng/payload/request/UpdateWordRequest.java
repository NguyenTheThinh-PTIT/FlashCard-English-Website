package com.education.flashEng.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
@Setter
@Getter
public class UpdateWordRequest {

    @NotNull(message = "Id is required")
    private Long id;

    private String word;

    private String ipa;

    private String definition;

    private String example;

    private MultipartFile image;

    private String audio;
}
