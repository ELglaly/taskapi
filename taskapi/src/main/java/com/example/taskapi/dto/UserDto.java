package com.example.taskapi.dto;

public record UserDto(
    Long id,
    String username,
    String email,
    String phoneNumber,
    String address
) {
}
