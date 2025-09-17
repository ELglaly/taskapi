package com.example.taskapi.controller;


import com.example.taskapi.dto.UserDto;
import com.example.taskapi.request.LoginRequest;
import com.example.taskapi.request.RegistrationRequest;
import com.example.taskapi.response.ApiResponse;
import com.example.taskapi.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegistrationRequest request) {
        UserDto userDto = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.loginUser(request);
        return ResponseEntity.ok(new ApiResponse("Bearer",token));
    }
}
