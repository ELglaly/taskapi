package com.example.taskapi.service.user;

import com.example.taskapi.dto.UserDto;
import com.example.taskapi.entity.user.AppUser;
import com.example.taskapi.exception.*;
import com.example.taskapi.factory.UserFactory;
import com.example.taskapi.mapper.UserMapper;
import com.example.taskapi.repository.UserRepository;
import com.example.taskapi.request.LoginRequest;
import com.example.taskapi.request.RegistrationRequest;
import com.example.taskapi.security.CustomUserDetails;
import com.example.taskapi.security.JwtService;
import com.example.taskapi.validation.UserValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;
@Service
@Slf4j
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserFactory userFactory;
    private final UserValidation userValidation;
    private final JwtService jwtService;
    private final TransactionTemplate transactionTemplate;
    private final AuthenticationManager authenticationManager;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, UserFactory userFactory, UserValidation userValidation, JwtService jwtService, TransactionTemplate transactionTemplate, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.userFactory = userFactory;
        this.userValidation = userValidation;
        this.jwtService = jwtService;
        this.transactionTemplate = transactionTemplate;
        this.authenticationManager = authenticationManager;
    }


    @Override
    public UserDto registerUser(RegistrationRequest request) {

        log.info("Validate RegistrationRequest {}",request);
        userValidation.registrationRequestValidation(request);

        log.info("Check if user email exists: {}", request.email());
        Boolean emailExists = transactionTemplate.execute(status -> {
            status.isReadOnly();
            return userRepository.existsByAppUserContactEmail(request.email());
        });

        if (emailExists != null && emailExists) {
            log.error("User Email Already Exists: {}", (request.email()));
            throw new UserAlreadyExistsException();
        }

        else {
            log.info("Creating new user with email: {}", request.email());
            AppUser user = userFactory.createUser(request);
            AppUser finalUser1 = user;
            user = transactionTemplate.execute(status -> userRepository.save(finalUser1));
            return userMapper.toDto(user);
        }
    }

    @Override
    public String loginUser(LoginRequest loginRequest) {

        log.info("Validate LoginRequest {}", loginRequest);
        userValidation.loginRequestValidation(loginRequest);

        try {
            log.info("Login attempt for email: {}", loginRequest.email());
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()
                    );

            //handle authentication through your CustomAuthenticationProvider
            log.info("Authenticating user with email: {}", loginRequest.email());
            Authentication authentication = authenticationManager.authenticate(authToken);

            // This line is NEVER reached if authentication fails
            if(authentication.isAuthenticated()) {
                log.info("User Authenticated successfully");

                // Generate JWT token
              return jwtService.generateToken((UserDetails) authentication.getPrincipal());
            } else {
                log.error("Authentication object exists but not authenticated");
                throw new BadCredentialsException("Authentication failed");
            }

        } catch (AccountDisabledException |InValidCredientailException e) {
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
       return Optional.ofNullable((userRepository.findByAppUserContactEmail(email, UserDto.class)))
               .orElseThrow(UserNotFoundException::new);
    }
}
