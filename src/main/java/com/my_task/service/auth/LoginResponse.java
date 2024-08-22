package com.my_task.service.auth;

import lombok.Builder;

@Builder
public record LoginResponse(String accessToken, String refreshToken) {

}
