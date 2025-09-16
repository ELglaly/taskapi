package com.example.taskapi.factory;

import com.example.taskapi.entity.user.AppUser;
import com.example.taskapi.request.RegistrationRequest;

public interface UserFactory {

    AppUser createUser(RegistrationRequest request);
}
