package com.education.flashEng.controller;

import com.education.flashEng.payload.response.ApiResponse;
import com.education.flashEng.payload.response.NotificationResponse;
import com.education.flashEng.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/user")
    public ResponseEntity<?> getAllCurrentUserNotifications() {
        ApiResponse<List<NotificationResponse>> response = new ApiResponse<>(true,"All Notifications Fetched Successfully", notificationService.getAllCurrentUserNotifications());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
