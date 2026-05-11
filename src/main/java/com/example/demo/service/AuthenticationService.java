package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.UserDTO;

public interface AuthenticationService {
    LoginResponse login(LoginRequest loginRequest);
    UserDTO register(UserDTO userDTO);
    LoginResponse refreshToken(String refreshToken);
    void logout(Long userId);
    UserDTO getCurrentUser();
}
