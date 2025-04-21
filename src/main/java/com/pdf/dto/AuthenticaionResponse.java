package com.pdf.dto;

import lombok.Data;

@Data
public class AuthenticaionResponse {
	private String firstName;
	private String lastName;
	private String token;
	private String message;

}
