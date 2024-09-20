package com.my_task.service.profile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.my_task.model.User;
import com.my_task.repository.UserRepository;
import com.my_task.service.exception.InvalidImageExcetption;
import com.my_task.utils.FileUtils;
import com.my_task.utils.UserUtils;

import lombok.Builder;

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

	public UpdateAvatarResponse updateAvatar(MultipartFile newAvatarFile) throws IOException {
		if (!FileUtils.validateAvatarFile(newAvatarFile, maxImageUploadSize)) {
			throw new InvalidImageExcetption("Uploaded file must has .png .jpg .jpeg");
		}
		var user = UserUtils.getAuthenticatedUser().orElseThrow(() -> new AccessDeniedException("Access denied"));

		// Create upload directories if it doesn't exist
		var imageUploadDirPath = Path.of(imageUploadDirectory);
		if (!Files.exists(imageUploadDirPath)) {
			Files.createDirectories(imageUploadDirPath);
		}

		// Save file to avatat folder with the generated name
		var oldFileName = Path.of(user.getAvatarUrl()).getFileName().toString();
		var generatedFileName = FileUtils.generateAvatarFileName(user, newAvatarFile, oldFileName);
		var saveToFilePath = imageUploadDirPath.resolve(generatedFileName);
		newAvatarFile.transferTo(saveToFilePath);

		// Save new avatar file path to database
		String savedAvatarName = saveToFilePath.toFile().getName();
		user.setAvatarUrl("/api/profiles/avatar/" + savedAvatarName);
		userRepository.save(user);

		// Delete if duplicated file name with different extension
		FileUtils.cleanUpDirectoryExcept(imageUploadDirPath, generatedFileName);

		return new UpdateAvatarResponse(savedAvatarName);
	}

	public ResponseEntity<?> getAvatar(String filename) {
		var optUser = UserUtils.getAuthenticatedUser();
		var user = optUser.orElseThrow(() -> new AccessDeniedException("Access denied"));

		if (user.getAvatarUrl() == null) {
			return ResponseEntity.notFound().build();
		}

		if (!user.getAvatarUrl().contains(filename)) {
			return ResponseEntity.notFound().build();
		}

		try {
			Resource resource;
			Path filePath = Path.of(imageUploadDirectory, filename).normalize();
			resource = new UrlResource(filePath.toUri());
			if (resource.exists() && resource.isReadable()) {
				MediaType mediaType = MediaType.parseMediaType(Files.probeContentType(filePath));
				return ResponseEntity.ok()
						.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
						.contentType(mediaType).body(resource);
			} else {
				throw new IOException();
			}
		} catch (IOException e) {
			return ResponseEntity.internalServerError().build();
		}
	}

	public Object getUserProfile() {
		var user = UserUtils.getAuthenticatedUser().orElseThrow(() -> new AccessDeniedException("Access denied"));
		return ProfileResponse.from(user);
	}

	@Builder
	private record ProfileResponse(String firstName, String lastName, String email, String role, String avatarUrl) {
		public static ProfileResponse from(User user) {
			return builder().firstName(user.getFirstName()).lastName(user.getLastName()).email(user.getEmail())
					.role(user.getRole().name()).avatarUrl(user.getAvatarUrl()).build();
		}
	}
}
