package com.animalfarm.mlf.common;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

	// properties에서 설정한 경로를 가져옴
	@Value("${file.upload.path}")
	private String uploadDir;

	@PostMapping("/project-image")
	public ResponseEntity<?> uploadProjectImage(@RequestParam("file")
	MultipartFile file) {
		try {
			if (file.isEmpty()) {
				return ResponseEntity.badRequest().body("파일이 비어있습니다.");
			}
			// 1. 저장 디렉토리 생성
			File directory = new File(uploadDir + "/projects");
			if (!directory.exists()) {
				directory.mkdirs(); // 폴더가 없으면 생성
			}

			// 2. 파일명 중복 방지 (UUID + 원본파일명)
			String originalFileName = file.getOriginalFilename();
			String uuid = UUID.randomUUID().toString();
			String savedFileName = uuid + "_" + originalFileName;

			// 3. 파일 저장
			File destination = new File(uploadDir + "/projects/" + savedFileName);
			file.transferTo(destination);

			// 4. 클라이언트에 반환할 정보 (DB 저장용 파일명 또는 접근 URL)
			Map<String, Object> response = new HashMap<>();
			response.put("savedFileName", savedFileName);
			response.put("originalFileName", originalFileName);
			// 브라우저에서 접근 가능한 URL 경로
			response.put("url", "/uploads/projects/" + savedFileName);

			return ResponseEntity.ok(response);

		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 저장 실패");
		}
	}
}