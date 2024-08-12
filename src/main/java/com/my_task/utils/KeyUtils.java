package com.my_task.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyUtils {
	public static PrivateKey getPrivateKey(String fileName) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		// Read key from key file
		String key = new String(Files.readAllBytes(Paths.get(fileName)));

		// Remove first and last lines
		key = key.replaceAll("-----BEGIN PRIVATE KEY-----", "")
				.replaceAll("-----END PRIVATE KEY-----", "")
				.replaceAll("\\s+", "");
		
		// Decode key
		var keyBytes = Base64.getDecoder().decode(key);

		// Generate private key
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePrivate(spec);
	}
	
	public static PublicKey getPublicKey(String fileName) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		// Read from key file
		String key = new String(Files.readAllBytes(Paths.get(fileName)));

		// Remove first and last lines
		key = key.replaceAll("-----BEGIN PUBLIC KEY-----", "")
				.replaceAll("-----END PUBLIC KEY-----", "")
				.replaceAll("\\s+", "");
		
		// Decode key
		var keyBytes = Base64.getDecoder().decode(key);

		// Generate public key
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePublic(spec);
	}
}
