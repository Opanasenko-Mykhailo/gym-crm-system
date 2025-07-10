package com.gcs.app.service;

import com.gcs.app.facade.dto.AuthRequestDto;
import com.gcs.app.facade.dto.AuthResponseDto;

public interface AuthService {
    AuthResponseDto authenticate(AuthRequestDto dto);
}
