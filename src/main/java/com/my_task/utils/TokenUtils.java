package com.my_task.utils;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

import com.my_task.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

public class TokenUtils {

	private TokenUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static String generateAccesToken(User user, PrivateKey key, Instant now) {

		var claims = new HashMap<String, String>();
		claims.put("id", user.getId());
		claims.put("firstName", user.getFirstName());
		claims.put("lastName", user.getLastName());
		claims.put("role", user.getRole().name());
		claims.put("type", "access");

		return Jwts.builder()
				.subject(user.getEmail())
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plus(7, ChronoUnit.DAYS)))
				.claims(claims)
				.signWith(key)
				.compact();
	}

	public static String generateRefreshToken(User user, PrivateKey key, Instant now) {
		return Jwts
				.builder()
				.subject(user.getEmail())
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plus(14, ChronoUnit.DAYS)))
				.claim("type", "refresh")
				.signWith(key)
				.compact();
	}

	public static Optional<Jws<Claims>> parseToken(String token, PublicKey key) {
		if (token == null)
			return Optional.empty();
		try {
			token = token.replace("Bearer", "").trim();
			var parser = Jwts.parser().verifyWith(key).build();
			return Optional.of(parser.parseSignedClaims(token));
		} catch (JwtException ex) {
			return Optional.empty();
		}
	}

}
