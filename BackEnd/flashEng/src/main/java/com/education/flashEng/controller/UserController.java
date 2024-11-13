package com.education.flashEng.controller;

import com.education.flashEng.payload.request.LoginRequest;
import com.education.flashEng.payload.request.RegisterRequest;
import com.education.flashEng.payload.request.UpdateUserPasswordRequest;
import com.education.flashEng.payload.request.UpdateUserRequest;
import com.education.flashEng.payload.response.ApiResponse;
import com.education.flashEng.service.AuthenticateService;
import com.education.flashEng.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private AuthenticateService authService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getUserDetail() {
        ApiResponse<?> response = new ApiResponse<>(true, "User Detail Fetched Successfully", userService.getUserDetailResponse());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        ApiResponse<String> response = new ApiResponse<String>(true,"Login Successful",authService.login(loginRequest));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        ApiResponse<String> response = new ApiResponse<String>(userService.register(registerRequest),"Registration Successful",null);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody UpdateUserRequest updateUserRequest) {
        ApiResponse<String> response = new ApiResponse<String>(userService.update(updateUserRequest),"Update Account Successful",null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody UpdateUserPasswordRequest updateUserPasswordRequest) {
        ApiResponse<String> response = new ApiResponse<String>(userService.updatePassword(updateUserPasswordRequest),"Update Password Successful",null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
