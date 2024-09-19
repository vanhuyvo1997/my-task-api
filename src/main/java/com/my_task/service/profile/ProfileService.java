package com.my_task.service.profile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.my_task.repository.UserRepository;
import com.my_task.service.exception.InvalidImageExcetption;
import com.my_task.utils.FileUtils;
import com.my_task.utils.UserUtils;

@Service
public class ProfileService {

	public ProfileService(@Value("${upload.image-upload-directory}") String uploadImagPath,
			@Value("${upload.max-image-upload-size}") long maxImageUploadSize, UserRepository userRepository) {
		this.imageUploadDirectory = uploadImagPath;
		this.maxImageUploadSize = maxImageUploadSize;
		this.userRepository = userRepository;
	}

	private final String imageUploadDirectory;

	private final long maxImageUploadSize;

	private final UserRepository userRepository;

	public UpdateAvatarResponse updateAvatar(MultipartFile newAvatarFile) throws IOException {
		if (!FileUtils.validateAvatarFile(newAvatarFile, maxImageUploadSize)) {
			throw new InvalidImageExcetption(
					"Uploaded file must has .png .jpg .jpeg less than " + maxImageUploadSize + " bytes");
		}
		var user = UserUtils.getAuthenticatedUser().orElseThrow(() -> new AccessDeniedException("Access denied"));

		// Create upload directories if it doesn't exist
		var imageUploadDirPath = Path.of(imageUploadDirectory);
		if (!Files.exists(imageUploadDirPath)) {
			Files.createDirectories(imageUploadDirPath);
		}

		// Save file to avatat folder with the generated name
		var generatedFileName = FileUtils.generateAvatarFileName(user, newAvatarFile);
		var saveToFilePath = imageUploadDirPath.resolve(generatedFileName);
		newAvatarFile.transferTo(saveToFilePath);

		// Save new avatar file path to database
		String savedAvatarPath = saveToFilePath.toString().replace("\\", "/");
		user.setAvatarUrl(savedAvatarPath);
		userRepository.save(user);

		// Delete if duplicated file name with different extension
		FileUtils.cleanUpDirectoryExcept(imageUploadDirPath, generatedFileName);

		return new UpdateAvatarResponse(savedAvatarPath);
	}
}
