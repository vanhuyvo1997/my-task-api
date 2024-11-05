package com.my_task.utils;

import java.io.File;
import java.nio.file.Path;
import java.util.regex.Pattern;

import org.apache.logging.log4j.util.Strings;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import com.my_task.model.User;

public class FileUtils {

	private FileUtils() {
	}

	public static final Pattern AVATAR_IMAGE_NAME_PATTERN = Pattern.compile("^([a-zA-Z0-9_-]+)\\.([a-zA-Z0-9_-]+)$");
	public static final String SPLIT_FILE_EXTESION_MARK = "\\.";

	public static boolean validateAvatarFile(MultipartFile newAvatarFile, long maxImageUploadSize) {
		if (newAvatarFile == null) {
			return false;
		}
		if (newAvatarFile.getSize() >= maxImageUploadSize) {
			throw new MaxUploadSizeExceededException(maxImageUploadSize);
		}
		var contentType = newAvatarFile.getContentType();
		if (contentType != null && (contentType.equals("image/png") || contentType.equals("image/jpeg"))) {
			return true;
		}

		var fileName = newAvatarFile.getOriginalFilename();
		if (fileName != null) {
			String lowerCaseFilename = fileName.toLowerCase();
			return fileName.endsWith(".png") || lowerCaseFilename.endsWith(".jpeg")
					|| lowerCaseFilename.endsWith(".jpg");
		}

		return false;
	}

	public static String generateAvatarFileName(User user, MultipartFile newAvatarFile, String avoidedName) {
		var splittedParts = newAvatarFile.getOriginalFilename().split(SPLIT_FILE_EXTESION_MARK);
		var extension = "." + splittedParts[splittedParts.length - 1];
		var result = "";
		do {
			var hash = Math.round(Math.random() * 1000);
			result = user.getId() + "_" + hash + extension;
		} while (result.equals(avoidedName));
		return result;
	}

	public static void cleanUpDirectoryExcept(Path targetDirPath, String exceptedFileName) {
		// The file must be not blank, and the form must be filename.extestion
		if (Strings.isBlank(exceptedFileName) || !AVATAR_IMAGE_NAME_PATTERN.matcher(exceptedFileName).matches()) {
			throw new IllegalArgumentException("file name is blank or not in form 'filename.extension'");
		}

		var targetDir = targetDirPath.toFile();
		if (!targetDir.isDirectory()) {
			throw new IllegalArgumentException("Not a directory");
		}

		var exceptedFileNameParts = exceptedFileName.split(SPLIT_FILE_EXTESION_MARK);
		File[] files = targetDir.listFiles();
		for (var file : files) {
			if (file.isDirectory())
				continue;

			if (file.getName().equals(exceptedFileName))
				continue;

			var fileNameWithoutExtension = file.getName().split(SPLIT_FILE_EXTESION_MARK)[0];
			if (fileNameWithoutExtension.substring(0, 37).equals(exceptedFileNameParts[0].substring(0, 37))) {
				file.delete();
			}
		}

	}
}
