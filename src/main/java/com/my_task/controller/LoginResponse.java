package com.my_task.controller;

import lombok.Builder;

@Builder
public record LoginResponse(String accessToken, String refreshToken) {

}
