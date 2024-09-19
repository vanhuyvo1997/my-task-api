package com.my_task.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.my_task.service.profile.ProfileService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("api/profile")
@AllArgsConstructor
public class ProfileController {

	private final ProfileService profileService;

	@PutMapping("avatar")
	public ResponseEntity<?> updateAvatar(MultipartFile newAvatarFile) throws IOException {
		return ResponseEntity.ok(profileService.updateAvatar(newAvatarFile));
	}

	@GetMapping("avatar")
	public ResponseEntity<?> getAvatar() {
		return profileService.getAvatar();
	}
}
