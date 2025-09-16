package com.example.taskapi.factory;

import com.example.taskapi.entity.user.AppUser;
import com.example.taskapi.mapper.UserMapper;
import com.example.taskapi.request.RegistrationRequest;
import com.example.taskapi.validation.UserValidationImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.ValidationUtils;

@Slf4j
public class UserFactoryImpl implements UserFactory {
    private final UserValidationImpl userValidationImpl;

    private final UserMapper userMapper;

    public UserFactoryImpl(UserValidationImpl userValidationImpl, UserMapper userMapper) {
        this.userValidationImpl = userValidationImpl;
        this.userMapper = userMapper;
    }


    public AppUser createUser(RegistrationRequest request) {
        AppUser user = userMapper.toEntity(request);
        user.setUsername(generateSafeUsername(request.email()));
        log.info("Created user: {}", user);
        ValidationUtils.invokeValidator(userValidationImpl,user);
        return user;
    }


    private String generateSafeUsername(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email for username generation");
        }

        String localPart = email.substring(0, email.indexOf("@"));

        // Clean up the local part to make it a valid username
        String username = localPart.replaceAll("[^a-zA-Z0-9._-]", "");

        // Ensure minimum length
        if (username.length() < 3) {
            username = username + "123";
        }

        // Ensure maximum length
        if (username.length() > 50) {
            username = username.substring(0, 50);
        }

        return username.toLowerCase();
    }

}
