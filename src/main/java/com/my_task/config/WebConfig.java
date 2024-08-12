package com.my_task.config;

import static com.my_task.utils.KeyUtils.getPrivateKey;
import static com.my_task.utils.KeyUtils.getPublicKey;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Value ("${jwt.private-key}")
	private String privateKeyFile;

	@Value("${jwt.public-key}")
	private String publicKeyFile;

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(BCryptVersion.$2A);
	}

	@Bean
	KeyPair keyPair() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		var privateKey = getPrivateKey(privateKeyFile);
		var publicKey = getPublicKey(publicKeyFile);
		return new KeyPair(publicKey, privateKey);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("http://localhost:3000/");
	}

}
