package com.example.taskapi.service.user;

import com.example.taskapi.dto.UserDto;
import com.example.taskapi.request.LoginRequest;
import com.example.taskapi.request.RegistrationRequest;

public interface UserService {
    UserDto registerUser(RegistrationRequest request);
    String loginUser(LoginRequest loginRequest);
    UserDto getUserByEmail(String email);



}
