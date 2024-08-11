package com.my_task.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;

import com.my_task.model.User;

import io.jsonwebtoken.Jwts;

public class TokenUtils {

	public static String generateAccesToken(User user, Instant now) {

		var claims = new HashMap<String, String>();
		claims.put("firName", user.getFirstName());
		claims.put("lastName", user.getLastName());
		claims.put("role", user.getRole().name());
		claims.put("type", "access");

		var token = Jwts.builder()
				.subject(user.getEmail())
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plus(7, ChronoUnit.DAYS)))
				.claims(claims)
				.compact();
		return token;
	}

	public static String generateRefreshToken(User user, Instant now) {
		var token = Jwts
				.builder()
				.subject(user.getEmail())
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plus(14, ChronoUnit.DAYS)))
				.claim("type", "refresh")
				.compact();
		return token;
	}

}
