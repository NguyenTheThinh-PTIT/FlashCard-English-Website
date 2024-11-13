package com.education.flashEng.controller;

import com.education.flashEng.payload.response.ApiResponse;
import com.education.flashEng.service.ClassSetRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/set-request")
public class ClassSetRequestController {
    @Autowired
    ClassSetRequestService classSetRequestService;

    @GetMapping("/{setRequestId}")
    public ResponseEntity<?> getSetRequest(@PathVariable Long setRequestId) {
        ApiResponse<?> response = new ApiResponse<>(true, "Accept Set Request Successfully", classSetRequestService.getSetRequest(setRequestId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/accept/{setRequestId}")
    public ResponseEntity<?> acceptSetRequest(@PathVariable Long setRequestId) {
        ApiResponse<?> response = new ApiResponse<>(classSetRequestService.acceptSetRequest(setRequestId), "Accept Set Request Successfully", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/reject/{setRequestId}")
    public ResponseEntity<?> rejectSetRequest(@PathVariable Long setRequestId) {
        ApiResponse<?> response = new ApiResponse<>(classSetRequestService.rejectSetRequest(setRequestId), "Reject Set Request Successfully", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
