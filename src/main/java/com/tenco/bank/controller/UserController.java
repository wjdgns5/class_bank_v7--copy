package com.tenco.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tenco.bank.dto.SignInDTO;
import com.tenco.bank.dto.SignUpDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController { 
	
	@Autowired // DI 처리 --> 의존성 주입
	private UserService userService;
	
	@Autowired // DI 처리 --> 의존성 주입
	private  HttpSession session; // 세션 준비
	
	/**
	 * 회원 가입 페이지 요청
	 * 주소 설계 : http://localhost:8080/user/sign-up
	 * @return
	 */
	@GetMapping("/sign-up")
	public String signUpPage() {
		
		// prefix: /WEB-INF/view/
		// return: user/signUp
		// suffix: .jsp 
		
		return "user/signUp";
	} // end of signUpPage()
	
	/**
	 * 회원 가입 로직 처리 요청
	 * 주소 설계 : http://localhost:8080/user/sign-up
	 * @param dto
	 * @return
	 */
	@PostMapping("/sign-up")
	public String signUpProc(SignUpDTO dto) {
		// SignUpDTO : SignUpDTO(username=이, password=asd123, fullname=순신)
		System.out.println("SignUpDTO : " + dto.toString()); // SignUpDTO 의 값을 toString으로 받는다.
		
		// 1. 인증검사 
		if(dto.getUsername() == null || dto.getUsername().trim().isEmpty()) { 
			// 유저 이름이 null 이거나 유저의 이름이 공백이 있거나 비어있는 경우
			throw new DataDeliveryException("username을 입력해주세요.", HttpStatus.BAD_REQUEST);
		}
		
		// 2. 인증검사
		if(dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
			// 유저 비밀번호가 null 이거나 유저 비밀번호가 공백이 들어가있거나 비어있는 경우
			throw new DataDeliveryException("password를 입력해주세요", HttpStatus.BAD_REQUEST);
		}
		
		// 3. 인증검사
		if(dto.getFullname() == null || dto.getFullname().trim().isEmpty()) {
			// 유저 풀 네임이 null 이거나 유저 풀 네임에 공백이 들어가있거나 비어있는 경우
			throw new DataDeliveryException("fullname을 입력해주세요.", HttpStatus.BAD_REQUEST);
		}
		
		// 유저 서비스의 회원 가입 등록 서비스 기능을 실행한다.
		userService.createUser(dto);
		
		return "redirect:/index";
	} // end of signUpProc()
	
	/**
	 * 로그인 페이지 이동
	 * 주소 설계 : http://localhost:8080/user/sign-in
	 * @return
	 */
	@GetMapping("/sign-in")
	public String signInPage() {
		
		return "/user/signIn";
	} // end of signInPage()
	
	/**
	 * 로그인 기능 설계
	 * @param dto
	 * @return
	 */
	@PostMapping("/sign-in")
	public String signProc(SignInDTO dto) {
		if(dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
			throw new DataDeliveryException("유저이름을 확인해주세요", HttpStatus.BAD_REQUEST);
		} 
		
		if(dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
			throw new DataDeliveryException("비밀번호를 입력해주세요", HttpStatus.BAD_REQUEST);
		}
		
		System.out.println("SignInDTO : " + dto.toString());
		
		User principal = userService.readUser(dto);
		
		session.setAttribute("principal", principal);
		System.out.println("principal : " + principal.toString());
		
		return "redirect:/index";
	}
	
	/**
	 * 로그아웃 기능 설계
	 * @return
	 */
	@GetMapping("/logout")
	public String logout() {
		
		session.invalidate(); // 세션 소멸
		
		return "redirect:/user/sign-in";
	}
	
	
	

}
