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
import com.example.taskapi.validation.UserValidationImpl;
import io.jsonwebtoken.security.Jwks;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.internal.TransactionManagement;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.security.auth.login.AccountLockedException;
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

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, UserFactory userFactory, UserValidation userValidation, JwtService jwtService, TransactionTemplate transactionTemplate) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.userFactory = userFactory;
        this.userValidation = userValidation;
        this.jwtService = jwtService;
        this.transactionTemplate = transactionTemplate;
    }


    @Override
    public UserDto registerUser(RegistrationRequest request) {

        log.info("Validate RegistrationRequest {}",request);
        userValidation.registrationRequestValidation(request);

        Boolean emailExists = transactionTemplate.execute(status -> {
            status.isReadOnly();
            return userRepository.existsByAppUserContactEmail(request.email());
        });

        if (emailExists != null && emailExists) {
            log.error("User Email Already Exists: {}", (request.email()));
            throw new UserAlreadyExistsException();
        }

        else {
            AppUser user = userFactory.createUser(request);
            log.error("User Created {}",user);
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
            AppUser user=  transactionTemplate.execute(status ->
                 Optional.ofNullable(userRepository.findByAppUserContactEmail(loginRequest.email(), AppUser.class))
                        .orElseThrow(() -> {
                            logFailedLogin(loginRequest.email(), "User not found");
                            return new InValidCredientailException();
                        })
                    );
            // Check if account is active
            assert user != null;
            if (user.getAppUserSecurity() !=null && user.getAppUserSecurity().getActive()) {
                logFailedLogin(loginRequest.email(), "Account inactive");
                throw new AccountDisabledException("Account is disabled");
            }


            // Create UserDetails for token generation
            CustomUserDetails userDetails = CustomUserDetails.from(user);

            // Generate JWT token with user details
            String token = jwtService.generateToken(userDetails);

            log.info("Login successful for email: {}", loginRequest.email());

            return token;

        } catch (AccountDisabledException |InValidCredientailException e) {
            throw e;
        }
        catch (Exception e) {
            // SECURITY: Log unexpected errors and throw generic exception
            log.error("Unexpected error during login for email: {}", (loginRequest.email()), e);
            throw new AppException("Login failed. Please try again later.");
        }
    }


    /**
     * SECURITY: Log failed login attempts
     */
    private void logFailedLogin(String email, String reason) {
        log.warn("Failed login attempt - email: {}, reason: {}", email, reason);
    }

    @Override
    public UserDto getUserByEmail(String email) {
       return Optional.ofNullable((userRepository.findByAppUserContactEmail(email, UserDto.class)))
               .orElseThrow(UserNotFoundException::new);
    }
}
