package com.example.taskapi.mapper;

import com.example.taskapi.dto.UserDto;
import com.example.taskapi.entity.user.AppUser;
import com.example.taskapi.request.RegistrationRequest;
import org.modelmapper.ModelMapper;

public interface UserMapper {
    AppUser toEntity(UserDto dto);

    AppUser toEntity(RegistrationRequest registrationRequest);

     UserDto toDto(AppUser user);
}
