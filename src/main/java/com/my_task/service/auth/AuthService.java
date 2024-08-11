package com.my_task.service.auth;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;

import org.springframework.stereotype.Service;

import com.my_task.controller.LoginResponse;
import com.my_task.model.User;
import com.my_task.utils.TokenUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {

	private final KeyPair keyPair;

	public LoginResponse generateToken(User user) {
		Instant now = Instant.now();
		String accessToken;
		String refreshToken;
		try {
			accessToken = TokenUtils.generateAccesToken(user, keyPair.getPrivate(), now);
			refreshToken = TokenUtils.generateRefreshToken(user, keyPair.getPrivate(), now);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
			throw new FailToGenerateTokenException("Something went wrong when trying to generate token");
		}
		return LoginResponse.builder().refreshToken(refreshToken).accessToken(accessToken).build();
	}
}
