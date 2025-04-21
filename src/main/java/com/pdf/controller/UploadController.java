package com.pdf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pdf.dto.FileCommentDto;
import com.pdf.dto.PageDto;
import com.pdf.dto.FileUploadDto.FileAccessDto;
import com.pdf.service.UploadService;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

	@Autowired
	UploadService uploadService;

	@PostMapping(path = "/uploadFiles", produces = "application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> uploadFile(@RequestParam(value = "file", required = true) MultipartFile file)
			throws Exception {
		String fileUploadResponse = uploadService.uploadFile(file);
		return new ResponseEntity<String>(fileUploadResponse, HttpStatus.OK);
	}

	@PostMapping("/v1/list")
	public PageDto getPageableData(@RequestBody PageDto pageDto) {
		return uploadService.getPageable(pageDto);

	}

	@PostMapping("/v1/access")
	public FileAccessDto giveAccess(@RequestBody FileAccessDto fileAccess) throws Exception {
		return uploadService.giveAccess(fileAccess);

	}

	@PostMapping("/v1/addComment")
	public FileCommentDto FileCommentDto(@RequestBody FileCommentDto fileCommentDto) throws Exception {
		return uploadService.addCommenToFile(fileCommentDto);

	}

}
