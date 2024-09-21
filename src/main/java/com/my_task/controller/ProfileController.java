package com.my_task.controller;

import java.io.IOException;
import java.nio.file.Files;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.my_task.service.profile.ProfileService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("api/profiles")
@AllArgsConstructor
public class ProfileController {

	private final ProfileService profileService;
	

	@GetMapping
	public ResponseEntity<?> getProfile() {
		return ResponseEntity.ok(profileService.getUserProfile());
	}

	@PutMapping("avatar")
	public ResponseEntity<?> updateAvatar(MultipartFile avatarFile) throws IOException {
		return ResponseEntity.ok(profileService.updateAvatar(avatarFile));
	}

	@GetMapping("avatar/{filename}")
	public ResponseEntity<?> getAvatar(@PathVariable String filename) throws IOException {
		var resource = profileService.getAvatar(filename);
		var mediaType =	MediaType.parseMediaType(Files.probeContentType(resource.getFile().toPath()));
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; file=/" + resource.getFilename() + "/")
				.contentType(mediaType).body(resource); 
	}
}
