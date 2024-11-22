package com.education.flashEng.controller;

import com.education.flashEng.payload.request.ClassJoinRequestRequest;
import com.education.flashEng.payload.response.ApiResponse;
import com.education.flashEng.service.ClassJoinRequestService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/class/request/join")
public class ClassJoinRequestController {

    @Autowired
    private ClassJoinRequestService classJoinRequestService;

    @GetMapping("/{requestId}")
    public ResponseEntity<?> getClassJoinRequest(@PathVariable @NotNull(message = "requestId is required") Long requestId) {
        ApiResponse<?> response = new ApiResponse<>(true, "Class Join Request Fetched Successfully", classJoinRequestService.getClassJoinRequest(requestId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/existence")
    public ResponseEntity<?> checkExistance(@RequestParam @NotNull(message = "classId is required") Long classId){
        Map<String,Long> requestId = classJoinRequestService.checkExistance(classId);
        ApiResponse<?> response = new ApiResponse<>(true, "Request existed", requestId);
        HttpStatus status = HttpStatus.OK;
        if (requestId == null){
            response.setMessage("Request does not exist");
            status = HttpStatus.NOT_FOUND;
        }
        return new ResponseEntity<>(response, status);
    }

    @PostMapping()
    public ResponseEntity<?> createClassJoinRequest(@Valid @RequestBody ClassJoinRequestRequest classJoinRequestRequest) {
        try {
            classJoinRequestService.createClassJoinRequest(classJoinRequestRequest.getClassId(), classJoinRequestRequest.getUserId());
            ApiResponse<String> response = new ApiResponse<>(true, "Join Request Created Successfully.", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(false, "Failed to create join request: " + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/accept")
    public ResponseEntity<?> acceptClassJoinRequest(@RequestParam @NotNull(message = "joinRequestId is required") Long requestId) {
        ApiResponse<String> response = new ApiResponse<>(classJoinRequestService.acceptClassJoinRequest(requestId), "Join Request Accepted Successfully.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/reject")
    public ResponseEntity<?> rejectClassJoinRequest(@RequestParam @NotNull(message = "joinRequestId is required") Long requestId) {
        ApiResponse<String> response = new ApiResponse<>(classJoinRequestService.rejectClassJoinRequest(requestId), "Request Rejected Successfully", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/revoke")
    public ResponseEntity<?> revokeClassJoinRequest(@RequestParam @NotNull(message = "requestId is required") Long classJoinRequestId) {
        ApiResponse<String> response = new ApiResponse<>(classJoinRequestService.revokeClassJoinRequest(classJoinRequestId), "Request Revoked Successfully", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
