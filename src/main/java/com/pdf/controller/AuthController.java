package com.pdf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pdf.dto.AuthenticaionResponse;
import com.pdf.dto.LoginRequest;
import com.pdf.dto.PageDto;
import com.pdf.dto.SignupRequest;
import com.pdf.service.AuthenticationService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthenticationService authService;

	@PostMapping("/v1/signup")
	public AuthenticaionResponse signUP(@RequestBody SignupRequest body) throws Exception {
		return authService.sendSignUpRequest(body);
	}

	@PostMapping("/v1/reset")
	public AuthenticaionResponse Reset(@RequestBody SignupRequest body) throws Exception {
		return authService.reset(body);
	}

	@PostMapping("/v1/updatePassword")
	public SignupRequest updatePassword(@RequestBody SignupRequest body) throws Exception {
		return authService.updatePassword(body);
	}

	@PostMapping("/v1/login")
	public AuthenticaionResponse login(@RequestBody LoginRequest loginRequest) {
		return authService.sendLoginRequest(loginRequest);
	}

	@PostMapping("/v1/list")
	public PageDto getPageableData(@RequestBody PageDto pageDto) {
		return authService.getPageable(pageDto);

	}

}
