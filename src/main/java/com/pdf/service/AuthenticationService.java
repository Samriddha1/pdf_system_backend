package com.pdf.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pdf.dto.AuthenticaionResponse;
import com.pdf.dto.LoginRequest;
import com.pdf.dto.PageDto;
import com.pdf.dto.SignupRequest;
import com.pdf.model.User;
import com.pdf.repository.UserRepository;

@Service
public class AuthenticationService {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	JwtService jwtService;
	@Autowired
	MailService mailService;

	public AuthenticaionResponse sendSignUpRequest(SignupRequest body) throws Exception {
		// TODO Auto-generated method stub
		AuthenticaionResponse authenticaionResponse = new AuthenticaionResponse();
		User user = userRepository.findByEmail(body.getEmail()).orElse(null);
		if (user != null) {
			throw new Exception("User Already Exist ");
		}
		user = new User();
		BeanUtils.copyProperties(body, user);
		user.setPassword(encoder.encode(body.getPassword()));
		userRepository.save(user);
		String jwt = jwtService.generateToken(user);
		BeanUtils.copyProperties(user, authenticaionResponse);
		authenticaionResponse.setToken("Bearer " + jwt);
		return authenticaionResponse;
	}

	public AuthenticaionResponse sendLoginRequest(LoginRequest loginRequest) {
		AuthenticaionResponse authenticaionResponse = new AuthenticaionResponse();
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		User user = userRepository.findByEmail(loginRequest.getUsername())
				.orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
		String jwt = jwtService.generateToken(user);
		System.out.println(jwt);
		BeanUtils.copyProperties(user, authenticaionResponse);
		authenticaionResponse.setToken("Bearer " + jwt);
		return authenticaionResponse;
	}

	public PageDto getPageable(PageDto pageDto) {
		Sort sort = Sort.by(pageDto.getSort().equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC,
				pageDto.getColumn() != null ? pageDto.getColumn() : "id");
		Pageable pageable = PageRequest.of(pageDto.getPageNumber(), pageDto.getPageSize(), sort);
		Page<User> page = userRepository.findAll(pageable);
		pageDto.setData(page.getContent());
		pageDto.setTotal(page.getTotalElements());
		return pageDto;

	}

	public AuthenticaionResponse reset(SignupRequest body) throws Exception {
		AuthenticaionResponse authenticaionResponse = new AuthenticaionResponse();
		User user = userRepository.findByEmail(body.getEmail()).orElseThrow(() -> new Exception("User Not Found"));
		String resetLink = "http://localhost:3000/reset-password?userId=" + user.getId();
		String bodyText = "Dear User,\n\n" + "We received a request to reset your password.\n\n"
				+ "Please click the link below to reset your password:\n\n " + resetLink
				+ "   If you did not request a password reset, please ignore this email. Your password will remain unchanged.\n\n"
				+ "Regards,\n" + " Support Team";
		mailService.sendMail(user.getEmail(), bodyText);
		BeanUtils.copyProperties(user, authenticaionResponse);
		return authenticaionResponse;
	}

	public SignupRequest updatePassword(SignupRequest body) throws Exception {
		User user = userRepository.findById(body.getUserId()).orElseThrow(() -> new Exception("User not Found"));
		user.setPassword(encoder.encode(body.getPassword()));
		userRepository.save(user);
		return body;
	}

}
