package com.gcs.app.mapper;

import com.gcs.app.facade.dto.AuthRequestDto;
import com.gcs.app.facade.dto.PasswordChangeRequestDto;
import com.gcs.app.rest.ChangePasswordRequest;
import com.gcs.app.rest.LoginRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    AuthRequestDto toAuthRequestDto(LoginRequest loginRequest);

    PasswordChangeRequestDto toPasswordChangeRequestDto(ChangePasswordRequest changePasswordRequest);
}