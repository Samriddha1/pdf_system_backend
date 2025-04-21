package com.pdf.dto;

import com.pdf.enums.Role;

import lombok.Data;

@Data
public class SignupRequest {

	private Long userId;
	private String email;
	private String firstName;
	private String lastName;
	private String password;
	private Role role;
}
