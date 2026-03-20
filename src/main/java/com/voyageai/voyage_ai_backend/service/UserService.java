package com.voyageai.voyage_ai_backend.service;

import com.voyageai.voyage_ai_backend.dto.SignupRequest;
import com.voyageai.voyage_ai_backend.entity.User;
import com.voyageai.voyage_ai_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    public User createNewUser(SignupRequest request){

        // Check if user already exists
        User existingUser = userRepository.findByEmail(request.getEmail());
        if(existingUser != null){
            throw new RuntimeException("User already exists with this email");
        }

        // Convert DTO -> Entity
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }


    public User findByEmail (String email){
        return userRepository.findByEmail(email);
    }


}
