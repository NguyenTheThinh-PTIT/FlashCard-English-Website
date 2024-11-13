package com.education.flashEng.security;

import com.education.flashEng.entity.UserEntity;
import com.education.flashEng.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> optionalUserEntity;

        if (username.contains("@")) {
            optionalUserEntity = userRepository.findByEmailAndStatus(username, 1);
        } else {
            optionalUserEntity = userRepository.findByUsernameAndStatus(username, 1);
        }

        UserEntity userEntity = optionalUserEntity
                .orElseThrow(() -> new UsernameNotFoundException("User doesn't exist"));

        return new User(userEntity.getUsername(), userEntity.getPassword(), new ArrayList<>());
    }
}
