package com.my_task.service.profile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.my_task.repository.UserRepository;
import com.my_task.service.exception.InvalidImageExcetption;
import com.my_task.service.exception.ResourceNotFoundException;
import com.my_task.utils.FileUtils;
import com.my_task.utils.UserUtils;

@Service
public class ProfileService {

	private final String imageUploadDirectory;

	private final long maxImageUploadSize;

	private final UserRepository userRepository;

	public ProfileService(@Value("${upload.image-upload-directory}") String uploadImagPath,
			@Value("${upload.max-image-upload-size}") long maxImageUploadSize, UserRepository userRepository) {
		this.imageUploadDirectory = uploadImagPath;
		this.maxImageUploadSize = maxImageUploadSize;
		this.userRepository = userRepository;
	}

	public Map<String,String> updateAvatar(MultipartFile newAvatarFile) throws IOException {
		if (!FileUtils.validateAvatarFile(newAvatarFile, maxImageUploadSize)) {
			throw new InvalidImageExcetption("Uploaded file must has .png .jpg .jpeg");
		}
		var user = UserUtils.getAuthenticatedUser().orElseThrow(() -> new AccessDeniedException("Access denied"));

		// Create upload directories if it doesn't exist
		var imageUploadDirPath = Path.of(imageUploadDirectory);
		if (!Files.exists(imageUploadDirPath)) {
			Files.createDirectories(imageUploadDirPath);
		}

		// Save file to avatar folder with the generated name
		var oldFileName = user.getAvatarUrl() != null ? Path.of(user.getAvatarUrl()).getFileName().toString() : "";
		var generatedFileName = FileUtils.generateAvatarFileName(user, newAvatarFile, oldFileName);
		var destination = imageUploadDirPath.resolve(generatedFileName);
		newAvatarFile.transferTo(destination);

		// Save new avatar file path to database
		var avatarUrl = "/api/profiles/avatar/" + generatedFileName;
		user.setAvatarUrl(avatarUrl);
		userRepository.save(user);

		// Delete if duplicated file name with different extension
		FileUtils.cleanUpDirectoryExcept(imageUploadDirPath, generatedFileName);
		
		var response = new HashMap<String, String>();
		response.put("avatarUrl", avatarUrl);
		return response;
	}

	public Resource getAvatar(String filename) throws IOException {
		var user = UserUtils.getAuthenticatedUser()
				.orElseThrow(() -> new AccessDeniedException("Access denied"));
		if (user.getAvatarUrl() == null || !user.getAvatarUrl().contains(filename)) {
			throw new ResourceNotFoundException("Not found file " + filename);
		}
		Path filePath = Path.of(imageUploadDirectory, filename).normalize();
		return getResource(filePath);
	}
	
	private Resource  getResource(Path filePath) throws IOException {
		Resource resource;
		resource = new UrlResource(filePath.toUri());
		if (resource.exists() && resource.isReadable()) {
			return resource;
		} else {
			throw new IOException();
		}
	}

	public ProfileResponse getUserProfile() {
		var user = UserUtils.getAuthenticatedUser().orElseThrow(() -> new AccessDeniedException("Access denied"));
		return ProfileResponse.from(user);
	}

	
}
