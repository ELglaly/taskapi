package com.example.taskapi.factory;

import com.example.taskapi.entity.user.AppUser;
import com.example.taskapi.entity.user.AppUserContact;
import com.example.taskapi.entity.user.AppUserSecurity;
import com.example.taskapi.exception.InvalidInputException;
import com.example.taskapi.mapper.UserMapper;
import com.example.taskapi.request.RegistrationRequest;
import com.example.taskapi.validation.UserValidationImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Slf4j
@Component
public class UserFactoryImpl implements UserFactory {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserFactoryImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }


    public AppUser createUser(RegistrationRequest request) {
        log.info("Creating user: {}", request);
        AppUser user = userMapper.toEntity(request);
        user.setName(request.name());
        // Create AppUserContact Object
        AppUserContact contact = new AppUserContact();
        contact.setEmail(request.email());
        user.setAppUserContact(contact);
        log.info("User Contact Done Successfully");

        // Create AppUserSecurity Object
        AppUserSecurity security = new AppUserSecurity();
        security.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setAppUserSecurity(security);
        log.info("User Security Done Successfully");

        user.setUsername(generateSafeUsername(request.email()));
        log.info("Created user: {}", user);

        return user;
    }


    private String generateSafeUsername(String email) {
        if (email == null || !email.contains("@")) {
            throw new InvalidInputException("Invalid email for username generation");
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
