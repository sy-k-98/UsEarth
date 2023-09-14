package com.isfive.usearth.domain.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FileImageService {

	private final String profileDir = "./src/main/resources/projectImg/";

	public List<FileImage> createFileImageList(List<MultipartFile> fileList) {
		List<FileImage> fileImageList = new ArrayList<>();
		for (MultipartFile file : fileList) {
			FileImage fileImage = createFileImage(file);
			fileImageList.add(fileImage);
		}
		return fileImageList;
	}

	public FileImage createFileImage(MultipartFile file) {
		try {
			Files.createDirectories(Path.of(profileDir));
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		String originalName = file.getOriginalFilename();
		String[] fileNameSplit = originalName.split("\\.");
		String extension = fileNameSplit[fileNameSplit.length - 1]; // 확장자

		UUID uuid = UUID.randomUUID();
		String storedName = uuid + "." + extension;
		String profilePath = profileDir + storedName;

		try {
			file.transferTo(Path.of(profilePath));
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return FileImage.builder()
			.originalName(originalName)
			.storedName(storedName)
			.build();
	}

	public void deleteFile(String filename) {
		String filePath = profileDir + filename;
		File file = new File(filePath);

		if (file.delete()) {
			log.info("{} 파일이 성공적으로 삭제되었습니다.", filePath);
		} else {
			log.info("{} 파일이파일 삭제에 실패했습니다.", filePath);
		}
	}
}
