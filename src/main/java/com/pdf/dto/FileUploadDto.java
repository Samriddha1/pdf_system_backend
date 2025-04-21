package com.pdf.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadDto {
	private String fileName;
	private String absolutePath;
	private String relativePath;
	private String fileUrl;
	@JsonFormat(pattern = "dd-MMM-yy")
	private Date createdAt;

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class FileAccessDto {
		private List<Long> userIds;
		private Long fileId;
	}

}
