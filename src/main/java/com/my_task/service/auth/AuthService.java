package com.my_task.service.auth;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.my_task.model.User;
import com.my_task.service.user.UserService;
import com.my_task.utils.TokenUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {

	private final KeyPair keyPair;
	private final UserService userService;

	public LoginResponse generateToken(User user) {
		try {
			Instant now = Instant.now();
			String accessToken = TokenUtils.generateAccesToken(user, keyPair.getPrivate(), now);
			String refreshToken = TokenUtils.generateRefreshToken(user, keyPair.getPrivate(), now);
			return LoginResponse.builder().refreshToken(refreshToken).accessToken(accessToken).build();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
			throw new FailToGenerateTokenException("Something went wrong when trying to generate token");
		}
	}

	public LoginResponse refreshToken(String refreshToken) {
		var optJws = TokenUtils.parseToken(refreshToken, keyPair.getPublic());
		if (!optJws.isPresent()) {
			throw new AccessDeniedException("Invalid refresh token");
		}
		var claims = optJws.get().getPayload();
		if (!claims.get("type").equals("refresh")) {
			throw new AccessDeniedException("Invalid refresh token");
		}
		var user = userService.loadUserByUsername(claims.getSubject());
		return generateToken(user);
	}
}
