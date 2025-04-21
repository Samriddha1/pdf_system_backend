package com.pdf.service;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pdf.dto.FileCommentDto;
import com.pdf.dto.FileUploadDto;
import com.pdf.dto.PageDto;
import com.pdf.dto.FileUploadDto.FileAccessDto;
import com.pdf.filter.TokenFilter;
import com.pdf.model.FileAccessUser;
import com.pdf.model.FileComment;
import com.pdf.model.FileUploadModel;
import com.pdf.model.User;
import com.pdf.repository.FileUploadRepository;
import com.pdf.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UploadService {

	@Value("${destination.path:c:/file-uploads/}")
	private String destPath;

	@Autowired
	FileUploadRepository fileUploadRepository;

	@Autowired
	UserRepository userRepository;

	public String uploadFile(MultipartFile file) throws Exception {
		FileUploadDto fileUploadDto = fileUpload(file);
		FileUploadModel fileUploadModel = new FileUploadModel();
		BeanUtils.copyProperties(fileUploadDto, fileUploadModel);
		fileUploadModel.setCreatedBy(TokenFilter.getGetUserId());
		fileUploadRepository.save(fileUploadModel);
		return null;
	}

	public FileUploadDto fileUpload(MultipartFile file) throws Exception {
		FileUploadDto fileUploadDto = new FileUploadDto();
		String originalFileName = file.getOriginalFilename();
		String fileExtension = "";
		int lastDotIndex = originalFileName.lastIndexOf(".");
		if (lastDotIndex != -1) {
			fileExtension = originalFileName.substring(lastDotIndex);
			originalFileName = originalFileName.substring(0, lastDotIndex);
		}
		String timeStamp = formatDateString(new Date(), "yyyyMMdd_HHmmss");
		String fileName = originalFileName + "_" + timeStamp + fileExtension;
		if (!new File(destPath).exists()) {
			log.info("Directory doesn't exist");
			new File(destPath).mkdirs();
		}
		File dest = new File(destPath, fileName);
		log.info("Destination fetched: {}", dest.getAbsolutePath());
		if (!dest.exists()) {
			try {
				log.info("File Created {}", dest.createNewFile());
			} catch (IOException e) {
				throw new Exception("File Error: " + e.getMessage());
			}
		}
		log.info("Multipart File: {}", file);
		try {
			file.transferTo(dest);
			log.info("file successfully uploaded to location: {}", dest);
			fileUploadDto.setAbsolutePath(String.valueOf(dest));
		} catch (IllegalStateException | IOException e) {
			throw new Exception("File Transfer Error: " + e.getMessage());
		}
		fileUploadDto.setFileName(fileName);
		return fileUploadDto;
	}

	public PageDto getPageable(PageDto pageDto) {
		Sort sort = Sort.by(pageDto.getSort().equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC,
				pageDto.getColumn() != null ? pageDto.getColumn() : "id");
		Pageable pageable = PageRequest.of(pageDto.getPageNumber(), pageDto.getPageSize(), sort);
		Page<FileUploadModel> page = fileUploadRepository.findAllByCreatedByOrFileAccessUserUserId(pageable,
				TokenFilter.getGetUserId(), TokenFilter.getGetUserId());
		Map<Long, String> userMap = userRepository.findAll().stream()
				.collect(Collectors.toMap(User::getId, obj -> obj.getFirstName() + " " + obj.getLastName()));
		List<FileUploadModel> contentList = page.stream().peek(file -> {
			Optional.ofNullable(file.getFileComment()).orElse(new ArrayList<>()).stream()
					.filter(comment -> comment.getUserId() != null)
					.forEach(comment -> comment.setCommentBy(userMap.get(comment.getUserId())));
		}).collect(Collectors.toList());
		pageDto.setData(contentList);
		pageDto.setTotal(page.getTotalElements());
		return pageDto;
	}

	public static Date getDateTimeFromString(String strDate, String formatter) {
		log.info("date String: {}, pattern: {}", strDate, formatter);
		try {
			Date dateTime = new SimpleDateFormat(formatter).parse(strDate);
			log.debug("dateTime: {}", dateTime);
			return dateTime;
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}

	public static String getDateFormateByStringDate(String dateStr) {
		List<String> patterns = List.of("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "dd-MMM-yy", "dd/MMM/yy", "dd.MM.yyyy",
				"dd-MM-yyyy HH:mm:ss");
		String formatter = patterns.stream().map(pattern -> {
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
			try {
				dateFormat.parse(dateStr);
				return pattern;
			} catch (ParseException ignored) {
				return null;
			}
		}).filter(dateFormat -> dateFormat != null).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Unknown date format: " + dateStr));

		return formatter;
	}

	public static String formatDateString(Date inputDate, String newPatternStr) {
		log.info("Original date to be formatted: {}", inputDate);
		DateTimeFormatter newPattern = DateTimeFormatter.ofPattern(newPatternStr);
		LocalDateTime datetime = LocalDateTime.ofInstant(inputDate.toInstant(), ZoneId.systemDefault());
		String output = datetime.format(newPattern);
		log.info("Formatted date Stirng: {}", output);
		return output;
	}

	public FileAccessDto giveAccess(FileAccessDto fileAccess) throws Exception {
		FileUploadModel fileUploadModel = fileUploadRepository.findById(fileAccess.getFileId())
				.orElseThrow(() -> new Exception("Not Found"));
		List<FileAccessUser> fileAccessUsers = fileAccess.getUserIds().stream().map(user -> {
			FileAccessUser accessUser = new FileAccessUser();
			accessUser.setUserId(user);
			accessUser.setFile(fileUploadModel);
			return accessUser;
		}).collect(Collectors.toList());
		fileUploadModel.getFileAccessUser().addAll(fileAccessUsers);
		fileUploadRepository.save(fileUploadModel);
		return fileAccess;
	}

	@Transactional
	public FileCommentDto addCommenToFile(FileCommentDto commentDto) throws Exception {
		FileUploadModel fileUploadModel = fileUploadRepository.findById(commentDto.getFileId())
				.orElseThrow(() -> new Exception("Not Found"));
		FileComment fileComment = new FileComment();
		fileComment.setUserId(TokenFilter.getGetUserId());
		fileComment.setComment(commentDto.getComment());
		fileComment.setFile(fileUploadModel);
		fileUploadModel.getFileComment().add(fileComment);
		fileUploadRepository.save(fileUploadModel);
		return commentDto;
	}

}
