package com.pdf.dto;

import java.util.List;

import lombok.Data;

@Data
public class PageDto {
	private Integer pageNumber;
	private Integer pageSize;
	private String column;
	private String sort;
	private String text;
	private String from;
	private String to;
	private Long total;
	private List data;
}
