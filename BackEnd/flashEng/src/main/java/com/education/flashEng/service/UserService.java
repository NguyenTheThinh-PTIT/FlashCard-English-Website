package com.education.flashEng.service;

import com.education.flashEng.entity.UserEntity;
import com.education.flashEng.payload.request.RegisterRequest;
import com.education.flashEng.payload.request.UpdateUserPasswordRequest;
import com.education.flashEng.payload.request.UpdateUserRequest;
import com.education.flashEng.payload.response.UserDetailResponse;

public interface UserService {
    boolean register(RegisterRequest registerRequest);
    UserEntity getUserFromSecurityContext();
    UserEntity getUserById(Long id);

    UserEntity getUserByUsername(String username);
    boolean update(UpdateUserRequest updateRequest);
    boolean updatePassword(UpdateUserPasswordRequest updateUserPasswordRequest);
    UserDetailResponse getUserDetailResponse();
}
