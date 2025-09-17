package com.example.taskapi.validation;

import com.example.taskapi.entity.user.AppUser;
import com.example.taskapi.repository.UserRepository;
import com.example.taskapi.request.LoginRequest;
import com.example.taskapi.request.RegistrationRequest;

public interface UserValidation {

    void registrationRequestValidation(RegistrationRequest registrationRequest);
    void loginRequestValidation(LoginRequest loginRequest);
}
