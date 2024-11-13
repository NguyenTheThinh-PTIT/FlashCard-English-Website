package com.education.flashEng.controller;

import com.education.flashEng.enums.AccessModifierType;
import com.education.flashEng.payload.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/access/type")
public class AccessModifierController {
    @GetMapping
    public ResponseEntity<?> getAccessModifier() {
        ApiResponse<?> response = new ApiResponse<>(true, "Privacy Set Information Fetched Successfully", AccessModifierType.type());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
