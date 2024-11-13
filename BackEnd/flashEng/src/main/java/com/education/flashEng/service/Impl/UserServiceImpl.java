package com.education.flashEng.service.Impl;

import com.education.flashEng.entity.UserEntity;
import com.education.flashEng.exception.PasswordMismatchException;
import com.education.flashEng.exception.ResourceAlreadyExistsException;
import com.education.flashEng.exception.UserNotAuthenticatedException;
import com.education.flashEng.payload.request.RegisterRequest;
import com.education.flashEng.payload.request.UpdateUserPasswordRequest;
import com.education.flashEng.payload.request.UpdateUserRequest;
import com.education.flashEng.payload.response.UserDetailResponse;
import com.education.flashEng.repository.UserRepository;
import com.education.flashEng.service.UserService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;


    @Transactional
    @Override
    public boolean register(RegisterRequest registerRequest) {

        if (userRepository.existsByUsernameAndStatus(registerRequest.getUsername(), 1))
            throw new ResourceAlreadyExistsException("Username is already taken");

        if (userRepository.existsByEmailAndStatus(registerRequest.getEmail(), 1))
            throw new ResourceAlreadyExistsException("Email is already taken");

        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        UserEntity user = modelMapper.map(registerRequest, UserEntity.class);
        userRepository.save(user);
        return true;
    }

    @Override
    public UserEntity getUserFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (authentication == null || !authentication.isAuthenticated() || !(principal instanceof UserDetails)) {
            throw new UserNotAuthenticatedException("User is not authenticated");
        }

        UserDetails userDetails = (UserDetails) principal;

        return userRepository.findByUsernameAndStatus(userDetails.getUsername(), 1)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userDetails.getUsername()));
    }

    @Override
    public UserEntity getUserById(Long id) {
        return userRepository.findByIdAndStatus(id, 1)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }

    @Override
    public boolean update(UpdateUserRequest updateRequest) {
        UserEntity user = getUserFromSecurityContext();
        modelMapper.map(updateRequest, user);
        userRepository.save(user);
        return true;
    }

    @Transactional
    @Override
    public boolean updatePassword(UpdateUserPasswordRequest updateUserPasswordRequest) {
        UserEntity user = getUserFromSecurityContext();
        if(!passwordEncoder.matches(updateUserPasswordRequest.getOldPassword(), user.getPassword()))
            throw new PasswordMismatchException("Current password is incorrect");
        if(!updateUserPasswordRequest.getNewPassword().equals(updateUserPasswordRequest.getConfirmPassword()))
            throw new PasswordMismatchException("New password and confirm password do not match");
        user.setPassword(passwordEncoder.encode(updateUserPasswordRequest.getNewPassword()));
        userRepository.save(user);
        return true;
    }

    @Override
    public UserDetailResponse getUserDetailResponse() {
        UserEntity user = getUserFromSecurityContext();
        return modelMapper.map(user, UserDetailResponse.class);
    }

    @Override
    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

}
