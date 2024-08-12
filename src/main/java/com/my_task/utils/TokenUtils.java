package com.my_task.utils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;

import com.my_task.model.User;

import io.jsonwebtoken.Jwts;

public class TokenUtils {

	public static String generateAccesToken(User user, PrivateKey key, Instant now) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		
		var claims = new HashMap<String, String>();
		claims.put("id", user.getId());
		claims.put("firstName", user.getFirstName());
		claims.put("lastName", user.getLastName());
		claims.put("role", user.getRole().name());
		claims.put("type", "access");

		var token = Jwts.builder()
				.subject(user.getEmail())
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plus(7, ChronoUnit.DAYS)))
				.claims(claims)
				.signWith(key)
				.compact();
		return token;
	}

	public static String generateRefreshToken(User user, PrivateKey key, Instant now) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		var token = Jwts
				.builder()
				.subject(user.getEmail())
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plus(14, ChronoUnit.DAYS)))
				.claim("type", "refresh")
				.signWith(key)
				.compact();
		return token;
	}
	


}
