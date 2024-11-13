package com.education.flashEng.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class CreateWordRequest {
    @NotNull(message = "Set Id is required")
    private Long setId;

    @NotBlank(message = "Word name is required")
    private String word;

    @NotBlank(message = "Ipa is required")
    private String ipa;

    @NotBlank(message = "Definition is required")
    private String definition;

    @NotBlank(message = "Example is required")
    private String example;

    @NotNull(message = "Image is required")
    private MultipartFile image;

    @NotBlank(message = "Audio is required")
    private String audio;
}
