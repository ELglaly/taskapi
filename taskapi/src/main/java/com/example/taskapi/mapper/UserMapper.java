package com.example.taskapi.mapper;

import com.example.taskapi.dto.UserDto;
import com.example.taskapi.entity.user.AppUser;
import com.example.taskapi.request.RegistrationRequest;
import org.modelmapper.ModelMapper;

public class UserMapper {

    private final ModelMapper modelMapper;

    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    // Add mapping methods here
    public AppUser toEntity(UserDto dto) {
        return modelMapper.map(dto, AppUser.class);
    }
   public AppUser toEntity(RegistrationRequest registrationRequest) {
        return modelMapper.map(registrationRequest, AppUser.class);
    }

    public UserDto toDto(AppUser user) {
        return modelMapper.map(user, UserDto.class);
    }


}
