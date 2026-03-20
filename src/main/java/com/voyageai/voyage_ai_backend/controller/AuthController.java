package com.voyageai.voyage_ai_backend.controller;

import com.voyageai.voyage_ai_backend.dto.LoginRequest;
import com.voyageai.voyage_ai_backend.dto.SignupRequest;
import com.voyageai.voyage_ai_backend.entity.User;
import com.voyageai.voyage_ai_backend.service.UserDetailsServiceImpl;
import com.voyageai.voyage_ai_backend.service.UserService;
import com.voyageai.voyage_ai_backend.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@Valid @RequestBody SignupRequest user) {
        try {
            userService.createNewUser(user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "User Created Successfully"));
        } catch (Exception e) {
            log.error("Error during signup", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest user) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );
            String jwt = jwtUtil.generateToken(user.getEmail());
            return ResponseEntity.ok(
                    Map.of(
                            "token", jwt,
                            "message", "Login Successful"
                    )
            );
        } catch (Exception e) {
            log.error("Exception occured while createAuthentcationToken ", e);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

}
